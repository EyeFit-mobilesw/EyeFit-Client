package com.example.eyefit

import android.R.attr.bottom
import android.R.attr.fontFamily
import android.R.attr.fontWeight
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.example.eyefit.pixelFont

@Composable
fun LoginScreen(navController: NavController) {
    // 색상 정의
    val backgroundColor = Color(0xFF222222)
    val mainColor = Color(0xFF2CCEF3) // 버튼 및 테두리 하늘색

    // 텍스트 그라데이션 브러시 (하늘색 -> 흰색)
    val textGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF7F8F9), //
            Color(0xFFC3F4FF)  //
        )
    )

    // 입력값 상태 관리
    var idText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp) // 좌우 여백
                .align(Alignment.Center), // 화면 중앙 정렬
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 로고
            Text(
                text = "EyeFit",
                fontSize = 50.sp,
                fontFamily = pixelFont, // OnBoarding에 있는 폰트 변수 사용 (또는 여기서 재선언)
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    brush = textGradient // 그라데이션 적용
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 2. 아이디 입력 섹션 (토마토 + 라벨 + 입력창)
            Box(modifier = Modifier.fillMaxWidth()) {
                // 라벨 (왼쪽 아래)
                Text(
                    text = "아이디",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 8.dp)
                )

                // 토마토 캐릭터 (오른쪽 아래) - 입력창 바로 위에 위치
                Image(
                    painter = painterResource(id = R.drawable.running_tomato), // 토마토 이미지
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                        .offset(y = 10.dp) // 살짝 아래로 내려서 입력창에 걸터앉은 느낌
                        .zIndex(1f) // 입력창보다 위에 그려지도록 설정
                )
            }

            // 아이디 입력창
            CustomTextField(
                value = idText,
                onValueChange = { idText = it },
                borderColor = mainColor
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 3. 비밀번호 입력 섹션
            Text(
                text = "비밀번호",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            // 비밀번호 입력창
            CustomTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                borderColor = mainColor,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 4. 로그인 버튼
            Button(
                onClick = {
                    // 홈 화면으로 이동하며 백스택 정리 (뒤로가기 시 로그인화면 안 나오게)
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainColor // 하늘색 배경
                )
            ) {
                Text(
                    text = "로그인",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. 회원가입 링크
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "아직 회원이 아니신가요?  ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "회원가입",
                    color = Color.White,
                    fontSize = 14.sp,
                    textDecoration = TextDecoration.Underline, // 밑줄
                    modifier = Modifier.clickable {
                        navController.navigate("signup") // 회원가입 화면으로 이동
                    }
                )
            }
        }

        // 6. 하단 선인장 장식 (우측 하단)
        Image(
            painter = painterResource(id = R.drawable.img_cactus_decor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd) // 오른쪽 아래 정렬
                .size(300.dp) // 크기 조절
                .offset(x = 50.dp, y = 50.dp) // 구석으로 밀어넣기
        )
    }
}

// 디자인 요구사항에 맞춘 커스텀 텍스트 필드
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    borderColor: Color,
    isPassword: Boolean = false
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp) // 높이 고정
                    .background(Color.White, RoundedCornerShape(12.dp)) // 흰색 배경 + 둥근 모서리
                    .border(3.dp, borderColor, RoundedCornerShape(12.dp)) // 하늘색 테두리
                    .padding(horizontal = 16.dp), // 내부 글자 여백
                contentAlignment = Alignment.CenterStart
            ) {
                innerTextField()
            }
        }
    )
}