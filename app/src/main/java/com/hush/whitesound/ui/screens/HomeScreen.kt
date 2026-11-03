package com.hush.whitesound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.data.model.LastPlaybackState
import com.hush.whitesound.data.model.MixLayer
import com.hush.whitesound.data.model.SoundCategory
import com.hush.whitesound.data.model.SoundItem
import com.hush.whitesound.data.repository.SoundCatalog
import com.hush.whitesound.ui.components.ResumeCard
import com.hush.whitesound.ui.components.SoundTile
import com.hush.whitesound.ui.theme.*

@Composable
fun HomeScreen(
    lastState: LastPlaybackState,
    activeSoundIds: List<String>,
    selectedCategory: SoundCategory,
    onCategorySelect: (SoundCategory) -> Unit,
    onSoundClick: (SoundItem) -> Unit,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sounds = remember(selectedCategory) {
        SoundCatalog.getSoundsByCategory(selectedCategory)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Greeting Header
        Text(
            text = "좋은 시간 보내세요",
            color = TextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = "오늘도 편안하게 쉼",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Resume Last Playback Card
        ResumeCard(
            lastState = lastState,
            onPlayClick = onResumeClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Category Segmented Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(DarkSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SoundCategory.values().forEach { category ->
                val isSelected = category == selectedCategory
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) DarkSurface2 else Color.Transparent)
                        .clickable { onCategorySelect(category) },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(Color(category.colorHex))
                        )
                        Text(
                            text = category.titleKo,
                            color = if (isSelected) TextPrimary else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sound Grid (2 Columns, 8 Sounds each)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sounds) { sound ->
                val isPlaying = activeSoundIds.contains(sound.id)
                SoundTile(
                    sound = sound,
                    isPlaying = isPlaying,
                    onClick = { onSoundClick(sound) }
                )
            }
        }
    }
}
