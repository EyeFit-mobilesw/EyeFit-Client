package com.example.eyefit.data.model

// [Entity 1] 운동 마스터 데이터 (DB: Exercise 테이블)
data class ExerciseEntity(
    val id: Long,
    val title: String,              // 운동 이름
    val subTitle: String,           // 부제목
    val description: String,        // 운동 설명
    val detailedDescription: String,// 상세 설명
    val time: Int,                  // 운동 시간 (초 단위)
    val imageUrl: String,           // 썸네일 이미지 (URL or Resource Name)
    val animationUrl: String        // 애니메이션 경로
)

// [Entity 2] 사용자별 잠금 현황 (DB: UserExercise 테이블)
data class UserExerciseEntity(
    val id: Long,
    val userId: Long,
    val exerciseId: Long,
    val isUnlocked: Boolean         // 잠금 해제 여부
)

// [UI Model] 화면에 보여주기 위해 가공된 데이터
data class ExerciseUiModel(
    val id: Long,
    val title: String,
    val subTitle: String,
    val timeString: String,         // "7분 00초" 포맷
    val imageResId: Int,            // 로컬 리소스 ID (실제 앱에선 Glide/Coil 사용 시 String)
    val isUnlocked: Boolean,        // 잠금 여부
    val isSelected: Boolean = false, // 선택 여부 (UI 상태)
    val descriptionTitle: String = "운동 방법", // 기본값 설정
    val descriptionContent: String = ""        // 실제 설명 내용
)