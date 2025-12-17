package com.example.eyefit.data.model

// 운동 마스터 데이터 (DB: Exercise 테이블)
data class ExerciseEntity(
    val id: Long = 0L,
    val title: String = "",
    val subTitle: String = "",
    val description: String = "",
    val detailedDescription: String = "",
    val time: Int = 0,
    val imageUrl: String = "",
    val animationUrl: String = ""
)

// UI Model
data class ExerciseUiModel(
    val id: Long,
    val title: String,
    val subTitle: String,
    val timeString: String,
    val imageUrl: String,
    val isUnlocked: Boolean,
    val isSelected: Boolean = false,
    val descriptionTitle: String = "운동 방법",
    val descriptionContent: String = "",
    val animationUrl: String = ""
)