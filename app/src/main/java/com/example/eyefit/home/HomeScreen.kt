package com.example.eyefit.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eyefit.R
import com.example.eyefit.components.CharacterWithBackground
import com.example.eyefit.components.DailyProgressBar
import com.example.eyefit.components.EyefitButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

@Composable
fun HomeScreen(
    onStartExercise: () -> Unit,
    onHabitDetailClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {

    val day by viewModel.currentDay.collectAsState()

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
        else -> "눈처럼\n맑고 깨끗한\n눈 건강 만들기 성공!"
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // --- 배경 ---
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // -------------------------------
        // 🔥 진행바 + 캐릭터 + 요일라벨
        // -------------------------------
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 490.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val progressWidth = progressForDay(day)

            // 진행바 전체 묶음
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),     // 진행바 + 캐릭터 공간
                contentAlignment = Alignment.Center
            ) {

                // (1) 배경 + 검은 진행바
                DailyProgressBar(
                    day = day,
                    progressWidth = progressWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(14.dp)
                        .align(Alignment.Center)
                )

                // (2) 요일 라벨
                DayProgressLabels(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 0.dp)
                )

                // (3) 캐릭터 이동 — Day4 이후에도 정상 동작
                CharacterWithBackground(
                    day = day,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(
                            x = (progressWidth * 300).dp,
                            y = (-45).dp
                        )
                )
            }
        }

        // ------------------------------
        // 🔽 아래 스크롤 영역
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
                onClick = onStartExercise,
                fontSize = 20,
                fontWeight = FontWeight.SemiBold,
                eyeSize = 33
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "지속적인 운동을 위해 알람 맞추기  >",
                color = Color(0xFF1A1A1A),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
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
                            text = "6개 달성 완료!",
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


@Composable
fun DayProgressLabels(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Day 3")
        Text("Day 4")
        Text("Day 5")
        Text("Day 6")
        Text("Day 7")
        Text("도착")
    }
}
