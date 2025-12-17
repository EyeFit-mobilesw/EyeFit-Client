package com.example.eyefit.exercise

import androidx.lifecycle.ViewModel
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ExerciseViewModel : ViewModel() {

    val uiList: StateFlow<List<ExerciseUiModel>> = ExerciseRepository.uiListFlow
    val userPoints: StateFlow<Int> = ExerciseRepository.userPoints

    // 팝업 상태 관리
    // 어떤 운동을 잠금 해제하려고 하는지 저장
    private val _selectedExerciseToUnlock = MutableStateFlow<ExerciseUiModel?>(null)
    val selectedExerciseToUnlock: StateFlow<ExerciseUiModel?> = _selectedExerciseToUnlock.asStateFlow()

    // 아이템 클릭 이벤트 처리
    fun onExerciseItemClick(exercise: ExerciseUiModel) {
        if (exercise.isUnlocked) {
            ExerciseRepository.toggleExerciseSelection(exercise.id)
        } else {
            _selectedExerciseToUnlock.value = exercise
        }
    }

    // 잠금 해제 실행 (팝업에서 버튼 클릭 시)
    fun unlockExercise() {
        val exercise = _selectedExerciseToUnlock.value ?: return
        val isSuccess = ExerciseRepository.unlockExercise(exercise.id)

        if (isSuccess) {
            dismissDialog()
        } else {
        }
    }

    // 팝업 닫기
    fun dismissDialog() {
        _selectedExerciseToUnlock.value = null
    }
    fun toggleSelection(exerciseId: Long) {
        ExerciseRepository.toggleExerciseSelection(exerciseId)
    }

    fun savePlaylist() {
    }

    // 플레이어 화면에서 사용할 운동 리스트
    val playlist: List<ExerciseUiModel>
        get() = ExerciseRepository.uiListFlow.value.filter { it.isSelected }

    // 포인트 적립 함수
    fun addPoints(amount: Int) {
        ExerciseRepository.addPoints(amount)
    }
}