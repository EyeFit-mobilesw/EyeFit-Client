package com.example.eyefit.exercise

import android.R.attr.fontWeight
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eyefit.R
import com.example.eyefit.components.ExerciseListItem // 분리한 컴포넌트 Import
import com.example.eyefit.data.repository.ExerciseRepository.userPoints

@Composable
fun ExerciseListScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel() // 뷰모델 주입
) {
    val exerciseList by viewModel.uiList.collectAsState()
    val userPoints by viewModel.userPoints.collectAsState()
    val unlockTarget by viewModel.selectedExerciseToUnlock.collectAsState()
    val mainBlue = Color(0xFF2CCEF3)

    // [팝업 표시 로직] target이 null이 아니면 Dialog를 띄움
    if (unlockTarget != null) {
        UnlockDialog(
            exerciseTitle = unlockTarget!!.title,
            currentPoints = userPoints,
            requiredPoints = 100,
            onDismiss = { viewModel.dismissDialog() },
            onUnlockClick = { viewModel.unlockExercise() },
            onAdClick = { /* 광고 보기 로직 */ }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9)) // 배경색 (살짝 회색)
    ) {
        // --- 1. 상단바 ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 21.dp, top = 70.dp)
        ) {
            // 뒤로가기 버튼
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = Color(color = 0xFF222222),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )
            Text(
                text = "눈 운동 변경하기",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // --- 2. 포인트 표시 ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 코인 아이콘 (필요시 이미지로 교체)
            Text(text = "🪙 ", fontSize = 16.sp)
            Text(
                text = "보유 포인트 : ${userPoints}p",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- 3. 운동 리스트 ---
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(exerciseList) { item ->
                ExerciseListItem(
                    data = item,
                    onClick = { viewModel.onExerciseItemClick(item) }
                )
            }
        }

        // --- 4. 추가하기 버튼 ---
        Button(
            onClick = {
                viewModel.savePlaylist() // 저장
                navController.popBackStack() // 완료 후 뒤로가기
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = mainBlue)
        ) {
            Text(
                text = "추가하기",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// [신규] 잠금 해제 팝업 컴포저블
@Composable
fun UnlockDialog(
    exerciseTitle: String,
    currentPoints: Int,
    requiredPoints: Int,
    onDismiss: () -> Unit,
    onUnlockClick: () -> Unit,
    onAdClick: () -> Unit
) {
    val isInsufficient = currentPoints < requiredPoints

    // 디자인 색상 정의
    // 그라데이션 브러시 정의 (2CCEF3 -> 88DEF2)
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF2CCEF3),
            Color(0xFF88DEF2)
        )
    )
    val warningRed = Color(0xFFFF5252) // 경고 문구 빨간색
    val adButtonBg = Color(0xFF424242).copy(alpha = 0.8f) // 광고 버튼 배경 (어두운 회색)
    val adButtonBorder = Color(0xFF2CCEF3) // 광고 버튼 테두리용 (기존 mainBlue)

    Dialog(onDismissRequest = onDismiss) {
        // 흰색 Card 배경을 제거하고, 투명한 Column 사용
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp) // 좌우 여백
        ) {

            // [1] 포인트 부족 경고 메시지 (부족할 때만 표시)
            if (isInsufficient) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(bottom = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info, // i 아이콘
                        contentDescription = null,
                        tint = warningRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "포인트가 부족합니다",
                        color = warningRed,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // [2] 포인트 결제 버튼 (하늘색)
            Button(
                onClick = { if (!isInsufficient) onUnlockClick() },
                enabled = true, // 디자인 유지를 위해 클릭은 되게 하되 로직에서 막음
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp) // 버튼 높이
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(gradientBrush), // 그라데이션 적용
                    contentAlignment = Alignment.Center
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 아이콘 + 100p
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // 코인 아이콘 (이미지 리소스가 있다면 교체 추천)
                            // Icon(painter = painterResource(R.drawable.ic_coin), ...)
                            Text(text = "🪙", fontSize = 20.sp) // 임시 이모지
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${requiredPoints}p",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "보유 포인트 : ${currentPoints}p",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // [3] 광고 보고 잠금 해제 버튼 (어두운 배경 + 테두리)
            Button(
                onClick = onAdClick,
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = adButtonBg
                ),
                border = androidx.compose.foundation.BorderStroke(2.dp, adButtonBorder), // 하늘색 테두리
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
            ) {
                Text(
                    text = "광고 보고 잠금 해제",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}