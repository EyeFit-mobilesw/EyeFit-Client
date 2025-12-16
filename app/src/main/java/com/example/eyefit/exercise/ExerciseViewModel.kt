package com.example.eyefit.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    // [수정] 뷰모델 내부 변수가 아니라, Repository의 Flow를 그대로 가져다 씁니다.
    // 이렇게 하면 어떤 화면의 뷰모델이든 똑같은 데이터를 보게 됩니다.
    val uiList: StateFlow<List<ExerciseUiModel>> = ExerciseRepository.uiListFlow
    val userPoints: StateFlow<Int> = ExerciseRepository.userPoints // 포인트 구독

    // [팝업 상태 관리]
    // 어떤 운동을 잠금 해제하려고 하는지 저장 (null이면 팝업 안 뜸)
    private val _selectedExerciseToUnlock = MutableStateFlow<ExerciseUiModel?>(null)
    val selectedExerciseToUnlock: StateFlow<ExerciseUiModel?> = _selectedExerciseToUnlock.asStateFlow()

    // 아이템 클릭 이벤트 처리
    fun onExerciseItemClick(exercise: ExerciseUiModel) {
        if (exercise.isUnlocked) {
            // 이미 잠금 해제됨 -> 선택 토글
            ExerciseRepository.toggleExerciseSelection(exercise.id)
        } else {
            // 잠겨있음 -> 팝업 띄우기
            _selectedExerciseToUnlock.value = exercise
        }
    }

    // 잠금 해제 실행 (팝업에서 버튼 클릭 시)
    fun unlockExercise() {
        val exercise = _selectedExerciseToUnlock.value ?: return

        // Repository에 요청
        val isSuccess = ExerciseRepository.unlockExercise(exercise.id)

        if (isSuccess) {
            dismissDialog() // 성공하면 닫기
        } else {
            // 포인트 부족 시 뷰모델에서 처리할 로직이 있다면 추가 (지금은 UI에서 처리함)
        }
    }

    // 팝업 닫기
    fun dismissDialog() {
        _selectedExerciseToUnlock.value = null
    }

    // [수정] 토글 요청이 오면 Repository에게 시킵니다.
    fun toggleSelection(exerciseId: Long) {
        ExerciseRepository.toggleExerciseSelection(exerciseId)
        // Repository가 알아서 uiListFlow를 업데이트하므로 여기서 uiList를 수동으로 바꿀 필요가 없습니다.
    }

    fun savePlaylist() {
        // 실제 DB 저장 로직이 필요하다면 여기서 호출
        // 지금은 Repository 메모리에 이미 저장되어 있으므로 아무것도 안 해도 됨
    }

    // [추가] 플레이어 화면에서 사용할 운동 리스트
    val playlist: List<ExerciseUiModel>
        get() = ExerciseRepository.uiListFlow.value.filter { it.isSelected }

    // [추가] 포인트 적립 함수
    fun addPoints(amount: Int) {
        ExerciseRepository.addPoints(amount)
    }
}