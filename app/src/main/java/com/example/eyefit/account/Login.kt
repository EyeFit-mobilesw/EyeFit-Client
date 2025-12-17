package com.example.eyefit

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
import com.example.eyefit.data.firebase.FirebaseProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(navController: NavController) {

    val auth = remember { FirebaseProvider.auth }
    val db = remember { FirebaseProvider.db }
    val scope = rememberCoroutineScope()

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // 색상 정의
    val backgroundColor = Color(0xFF222222)
    val mainColor = Color(0xFF2CCEF3) // 버튼 및 테두리 하늘색
    val disabledColor = Color.Gray    // 비활성화 색상

    // 텍스트 그라데이션 브러시
    val textGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFF7F8F9),
            Color(0xFFC3F4FF)
        )
    )

    // 입력값 상태 관리
    var idText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }

    // 두 필드가 모두 채워졌는지 확인
    val isLoginValid = idText.isNotEmpty() && passwordText.isNotEmpty()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. 로고
            Text(
                text = "EyeFit",
                fontSize = 50.sp,
                fontFamily = pixelFont,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    brush = textGradient
                ),
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 2. 아이디 입력 섹션
            Box(modifier = Modifier.fillMaxWidth()) {
                // 라벨
                Text(
                    text = "아이디",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 8.dp)
                )
                Image(
                    painter = painterResource(id = R.drawable.left_tomato),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.BottomEnd)
                        .offset(y = 10.dp)
                        .zIndex(1f)
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
                    scope.launch {
                        loading = true
                        errorMsg = null

                        try {
                            // 1) username(아이디)로 users에서 email 찾기
                            val snap = db.collection("users")
                                .whereEqualTo("username", idText)
                                .limit(1)
                                .get()
                                .await()

                            if (snap.isEmpty) {
                                throw IllegalStateException("존재하지 않는 아이디입니다.")
                            }

                            val email = snap.documents[0].getString("email")
                                ?: throw IllegalStateException("이 계정에 이메일 정보가 없어요.")

                            // 2) email+password로 Auth 로그인
                            auth.signInWithEmailAndPassword(email, passwordText).await()

                            // 3) 성공 이동
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            errorMsg = e.message ?: "로그인 실패"
                        } finally {
                            loading = false
                        }
                    }
                },
                // 유효성 검사 결과에 따라 활성/비활성
                enabled = isLoginValid && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainColor, // 활성 상태 배경
                    disabledContainerColor = disabledColor, // 비활성 상태 배경
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "로그인",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorMsg!!, color = Color.Red, fontSize = 12.sp)
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
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        navController.navigate("signup")
                    }
                )
            }
        }

        // 6. 하단 선인장 장식
        Image(
            painter = painterResource(id = R.drawable.img_cactus_decor),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(300.dp)
                .offset(x = 50.dp, y = 50.dp)
        )
    }
}
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
                    .height(55.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(3.dp, borderColor, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                innerTextField()
            }
        }
    )
}