package com.example.eyefit.data.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateKeyUtil {
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    fun todayKey(): String = fmt.format(Date())

    // 이번 주(월~일) dateKey 7개
    fun weekKeysMonToSun(base: Date = Date()): List<String> {
        val cal = Calendar.getInstance()
        cal.time = base
        cal.firstDayOfWeek = Calendar.MONDAY

        // 월요일로 이동
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val diffToMon = when (dayOfWeek) {
            Calendar.SUNDAY -> -6
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> -1
            Calendar.WEDNESDAY -> -2
            Calendar.THURSDAY -> -3
            Calendar.FRIDAY -> -4
            Calendar.SATURDAY -> -5
            else -> 0
        }
        cal.add(Calendar.DAY_OF_MONTH, diffToMon)

        // 월~일 7개 생성
        return (0..6).map {
            val c = cal.clone() as Calendar
            c.add(Calendar.DAY_OF_MONTH, it)
            fmt.format(c.time)
        }
    }
}
