package com.example.eyefit.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseDetailViewModel : ViewModel() {
    // UI 상태 (초기값 null -> 로딩 중 처리가능)
    private val _exerciseState = MutableStateFlow<ExerciseUiModel?>(null)
    val exerciseState: StateFlow<ExerciseUiModel?> = _exerciseState

    // ID를 받아서 데이터 로드
    fun loadExercise(id: Long) {
        viewModelScope.launch {
            val data = ExerciseRepository.getExerciseById(id)
            _exerciseState.value = data
        }
    }
}