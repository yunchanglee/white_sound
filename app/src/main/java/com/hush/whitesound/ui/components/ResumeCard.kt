package com.hush.whitesound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.data.model.LastPlaybackState
import com.hush.whitesound.data.repository.SoundCatalog
import com.hush.whitesound.ui.theme.DarkOutline
import com.hush.whitesound.ui.theme.MoonOnColor
import com.hush.whitesound.ui.theme.MoonPrimary
import com.hush.whitesound.ui.theme.TextPrimary
import java.util.Calendar

@Composable
fun ResumeCard(
    lastState: LastPlaybackState,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val soundNames = lastState.soundIds.mapNotNull { SoundCatalog.getSoundById(it)?.nameKo }
    val displayTitle = lastState.mixTitle ?: (if (soundNames.isEmpty()) "잔잔한 빗소리" else soundNames.joinToString(" + "))

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..10 -> "상쾌한 아침이에요"
        in 11..17 -> "집중할 시간이에요"
        in 18..21 -> "편안한 저녁이에요"
        else -> "오늘도 푹 쉬어요 🌙"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF3B4A85),
                        Color(0xFF5A3F6E)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.78f)
        ) {
            Text(
                text = "이어서 듣기 · $greeting",
                color = Color(0xFFD9DBF5),
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = displayTitle,
                color = TextPrimary,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(lastState.category.colorHex))
                        )
                        Text(
                            text = lastState.category.titleKo,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Timer Chip
                val timerText = if ((lastState.timerDurationMinutes ?: 0) > 0) {
                    "${lastState.timerDurationMinutes}분 후 꺼짐"
                } else {
                    "무한 재생"
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = timerText,
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Play FAB Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(54.dp)
                .clip(CircleShape)
                .background(MoonPrimary)
                .clickable { onPlayClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "재생",
                tint = MoonOnColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}
