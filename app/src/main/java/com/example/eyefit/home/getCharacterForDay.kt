package com.example.eyefit.home

import com.example.eyefit.R

fun getCharacterForDay(day: Int): Int {
    return when (day) {
        1, 4 -> R.drawable.character_day1
        2, 5 -> R.drawable.character_day2
        3, 6 -> R.drawable.character_day3
        7 -> R.drawable.character_day7
        else -> R.drawable.character_day1
    }
}
