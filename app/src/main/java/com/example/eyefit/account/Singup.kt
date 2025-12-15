package com.example.eyefit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.eyefit.data.firebase.FirebaseProvider
import com.example.eyefit.data.model.UserProfile
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


@Composable
fun SignupScreen(navController: NavController) {

    val auth = remember { FirebaseProvider.auth }
    val db = remember { FirebaseProvider.db }
    val scope = rememberCoroutineScope()

    var errorMsg by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // 색상 정의
    val backgroundColor = Color(0xFF222222)
    val mainColor = Color(0xFF5CC1F0) // 하늘색
    val disabledColor = Color.Gray // 비활성화 버튼 색상

    // 입력값 상태 관리
    var emailText by remember { mutableStateOf("") }
    var idText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var passwordCheckText by remember { mutableStateOf("") }

    // [추가됨] 모든 필드가 채워졌는지 확인하는 변수
    // isNotEmpty()는 빈 문자열이 아닐 때 true를 반환합니다.
    // 공백(스페이스바)도 입력으로 치지 않으려면 isNotBlank()를 사용하세요.
    val isFormValid = emailText.isNotEmpty() &&
            idText.isNotEmpty() &&
            passwordText.isNotEmpty() &&
            passwordCheckText.isNotEmpty() &&
            passwordText == passwordCheckText

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // 1. 메인 콘텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 30.dp)
        ) {
            // --- 상단 바 ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    scope.launch {
                        loading = true
                        errorMsg = null

                        try {
                            // 0) username(아이디) 중복 체크 (Firestore)
                            val duplicated = db.collection("users")
                                .whereEqualTo("username", idText)
                                .limit(1)
                                .get()
                                .await()
                                .isEmpty.not()

                            if (duplicated) {
                                throw IllegalStateException("이미 사용 중인 아이디입니다.")
                            }

                            // 1) Auth 계정 생성 (email/password)
                            val result = auth.createUserWithEmailAndPassword(emailText, passwordText).await()
                            val uid = result.user?.uid ?: throw IllegalStateException("uid 생성 실패")

                            // 2) Firestore에 프로필 저장 (users/{uid})
                            val profile = UserProfile(
                                uid = uid,
                                email = emailText,
                                username = idText
                            )

                            db.collection("users")
                                .document(uid)
                                .set(profile)
                                .await()

                            // 3) 성공 → 이동
                            navController.navigate("signup_complete")
                        } catch (e: Exception) {
                            errorMsg = e.message ?: "회원가입 중 오류가 발생했어요."
                        } finally {
                            loading = false
                        }
                    }
                },
                // [수정됨] 유효성 검사 결과에 따라 버튼 활성/비활성 결정
                enabled = isFormValid && !loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = mainColor, // 활성 상태일 때 배경색
                    disabledContainerColor = disabledColor, // [추가됨] 비활성 상태일 때 배경색 (회색)
                    contentColor = Color.White,
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "회원가입",
                    color = Color.White, // 텍스트 색상
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = errorMsg!!, color = Color.Red, fontSize = 12.sp)
            }

        }

        // 2. 하단 선인장 장식
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

// (아래 LabelText, SignupTextField 컴포넌트는 기존과 동일하므로 생략 가능하지만,
// 복사 붙여넣기 편의를 위해 그대로 둡니다.)

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