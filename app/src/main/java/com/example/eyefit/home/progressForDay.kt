// progressForDay.kt
package com.example.eyefit.home

fun progressForDay(day: Int): Float {
    val d = day.coerceIn(1, 7)  // 1~7
    return (d - 1) / 6f         // day1=0.0, day7=1.0
}
