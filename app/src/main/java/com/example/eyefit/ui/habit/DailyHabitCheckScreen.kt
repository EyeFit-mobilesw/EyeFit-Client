package com.example.eyefit.ui.habit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eyefit.R
import kotlinx.coroutines.delay


@Composable
fun DailyHabitCheckScreen(
    onBack: () -> Unit = {}
) {
    var habitStates by remember {
        mutableStateOf(
            listOf(
                false,   // 스마트폰 장시간 사용 X
                false,   // 충분한 수면
                false,  // 흡연 X
                false,  // 인공눈물
                false,   // 눈 찜질
                false   // 렌즈 장시간 사용 X
            )
        )
    }

    var showCompletePopup by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
            .padding(horizontal = 24.dp)
    ) {

        Spacer(modifier = Modifier.height(39.dp))

        /** ---------------------------
         *   상단 UI
         *   뒤로가기 + 제목 중앙정렬
        ----------------------------- */

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(21.dp)
                    .clickable { onBack() }
            )

            // 제목 중앙 정렬
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "눈 건강 습관 체크",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // 오른쪽 공간 균형용 더미
            Spacer(modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(33.dp))

        /** ---------------------------
         *   수정 버튼
        ----------------------------- */

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "수정",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(17.dp))

        /** ---------------------------
         *   습관 리스트
        ----------------------------- */

        val habits = listOf(
            Pair(R.drawable.ic_phone, "스마트폰을 장시간 이용하지 않았다"),
            Pair(R.drawable.ic_sleep, "충분한 수면을 취했다"),
            Pair(R.drawable.ic_nosmoking, "흡연을 하지 않았다"),
            Pair(R.drawable.ic_eye_drop, "눈이 건조할 때 인공눈물을 넣었다"),
            Pair(R.drawable.ic_hotpack, "눈 찜질을 하루 1회 이상 했다"),
            Pair(R.drawable.ic_lens, "콘택트 렌즈를 장시간 사용하지 않기")
        )

        habits.forEachIndexed { index, item ->

            HabitItem(
                icon = item.first,
                text = item.second,
                selected = habitStates[index],
                onClick = {
                    habitStates = habitStates.toMutableList().apply {
                        this[index] = !this[index]
                    }
                }
            )

            Spacer(modifier = Modifier.height(17.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        /** ---------------------------
         *   체크완료 버튼
        ----------------------------- */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF74D6FF))
                .clickable { showCompletePopup = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "체크완료",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    /** ---------------------------
     *   팝업 표시 + 자동 사라짐
    ----------------------------- */
    if (showCompletePopup) {

        CompletePopup(
            onDismiss = { showCompletePopup = false }
        )

        // 2초 뒤 자동 dismiss
        LaunchedEffect(Unit) {
            delay(2000)
            showCompletePopup = false
        }
    }
}


/* -----------------------------------------------------
   습관 박스 (두껍게 수정됨)
------------------------------------------------------ */

@Composable
fun HabitItem(
    icon: Int,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)              // ← 박스 더 두꺼움
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (selected) Color(0xFFFFF4A8)
                else Color.White
            )
            .clickable { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 16.sp,
                color = if (selected) Color.Black else Color(0xFF707070),
                fontWeight = FontWeight.Medium
            )
        }
    }
}


/* -----------------------------------------------------
   체크완료 팝업 (버튼 위에 표시 + 자동 사라짐)
------------------------------------------------------ */

@Composable
fun CompletePopup(
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {

        Row(
            modifier = Modifier
                .padding(bottom = 110.dp)    // 체크완료 버튼 바로 위
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "오늘의 눈 건강 습관 체크완료!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}
