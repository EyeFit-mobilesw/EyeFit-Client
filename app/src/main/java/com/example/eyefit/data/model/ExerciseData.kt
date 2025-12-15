package com.example.eyefit.data.model

import com.example.eyefit.R

data class ExerciseData(
    val id: Int,                // 식별자 (네비게이션용)
    val title: String,
    val subTitle: String,
    val time: String,
    val imageResId: Int,
    val descriptionTitle: String,   // 상세페이지 설명 제목 (ex: 원근조절을 통한...)
    val descriptionContent: String  // 상세페이지 설명 내용
)

val exerciseList = listOf(
    ExerciseData(
        id = 0,
        title = "8자 그리기 운동",
        subTitle = "원근 조절을 통한 노안 예방",
        time = "3분 30초",
        imageResId = R.drawable.img_infinity,
        descriptionTitle = "원근조절을 통한 노안 예방",
        descriptionContent = "8자 그리기 운동은 눈의 혈액순환을 도와 눈 근육을 좋게 만듭니다. 이는 시력에 좋은 영향을 줄 수 있어요.\n" +
                "또한, 혈액 효율과 시각세포를 활성화해서 노안 예방에도 도움을 줄 수 있어요, 꾸준히 할수록 효과가 커지는 운동입니다."
    ),
    ExerciseData(
        id = 1,
        title = "눈 깜빡이기 운동",
        subTitle = "안구 건조 완화",
        time = "7분 00초",
        imageResId = R.drawable.img_blink,
        descriptionTitle = "안구 건조증 완화 효과",
        descriptionContent = "눈을 자주 깜빡이면 눈물샘을 자극하여 눈을 촉촉하게 유지할 수 있습니다.\n" +
                "스마트폰이나 모니터를 오래 볼 때 의식적으로 눈을 깜빡여주세요."
    )
)