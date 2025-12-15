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
        ExerciseEntity(6, "잠금된 운동", "눈 운동 지속 완료 시 오픈 예정", "", "", 0, "ic_lock", "")
    )

    // [2] 사용자 상태 (잠금 여부)
    private val userStatus = listOf(
        UserExerciseEntity(1, 100, 1, true),
        UserExerciseEntity(2, 100, 2, true),
        UserExerciseEntity(3, 100, 3, true),
        UserExerciseEntity(4, 100, 4, true),
        UserExerciseEntity(5, 100, 5, true),
        UserExerciseEntity(6, 100, 6, false)
    )

    // [3] 플레이리스트 (사용자가 선택한 운동 ID 저장소) - DB 역할
    private val selectedExerciseIds = mutableSetOf<Long>()

    // [4] 실시간 데이터 스트림 (방송국 역할)
    // 뷰모델들은 이제 이 _uiListFlow만 바라봅니다.
    private val _uiListFlow = MutableStateFlow<List<ExerciseUiModel>>(emptyList())
    val uiListFlow: StateFlow<List<ExerciseUiModel>> = _uiListFlow.asStateFlow()

    init {
        // 앱 시작 시 초기 데이터 로드
        refreshData()
    }

    // 내부 데이터를 다시 계산해서 Flow에 쏘는 함수
    private fun refreshData() {
        val newList = allExercises.map { exercise ->
            val status = userStatus.find { it.exerciseId == exercise.id }
            val isUnlocked = status?.isUnlocked ?: false

            // [중요] 저장된 ID set에 포함되어 있으면 selected = true
            val isSelected = selectedExerciseIds.contains(exercise.id)

            val min = exercise.time / 60
            val sec = exercise.time % 60
            val timeStr = if (exercise.time > 0) "%d분 %02d초".format(min, sec) else ""
            val imgRes = getDrawableIdByName(exercise.imageUrl)

            ExerciseUiModel(
                id = exercise.id,
                title = exercise.title,
                subTitle = exercise.subTitle,
                timeString = timeStr,
                imageResId = imgRes,
                isUnlocked = isUnlocked,
                isSelected = isSelected // 여기가 핵심!
            )
        }
        // 구독자들에게 새 데이터 전송
        _uiListFlow.value = newList
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