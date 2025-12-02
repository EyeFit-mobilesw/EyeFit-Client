package com.example.eyefit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController

@Composable
fun SignupCompleteScreen(navController: NavController) {
    val backgroundColor = Color(0xFF222222)
    val mainColor = Color(0xFF5CC1F0) // 하늘색

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- 1. 배경 장식 요소 (구름, 해) ---
        // 구름 (왼쪽 위)
        Image(
            painter = painterResource(id = R.drawable.img_cloud), // 구름 이미지 리소스
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 120.dp, start = 40.dp)
                .size(60.dp)
        )

        // 해 (오른쪽 위)
        Image(
            painter = painterResource(id = R.drawable.img_sun), // 해 이미지 리소스
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 100.dp, end = 40.dp)
                .size(50.dp)
        )

        // --- 2. 중앙 콘텐츠 (폭죽, 텍스트, 버튼) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 폭죽 아이콘
            Image(
                painter = painterResource(id = R.drawable.img_party), // 폭죽 이미지 필요
                contentDescription = "Congratulations",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 타이틀
            Text(
                text = "가입 완료 !",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 서브 텍스트
            Text(
                text = "EyeFit 회원가입을 진심으로 환영합니다",
                color = Color.LightGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(60.dp))

            // 버튼 + 토마토 캐릭터 영역
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.BottomStart
            ) {
                // 시작하기 버튼
                Button(
                    onClick = {
                        // 홈으로 이동 + 가입 관련 화면들 스택에서 제거
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = mainColor
                    )
                ) {
                    Text(
                        text = "EyeFit 시작하기",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 토마토 캐릭터 (버튼보다 위에 보여야 하므로 zIndex 높임 or 코드 순서 주의)
                Image(
                    painter = painterResource(id = R.drawable.right_tomato),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .padding(start = 10.dp) // 버튼 왼쪽 끝에서의 위치
                        .offset(y = -45.dp) // 버튼 위로 살짝 올라오게 조정
                        .zIndex(1f) // 버튼보다 앞으로 나오게 함
                )
            }
        }

        // --- 3. 하단 선인장 장식 ---
        Image(
            painter = painterResource(id = R.drawable.img_cactus_decor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(250.dp)
                .offset(x = 40.dp, y = 40.dp) // 오른쪽 구석으로 밀어넣기
        )
    }
}