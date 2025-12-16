package com.example.eyefit.data.model

// [Entity 1] 운동 마스터 데이터 (DB: Exercise 테이블)
// [중요] Firestore용 빈 생성자를 위해 모든 필드에 초기값을 줍니다.
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

// [UI Model] (변경 없음)
data class ExerciseUiModel(
    val id: Long,
    val title: String,
    val subTitle: String,
    val timeString: String,
    val imageResId: Int,
    val isUnlocked: Boolean,
    val isSelected: Boolean = false,
    val descriptionTitle: String = "운동 방법",
    val descriptionContent: String = ""
)