package com.example.eyefit.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eyefit.R
import com.example.eyefit.components.ExerciseListItem // 분리한 컴포넌트 Import

@Composable
fun ExerciseListScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel() // 뷰모델 주입
) {
    val exerciseList by viewModel.uiList.collectAsState()
    val mainBlue = Color(0xFF2CCEF3)

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
                text = "보유 포인트 : 200p",
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
                    onClick = { viewModel.toggleSelection(item.id) }
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