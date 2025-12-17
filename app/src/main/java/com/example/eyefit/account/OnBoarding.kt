package com.example.eyefit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

val pixelFont = FontFamily(Font(R.font.dnfbitbitv2))
@Composable
fun OnBoardingScreen(onTimeout: () -> Unit) {
    // 배경색 설정 (다크 모드 스타일)
    val backgroundColor = Color(0xFF222222)

    // 텍스트 그라데이션 브러시 (하늘색 -> 흰색)
    val textGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF7F8F9), //
            Color(0xFFC3F4FF)  //
        )
    )

    // 3초 타이머 로직
    LaunchedEffect(Unit) {
        delay(3000) // 3초 대기
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // 중앙 콘텐츠
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "EyeFit",
                fontSize = 50.sp,
                fontFamily = pixelFont,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    brush = textGradient // 그라데이션 적용
                )
            )

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(300.dp) // 캐릭터 주변 공간 확보
            ) {
                // (1) 하늘색 구름 (왼쪽 위)
                Image(
                    painter = painterResource(id = R.drawable.img_cloud), // 구름 이미지 리소스 필요
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopStart) // 박스 기준 왼쪽 상단
                        .offset(x = 10.dp, y = 40.dp) // 위치 미세 조정
                )

                // (2) 노란색 원/해 (오른쪽 위)
                Image(
                    painter = painterResource(id = R.drawable.img_sun), // 해 이미지 리소스 필요
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .align(Alignment.TopEnd) // 박스 기준 오른쪽 상단
                        .offset(x = (-20).dp, y = 20.dp) // 위치 미세 조정
                )

                // (3) 토마토 캐릭터 (중앙)
                Image(
                    painter = painterResource(id = R.drawable.running_tomato),
                    contentDescription = "Character",
                    modifier = Modifier.size(200.dp)
                )
            }
        }

         Image(
             painter = painterResource(id = R.drawable.img_cactus_decor),
             contentDescription = null,
             modifier = Modifier
                 .align(Alignment.BottomStart) // 왼쪽 하단 정렬
                 .width(300.dp) // 너비를 키워서 전체적으로 크게 만듦
                 .height(300.dp)
                 .offset(x = (-80).dp, y = 50.dp) // 왼쪽/아래로 살짝 밀어서 잘리는 느낌 연출
         )
    }
}

@Preview
@Composable
fun OnBoardingPreview() {
    OnBoardingScreen(onTimeout = {})
}