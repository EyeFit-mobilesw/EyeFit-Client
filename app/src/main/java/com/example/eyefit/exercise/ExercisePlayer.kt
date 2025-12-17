package com.example.eyefit.exercise

import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.eyefit.data.util.LockScreenOrientation// 위에서 만든 유틸리티 import
import com.example.eyefit.R
import com.example.eyefit.data.model.ExerciseUiModel
import com.example.eyefit.data.util.LockScreenOrientation
import kotlinx.coroutines.delay
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import coil.compose.AsyncImage

// 화면 단계 정의
enum class PlayerStage { INTRO, COUNTDOWN, VIDEO }

@OptIn(UnstableApi::class)
@Composable
fun ExercisePlayerScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    val playlist = viewModel.playlist

    // 현재 진행 중인 운동의 인덱스
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    // 현재 보여줄 화면 단계 (인트로 -> 카운트다운 -> 비디오)
    var currentStage by rememberSaveable { mutableStateOf(PlayerStage.INTRO) }

    // [추가] 완료 팝업 표시 여부 상태
    var showCompletionPopup by rememberSaveable { mutableStateOf(false) }

    // 모든 운동이 끝났는지 확인
    if (playlist.isEmpty() || currentIndex >= playlist.size) {
        LaunchedEffect(Unit) {
            navController.popBackStack() // 혹은 결과 화면으로 이동
        }
        return
    }

    val currentExercise = playlist[currentIndex]
    val isLastExercise = currentIndex == playlist.size - 1

    // 단계별 화면 교체 로직
    // 전체 화면을 Box로 감싸서 팝업을 위에 띄울 수 있게 함
    Box(modifier = Modifier.fillMaxSize()) {

        // 1. 기존 화면 내용 (인트로 -> 카운트다운 -> 비디오)
        when (currentStage) {
            PlayerStage.INTRO -> {
                LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                ExerciseIntroView(
                    exercise = currentExercise,
                    onNextClick = { currentStage = PlayerStage.COUNTDOWN },
                    onBackClick = { navController.popBackStack() }
                )
            }
            PlayerStage.COUNTDOWN -> {
                LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                ExerciseCountdownView(
                    exercise = currentExercise,
                    onFinishCountdown = { currentStage = PlayerStage.VIDEO },
                    onBackClick = { currentStage = PlayerStage.INTRO }
                )
            }
            PlayerStage.VIDEO -> {
                LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                ExerciseVideoView(
                    exercise = currentExercise,
                    isLastExercise = isLastExercise,
                    onVideoFinished = {
                        if (!isLastExercise) {
                            // 다음 운동으로 이동
                            currentIndex++
                            currentStage = PlayerStage.INTRO
                        } else {
                            // [수정] 마지막 운동이면 팝업 띄우기 (바로 나가지 않음)
                            showCompletionPopup = true

                            // [로직] 포인트 10p 적립
                            viewModel.addPoints(10)
                        }
                    }
                )
            }
        }

        // 2. 완료 팝업 (showCompletionPopup이 true일 때만 표시)
        if (showCompletionPopup) {
            CompletionPopup(
                onClose = {
                    showCompletionPopup = false
                    navController.popBackStack() // X 누르면 홈으로 이동
                }
            )
        }
    }
}

// --- 1. 인트로 화면 (설명 + 토마토) ---
@Composable
fun ExerciseIntroView(
    exercise: ExerciseUiModel,
    onNextClick: () -> Unit,
    onBackClick: () -> Unit // 뒤로가기 버튼 클릭 시 동작할 함수 추가
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // 배경색 흰색으로 변경
            .padding(start = 21.dp, top = 70.dp, end = 21.dp, bottom = 70.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 바 (뒤로가기 버튼 + 타이틀)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color(0xFF222222), // 짙은 회색
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onBackClick() } // 뒤로가기 동작 연결
            )
            Spacer(modifier = Modifier.weight(1f)) // 타이틀을 중앙으로 밀어내기 위한 Spacer
        }

        // 타이틀 및 서브타이틀
        Text(
            text = exercise.title,
            fontSize = 26.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(color = 0xFF222222)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "이렇게 해보세요",
            fontSize = 26.sp,
            fontWeight = FontWeight.Medium,
            color = Color(color = 0xFF222222)
        )

        Spacer(modifier = Modifier.height(60.dp)) // 이미지와 타이틀 사이 여백

        // 토마토 캐릭터 이미지
        Image(
            painter = painterResource(id = R.drawable.infinity_exercise_tomato),
            contentDescription = null,
            modifier = Modifier.size(280.dp)
        )

        Spacer(modifier = Modifier.height(50.dp)) // 이미지와 설명 텍스트 사이 여백

        // 설명 텍스트
        Text(
            text = "설명을 이해한 후 운동 할 때는\n주변의 먼 곳을 바라보며 해주면\n더욱 효과적이에요!",
            textAlign = TextAlign.Center,
            color = Color(color = 0xFF222222),
            fontSize = 16.sp,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.weight(1f)) // 하단 버튼을 아래로 밀어내기 위한 Spacer

        // 하단 다음 버튼 (우측 하단 배치)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End // 우측 정렬
        ) {
            IconButton(
                onClick = onNextClick,
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF2CCEF3), CircleShape) // 파란색 배경
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward, // 앞으로 가기 아이콘
                    contentDescription = "Next",
                    tint = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp)) // 하단 여백
    }
}

// --- 2. 카운트다운 화면 (3, 2, 1) ---
@Composable
fun ExerciseCountdownView(
    exercise: ExerciseUiModel,
    onFinishCountdown: () -> Unit,
    onBackClick: () -> Unit // [추가됨] 뒤로가기 이벤트
) {
    var count by remember { mutableIntStateOf(5) } // 3초가 일반적이지만 5초 원하시면 5로 변경

    // 1초마다 카운트 다운
    LaunchedEffect(Unit) {
        while (count > 0) {
            delay(1000L)
            count--
        }
        // 0이 되면 다음 단계로
        onFinishCountdown()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- 상단 흰색 영역 (Box로 변경하여 겹치기 허용) ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFFF9F9F9))
                .padding(start = 21.dp, top = 70.dp)
        ) {
            // 1. 뒤로가기 버튼 (좌측 상단 고정)
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_back),
                contentDescription = "Back",
                tint = Color(0xFF222222),
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.TopStart) // 좌측 상단 배치
                    .clickable { onBackClick() }
            )

            // 2. 중앙 컨텐츠 (STEP, 이미지, 텍스트)
            Column(
                modifier = Modifier.align(Alignment.Center), // 박스 정중앙 배치
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "STEP", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color(0xFF222222), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "1", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))

                AsyncImage(
                    model = exercise.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.size(250.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = exercise.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = exercise.subTitle,
                    fontSize = 16.sp,
                    color = Color(0xFF8D8D8D)
                )
            }
        }

        // --- 하단 파란색 영역 (카운트다운) ---
        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .background(Color(0xFF2CCEF3)), // mainBlue
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 카운트다운 숫자
                Text(
                    text = count.toString(),
                    fontSize = 100.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF222222)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // "자 그럼 시작합니다!" 뱃지
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFF383), RoundedCornerShape(30.dp))
                        .padding(horizontal = 30.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "자 그럼 시작합니다!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

// --- 3. 비디오 재생 화면 (가로 모드) ---
@OptIn(UnstableApi::class)
@Composable
fun ExerciseVideoView(
    exercise: ExerciseUiModel,
    isLastExercise: Boolean,
    onVideoFinished: () -> Unit
) {
    val context = LocalContext.current

    // [상태 추가] 영상이 끝났는지 여부
    var isVideoEnded by remember { mutableStateOf(false) }

    // ExoPlayer 설정 (기존 동일)
    val exoPlayer = remember(exercise.animationUrl) {
        ExoPlayer.Builder(context).build().apply {
            // [수정 핵심] 하드코딩된 주소 -> exercise 객체의 URL 사용
            val videoUri = Uri.parse(exercise.animationUrl)

            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

    // 리스너 설정 (기존 동일)
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    isVideoEnded = true
                    Log.d("ExerciseVideo", "영상 재생 완료됨 -> 다음 화면으로 이동")
                }
            }
            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExerciseVideo", "에러 발생: ${error.message}")
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    // UI 상태 관리 (기존 동일)
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while(true) {
            currentPosition = exoPlayer.currentPosition
            duration = exoPlayer.duration.coerceAtLeast(0L)
            delay(1000)
        }
    }

    // --- UI 레이아웃 ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 1. 좌측 가이드 영역 (TIP) - 기존 유지
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "TIP", color = Color(0xFFFF7F6E), fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "화면 밖을 보면서\n하면 더 좋아요",
                    textAlign = TextAlign.Center,
                    color = Color(color = 0xFF8D8D8D),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Image(
                    painter = painterResource(id = R.drawable.outside_tomato),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }

            // 2. 우측 비디오 영역
            Box(
                modifier = Modifier
                    .weight(2.5f)
                    .fillMaxHeight()
                    .clipToBounds() // 혹시 모를 넘침 방지
                    .background(Color.Black) // 영상 비율이 안 맞을 때 보일 배경색
            ) {
                // 2-1. 비디오 뷰 (배경)
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { view ->
                        (view.layoutParams as? FrameLayout.LayoutParams)?.gravity = android.view.Gravity.CENTER
                    }
                )

                // 2-2. 오버레이 UI (컨트롤러만 남김)
                // [수정] Column -> Box로 변경하여 정렬 제어
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // [삭제됨] 중앙 텍스트 Box 제거

                    // 하단 컨트롤러 Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter), // [수정] 하단 중앙 정렬
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                                isPlaying = !isPlaying
                            },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Play",
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // 슬라이더
                        Slider(
                            value = if (duration > 0) currentPosition.toFloat() / duration.toFloat() else 0f,
                            onValueChange = { ratio ->
                                val newPos = (ratio * duration).toLong()
                                exoPlayer.seekTo(newPos)
                                currentPosition = newPos
                            },
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFF5CC1F0),
                                activeTrackColor = Color(0xFF5CC1F0),
                                inactiveTrackColor = Color.LightGray
                            )
                        )

                        // [핵심 로직] 영상이 끝났을 때만 버튼 표시
                        if (isVideoEnded) {
                            Spacer(modifier = Modifier.width(10.dp))

                            Button(
                                onClick = onVideoFinished,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A6B8D)),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 20.dp)
                            ) {
                                // 마지막 운동이면 "완료", 아니면 "다음"
                                Text(
                                    text = if (isLastExercise) "완료" else "다음",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// [신규] 완료 팝업 컴포저블
@Composable
fun CompletionPopup(onClose: () -> Unit) {
    // 배경을 반투명 검정으로 덮음 (Dialog 대신 Box로 오버레이)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)) // 배경 어둡게
            .clickable(enabled = false) {} // 뒷배경 클릭 방지
    ) {
        // 1. 우측 상단 닫기(X) 버튼
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 24.dp, end = 24.dp)
                .size(32.dp)
                .clickable { onClose() }
        )

        // 2. 중앙 적립 메시지 뱃지
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2CCEF3), Color(0xFF88DEF2)) // 하늘색 그라데이션
                    ),
                    shape = RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 체크 아이콘
                Icon(
                    imageVector = Icons.Default.CheckCircle, // 혹은 유사한 체크 아이콘
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // 텍스트
                Text(
                    text = "10p 적립되었습니다",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}