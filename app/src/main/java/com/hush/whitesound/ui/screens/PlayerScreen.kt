package com.hush.whitesound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeDown
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.data.model.MixLayer
import com.hush.whitesound.data.model.SleepTimerState
import com.hush.whitesound.data.model.SoundType
import com.hush.whitesound.data.repository.SoundCatalog
import com.hush.whitesound.ui.components.BreathingOrb
import com.hush.whitesound.ui.theme.*

@Composable
fun PlayerScreen(
    currentLayers: List<MixLayer>,
    isPlaying: Boolean,
    timerState: SleepTimerState,
    masterVolume: Float,
    onPlayPauseClick: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onOpenTimerClick: () -> Unit,
    onOpenMixerClick: () -> Unit,
    onOpenSleepScreenClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstSound = currentLayers.firstOrNull()?.let { SoundCatalog.getSoundById(it.soundId) }
        ?: SoundCatalog.allSounds.first()

    val title = if (currentLayers.size > 1) {
        val names = currentLayers.mapNotNull { SoundCatalog.getSoundById(it.soundId)?.nameKo }
        names.joinToString(" + ")
    } else {
        firstSound.nameKo
    }

    val subtitle = if (currentLayers.size > 1) {
        "${currentLayers.size}개 사운드 믹스"
    } else {
        "${firstSound.type.labelKo} · 내장 사운드"
    }

    val accentColor = Color(firstSound.gradientStart)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2D3A75),
                        Color(0xFF141836),
                        DarkBg
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Top Bar with large touch targets and status bar clearance
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Generous 48dp touch target for closing / sliding down player
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "화면 내리기",
                        tint = TextPrimary,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Text(
                    text = firstSound.categories.firstOrNull()?.titleKo ?: "깊은 잠",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onOpenSleepScreenClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Bedtime,
                        contentDescription = "수면 모드",
                        tint = TextPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Breathing Orb Visualizer
            BreathingOrb(
                isPlaying = isPlaying,
                soundType = firstSound.type,
                accentColor = accentColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Sound Title & Subtitle
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Timer Status Chip
            val timerChipText = if (timerState.isRunning) {
                val mins = (timerState.remainingMillis / 60000).coerceAtLeast(1)
                val secs = ((timerState.remainingMillis % 60000) / 1000)
                String.format("%02d:%02d 후 꺼짐", mins, secs)
            } else {
                "타이머 설정"
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MoonPrimary.copy(alpha = 0.12f))
                    .clickable { onOpenTimerClick() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Timer,
                        contentDescription = null,
                        tint = MoonPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = timerChipText,
                        color = MoonPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Main Playback Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Timer Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenTimerClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(DarkSurface2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Timer,
                            contentDescription = "타이머",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("타이머", color = TextSecondary, fontSize = 12.sp)
                }

                // Primary Play/Pause FAB Button (80dp)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MoonPrimary)
                        .clickable { onPlayPauseClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (isPlaying) "일시정지" else "재생",
                        tint = MoonOnColor,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Mixer Button
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onOpenMixerClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(DarkSurface2),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tune,
                            contentDescription = "믹스",
                            tint = TextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("믹스", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Volume Slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeDown,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
                Slider(
                    value = masterVolume,
                    onValueChange = onVolumeChange,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MoonPrimary,
                        activeTrackColor = MoonPrimary,
                        inactiveTrackColor = DarkSurface2
                    )
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.VolumeUp,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
