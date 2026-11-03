package com.hush.whitesound.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.data.repository.SoundCatalog
import com.hush.whitesound.ui.theme.*

@Composable
fun SettingsScreen(
    isWifiOnly: Boolean,
    onWifiOnlyToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text("설정", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Download Preference Card
            item {
                Text("다운로드 & 네트워크", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(Icons.Rounded.Wifi, contentDescription = null, tint = MoonPrimary)
                            Column {
                                Text("Wi-Fi에서만 다운로드", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("모바일 데이터 요금 발생 방지", color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                        Switch(
                            checked = isWifiOnly,
                            onCheckedChange = onWifiOnlyToggle,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MoonOnColor,
                                checkedTrackColor = MoonPrimary,
                                uncheckedTrackColor = DarkSurface2
                            )
                        )
                    }
                }
            }

            // Free App Policy Card
            item {
                Text("앱 정책", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(DarkSurface)
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = CategoryStudy, modifier = Modifier.size(18.dp))
                            Text("완전 무료 & 광고 없음", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "쉼(Hush)은 잠과 집중에 방해되는 어떠한 전면/배너 광고나 결제 유도 없이 100% 무료로 제공됩니다.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            // Sound Licenses Section
            item {
                Text("사운드 출처 및 오픈 라이선스", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "앱에 수록된 모든 음원은 CC0/Public Domain 공개 라이브러리 및 자체 절차적 합성기를 통해 제공됩니다.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            items(SoundCatalog.allSounds) { sound ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(sound.nameKo, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(sound.type.labelKo, color = MoonPrimary, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(sound.licenseNotice, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            // Version info
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("쉼 (Hush) v1.0.0", color = TextTertiary, fontSize = 12.sp)
                }
            }
        }
    }
}
