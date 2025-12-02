package com.example.eyefit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eyefit.ui.home.HomeScreen
import com.example.eyefit.ui.exercise.ExerciseScreen
import com.example.eyefit.ui.habit.DailyHabitCheckScreen
import com.example.eyefit.ui.habit.EyeHabitScreen

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
                onDailyAnalysisClick = { navController.navigate("daily_analysis") }
            )
        }

        composable("daily_check") {
            DailyHabitCheckScreen(
                onBack = { navController.popBackStack() }
            )
        }



    }
}
