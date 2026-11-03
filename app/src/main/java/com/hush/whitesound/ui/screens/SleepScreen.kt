package com.hush.whitesound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.data.model.SleepTimerState
import com.hush.whitesound.ui.theme.PureBlack
import com.hush.whitesound.ui.theme.SleepDimText
import com.hush.whitesound.ui.theme.SleepFaintText
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

@Composable
fun SleepScreen(
    currentTitle: String,
    timerState: SleepTimerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTimeStr by remember { mutableStateOf("") }
    var burnInOffsetX by remember { mutableStateOf(0) }
    var burnInOffsetY by remember { mutableStateOf(0) }

    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeStr = timeFormat.format(Date())
            // Shift position every minute to prevent OLED burn-in
            burnInOffsetX = Random.nextInt(-16, 16)
            burnInOffsetY = Random.nextInt(-16, 16)
            delay(30000L)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureBlack)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDismiss() }
                )
            }
            .clickable { onDismiss() } // Single tap fallback for easy wake
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(burnInOffsetX, burnInOffsetY) },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.Bedtime,
                contentDescription = null,
                tint = SleepFaintText,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Large Subtle Clock (Light weight, low brightness)
            Text(
                text = currentTimeStr,
                color = SleepDimText,
                fontSize = 64.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(8.dp))

            val timerText = if (timerState.isRunning) {
                val mins = (timerState.remainingMillis / 60000).coerceAtLeast(1)
                "$currentTitle · ${mins}분 후 꺼짐"
            } else {
                currentTitle
            }

            Text(
                text = timerText,
                color = SleepFaintText,
                fontSize = 13.sp
            )
        }

        Text(
            text = "화면을 두 번 탭하면 돌아가요",
            color = Color(0xFF33354A),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        )
    }
}
