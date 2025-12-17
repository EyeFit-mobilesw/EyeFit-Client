package com.example.eyefit.data.repository

import android.util.Log
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

    // UI 상태 관리
    private val _uiListFlow = MutableStateFlow<List<ExerciseUiModel>>(emptyList())
    val uiListFlow: StateFlow<List<ExerciseUiModel>> = _uiListFlow.asStateFlow()

    private val _userPoints = MutableStateFlow(0)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    // 로컬 캐시
    private var cachedExercises = listOf<ExerciseEntity>()
    private var cachedUnlockedIds = mutableSetOf<Long>()
    private val selectedExerciseIds = mutableSetOf<Long>()

    // 리스너 (로그아웃 시 해제용)
    private var userListener: ListenerRegistration? = null

    init {
        fetchMasterData()

        // 로그인 상태 감지 -> 유저 데이터(포인트, 해금목록) 리스닝
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

    // 운동 목록 가져오기 ---
    private fun fetchMasterData() {
        db.collection("exercises").orderBy("id").get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    // 개발용
                    uploadInitialData()
                } else {
                    cachedExercises = result.toObjects(ExerciseEntity::class.java)
                    Log.d("Repo", "운동 데이터 로드 완료: ${cachedExercises.size}개")
                    refreshUiData()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Repo", "운동 데이터 로드 실패", e)
            }
    }

    // 유저 정보 실시간 감지
    private fun startListeningToUser(uid: String) {
        userListener?.remove() // 기존 리스너 제거

        val userDocRef = db.collection("users").document(uid)

        userListener = userDocRef.addSnapshotListener { snapshot, e ->
            if (e != null) return@addSnapshotListener

            if (snapshot != null && snapshot.exists()) {
                val points = snapshot.getLong("points")?.toInt() ?: 0
                _userPoints.value = points

                val rawUnlocked = snapshot.get("unlockedExercises") as? List<*>
                val unlockedList = rawUnlocked?.mapNotNull { (it as? Number)?.toLong() } ?: listOf()

                cachedUnlockedIds.clear()
                cachedUnlockedIds.addAll(unlockedList)

                // DB에서 플레이리스트 가져오기
                val rawPlaylist = snapshot.get("playlist") as? List<*>
                val dbPlaylist = rawPlaylist?.mapNotNull { (it as? Number)?.toLong() } ?: listOf(1L)
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

    // 데이터 병합 ---
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

            val displayImageUrl = if (isUnlocked) exercise.imageUrl else ""

            ExerciseUiModel(
                id = exercise.id,
                title = displayTitle,
                subTitle = displaySubTitle,
                timeString = displayTimeStr,
                imageUrl = displayImageUrl,
                isUnlocked = isUnlocked,
                isSelected = isSelected,
                descriptionTitle = exercise.description,
                descriptionContent = if (isUnlocked) exercise.detailedDescription else "",
                animationUrl = exercise.animationUrl
            )
        }
        _uiListFlow.value = newList
    }

    // 잠금 해제
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

    // 포인트 적립
    fun addPoints(amount: Int) {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid)
            .update("points", FieldValue.increment(amount.toLong()))
    }

    // 선택 토글
    fun toggleExerciseSelection(id: Long) {
        val uid = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(uid)

        // 로컬에서 먼저 바꿈
        if (selectedExerciseIds.contains(id)) {
            selectedExerciseIds.remove(id)
        } else {
            selectedExerciseIds.add(id)
        }
        refreshUiData() // 화면 먼저 갱신

        // DB 동기화 - 백그라운드에서 Firestore 업데이트
        if (selectedExerciseIds.contains(id)) {
            userDocRef.update("playlist", FieldValue.arrayUnion(id))
        } else {
            userDocRef.update("playlist", FieldValue.arrayRemove(id))
        }
    }

    // Detail 화면용
    fun getExerciseById(id: Long): ExerciseUiModel? {
        return _uiListFlow.value.find { it.id == id }
    }

    // --- Helper: 초기 데이터 업로드 ---
    private fun uploadInitialData() {
    }
}