package com.example.eyefit.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.eyefit.R

@Composable
fun ExerciseDetailScreen(
    navController: NavController,
    exerciseId: Int, // 네비게이션 인자
    viewModel: ExerciseDetailViewModel = viewModel() // 뷰모델 주입
) {
    // 화면 진입 시 데이터 로드 요청
    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId.toLong())
    }

    val uiState by viewModel.exerciseState.collectAsState()

    // 데이터가 로드되기 전이면 빈 화면이나 로딩바 표시
    if (uiState == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White))
        return
    }

    val data = uiState!!

    val mainBlue = Color(0xFF5CC1F0)
    val badgeYellow = Color(0xFFFFF383)
    val grayText = Color(0xFF8D8D8D)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // 1. 상단바 (뒤로가기)
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = Color(color = 0xFF222222),
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

            // 2. 타이틀 & 시간 배지
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
                Box(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = Color(0xFF8D8D8D),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(badgeYellow, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = data.timeString,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. 운동 이미지
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = data.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 4. 효과 버튼
            Button(
                onClick = {},
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

            // 5. 설명 섹션 (동적 데이터)
            Text(
                text = data.descriptionTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = data.descriptionContent,
                fontSize = 15.sp,
                color = grayText,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 6. 참고사항 (고정 텍스트)
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

            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}