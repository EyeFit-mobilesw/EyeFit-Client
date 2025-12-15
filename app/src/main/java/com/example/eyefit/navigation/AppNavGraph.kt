package com.example.eyefit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyefit.home.HomeScreen
import com.example.eyefit.exercise.ExerciseScreen
import com.example.eyefit.habit.DailyHabitCheckScreen
import com.example.eyefit.habit.EyeHabitScreen
import com.example.eyefit.ui.habit.HabitAnalysisScreen
import androidx.navigation.navArgument
import com.example.eyefit.ExerciseHomeScreen
import com.example.eyefit.LoginScreen
import com.example.eyefit.OnBoardingScreen
import com.example.eyefit.SignupCompleteScreen
import com.example.eyefit.SignupScreen
import com.example.eyefit.exercise.ExerciseDetailScreen
import com.example.eyefit.exercise.ExerciseListScreen
import com.example.eyefit.home.HomeScreen
import com.example.eyefit.habit.DailyHabitCheckScreen
import com.example.eyefit.habit.EyeHabitScreen


@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {

    NavHost(
        navController = navController,
        startDestination = "exercise_home"
    ) {
        composable(route = "home") {
            HomeScreen(
                onStartExercise = { navController.navigate("exercise_home") },
                onHabitDetailClick = { navController.navigate("habit_detail") }
            )
        }


        composable(route = "exercise_home") {
            ExerciseHomeScreen(navController = navController)
        }

        composable(
            route = "exercise_detail/{exerciseId}",
            arguments = listOf(navArgument("exerciseId") { type = NavType.IntType })
        ) { backStackEntry ->
            // URL에서 ID 추출
            val exerciseId = backStackEntry.arguments?.getInt("exerciseId") ?: 0
            ExerciseDetailScreen(navController = navController, exerciseId = exerciseId)
        }

        // [추가됨] 전체 운동 리스트 (운동 변경하기) 화면
        composable(route = "exercise_list") {
            ExerciseListScreen(navController = navController)
        }

        composable(route = "habit_detail") {
            EyeHabitScreen(
                onBack = { navController.popBackStack() },
                onDailyCheckClick = { navController.navigate("daily_check") },
                onDailyAnalysisClick = { navController.navigate("habit_analysis") }  // ⭐ 수정!!
            )
        }

        composable("daily_check") {
            DailyHabitCheckScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("habit_analysis") {
            HabitAnalysisScreen(
                onBack = { navController.popBackStack() },
                weeklyHabitCounts = listOf(1,6,4,2,1,4,6),     // 예시 데이터
                todayUncheckedHabits = listOf(
                    "콘택트 렌즈를 장시간 사용 않기",
                    "충분한 수면을 취하기",
                    "눈 찜질을 하루 1회 하자"
                )
            )
        }

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

        // 회원가입 완료 스크린
        composable("signup_complete") {
            SignupCompleteScreen(navController)
        }
    }
}
