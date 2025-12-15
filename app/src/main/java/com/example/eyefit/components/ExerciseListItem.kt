package com.example.eyefit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyefit.data.model.ExerciseUiModel

@Composable
fun ExerciseListItem(
    data: ExerciseUiModel,
    onClick: () -> Unit
) {
    // 색상 정의
    val selectedBgColor = Color(208, 239, 246) // 선택됨(하늘색)
    val normalBgColor = Color.White         // 평상시
    val badgeYellow = Color(0xFFFFF383)
    val grayText = Color(0xFF8D8D8D)

    // 상태에 따른 스타일 결정
    val backgroundColor = if (data.isSelected) selectedBgColor else normalBgColor
    val titleColor = if (data.isUnlocked) Color(color = 0xFF222222) else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(16.dp))
            .background(backgroundColor, RoundedCornerShape(16.dp))
            .clickable(enabled = data.isUnlocked) { onClick() } // 잠겨있으면 클릭 불가
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. 이미지 (좌측)
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = data.imageResId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 2. 텍스트 (중앙)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = data.subTitle,
                fontSize = 13.sp,
                color = grayText
            )
        }

        // 3. 시간 배지 (우측) - 잠금 해제 시에만 표시
        if (data.isUnlocked) {
            Box(
                modifier = Modifier
                    .border(0.5.dp, grayText, RoundedCornerShape(20.dp))
                    .background(badgeYellow, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = data.timeString,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        } else {
            // 잠금 상태일 때 자물쇠 아이콘 (필요시 추가)
        }
    }
}