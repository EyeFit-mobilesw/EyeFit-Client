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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.eyefit.components.ExerciseCardItem
import com.example.eyefit.exercise.ExerciseViewModel

@Composable
fun ExerciseHomeScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel() // 뷰모델 주입 (싱글톤처럼 동작하려면 Hilt나 상위 주입 필요)
) {
    val backgroundColor = Color(0xFF222222)
    val mainBlue = Color(0xFF2CCEF3)
    val badgeYellow = Color(0xFFFFF383)

    // 1. 전체 데이터 관찰
    val allExercises by viewModel.uiList.collectAsState()

    // 2. [플레이리스트] 사용자가 선택한 운동만 필터링
    val playlist = remember(allExercises) {
        allExercises.filter { it.isSelected }
    }

    // 3. [총 시간 계산] "7분 00초" 문자열 파싱하여 합산
    val totalSeconds = remember(playlist) {
        playlist.sumOf { item ->
            // 간단한 파싱 로직 (실제로는 모델에 Int형 time 필드가 있으면 더 좋음)
            try {
                val min = item.timeString.substringBefore("분").trim().toInt()
                val sec = item.timeString.substringAfter("분").substringBefore("초").trim().toInt()
                min * 60 + sec
            } catch (e: Exception) {
                0
            }
        }
    }

    // 초 -> "분 초" 포맷 변환
    val totalTimeStr = "%d분 %02d초".format(totalSeconds / 60, totalSeconds % 60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // --- 1. 상단 영역 (배경 및 장식) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // 뒤로가기 버튼
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .padding(start = 21.dp, top = 70.dp)
                    .size(28.dp)
                    .clickable { navController.popBackStack() }
            )

            // 메인 텍스트
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

            // 장식 이미지들
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

        // --- 2. 하단 영역 (리스트 및 컨트롤) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                .padding(top = 30.dp, start = 20.dp, end = 20.dp, bottom = 30.dp)
        ) {
            Column {
                // 리스트 타이틀 & 수정 버튼
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("눈 운동 리스트", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    // [수정 버튼] 클릭 시 운동 변경하기 화면으로 이동
                    Row(
                        modifier = Modifier.clickable {
                            navController.navigate("exercise_list")
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("수정", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

                // [플레이리스트] LazyRow
                if (playlist.isEmpty()) {
                    // 리스트가 비었을 때 안내 문구
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("운동을 추가해주세요 +", color = Color.LightGray)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(15.dp),
                        contentPadding = PaddingValues(end = 20.dp)
                    ) {
                        itemsIndexed(playlist) { index, exercise ->
                            Box(
                                modifier = Modifier.clickable {
                                    // 상세 페이지로 이동
                                    navController.navigate("exercise_detail/${exercise.id}")
                                }
                            ) {
                                ExerciseCardItem(
                                    index = index + 1,
                                    data = exercise,
                                    badgeColor = badgeYellow,
                                    onRemoveClick = {
                                        // [삭제] 홈 화면에서 바로 삭제 가능
                                        viewModel.toggleSelection(exercise.id)
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 총 소요시간 (동적 계산)
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
                    Text("총 소요시간", fontSize = 14.sp, color = Color.Gray)
                    Text(totalTimeStr, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(20.dp))

                // 운동 시작 버튼
                Button(
                    onClick = { /* TODO: 운동 플레이어 화면으로 이동 */ },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = mainBlue),
                    // 리스트가 비어있으면 시작 버튼 비활성화 (선택 사항)
                    enabled = playlist.isNotEmpty()
                ) {
                    Text("운동 시작", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}