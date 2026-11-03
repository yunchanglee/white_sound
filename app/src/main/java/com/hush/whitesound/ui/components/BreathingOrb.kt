package com.hush.whitesound.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hush.whitesound.data.model.SoundType
import com.hush.whitesound.ui.theme.MoonPrimary

@Composable
fun BreathingOrb(
    isPlaying: Boolean,
    soundType: SoundType,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    val currentScale = if (isPlaying) scale else 1.0f
    val currentGlowAlpha = if (isPlaying) glowAlpha else 0.15f

    Box(
        modifier = modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer radial ambient glow
        Canvas(
            modifier = Modifier
                .size(270.dp)
                .scale(currentScale)
        ) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accentColor.copy(alpha = currentGlowAlpha), Color.Transparent),
                    radius = size.width / 2
                )
            )
        }

        // Concentric thin ring
        Box(
            modifier = Modifier
                .size(210.dp)
                .scale(currentScale * 0.98f)
                .border(1.dp, MoonPrimary.copy(alpha = 0.12f), CircleShape)
        )

        // Core pulsating orb
        Box(
            modifier = Modifier
                .size(160.dp)
                .scale(currentScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.85f),
                            Color(0xFF141836)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            val icon = when (soundType) {
                SoundType.NATURE -> Icons.Rounded.WaterDrop
                SoundType.FIRE -> Icons.Rounded.LocalFireDepartment
                SoundType.NOISE -> Icons.Rounded.Equalizer
                SoundType.FREQUENCY -> Icons.Rounded.Bedtime
                SoundType.AMBIENCE -> Icons.Rounded.Bedtime
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MoonPrimary,
                modifier = Modifier.size(54.dp)
            )
        }
    }
}
