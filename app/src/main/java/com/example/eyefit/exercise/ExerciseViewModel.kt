package com.example.eyefit.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel : ViewModel() {

    // [수정] 뷰모델 내부 변수가 아니라, Repository의 Flow를 그대로 가져다 씁니다.
    // 이렇게 하면 어떤 화면의 뷰모델이든 똑같은 데이터를 보게 됩니다.
    val uiList: StateFlow<List<ExerciseUiModel>> = ExerciseRepository.uiListFlow

    // [수정] 토글 요청이 오면 Repository에게 시킵니다.
    fun toggleSelection(exerciseId: Long) {
        ExerciseRepository.toggleExerciseSelection(exerciseId)
        // Repository가 알아서 uiListFlow를 업데이트하므로 여기서 uiList를 수동으로 바꿀 필요가 없습니다.
    }

    fun savePlaylist() {
        // 실제 DB 저장 로직이 필요하다면 여기서 호출
        // 지금은 Repository 메모리에 이미 저장되어 있으므로 아무것도 안 해도 됨
    }
}