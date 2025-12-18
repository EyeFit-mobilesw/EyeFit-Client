package com.example.eyefit.habit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.eyefit.data.firebase.FirebaseProvider
import com.google.firebase.firestore.SetOptions

@Composable
fun DailyHabitCheckScreen(
    onBack: () -> Unit = {}
) {
    // Firebase
    val auth = remember { FirebaseProvider.auth }
    val db = remember { FirebaseProvider.db }
    val scope = rememberCoroutineScope()

    val uid = auth.currentUser?.uid
    val dateKey = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(Date())
    }

    // Firestore에 저장할 습관 key (index 순서와 1:1 매칭)
    val habitKeys = remember {
        listOf(
            "SMARTPHONE_LIMIT",
            "SLEEP_ENOUGH",
            "NO_SMOKING",
            "USE_ARTIFICIAL_TEARS",
            "EYE_STEAM",
            "CONTACT_LENS_LIMIT"
        )
    }

    // 실제 저장된 값 (초기: all false)
    var originalHabitStates by remember { mutableStateOf(List(6) { false }) }

    // 현재 선택 값
    var habitStates by remember { mutableStateOf(originalHabitStates) }

    // 수정모드 여부
    var isEditing by remember { mutableStateOf(true) }

    // 체크완료 팝업
    var showCompletePopup by remember { mutableStateOf(false) }

    // 로딩/에러
    var loading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var didLoadOnce by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    /** ---------------------------
     *  화면 진입 시: 오늘 기록 Firestore에서 불러오기
     *  users/{uid}/habitChecks/{dateKey}
    ----------------------------- */
    LaunchedEffect(uid) {
        if (uid == null) return@LaunchedEffect
        if (didLoadOnce) return@LaunchedEffect

        loading = true
        errorMsg = null
        try {
            val doc = db.collection("users")
                .document(uid)
                .collection("habitChecks")
                .document(dateKey)
                .get()
                .await()

            if (doc.exists()) {
                val items = doc.get("items") as? Map<*, *>

                val loaded = habitKeys.map { key ->
                    (items?.get(key) as? Boolean) ?: false
                }

                originalHabitStates = loaded
                habitStates = loaded
            } else {
                originalHabitStates = List(6) { false }
                habitStates = originalHabitStates
            }
        } catch (e: Exception) {
            errorMsg = e.message ?: "오늘 습관 기록을 불러오지 못했어요."
        } finally {
            loading = false
            didLoadOnce = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F6F6))
    ) {

        /** ---------------------------
         *  본문(스크롤 영역)
        ----------------------------- */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {

            Spacer(modifier = Modifier.height(39.dp))

            /** ---------------------------
             *   상단 (뒤로가기 + 제목)
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

                Spacer(modifier = Modifier.size(28.dp))
            }

            Spacer(modifier = Modifier.height(33.dp))

            /** ---------------------------
             *   수정 버튼
            ----------------------------- */
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditing = true },
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
                    text = if (isEditing) "수정중" else "수정",
                    color = if (isEditing) Color.Black else Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = if (isEditing) FontWeight.Bold else FontWeight.Normal
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
                    enabled = isEditing && !loading,
                    onClick = {
                        if (isEditing && !loading) {
                            habitStates = habitStates.toMutableList().apply {
                                this[index] = !this[index]
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(17.dp))
            }

            // 에러 메시지
            if (errorMsg != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMsg!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            // 하단 고정 버튼에 가리지 않도록 아래 여백 확보
            Spacer(modifier = Modifier.height(120.dp))
        }

        /** ---------------------------
         *   체크완료 버튼
        ----------------------------- */
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth()
                .height(65.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF2CCEF3))
                .clickable(enabled = !loading) {

                    // 변경 여부 체크
                    val changed = habitStates != originalHabitStates
                    if (!changed) {
                        isEditing = false
                        return@clickable
                    }

                    // 로그인 체크
                    if (uid == null) {
                        errorMsg = "로그인 상태가 아니어서 저장할 수 없어요."
                        return@clickable
                    }

                    scope.launch {
                        loading = true
                        errorMsg = null

                        try {
                            // Firestore 저장 데이터
                            val itemsMap: Map<String, Boolean> =
                                habitKeys.mapIndexed { idx, key -> key to habitStates[idx] }.toMap()

                            val achievedCount = habitStates.count { it } // ✅ 그래프용

                            val data = mapOf(
                                "dateKey" to dateKey,
                                "items" to itemsMap,
                                "achievedCount" to achievedCount,
                                "updatedAt" to System.currentTimeMillis()
                            )

                            db.collection("users")
                                .document(uid)
                                .collection("habitChecks")
                                .document(dateKey)
                                .set(data, SetOptions.merge())
                                .await()

                            // 저장 성공 → 로컬 기준값 갱신 + 팝업
                            originalHabitStates = habitStates
                            showCompletePopup = true
                            isEditing = false

                        } catch (e: Exception) {
                            errorMsg = e.message ?: "저장에 실패했어요."
                        } finally {
                            loading = false
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (loading) "저장중..." else "체크완료",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    /** ---------------------------
     *   팝업 + 자동 종료
    ----------------------------- */
    if (showCompletePopup) {
        CompletePopup(onDismiss = { showCompletePopup = false })

        LaunchedEffect(showCompletePopup) {
            delay(2000)
            showCompletePopup = false
        }
    }
}

/* -----------------------------------------------------
   습관 박스
------------------------------------------------------ */
@Composable
fun HabitItem(
    icon: Int,
    text: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val yellowMain = Color(0xFFFEE712)
    val yellowSoft = Color(0xFFFFF383)
    val grayIconBg = Color(0xFFF7F8F9)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) yellowSoft else Color.White)
            .clickable(enabled = enabled) { onClick() },
        contentAlignment = Alignment.CenterStart
    ) {

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(41.dp)
                    .background(
                        if (selected) yellowMain else grayIconBg,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.Black else Color(0xFF8A8A8A)
            )
        }
    }
}

/* -----------------------------------------------------
   완료 팝업
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
                .padding(bottom = 180.dp)
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

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "오늘의 눈 건강 습관 체크완료!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
