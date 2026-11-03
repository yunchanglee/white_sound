package com.hush.whitesound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.hush.whitesound.data.model.SavedMix
import com.hush.whitesound.data.model.SoundCategory
import com.hush.whitesound.data.model.SoundItem
import com.hush.whitesound.data.repository.SoundCatalog
import com.hush.whitesound.ui.theme.*

@Composable
fun MixerScreen(
    currentLayers: List<MixLayer>,
    savedMixes: List<SavedMix>,
    onLayerVolumeChange: (String, Float) -> Unit,
    onRemoveLayer: (String) -> Unit,
    onAddLayer: (SoundItem) -> Unit,
    onPlayMix: (List<MixLayer>, String?) -> Unit,
    onSaveMix: (String, List<MixLayer>) -> Unit,
    onDeleteSavedMix: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddSoundDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var mixNameInput by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(horizontal = 20.dp)
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
            Text(
                text = "나만의 믹스",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "저장",
                color = if (currentLayers.isNotEmpty()) MoonPrimary else TextTertiary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(enabled = currentLayers.isNotEmpty()) {
                    mixNameInput = "나만의 믹스 " + (savedMixes.size + 1)
                    showSaveDialog = true
                }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Header info card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF2E3A6B), Color(0xFF3B2B52))
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Text("동시 재생 중", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${currentLayers.size} / 4 레이어",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "원하는 사운드를 조합하여 나만의 수면/집중 환경을 만들어보세요.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Layer List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(currentLayers) { layer ->
                val sound = SoundCatalog.getSoundById(layer.soundId) ?: return@items
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface)
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = sound.nameKo,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${(layer.volume * 100).toInt()}%",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "삭제",
                                    tint = TextSecondary,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clickable { onRemoveLayer(layer.soundId) }
                                )
                            }
                        }
                        Slider(
                            value = layer.volume,
                            onValueChange = { onLayerVolumeChange(layer.soundId, it) },
                            colors = SliderDefaults.colors(
                                thumbColor = MoonPrimary,
                                activeTrackColor = MoonPrimary,
                                inactiveTrackColor = DarkSurface2
                            )
                        )
                    }
                }
            }

            if (currentLayers.size < 4) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface2.copy(alpha = 0.5f))
                            .clickable { showAddSoundDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, tint = MoonPrimary)
                            Text("소리 더하기", color = MoonPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Saved Mixes Section
            if (savedMixes.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("저장된 믹스 목록", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                items(savedMixes) { mix ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurface)
                            .clickable { onPlayMix(mix.layers, mix.title) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(mix.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("${mix.layers.size}개 사운드", color = TextSecondary, fontSize = 11.sp)
                        }
                        IconButton(onClick = { onDeleteSavedMix(mix.id) }) {
                            Icon(Icons.Rounded.DeleteOutline, contentDescription = "삭제", tint = TextTertiary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Play Mix Button
        Button(
            onClick = { onPlayMix(currentLayers, null) },
            enabled = currentLayers.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MoonPrimary,
                contentColor = MoonOnColor
            )
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text("믹스 재생하기", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Add Sound Selection Dialog
    if (showAddSoundDialog) {
        val currentIds = currentLayers.map { it.soundId }.toSet()
        val availableSounds = SoundCatalog.allSounds.filter { !currentIds.contains(it.id) }

        AlertDialog(
            onDismissRequest = { showAddSoundDialog = false },
            containerColor = DarkSheet,
            title = { Text("사운드 추가", color = TextPrimary) },
            text = {
                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(availableSounds) { sound ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAddLayer(sound)
                                    showAddSoundDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(sound.gradientStart)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.MusicNote, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(sound.nameKo, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text(sound.description, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAddSoundDialog = false }) {
                    Text("닫기", color = MoonPrimary)
                }
            }
        )
    }

    // Save Mix Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            containerColor = DarkSheet,
            title = { Text("믹스 저장", color = TextPrimary) },
            text = {
                OutlinedTextField(
                    value = mixNameInput,
                    onValueChange = { mixNameInput = it },
                    label = { Text("믹스 이름") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = MoonPrimary,
                        unfocusedBorderColor = DarkOutline
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (mixNameInput.isNotBlank()) {
                            onSaveMix(mixNameInput.trim(), currentLayers)
                            showSaveDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoonPrimary, contentColor = MoonOnColor)
                ) {
                    Text("저장")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("취소", color = TextSecondary)
                }
            }
        )
    }
}
