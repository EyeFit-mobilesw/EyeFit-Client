package com.example.eyefit // 패키지명 본인 것으로 수정

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.eyefit.ui.theme.EyeFitTheme
import com.example.eyefit.navigation.AppNavGraph
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 테마 적용 (선택사항)
            EyeFitTheme {
                // 1. 네비게이션 컨트롤러 생성
                val navController = rememberNavController()

                // 2. 네비게이션 그래프 설정 (시작점: onboarding)
                NavHost(navController = navController, startDestination = "onboarding") {

                    // 온보딩 스크린
                    composable("onboarding") {
                        OnBoardingScreen(
                            onTimeout = {
                                // 3초 뒤 실행될 동작: 로그인 화면으로 이동
                                navController.navigate("login") {
                                    // 중요: 뒤로가기 눌렀을 때 다시 온보딩이 나오지 않게 스택 제거
                                    popUpTo("onboarding") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 로그인 스크린
                    composable("login") {
                        LoginScreen(navController = navController)
                    }

                    // 회원가입 스크린
                    composable("signup") {
                        SignupScreen(navController)
                    }

                    // [추가해야 할 부분]
                    composable("home") {
                        // Home.kt를 아직 안 만들었다면 임시 텍스트 표시
                        Text("홈 화면입니다", color = androidx.compose.ui.graphics.Color.White)
                        // 나중에 HomeScreen(navController)로 변경
                    }

                    // 회원가입 완료 스크린
                    composable("signup_complete") {
                        SignupCompleteScreen(navController)
                    }
                }
            }
        }
    }
}
