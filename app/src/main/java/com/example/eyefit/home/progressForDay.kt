package com.example.eyefit.home

fun progressForDay(day: Int): Float {
    return when (day) {
        1 -> 2f / 7f  // Day1 = Day2 위치까지
        2 -> 2f / 7f
        3 -> 3f / 7f
        4 -> 4f / 7f
        5 -> 5f / 7f
        6 -> 6f / 7f
        7 -> 1f
        else -> 0f
    }
}