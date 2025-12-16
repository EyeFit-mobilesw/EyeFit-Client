package com.example.eyefit.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.eyefit.data.model.ExerciseUiModel // [중요] 수정된 데이터 모델 Import

@Composable
fun ExerciseCardItem(
    index: Int,
    data: ExerciseUiModel, // [변경] ExerciseData -> ExerciseUiModel
    badgeColor: Color = Color(0xFFFFF383), // 기본 노란색
    onRemoveClick: () -> Unit = {} // [추가] 삭제 버튼 클릭 이벤트
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
                .size(18.dp) // 터치 영역 고려하여 아이콘 크기 살짝 조정
                .align(Alignment.TopEnd)
                .clickable { onRemoveClick() } // [추가] 클릭 시 삭제 동작 수행
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
                    fontSize = 16.sp,
                    maxLines = 1 // 텍스트 넘침 방지
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subTitle,
                    color = Color(0xFF8D8D8D),
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }

            // 3. 하단 (시간 배지 + 이미지)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // [시간 배지] - 테두리 이슈 해결 적용 (Border -> Background -> Padding)
                Box(
                    modifier = Modifier
                        .border(
                            width = 0.8.dp,
                            color = Color(0xFF8D8D8D),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .background(badgeColor, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        // [변경] data.time -> data.timeString (새 모델 필드명)
                        text = data.timeString,
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
                        .size(50.dp) // width/height 대신 size로 통일
                )
            }
        }
    }
}