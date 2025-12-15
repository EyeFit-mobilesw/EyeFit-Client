package com.example.eyefit.ui.habit

import android.R.attr.fontWeight
import android.R.attr.lineHeight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyefit.R
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

fun getCurrentYearMonthWeek(): String {
    val today = LocalDate.now()

    val year = today.year
    val month = today.monthValue
    val weekFields = WeekFields.of(Locale.KOREA)
    val weekOfMonth = today.get(weekFields.weekOfMonth())

    val weekName = when (weekOfMonth) {
        1 -> "첫째주"
        2 -> "둘째주"
        3 -> "셋째주"
        4 -> "넷째주"
        else -> "다섯째주"
    }

    return "${year}년 ${month}월 ${weekName}"
}

@Composable
fun HabitAnalysisScreen(
    onBack: () -> Unit,
    weeklyHabitCounts: List<Int>,
    todayUncheckedHabits: List<String>
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(36.dp))

        /** --------------------- 상단 바 --------------------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "back",
                tint = Color.Black,
                modifier = Modifier
                    .size(22.dp)
                    .clickable { onBack() }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "하루 습관 분석",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        /** --------------------- 날짜 --------------------- */
        Text(
            text = getCurrentYearMonthWeek(),
            fontSize = 15.sp,
            color = Color(0xFFB1B1B1),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        /** --------------------- 제목 --------------------- */
        Text(
            text = "조금 더 힘내서\n오아시스에 가볼까요?",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        /** --------------------- 달성 배지 --------------------- */
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(2.dp, Color.Black, RoundedCornerShape(50))
                .background(Color(0xFFFFF383))
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = "달성한 습관 수",
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        /** --------------------- 그래프 --------------------- */
        HabitGraph(weeklyCounts = weeklyHabitCounts)

        Spacer(modifier = Modifier.height(26.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E5E5))
        )

        Spacer(modifier = Modifier.height(24.dp))

        /** --------------------- 내일 추천 --------------------- */
        Text(
            text = "내일은 달성해보면 어떨까요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8D8D8D)
        )

        Spacer(modifier = Modifier.height(22.dp))

        todayUncheckedHabits.forEachIndexed { idx, text ->
            HabitSuggestionRow(
                number = idx + 1,
                text = text
            )
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

/** --------------------- 그래프 컴포넌트 --------------------- */
@Composable
fun HabitGraph(weeklyCounts: List<Int>) {

    val days = listOf("월", "화", "수", "목", "금", "토", "일")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {

        weeklyCounts.forEachIndexed { index, count ->

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {

                val totalHeight = 220.dp
                val filledHeight = (count / 6f) * 150f

                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(totalHeight)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF1D1068)),
                    contentAlignment = Alignment.BottomCenter
                ) {

                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(filledHeight.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (count == 0) Color(0xFF0D1440)
                                else Color(0xFF3F97FF)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = days[index],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun HabitSuggestionRow(
    number: Int,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFF2CCEF3)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
