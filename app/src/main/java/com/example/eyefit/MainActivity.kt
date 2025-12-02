package com.example.eyefit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.eyefit.ui.theme.EyeFitTheme
import com.example.eyefit.navigation.AppNavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EyeFitTheme {
                AppNavGraph()
            }
        }
    }
}
