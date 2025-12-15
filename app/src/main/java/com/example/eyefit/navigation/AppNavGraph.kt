package com.example.eyefit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyefit.home.HomeScreen
import com.example.eyefit.exercise.ExerciseScreen
import com.example.eyefit.habit.DailyHabitCheckScreen
import com.example.eyefit.habit.EyeHabitScreen
import com.example.eyefit.ui.habit.HabitAnalysisScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            HomeScreen(
                onStartExercise = { navController.navigate("exercise") },
                onHabitDetailClick = { navController.navigate("habit_detail") }
            )
        }


        composable("exercise") {
            ExerciseScreen()
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
    }
}
