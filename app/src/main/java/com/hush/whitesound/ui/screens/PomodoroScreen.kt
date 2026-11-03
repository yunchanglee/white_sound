package com.hush.whitesound.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen(
    todayFocusMillis: Long,
    onStartFocusAudio: () -> Unit,
    onStartBreakAudio: () -> Unit,
    onPauseAudio: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var focusMinutes by remember { mutableStateOf(25) }
    var breakMinutes by remember { mutableStateOf(5) }
    var isBreak by remember { mutableStateOf(false) }
    var isRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableStateOf(25 * 60) }

    LaunchedEffect(isRunning, remainingSeconds, isBreak) {
        if (isRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (isRunning && remainingSeconds <= 0) {
            // Switch session
            if (!isBreak) {
                isBreak = true
                remainingSeconds = breakMinutes * 60
                onStartBreakAudio()
            } else {
                isBreak = false
                remainingSeconds = focusMinutes * 60
                onStartFocusAudio()
            }
        }
    }

    val totalSeconds = if (isBreak) breakMinutes * 60 else focusMinutes * 60
    val progress = (remainingSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F4A3E),
                        Color(0xFF11202A),
                        DarkBg
                    )
                )
            )
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "뒤로",
                tint = TextPrimary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Text("포모도로 타이머", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Circular Timer Gauge
        Box(
            modifier = Modifier.size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background Track
                drawArc(
                    color = Color(0xFF22323A),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
                // Active Arc
                drawArc(
                    color = if (isBreak) CategoryFocus else CategoryStudy,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isBreak) "휴식 시간" else "집중 모드",
                    color = if (isBreak) CategoryFocus else CategoryStudy,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                val mins = remainingSeconds / 60
                val secs = remainingSeconds % 60
                Text(
                    text = String.format("%02d:%02d", mins, secs),
                    color = TextPrimary,
                    fontSize = 52.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBreak) "다음: 집중 ${focusMinutes}분" else "다음: 휴식 ${breakMinutes}분",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Preset Chips
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(25 to 5, 50 to 10, 90 to 20).forEach { (f, b) ->
                val isSelected = focusMinutes == f && breakMinutes == b
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) CategoryStudy else DarkSurface2)
                        .clickable {
                            focusMinutes = f
                            breakMinutes = b
                            isBreak = false
                            remainingSeconds = f * 60
                            isRunning = false
                            onPauseAudio()
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "$f / $b",
                        color = if (isSelected) Color(0xFF13241B) else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DarkSurface2)
                    .clickable {
                        isRunning = false
                        isBreak = false
                        remainingSeconds = focusMinutes * 60
                        onPauseAudio()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Stop, contentDescription = "정지", tint = TextPrimary)
            }

            Spacer(modifier = Modifier.width(28.dp))

            // Play/Pause FAB
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CategoryStudy)
                    .clickable {
                        isRunning = !isRunning
                        if (isRunning) {
                            if (isBreak) onStartBreakAudio() else onStartFocusAudio()
                        } else {
                            onPauseAudio()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF13241B),
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(28.dp))

            // +5 Min button
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DarkSurface2)
                    .clickable { remainingSeconds += 300 },
                contentAlignment = Alignment.Center
            ) {
                Text("+5분", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Today focus duration card
        val todayMins = (todayFocusMillis / 60000)
        val todayHours = todayMins / 60
        val remainingTodayMins = todayMins % 60
        val durationStr = if (todayHours > 0) "${todayHours}시간 ${remainingTodayMins}분" else "${todayMins}분"

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurface)
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("오늘 집중 시간", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "$durationStr 달성 🔥",
                    color = CategoryStudy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
