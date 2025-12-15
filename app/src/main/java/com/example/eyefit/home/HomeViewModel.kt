package com.example.eyefit.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val _currentDay = MutableStateFlow(7)
    val currentDay: StateFlow<Int> = _currentDay

    fun moveToNextDay() {
        if (_currentDay.value < 7) {
            _currentDay.value += 1
        }
    }
}
