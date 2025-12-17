package com.example.eyefit.habit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyefit.R

@Composable
fun EyeHabitScreen(
    onBack: () -> Unit,
    onDailyCheckClick: () -> Unit,
    onDailyAnalysisClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B1B))
    ) {

        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .padding(start = 21.dp, top = 70.dp)
                .size(25.dp)
                .clickable { onBack() }
        )

        Column(
            modifier = Modifier.padding(horizontal = 27.dp, vertical = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(23.dp))

            Text(
                text = "오늘의 눈 건강습관을",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "체크해봐요",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(42.dp))

            HabitCard(
                backgroundColor = Color(0xFF2CCEF3),
                title = "하루 습관 체크",
                subtitle = "오늘 습관 몇 개를 달성하셨나요?",
                image = R.drawable.habit_card1,
                buttonColor = Color(0xFF2CCEF3),
                onClick = onDailyCheckClick
            )

            Spacer(modifier = Modifier.height(29.dp))

            HabitCard(
                backgroundColor = Color(0xFFA953FF),
                title = "하루 습관 분석",
                subtitle = "일주일의 습관 분석을 확인해볼까요?",
                image = R.drawable.habit_card2,
                buttonColor = Color(0xFFA953FF),
                onClick = onDailyAnalysisClick
            )
        }
    }
}


@Composable
fun HabitCard(
    backgroundColor: Color,
    title: String,
    subtitle: String,
    image: Int,
    buttonColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = title,
                        color = Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = subtitle,
                        color = Color(0xFF797979),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(buttonColor, shape = CircleShape)
                        .clickable { onClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}
