package com.example.eyefit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyefit.model.ExerciseData

@Composable
fun ExerciseCardItem(
    index: Int,
    data: ExerciseData,
    badgeColor: Color
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(160.dp)
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(15.dp)
    ) {
        // 우측 상단 닫기 아이콘
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove",
            tint = Color.LightGray,
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. 번호 (파란 원)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF5CC1F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = index.toString(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // 2. 텍스트 (제목, 서브타이틀)
            Column {
                Text(
                    text = data.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subTitle,
                    color = Color(0xFF8D8D8D),
                    fontSize = 12.sp
                )
            }

            // 3. 하단 (시간 배지 + 이미지)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [시간 배지] - 요청하신 테두리 적용
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(12.dp))
                        .border(
                            width = 0.8.dp,              // 테두리 굵기 0.8dp
                            color = Color(0xFF8D8D8D),   // 테두리 색상
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = data.time,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )
                }

                // 이미지
                Image(
                    painter = painterResource(id = data.imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(50.dp)
                        .height(50.dp)
                )
            }
        }
    }
}