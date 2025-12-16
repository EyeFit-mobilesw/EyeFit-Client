package com.example.eyefit.data.repository

import android.util.Log
import com.example.eyefit.R
import com.example.eyefit.data.model.ExerciseEntity
import com.example.eyefit.data.model.ExerciseUiModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ExerciseRepository {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // [1] UI 상태 관리
    private val _uiListFlow = MutableStateFlow<List<ExerciseUiModel>>(emptyList())
    val uiListFlow: StateFlow<List<ExerciseUiModel>> = _uiListFlow.asStateFlow()

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    // 로컬 캐시
    private var cachedExercises = listOf<ExerciseEntity>()
    private var cachedUnlockedIds = mutableSetOf<Long>()
    private val selectedExerciseIds = mutableSetOf<Long>() // 기본 선택: 1번

    // 리스너 (로그아웃 시 해제용)
    private var userListener: ListenerRegistration? = null

    init {
        // 1. 운동 데이터(Master Data)는 로그인 여부와 상관없이 불러옴
        fetchMasterData()

        // 2. 로그인 상태 감지 -> 유저 데이터(포인트, 해금목록) 리스닝
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d("Repo", "로그인 감지됨: ${user.uid}")
                startListeningToUser(user.uid)
            } else {
                Log.d("Repo", "로그아웃됨")
                stopListeningToUser()
                _userPoints.value = 0
            }
        }
    }

    // --- [Master Data] 운동 목록 가져오기 ---
    private fun fetchMasterData() {
        db.collection("exercises").orderBy("id").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // DB에 운동 데이터가 아예 없으면 업로드 (개발용)
                    uploadInitialData()
                } else {
                    // [중요] toObjects가 동작하려면 Entity에 기본값이 있어야 함
                    cachedExercises = result.toObjects(ExerciseEntity::class.java)
                    Log.d("Repo", "운동 데이터 로드 완료: ${cachedExercises.size}개")
                    refreshUiData()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Repo", "운동 데이터 로드 실패", e)
            }
    }

    // --- [User Data] 유저 정보 실시간 감지 ---
    private fun startListeningToUser(uid: String) {
        userListener?.remove() // 기존 리스너 제거

        val userDocRef = db.collection("users").document(uid)

        userListener = userDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener

            if (snapshot != null && snapshot.exists()) {
                val points = snapshot.getLong("points")?.toInt() ?: 0
                _userPoints.value = points

                val unlockedList = snapshot.get("unlockedExercises") as? List<Long> ?: listOf()
                cachedUnlockedIds.clear()
                cachedUnlockedIds.addAll(unlockedList)

                // [추가] DB에서 플레이리스트 가져오기
                val dbPlaylist = snapshot.get("playlist") as? List<Long> ?: listOf(1L)
                selectedExerciseIds.clear()
                selectedExerciseIds.addAll(dbPlaylist)

                refreshUiData()
            }
        }
    }

    private fun stopListeningToUser() {
        userListener?.remove()
        userListener = null
    }

    // --- [UI Logic] 데이터 병합 ---
    private fun refreshUiData() {
        if (cachedExercises.isEmpty()) return

        val newList = cachedExercises.map { exercise ->
            val isUnlocked = cachedUnlockedIds.contains(exercise.id)
            val isSelected = selectedExerciseIds.contains(exercise.id)

            val displayTitle = if (isUnlocked) exercise.title else "잠금된 운동"
            val displaySubTitle = if (isUnlocked) exercise.subTitle else "눈 운동 지속 완료 시 오픈 예정"
            val displayTimeStr = if (isUnlocked && exercise.time > 0) {
                "%d분 %02d초".format(exercise.time / 60, exercise.time % 60)
            } else ""

            // 리소스 ID 매핑
            val imgRes = if (isUnlocked) getDrawableIdByName(exercise.imageUrl) else R.drawable.ic_lock

            ExerciseUiModel(
                id = exercise.id,
                title = displayTitle,
                subTitle = displaySubTitle,
                timeString = displayTimeStr,
                imageResId = imgRes,
                isUnlocked = isUnlocked,
                isSelected = isSelected,
                descriptionTitle = "운동 방법", // DB에 필드가 있다면 exercise.description 사용
                descriptionContent = if(isUnlocked) exercise.description else ""
            )
        }
        _uiListFlow.value = newList
    }

    // --- [Action] 잠금 해제 ---
    fun unlockExercise(exerciseId: Long): Boolean {
        val uid = auth.currentUser?.uid ?: return false
        val userDocRef = db.collection("users").document(uid)
        val cost = 100

        if (_userPoints.value < cost) return false

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userDocRef)
            val currentPoints = snapshot.getLong("points") ?: 0

            if (currentPoints >= cost) {
                transaction.update(userDocRef, "points", currentPoints - cost)
                transaction.update(userDocRef, "unlockedExercises", FieldValue.arrayUnion(exerciseId))
            } else {
                throw FirebaseFirestoreException("포인트 부족", FirebaseFirestoreException.Code.ABORTED)
            }
        }.addOnSuccessListener {
            Log.d("Repo", "잠금 해제 성공")
        }.addOnFailureListener {
            Log.e("Repo", "잠금 해제 실패", it)
        }
        return true
    }

    // --- [Action] 포인트 적립 ---
    fun addPoints(amount: Int) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("points", FieldValue.increment(amount.toLong()))
    }

    // --- [Action] 선택 토글 ---
    fun toggleExerciseSelection(id: Long) {
        val uid = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(uid)

        // 1. [UI 반응 속도 UP] 로컬에서 먼저 바꿈 (Optimistic Update)
        if (selectedExerciseIds.contains(id)) {
            selectedExerciseIds.remove(id)
        } else {
            selectedExerciseIds.add(id)
        }
        refreshUiData() // 화면 먼저 갱신

        // 2. [DB 동기화] 백그라운드에서 Firestore 업데이트
        if (selectedExerciseIds.contains(id)) {
            // 방금 추가했으므로 DB에도 추가 (Union)
            userDocRef.update("playlist", FieldValue.arrayUnion(id))
        } else {
            // 방금 삭제했으므로 DB에서도 삭제 (Remove)
            userDocRef.update("playlist", FieldValue.arrayRemove(id))
        }
    }

    // Detail 화면용
    fun getExerciseById(id: Long): ExerciseUiModel? {
        return _uiListFlow.value.find { it.id == id }
    }

    // --- Helper: 초기 데이터 업로드 ---
    private fun uploadInitialData() {
        Log.d("Repo", "초기 운동 데이터 업로드 시작")
        val initialData = listOf(
            ExerciseEntity(1, "8자 그리기 운동", "눈 혈액순환 개선", "눈을 크게 뜨고 8자를 그려보세요.", "", 420, "img_infinity", ""),
            ExerciseEntity(2, "눈 깜빡이기 운동", "안구건조 완화", "4초에 한 번씩 눈을 깜빡이세요.", "", 420, "img_blink", ""),
            ExerciseEntity(3, "눈 근육 스트레칭", "홍채 근육 단련", "위 아래 왼쪽 오른쪽을 끝까지 보세요.", "", 120, "img_stretch", ""),
            ExerciseEntity(4, "눈 콧등 마사지", "눈 피로도 개선", "엄지와 검지로 코 양옆을 눌러주세요.", "", 90, "img_massage", ""),
            ExerciseEntity(5, "X자 그리기 운동", "시력 개선 도움", "대각선 방향으로 눈을 움직이세요.", "", 150, "img_x_shape", ""),
            ExerciseEntity(6, "매직 아이 운동", "집중력 향상 최고", "화면 너머를 응시하세요.", "", 180, "img_infinity", "")
        )
        initialData.forEach {
            db.collection("exercises").document(it.id.toString()).set(it)
        }
    }

    private fun getDrawableIdByName(name: String): Int {
        return when(name) {
            "img_infinity" -> R.drawable.img_infinity
            "img_blink" -> R.drawable.img_blink
            "img_stretch" -> R.drawable.img_massage
            "img_massage" -> R.drawable.img_massage
            "img_x_shape" -> R.drawable.img_x_shape
            else -> R.drawable.ic_lock
        }
    }
}