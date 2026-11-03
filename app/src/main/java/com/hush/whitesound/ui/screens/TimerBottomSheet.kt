package com.hush.whitesound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.data.model.SleepTimerState
import com.hush.whitesound.data.model.TimerMode
import com.hush.whitesound.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TimerBottomSheet(
    timerState: SleepTimerState,
    onStartDurationTimer: (Int, Boolean, Int) -> Unit,
    onStartEndTimeTimer: (Int, Int, Boolean, Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = DarkSheet,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(36.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF4A5080))
            )
        }
    ) {
        var selectedTab by remember { mutableStateOf(0) } // 0: 시간 후, 1: 종료 시각
        var selectedMinutes by remember { mutableStateOf(60) }
        var isFadeOutEnabled by remember { mutableStateOf(true) }
        var fadeOutSeconds by remember { mutableStateOf(60) }

        // End Time Picker states
        var targetHour by remember { mutableStateOf(2) }
        var targetMinute by remember { mutableStateOf(30) }
        var isAm by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "자동 꺼짐",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Tabs (시간 후 끄기 / 종료 시각 지정)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .padding(4.dp)
            ) {
                listOf("시간 후 끄기", "종료 시각 지정").forEachIndexed { idx, tabTitle ->
                    val isSelected = selectedTab == idx
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) DarkSurface2 else Color.Transparent)
                            .clickable { selectedTab = idx },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabTitle,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (selectedTab == 0) {
                // Duration Mode Chips
                val presetMinutes = listOf(15, 30, 45, 60, 90, 120, 240, 480)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presetMinutes.forEach { mins ->
                        val isSelected = selectedMinutes == mins
                        val label = when {
                            mins < 60 -> "${mins}분"
                            mins % 60 == 0 -> "${mins / 60}시간"
                            else -> "${mins / 60.0}시간"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) MoonPrimary else DarkSurface2)
                                .clickable { selectedMinutes = mins }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) MoonOnColor else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calculated End Time Card
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, selectedMinutes)
                }
                val endAmPm = if (calendar.get(Calendar.AM_PM) == Calendar.AM) "오전" else "오후"
                val endH = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
                val endM = calendar.get(Calendar.MINUTE)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface2)
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("종료 예정 시각", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = String.format("%s %d:%02d", endAmPm, endH, endM),
                            color = MoonPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // End Time Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface2)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // AM/PM Toggle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAm) MoonPrimary else Color.Transparent)
                                .clickable { isAm = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("오전", color = if (isAm) MoonOnColor else TextSecondary, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isAm) MoonPrimary else Color.Transparent)
                                .clickable { isAm = false }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("오후", color = if (!isAm) MoonOnColor else TextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Hour Stepper
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "▲",
                            color = TextSecondary,
                            modifier = Modifier.clickable { targetHour = if (targetHour >= 12) 1 else targetHour + 1 }
                        )
                        Text(
                            text = String.format("%02d", targetHour),
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "▼",
                            color = TextSecondary,
                            modifier = Modifier.clickable { targetHour = if (targetHour <= 1) 12 else targetHour - 1 }
                        )
                    }

                    Text(":", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)

                    // Minute Stepper
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "▲",
                            color = TextSecondary,
                            modifier = Modifier.clickable { targetMinute = (targetMinute + 5) % 60 }
                        )
                        Text(
                            text = String.format("%02d", targetMinute),
                            color = TextPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "▼",
                            color = TextSecondary,
                            modifier = Modifier.clickable { targetMinute = if (targetMinute < 5) 55 else targetMinute - 5 }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "해당 시각이 현재보다 이전이면 내일로 자동 예약됩니다.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Fade-out settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("끄기 전 서서히 줄이기", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Switch(
                    checked = isFadeOutEnabled,
                    onCheckedChange = { isFadeOutEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MoonOnColor,
                        checkedTrackColor = MoonPrimary,
                        uncheckedTrackColor = DarkSurface2
                    )
                )
            }

            if (isFadeOutEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(30 to "30초", 60 to "1분", 180 to "3분", 300 to "5분").forEach { (sec, label) ->
                        val isSelected = fadeOutSeconds == sec
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) MoonPrimary else DarkSurface2)
                                .clickable { fadeOutSeconds = sec }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) MoonOnColor else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Button(
                onClick = {
                    if (selectedTab == 0) {
                        onStartDurationTimer(selectedMinutes, isFadeOutEnabled, fadeOutSeconds)
                    } else {
                        val hour24 = if (isAm) (if (targetHour == 12) 0 else targetHour) else (if (targetHour == 12) 12 else targetHour + 12)
                        onStartEndTimeTimer(hour24, targetMinute, isFadeOutEnabled, fadeOutSeconds)
                    }
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoonPrimary,
                    contentColor = MoonOnColor
                )
            ) {
                Text("타이머 시작", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            if (timerState.isRunning) {
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(
                    onClick = {
                        onCancelTimer()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("타이머 끄기", color = TextSecondary, fontSize = 14.sp)
                }
            }
        }
    }
}
