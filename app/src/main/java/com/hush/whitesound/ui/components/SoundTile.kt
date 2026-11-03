package com.hush.whitesound.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.hush.whitesound.data.model.SoundItem
import com.hush.whitesound.data.model.SoundType
import com.hush.whitesound.ui.theme.DarkOutline
import com.hush.whitesound.ui.theme.MoonPrimary
import com.hush.whitesound.ui.theme.TextPrimary
import com.hush.whitesound.ui.theme.TextSecondary

@Composable
fun SoundTile(
    sound: SoundItem,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(112.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(sound.gradientStart),
                        Color(sound.gradientEnd)
                    )
                )
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        // Top Left Icon
        val icon = when (sound.type) {
            SoundType.NATURE -> Icons.Rounded.WaterDrop
            SoundType.FIRE -> Icons.Rounded.LocalFireDepartment
            SoundType.NOISE -> Icons.Rounded.GraphicEq
            SoundType.FREQUENCY -> Icons.Rounded.Waves
            SoundType.AMBIENCE -> Icons.Rounded.Storefront
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(26.dp)
        )

        // Top Right Badges
        Row(
            modifier = Modifier.align(Alignment.TopEnd),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (sound.requiresHeadphones) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text("🎧", fontSize = 10.sp)
                }
            }
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(MoonPrimary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "재생 중",
                        color = Color(0xFF1B1F3B),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom Title and Subtitle
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = sound.nameKo,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            val detail = if (sound.isProcedural) "주파수 · 생성음" else "${sound.type.labelKo} · 내장"
            Text(
                text = detail,
                color = TextSecondary.copy(alpha = 0.85f),
                fontSize = 11.sp
            )
        }
    }
}
