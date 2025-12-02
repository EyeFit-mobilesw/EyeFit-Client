package com.example.eyefit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip

@Composable
fun DailyProgressBar(
    day: Int,
    progressWidth: Float,
    modifier: Modifier = Modifier
) {
    val backgroundColor = Color(0xFFEDEDED)
    val progressColor = Color.Black

    val isRightAttached = day <= 3   // Day1~3: 오른쪽 벽
    val isLeftAttached = day >= 4    // Day4~7: 왼쪽 벽

    // 전체 트랙
    val trackShape = RoundedCornerShape(50)

    // 진행바 shape
    val barShape =
        if (isRightAttached) {
            RoundedCornerShape(
                topStart = 50.dp,
                bottomStart = 50.dp,
                topEnd = 0.dp,
                bottomEnd = 0.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = 50.dp,
                bottomEnd = 50.dp
            )
        }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(14.dp)
    ) {
        // 흰색 트랙
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(trackShape)
                .background(backgroundColor)
        )

        // 검은색 진행도 바
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progressWidth)
                .clip(barShape)
                .background(progressColor)
        )
    }
}
