package com.example.eyefit.data.model

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val createdAt: Long = System.currentTimeMillis(),

    // 운동 데이터 (기본값 설정 필수)
    val points: Int = 90, // 가입 시 기본 90포인트
    val unlockedExercises: List<Long> = listOf(1, 2, 3, 4, 5), // 1~5번은 기본 해제

    val playlist: List<Long> = listOf(1)
)