package com.example.eyefit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.eyefit.components.ExerciseCardItem

// [데이터 모델]
data class ExerciseData(
    val title: String,
    val subTitle: String,
    val time: String,
    val imageResId: Int
)

@Composable
fun ExerciseHomeScreen(navController: NavController) {
    val backgroundColor = Color(0xFF222222)
    val mainBlue = Color(0xFF2CCEF3)
    val badgeYellow = Color(0xFFFFF383)

    val exerciseList = listOf(
        ExerciseData("8자 그리기 운동", "눈 혈액순환 개선", "3분 30초", R.drawable.img_infinity),
        ExerciseData("눈 깜빡이기 운동", "안구 건조 완화", "7분 00초", R.drawable.img_blink)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- 1. 상단 영역 ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // [추가됨] 뒤로가기 버튼 (디자인 좌측 상단 화살표)
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 21.dp, top = 70.dp)
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )

            // (1) 텍스트
            Column(
                modifier = Modifier.padding(top = 120.dp, start = 30.dp)
            ) {
                Text(
                    text = "오아시스를 향한",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "오늘의 눈 운동",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // (2) 장식 요소들 (기존 코드 동일)
            Image(
                painter = painterResource(id = R.drawable.img_sun),
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 60.dp, end = 30.dp).size(60.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.img_cloud),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterStart).padding(bottom = 5.dp, start = 80.dp).size(50.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.running_tomato),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 20.dp, end = 20.dp).size(180.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.img_cactus_decor),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomStart).offset(x = (-20).dp, y = 40.dp).size(200.dp)
            )
        }

        // --- 2. 하단 영역 (기존 코드 동일) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 30.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("눈 운동 리스트", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("수정", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(15.dp),
                    contentPadding = PaddingValues(end = 20.dp)
                ) {
                    itemsIndexed(exerciseList) { index, exercise ->
                        ExerciseCardItem(index = index + 1, data = exercise, badgeColor = badgeYellow)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text("총 소요시간", fontSize = 14.sp, color = Color.Gray)
                    Text("7분 50초", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { /* 운동 시작 로직 */ },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainBlue)
                ) {
                    Text("운동 시작", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}