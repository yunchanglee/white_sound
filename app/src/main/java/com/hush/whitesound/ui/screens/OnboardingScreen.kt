package com.hush.whitesound.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hush.whitesound.R
import com.hush.whitesound.ui.theme.*

@Composable
fun OnboardingScreen(
    isWifiOnly: Boolean,
    onWifiOnlyToggle: (Boolean) -> Unit,
    onCompleteOnboarding: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2E3A6B),
                        DarkBg,
                        DarkBg
                    )
                )
            )
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(70.dp))

            // App Icon Graphic
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B1F3B)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Hush App Icon",
                    modifier = Modifier.size(96.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "쉼",
                color = TextPrimary,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "잠들 때, 집중할 때, 공부할 때\n당신 곁에 머무는 소리",
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Sound Pack Status Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("사운드 팩 준비 완료", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("24종 완비", color = MoonPrimary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { 1.0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MoonPrimary,
                        trackColor = DarkSurface2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "✓ 기본 자연/불 사운드 16종 준비 완료\n✓ 주파수 및 노이즈 실시간 생성음 8종 탑재\n✓ 설치 즉시 오프라인 무제한 무료 감상",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DarkOutline)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Rounded.Wifi, contentDescription = null, tint = MoonPrimary, modifier = Modifier.size(18.dp))
                            Text("Wi-Fi에서만 다운로드", color = TextPrimary, fontSize = 13.sp)
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

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onCompleteOnboarding,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MoonPrimary,
                    contentColor = MoonOnColor
                )
            ) {
                Text("지금 바로 듣기", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text("소리는 백그라운드에서도 계속 재생돼요", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
