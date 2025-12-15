package com.example.eyefit.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.navigation.NavController
import com.example.eyefit.R
import com.example.eyefit.model.ExerciseData

@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    exerciseId: Int // 네비게이션으로 전달받은 ID
) {
    // ID로 해당 운동 데이터 찾기 (없으면 첫 번째 데이터 보여줌)
    val data = com.example.eyefit.model.exerciseList.find { it.id == exerciseId }
        ?: com.example.eyefit.model.exerciseList[0]

    val mainBlue = Color(0xFF5CC1F0)
    val badgeYellow = Color(0xFFFFF383)
    val grayText = Color(0xFF8D8D8D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()) // 세로 스크롤 가능하게
    ) {
        // --- 1. 상단바 (뒤로가기) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // [추가됨] 뒤로가기 버튼 (디자인 좌측 상단 화살표)
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .padding(start = 21.dp, top = 70.dp)
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- 2. 타이틀 & 시간 배지 ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // 제목 + 부제목
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = data.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = data.subTitle,
                        fontSize = 18.sp,
                        color = grayText
                    )
                }

                // 시간 배지 (노란색)
                // 시간 배지 (노란색)
                Box(
                    modifier = Modifier
                        // 1. 테두리를 먼저 그리거나 (배경과 같은 크기)
                        .border(
                            width = 0.8.dp,
                            color = Color(0xFF8D8D8D),
                            shape = RoundedCornerShape(20.dp)
                        )
                        // 2. 배경을 설정하고
                        .background(badgeYellow, RoundedCornerShape(20.dp))
                        // 3. 마지막에 내용물(Text)과의 간격을 둡니다.
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = data.time,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- 3. 운동 이미지 ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // 적절한 높이 설정
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = data.imageResId),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- 4. 효과 버튼 (장식용) ---
            Button(
                onClick = { /* 효과 팝업 등 기능이 있다면 추가 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = mainBlue)
            ) {
                Text(
                    text = "효과",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- 5. 설명 섹션 (동적 데이터) ---
            Text(
                text = data.descriptionTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(color = 0xFF222222)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = data.descriptionContent,
                fontSize = 15.sp,
                color = grayText,
                lineHeight = 24.sp // 줄간격 넉넉하게
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- 6. 참고사항 (고정 텍스트) ---
            Text(
                text = "참고해주세요!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "눈에 통증 발생 시 운동을 멈춰주세요.\n또한, 눈 운동 효과는 개인마다 편차가 있을 수 있습니다.",
                fontSize = 15.sp,
                color = grayText,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(50.dp)) // 하단 여백
        }
    }
}