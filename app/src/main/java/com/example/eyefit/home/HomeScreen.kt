package com.example.eyefit.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eyefit.R
import com.example.eyefit.components.CharacterWithBackground
import com.example.eyefit.components.EyefitButton
import com.example.eyefit.data.firebase.FirebaseProvider
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    onStartExercise: () -> Unit,
    onHabitDetailClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val day by viewModel.currentDay.collectAsState()

    // 오늘 습관 달성 개수 (Firestore에서 items true 개수) 실시간 반영
    val db = remember { FirebaseProvider.db }
    val uid = FirebaseProvider.auth.currentUser?.uid
    val todayKey = remember { LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

    var todayAchievedCount by remember { mutableStateOf(0) }

    DisposableEffect(uid, todayKey) {
        if (uid == null) {
            todayAchievedCount = 0
            onDispose { }
        } else {
            val reg = db.collection("users")
                .document(uid)
                .collection("habitChecks")
                .document(todayKey)
                .addSnapshotListener { snap, _ ->
                    val items = snap?.get("items") as? Map<*, *>
                    val count = items?.values?.count { it == true } ?: 0
                    todayAchievedCount = count
                }

            onDispose { reg.remove() }
        }
    }

    // Day 배경
    val backgroundRes = when (day) {
        1 -> R.drawable.bg_day1
        2 -> R.drawable.bg_day2
        3 -> R.drawable.bg_day3
        4 -> R.drawable.bg_day4
        5 -> R.drawable.bg_day5
        6 -> R.drawable.bg_day6
        else -> R.drawable.bg_day7
    }

    // 타이틀 텍스트
    val titleText = when (day) {
        1 -> "사막 같은\n눈을 위해\n오아시스로 출발!"
        2 -> "촉촉하고\n맑은 눈을 위한 한 걸음!"
        3 -> "차가운 바람에\n시린 눈을\n보호하는 중!"
        4 -> "푸르른\n오아시스에\n가까워지는 중!"
        5 -> "서늘한\n공기를 피한\n빠른 지름길!"
        6 -> "포근한 햇살\n덕분에\n촉촉한 눈"
        else -> "눈처럼 맑고 깨끗한\n눈 건강 만들기 성공!\n오아시스에 도착!"
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- 배경 ---
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // -------------------------------
        // 진행바 + 캐릭터 + 요일라벨
        // -------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 490.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            JourneyProgress(day = day)
        }

        // ------------------------------
        // 아래 스크롤 영역
        // ------------------------------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 80.dp),
            horizontalAlignment = Alignment.Start
        ) {

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = titleText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A),
                lineHeight = 44.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            EyefitButton(
                text = "눈 운동 시작하기",
                onClick = { onStartExercise() },
                fontSize = 20,
                fontWeight = FontWeight.SemiBold,
                eyeSize = 33
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "지속적인 운동을 위해 운동하러 가볼까요?",
                color = Color(0xFF1A1A1A),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(270.dp))

            Text(
                text = "오늘의 눈 습관",
                color = Color(0xFF1A1A1A),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "오아시스를 향한 눈의 여정에 도움이 될 거예요.",
                color = Color(0xFF8D8D8D),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clickable { onHabitDetailClick() },
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "습관 달성을 위해 힘내세요!",
                            color = Color(0xFF1A1A1A),
                            fontSize = 23.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "${todayAchievedCount}개 달성 완료!",
                            color = Color(0xFF8D8D8D),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.arrow),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * - Day1~3: 오른쪽에 붙고, 라벨 Day1~Day6만 보임
 * - Day4~7: 왼쪽에 붙고, 라벨 Day3~Day7~도착 보임
 * - 진행 바: 현재 Day "원 중심"까지
 * - Day7일 때만 도착까지 끝
 */
@Composable
private fun JourneyProgress(
    day: Int,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(90.dp)
) {
    val safeDay = day.coerceIn(1, 7)
    val isEarly = safeDay <= 3


    val labels: List<String> = if (isEarly) {
        (1..6).map { "Day $it" }                 // Day1~Day6
    } else {
        (3..7).map { "Day $it" } + "도착"        // Day3~Day7~도착
    }

    // 현재 Day가 labels에서 몇 번째인지
    val currentIdx = if (isEarly) {
        (safeDay - 1).coerceIn(0, 5)
    } else {
        (safeDay - 3).coerceIn(0, 4) // Day7 -> idx 4 (도착은 idx 5)
    }

    val trackHeight = 15.dp
    val circleSize = 44.dp
    val r = circleSize / 2

    BoxWithConstraints(modifier = modifier) {
        val trackWidth = (maxWidth * 0.92f).coerceAtLeast(260.dp)
        val trackAlign = if (isEarly) Alignment.CenterEnd else Alignment.CenterStart

        Box(
            modifier = Modifier
                .width(trackWidth)
                .fillMaxHeight()
                .align(trackAlign)
        ) {
            val stops = labels.size // 항상 6
            val spacing = (trackWidth - circleSize) / (stops - 1)

            fun centerX(i: Int): Dp = r + spacing * i

            // 진행 바 끝: 원 중심까지
            // Day7이면 도착까지 끝
            val progressWidth: Dp = if (!isEarly && safeDay == 7) {
                trackWidth
            } else {
                centerX(currentIdx)
            }

            // (1) 흰 트랙 + 검은 진행바
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.95f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(progressWidth)
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black)
                )
            }

            // (2) 캐릭터 위치: 현재 원 중심에 맞춤
            val characterCenterX = centerX(currentIdx)
            CharacterWithBackground(
                day = safeDay,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(
                        x = characterCenterX - 50.dp,   // 캐릭터 중심 맞춤용
                        y = (-45).dp
                    )
            )

            // 바와 day 사이의 간격 조정
            val labelTop = 60.dp

            for (i in 0 until stops) {
                val label = labels[i]
                val xLeft = centerX(i) - r

                Box(
                    modifier = Modifier
                        .offset(x = xLeft, y = labelTop)
                        .size(circleSize),
                    contentAlignment = Alignment.Center
                ) {
                    val isCurrentDayCircle =
                        label.startsWith("Day ") && label == "Day $safeDay"

                    if (isCurrentDayCircle) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Text(
                            text = label,
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
