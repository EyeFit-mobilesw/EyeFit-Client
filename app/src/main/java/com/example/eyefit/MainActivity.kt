package com.example.eyefit // 패키지명 본인 것으로 수정

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyefit.ui.theme.EyeFitTheme // 테마 이름 다르면 수정

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

                    // [화면 1] 온보딩 스크린
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

                    // [화면 2] 로그인 스크린
                    composable("login") {
                        LoginScreen()
                    }
                }
            }
        }
    }
}