package com.example.eyefit.data.repository

import com.example.eyefit.R
import com.example.eyefit.data.model.ExerciseEntity
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.model.UserExerciseEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ExerciseRepository {

    // [1] 마스터 데이터 (고정)
    private val allExercises = listOf(
        ExerciseEntity(1, "8자 그리기 운동", "눈 혈액순환 개선", "", "", 420, "img_infinity", ""),
        ExerciseEntity(2, "눈 깜빡이기 운동", "안구건조 완화", "", "", 420, "img_blink", ""),
        ExerciseEntity(3, "눈 근육 스트레칭", "홍채 근육 단련", "", "", 120, "img_stretch", ""),
        ExerciseEntity(4, "눈 콧등 마사지", "눈 피로도 개선", "", "", 90, "img_massage", ""),
        ExerciseEntity(5, "X자 그리기 운동", "시력 개선 도움", "", "", 150, "img_x_shape", ""),
        ExerciseEntity(
            id = 6,
            title = "매직 아이 운동",
            subTitle = "집중력 향상 최고",
            description = "...",
            detailedDescription = "...",
            time = 180,
            imageUrl = "img_infinity", // 잠금 해제되면 보일 이미지 (테스트용으로 기존 이미지 사용)
            animationUrl = ""
        )
    )

    // [2] 사용자 상태 (잠금 여부)
    private var userStatus = listOf(
        UserExerciseEntity(1, 100, 1, true),
        UserExerciseEntity(2, 100, 2, true),
        UserExerciseEntity(3, 100, 3, true),
        UserExerciseEntity(4, 100, 4, true),
        UserExerciseEntity(5, 100, 5, true),
        UserExerciseEntity(6, 100, 6, false)
    )

    // [1] 사용자 포인트 (초기값 50으로 설정하여 '부족함' 테스트 가능하게 함)
    private val _userPoints = MutableStateFlow(50)
    val userPoints: StateFlow<Int> = _userPoints.asStateFlow()

    // [3] 플레이리스트 (사용자가 선택한 운동 ID 저장소) - DB 역할
    private val selectedExerciseIds = mutableSetOf<Long>(1)

    // [4] 실시간 데이터 스트림 (방송국 역할)
    // 뷰모델들은 이제 이 _uiListFlow만 바라봅니다.
    private val _uiListFlow = MutableStateFlow<List<ExerciseUiModel>>(emptyList())
    val uiListFlow: StateFlow<List<ExerciseUiModel>> = _uiListFlow.asStateFlow()

    init {
        // 앱 시작 시 초기 데이터 로드
        refreshData()
    }

    // 내부 데이터를 다시 계산해서 Flow에 쏘는 함수
    // ExerciseRepository.kt 내부

    private fun refreshData() {
        val newList = allExercises.map { exercise ->
            val status = userStatus.find { it.exerciseId == exercise.id }
            val isUnlocked = status?.isUnlocked ?: false
            val isSelected = selectedExerciseIds.contains(exercise.id)

            // [핵심 로직 추가] 잠금 상태에 따라 보여줄 텍스트와 이미지 결정
            val displayTitle = if (isUnlocked) exercise.title else "잠금된 운동"
            val displaySubTitle = if (isUnlocked) exercise.subTitle else "눈 운동 지속 완료 시 오픈 예정"
            val displayTimeStr = if (isUnlocked && exercise.time > 0) {
                val min = exercise.time / 60
                val sec = exercise.time % 60
                "%d분 %02d초".format(min, sec)
            } else {
                "" // 잠겨있으면 시간 표시 안 함
            }

            // 잠겨있으면 자물쇠 아이콘(R.drawable.ic_lock), 풀려있으면 원본 이미지
            val displayImgRes = if (isUnlocked) {
                getDrawableIdByName(exercise.imageUrl)
            } else {
                R.drawable.ic_lock // 자물쇠 이미지 리소스 ID (없으면 추가 필요)
            }

            ExerciseUiModel(
                id = exercise.id,
                title = displayTitle,       // 변환된 타이틀
                subTitle = displaySubTitle, // 변환된 서브타이틀
                timeString = displayTimeStr,// 변환된 시간
                imageResId = displayImgRes, // 변환된 이미지
                isUnlocked = isUnlocked,
                isSelected = isSelected
            )
        }
        _uiListFlow.value = newList
    }

    // [2] 운동 잠금 해제 요청 (성공 시 true, 포인트 부족 시 false 반환)
    fun unlockExercise(exerciseId: Long): Boolean {
        val cost = 100
        val currentPoints = _userPoints.value

        if (currentPoints >= cost) {
            // 1. 포인트 차감
            _userPoints.update { it - cost }

            // 2. 잠금 상태 해제 (userStatus 리스트 업데이트)
            // 실제로는 DB 업데이트가 필요하지만, 여기선 메모리 리스트 수정
            val existingStatus = userStatus.find { it.exerciseId == exerciseId }
            if (existingStatus != null) {
                // 불변 리스트라 교체 작업 (Mock Logic)
                userStatus = userStatus.map {
                    if (it.exerciseId == exerciseId) it.copy(isUnlocked = true) else it
                }
            } else {
                // 없으면 새로 생성
                userStatus = userStatus + UserExerciseEntity(
                    id = userStatus.size.toLong() + 1,
                    userId = 100,
                    exerciseId = exerciseId,
                    isUnlocked = true
                )
            }

            // 3. 데이터 갱신 알림
            refreshData()
            return true
        } else {
            return false // 포인트 부족
        }
    }

    // 테스트용: 포인트 충전 함수 (필요 시 사용)
    fun addPoints(amount: Int) {
        _userPoints.update { it + amount }
    }

    // [기능] 선택 상태 토글 (뷰모델이 호출함)
    fun toggleExerciseSelection(id: Long) {
        if (selectedExerciseIds.contains(id)) {
            selectedExerciseIds.remove(id)
        } else {
            selectedExerciseIds.add(id)
        }
        // 데이터가 변경되었으니 방송 갱신
        refreshData()
    }

    // 상세 정보 가져오기 (기존 코드 유지)
    fun getExerciseById(id: Long): ExerciseUiModel? {
        // 현재 Flow에 있는 최신 상태에서 가져옴
        return _uiListFlow.value.find { it.id == id }
    }

    // (이미지 매핑 함수 기존 유지)
    private fun getDrawableIdByName(name: String): Int {
        return when(name) {
            "img_infinity" -> R.drawable.img_infinity
            "img_blink" -> R.drawable.img_blink
            "img_stretch" -> R.drawable.img_massage // 수정됨
            "img_massage" -> R.drawable.img_massage
            "img_x_shape" -> R.drawable.img_x_shape
            else -> R.drawable.ic_lock
        }
    }
}