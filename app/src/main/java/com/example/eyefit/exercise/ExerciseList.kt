package com.example.eyefit.exercise

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
    val popupBlue = Color(0xFF5CC1F0)
    val popupGray = Color(0xFF666666) // 광고 버튼 색상
    val warningRed = Color(0xFFFF5252)

    Dialog(onDismissRequest = onDismiss) {
        // 팝업 배경 (카드)
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White), // 반투명 배경 효과 원하면 수정
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. 타이틀
                Text(
                    text = exerciseTitle, // "눈 운동 팩" 대신 운동 이름 표시
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "눈 운동", // 서브타이틀 고정 or 파라미터로 받기
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // [조건부] 포인트 부족 경고 메시지
                if (isInsufficient) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info, // 느낌표 아이콘
                            contentDescription = null,
                            tint = warningRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "포인트가 부족합니다",
                            color = warningRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // 2. 포인트 결제 버튼 (메인 버튼)
                Button(
                    onClick = { if (!isInsufficient) onUnlockClick() },
                    // 포인트가 부족하면 클릭은 되지만 동작 안 하게 하거나, 아예 비활성화 할 수 있음
                    // 디자인상 비활성화 색상이 아니라 그대로 유지되길 원하면 enabled=true 유지
                    enabled = true,
                    shape = RoundedCornerShape(30.dp), // 둥근 알약 모양
                    colors = ButtonDefaults.buttonColors(containerColor = popupBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp) // 버튼 높이 키움
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 코인 아이콘 + 가격
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🪙", fontSize = 24.sp) // 코인 이미지 대신 이모지
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${requiredPoints}p",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "보유 포인트 : ${currentPoints}p",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 3. 광고 보고 잠금 해제 버튼 (서브 버튼)
                Button(
                    onClick = onAdClick,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = popupGray), // 짙은 회색
                    border = null, // 테두리 없음
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                ) {
                    Text(
                        text = "광고 보고 잠금 해제",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}