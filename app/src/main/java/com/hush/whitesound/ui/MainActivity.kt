package com.hush.whitesound.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hush.whitesound.HushApplication
import com.hush.whitesound.data.model.LastPlaybackState
import com.hush.whitesound.data.model.MixLayer
import com.hush.whitesound.data.model.SavedMix
import com.hush.whitesound.data.model.SleepTimerState
import com.hush.whitesound.data.model.SoundCategory
import com.hush.whitesound.data.repository.SoundCatalog
import com.hush.whitesound.service.HushPlaybackService
import com.hush.whitesound.ui.screens.*
import com.hush.whitesound.ui.theme.*
import kotlinx.coroutines.launch

enum class MainTab(val title: String, val icon: ImageVector) {
    HOME("홈", Icons.Rounded.Home),
    MIXER("믹스", Icons.Rounded.Layers),
    POMODORO("루틴", Icons.Rounded.Timer),
    SETTINGS("설정", Icons.Rounded.Settings)
}

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            HushTheme {
                val app = HushApplication.instance
                val scope = rememberCoroutineScope()

                // State from preferences
                val isOnboardingCompleted by app.preferencesRepository.isOnboardingCompleted
                    .collectAsState(initial = true)
                val isWifiOnly by app.preferencesRepository.isWifiOnlyDownload
                    .collectAsState(initial = true)
                val lastPlaybackState by app.preferencesRepository.lastPlaybackState
                    .collectAsState(initial = LastPlaybackState())
                val savedMixes by app.preferencesRepository.savedMixes
                    .collectAsState(initial = emptyList())
                val todayFocusMillis by app.preferencesRepository.todayFocusMillis
                    .collectAsState(initial = 0L)

                // Service instance binding
                val serviceNullable by app.serviceInstance.collectAsState()
                val boundService = serviceNullable

                // Collect service flows safely with explicit states
                val currentLayers: List<MixLayer> = boundService?.let { s ->
                    val layersState by s.currentLayers.collectAsState()
                    layersState
                } ?: emptyList()

                val isPlaying: Boolean = boundService?.let { s ->
                    val playingState by s.isPlaying.collectAsState()
                    playingState
                } ?: false

                val timerState: SleepTimerState = boundService?.let { s ->
                    val tState by s.timerManager.timerState.collectAsState()
                    tState
                } ?: SleepTimerState()

                var masterVolume by remember { mutableFloatStateOf(0.85f) }
                var currentTab by rememberSaveable { mutableStateOf(MainTab.HOME) }
                var homeSelectedCategory by rememberSaveable { mutableStateOf(SoundCategory.SLEEP) }
                val tabHistory = remember { mutableStateListOf(MainTab.HOME) }

                var isPlayerOpen by remember { mutableStateOf(false) }
                var isTimerSheetOpen by remember { mutableStateOf(false) }
                var isSleepScreenOpen by remember { mutableStateOf(false) }

                // System Back Button Handling
                BackHandler(
                    enabled = isSleepScreenOpen || isTimerSheetOpen || isPlayerOpen || currentTab != MainTab.HOME
                ) {
                    when {
                        isSleepScreenOpen -> isSleepScreenOpen = false
                        isTimerSheetOpen -> isTimerSheetOpen = false
                        isPlayerOpen -> isPlayerOpen = false
                        currentTab != MainTab.HOME -> {
                            if (tabHistory.size > 1) {
                                tabHistory.removeAt(tabHistory.lastIndex)
                                currentTab = tabHistory.last()
                            } else {
                                currentTab = MainTab.HOME
                            }
                        }
                    }
                }

                fun switchTab(tab: MainTab) {
                    if (currentTab != tab) {
                        tabHistory.add(tab)
                        currentTab = tab
                    }
                }

                fun playLayers(layers: List<MixLayer>, title: String? = null) {
                    boundService?.playLayers(layers, title)
                    val sound = layers.firstOrNull()?.let { SoundCatalog.getSoundById(it.soundId) }
                    scope.launch {
                        app.preferencesRepository.saveLastPlaybackState(
                            LastPlaybackState(
                                soundIds = layers.map { it.soundId },
                                volumes = layers.associate { it.soundId to it.volume },
                                mixTitle = title,
                                category = sound?.categories?.firstOrNull() ?: lastPlaybackState.category,
                                timerDurationMinutes = 60
                            )
                        )
                    }
                }

                if (!isOnboardingCompleted) {
                    OnboardingScreen(
                        isWifiOnly = isWifiOnly,
                        onWifiOnlyToggle = { scope.launch { app.preferencesRepository.setWifiOnlyDownload(it) } },
                        onCompleteOnboarding = { scope.launch { app.preferencesRepository.setOnboardingCompleted(true) } }
                    )
                } else if (isSleepScreenOpen) {
                    val displayTitle = if (currentLayers.size > 1) {
                        currentLayers.mapNotNull { SoundCatalog.getSoundById(it.soundId)?.nameKo }.joinToString(" + ")
                    } else {
                        currentLayers.firstOrNull()?.let { SoundCatalog.getSoundById(it.soundId)?.nameKo } ?: "쉼"
                    }
                    SleepScreen(
                        currentTitle = displayTitle,
                        timerState = timerState,
                        onDismiss = { isSleepScreenOpen = false }
                    )
                } else if (isPlayerOpen) {
                    PlayerScreen(
                        currentLayers = currentLayers,
                        isPlaying = isPlaying,
                        timerState = timerState,
                        masterVolume = masterVolume,
                        onPlayPauseClick = {
                            if (isPlaying) boundService?.pausePlayback() else boundService?.resumePlayback()
                        },
                        onVolumeChange = {
                            masterVolume = it
                            boundService?.setMasterVolume(it)
                        },
                        onOpenTimerClick = { isTimerSheetOpen = true },
                        onOpenMixerClick = {
                            isPlayerOpen = false
                            switchTab(MainTab.MIXER)
                        },
                        onOpenSleepScreenClick = { isSleepScreenOpen = true },
                        onBackClick = { isPlayerOpen = false }
                    )
                } else {
                    Scaffold(
                        bottomBar = {
                            NavigationBar(
                                containerColor = Color(0xFF12162E),
                                tonalElevation = 0.dp
                            ) {
                                MainTab.values().forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { switchTab(tab) },
                                        icon = {
                                            Icon(
                                                imageVector = tab.icon,
                                                contentDescription = tab.title,
                                                tint = if (isSelected) MoonPrimary else TextTertiary
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = tab.title,
                                                color = if (isSelected) MoonPrimary else TextTertiary,
                                                fontSize = 11.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            indicatorColor = Color(0xFF2A3160)
                                        )
                                    )
                                }
                            }
                        }
                    ) { paddingValues ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .padding(paddingValues)
                        ) {
                            when (currentTab) {
                                MainTab.HOME -> {
                                    HomeScreen(
                                        lastState = lastPlaybackState,
                                        activeSoundIds = currentLayers.map { it.soundId },
                                        selectedCategory = homeSelectedCategory,
                                        onCategorySelect = { homeSelectedCategory = it },
                                        onSoundClick = { sound ->
                                            playLayers(listOf(MixLayer(sound.id, 0.75f)))
                                            isPlayerOpen = true
                                        },
                                        onResumeClick = {
                                            val layers = lastPlaybackState.soundIds.map { id ->
                                                MixLayer(id, lastPlaybackState.volumes[id] ?: 0.75f)
                                            }
                                            playLayers(layers, lastPlaybackState.mixTitle)
                                            isPlayerOpen = true
                                        }
                                    )
                                }
                                MainTab.MIXER -> {
                                    MixerScreen(
                                        currentLayers = currentLayers,
                                        savedMixes = savedMixes,
                                        onLayerVolumeChange = { soundId, vol ->
                                            boundService?.setTrackVolume(soundId, vol)
                                        },
                                        onRemoveLayer = { soundId ->
                                            val newLayers = currentLayers.filter { it.soundId != soundId }
                                            playLayers(newLayers)
                                        },
                                        onAddLayer = { sound ->
                                            val newLayers = currentLayers + MixLayer(sound.id, 0.6f)
                                            playLayers(newLayers)
                                        },
                                        onPlayMix = { layers, title ->
                                            playLayers(layers, title)
                                            isPlayerOpen = true
                                        },
                                        onSaveMix = { title, layers ->
                                            val newMix = SavedMix(
                                                id = "mix_${System.currentTimeMillis()}",
                                                title = title,
                                                category = lastPlaybackState.category,
                                                layers = layers
                                            )
                                            scope.launch { app.preferencesRepository.saveMix(newMix) }
                                        },
                                        onDeleteSavedMix = { mixId ->
                                            scope.launch { app.preferencesRepository.deleteMix(mixId) }
                                        },
                                        onBackClick = {
                                            if (tabHistory.size > 1) {
                                                tabHistory.removeAt(tabHistory.lastIndex)
                                                currentTab = tabHistory.last()
                                            } else {
                                                currentTab = MainTab.HOME
                                            }
                                        }
                                    )
                                }
                                MainTab.POMODORO -> {
                                    PomodoroScreen(
                                        todayFocusMillis = todayFocusMillis,
                                        onStartFocusAudio = {
                                            playLayers(listOf(MixLayer("library_ambience", 0.6f), MixLayer("alpha_binaural_10hz", 0.4f)), "도서관 + 알파파 10Hz")
                                        },
                                        onStartBreakAudio = {
                                            playLayers(listOf(MixLayer("forest_creek", 0.7f)), "휴식 · 시냇물 소리")
                                        },
                                        onPauseAudio = {
                                            boundService?.pausePlayback()
                                        },
                                        onBackClick = {
                                            if (tabHistory.size > 1) {
                                                tabHistory.removeAt(tabHistory.lastIndex)
                                                currentTab = tabHistory.last()
                                            } else {
                                                currentTab = MainTab.HOME
                                            }
                                        }
                                    )
                                }
                                MainTab.SETTINGS -> {
                                    SettingsScreen(
                                        isWifiOnly = isWifiOnly,
                                        onWifiOnlyToggle = { scope.launch { app.preferencesRepository.setWifiOnlyDownload(it) } },
                                        onBackClick = {
                                            if (tabHistory.size > 1) {
                                                tabHistory.removeAt(tabHistory.lastIndex)
                                                currentTab = tabHistory.last()
                                            } else {
                                                currentTab = MainTab.HOME
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Global Timer Bottom Sheet
                if (isTimerSheetOpen) {
                    TimerBottomSheet(
                        timerState = timerState,
                        onStartDurationTimer = { mins, fade, fadeSec ->
                            boundService?.timerManager?.startDurationTimer(mins, fade, fadeSec)
                        },
                        onStartEndTimeTimer = { h, m, fade, fadeSec ->
                            boundService?.timerManager?.startEndTimeTimer(h, m, fade, fadeSec)
                        },
                        onCancelTimer = {
                            boundService?.timerManager?.cancelTimer()
                        },
                        onDismiss = { isTimerSheetOpen = false }
                    )
                }
            }
        }
    }
}
