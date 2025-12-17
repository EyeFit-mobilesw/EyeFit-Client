package com.example.eyefit.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseDetailViewModel : ViewModel() {
    private val _exerciseState = MutableStateFlow<ExerciseUiModel?>(null)
    val exerciseState: StateFlow<ExerciseUiModel?> = _exerciseState

    fun loadExercise(id: Long) {
        viewModelScope.launch {
            val data = ExerciseRepository.getExerciseById(id)
            _exerciseState.value = data
        }
    }
}