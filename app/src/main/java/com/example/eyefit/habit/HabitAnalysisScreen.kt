package com.example.eyefit.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.example.eyefit.data.firebase.FirebaseProvider
import kotlinx.coroutines.tasks.await
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
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

/**
 * - Firestore에서 이번 주(월~일) achievedCount를 읽어서 그래프에 반영
 * - 오늘 체크 안 된 습관 3개를 추천에 반영
 */
@Composable
fun HabitAnalysisScreen(
    onBack: () -> Unit
) {
    val auth = remember { FirebaseProvider.auth }
    val db = remember { FirebaseProvider.db }
    val uid = auth.currentUser?.uid

    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }

    // ✅ 월~일 날짜키 7개 생성 (무조건 '월요일' 기준으로!)
    val weekKeys = remember {
        val today = LocalDate.now()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        (0..6).map { monday.plusDays(it.toLong()).format(formatter) } // 월~일
    }

    val todayKey = remember { LocalDate.now().format(formatter) }

    // 습관 key + 라벨 (DailyHabitCheckScreen 저장 key와 동일해야 함!)
    val habitKeys = remember {
        listOf(
            "SMARTPHONE_LIMIT" to "스마트폰을 장시간 이용하지 않았다",
            "SLEEP_ENOUGH" to "충분한 수면을 취했다",
            "NO_SMOKING" to "흡연을 하지 않았다",
            "USE_ARTIFICIAL_TEARS" to "눈이 건조할 때 인공눈물을 넣었다",
            "EYE_STEAM" to "눈 찜질을 하루 1회 이상 했다",
            "CONTACT_LENS_LIMIT" to "콘택트 렌즈를 장시간 사용하지 않기"
        )
    }

    var weeklyCounts by remember { mutableStateOf(List(7) { 0 }) }
    var todayUnchecked by remember { mutableStateOf(listOf<String>()) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(uid) {
        if (uid == null) {
            weeklyCounts = List(7) { 0 }
            todayUnchecked = habitKeys.map { it.second }.take(3)
            errorMsg = "로그인이 필요해요."
            return@LaunchedEffect
        }

        loading = true
        errorMsg = null

        try {
            val startKey = weekKeys.first()
            val endKey = weekKeys.last()

            // 이번 주 문서들 조회 (dateKey 필드 기준)
            val snap = db.collection("users")
                .document(uid)
                .collection("habitChecks")
                .whereGreaterThanOrEqualTo("dateKey", startKey)
                .whereLessThanOrEqualTo("dateKey", endKey)
                .get()
                .await()

            val achievedMap = mutableMapOf<String, Int>()
            var todayItems: Map<*, *>? = null

            for (doc in snap.documents) {
                val dk = doc.getString("dateKey") ?: doc.id

                val count = doc.getLong("achievedCount")?.toInt()
                    ?: run {
                        val items = doc.get("items") as? Map<*, *>
                        items?.values?.count { it == true } ?: 0
                    }

                achievedMap[dk] = count

                if (dk == todayKey) {
                    todayItems = doc.get("items") as? Map<*, *>
                }
            }

            // 그래프 값 월~일 순서로 구성
            weeklyCounts = weekKeys.map { achievedMap[it] ?: 0 }

            // 오늘 문서가 쿼리 결과에 없을 수도 있으니(저장 안 했으면) 한 번 더 조회
            if (todayItems == null) {
                val todayDoc = db.collection("users")
                    .document(uid)
                    .collection("habitChecks")
                    .document(todayKey)
                    .get()
                    .await()

                if (todayDoc.exists()) {
                    todayItems = todayDoc.get("items") as? Map<*, *>
                }
            }

            // 내일 추천: 오늘 false인 항목들
            val unchecked = mutableListOf<String>()
            for ((key, label) in habitKeys) {
                val checked = (todayItems?.get(key) as? Boolean) ?: false
                if (!checked) unchecked.add(label)
            }

            todayUnchecked = if (todayItems == null) {
                habitKeys.map { it.second }
            } else {
                unchecked
            }

        } catch (e: Exception) {
            errorMsg = e.message ?: "습관 분석 데이터를 불러오지 못했어요."
            weeklyCounts = List(7) { 0 }
            todayUnchecked = habitKeys.map { it.second }.take(3)
        } finally {
            loading = false
        }
    }

    HabitAnalysisContent(
        onBack = onBack,
        weeklyHabitCounts = weeklyCounts,
        todayUncheckedHabits = todayUnchecked,
        loading = loading,
        errorMsg = errorMsg
    )
}

@Composable
private fun HabitAnalysisContent(
    onBack: () -> Unit,
    weeklyHabitCounts: List<Int>,
    todayUncheckedHabits: List<String>,
    loading: Boolean,
    errorMsg: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(36.dp))

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

        Text(
            text = getCurrentYearMonthWeek(),
            fontSize = 15.sp,
            color = Color(0xFFB1B1B1),
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "조금 더 힘내서\n오아시스에 가볼까요?",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .border(2.dp, Color.Black, RoundedCornerShape(50))
                .background(Color(0xFFFFF383))
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                text = if (loading) "불러오는 중..." else "달성한 습관 수",
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                fontSize = 16.sp
            )
        }

        if (errorMsg != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMsg, color = Color.Red, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        HabitGraph(weeklyCounts = weeklyHabitCounts)

        Spacer(modifier = Modifier.height(26.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E5E5))
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "내일은 달성해보면 어떨까요?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF8D8D8D)
        )

        Spacer(modifier = Modifier.height(22.dp))

        todayUncheckedHabits.forEachIndexed { idx, text ->
            HabitSuggestionRow(number = idx + 1, text = text)
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

@Composable
fun HabitGraph(weeklyCounts: List<Int>) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val maxCount = 6f

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        weeklyCounts.forEachIndexed { index, rawCount ->
            val count = rawCount.coerceIn(0, 6)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                val totalHeight = 220.dp
                val filledHeight = totalHeight * (count / maxCount)

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
                            .height(filledHeight)
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
