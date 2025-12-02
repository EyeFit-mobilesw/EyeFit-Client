package com.example.eyefit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SignupScreen(navController: NavController) {
    // 색상 정의
    val backgroundColor = Color(0xFF222222)
    val mainColor = Color(0xFF5CC1F0) // 하늘색

    // 입력값 상태 관리
    var emailText by remember { mutableStateOf("") }
    var idText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordCheckText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // 1. 메인 콘텐츠 (스크롤이 필요하다면 Column을 verticalScroll로 감싸세요)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 30.dp)
        ) {
            // --- 상단 바 (뒤로가기 + 타이틀) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }, // 뒤로가기
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Text(
                    text = "회원가입",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // --- 입력 폼 ---

            // 1. 이메일
            LabelText("이메일")
            SignupTextField(
                value = emailText,
                onValueChange = { emailText = it },
                borderColor = mainColor
            )
            Spacer(modifier = Modifier.height(25.dp))

            // 2. 아이디
            LabelText("아이디")
            SignupTextField(
                value = idText,
                onValueChange = { idText = it },
                borderColor = mainColor
            )
            Spacer(modifier = Modifier.height(25.dp))

            // 3. 비밀번호
            LabelText("비밀번호")
            SignupTextField(
                value = passwordText,
                onValueChange = { passwordText = it },
                borderColor = mainColor,
                isPassword = true
            )
            Spacer(modifier = Modifier.height(25.dp))

            // 4. 비밀번호 확인
            LabelText("비밀번호 확인")
            SignupTextField(
                value = passwordCheckText,
                onValueChange = { passwordCheckText = it },
                borderColor = mainColor,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(50.dp))

            // --- 회원가입 버튼 ---
            Button(
                onClick = {
                    // 가입 완료 화면으로 이동
                    navController.navigate("signup_complete")
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
                    text = "회원가입",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. 하단 선인장 장식 (화면 제일 아래, 뒤에 깔리도록 Box의 마지막에 배치하거나 zIndex 고려)
        // 입력창을 가리지 않게 하기 위해 이번에는 배경처럼 맨 밑에 깔고 싶다면 Column 위로 올리거나,
        // UI 구조상 겹쳐도 되면 여기에 둡니다. (사진상 버튼 뒤에 있음)
        Image(
            painter = painterResource(id = R.drawable.img_cactus_decor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(300.dp)
                .offset(x = 50.dp, y = 50.dp) // 위치 미세 조정
        )
    }
}

// 라벨 텍스트 컴포넌트
@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// 회원가입용 텍스트 필드 (Placeholder 기능 추가)
@Composable
fun SignupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    borderColor: Color,
    isPassword: Boolean = false,
    placeholder: String? = null
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
                    .height(50.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(3.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                // Placeholder 표시 로직
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                innerTextField()
            }
        }
    )
}