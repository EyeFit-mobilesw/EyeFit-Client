package com.example.eyefit.data.util

import androidx.compose.runtime.Composable

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as? android.app.Activity
    androidx.compose.runtime.DisposableEffect(orientation) {
        val originalOrientation = activity?.requestedOrientation ?: android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = orientation
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }
}