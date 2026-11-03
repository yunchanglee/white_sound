package com.hush.whitesound.data.model

enum class SoundCategory(val titleKo: String, val colorHex: Long) {
    SLEEP("깊은 잠", 0xFF9C9CFF),
    FOCUS("집중", 0xFF4FD1C5),
    STUDY("공부", 0xFFA8D672)
}

enum class SoundType(val labelKo: String) {
    NATURE("자연"),
    FIRE("불"),
    NOISE("노이즈"),
    FREQUENCY("주파수"),
    AMBIENCE("생활")
}

data class SoundItem(
    val id: String,
    val nameKo: String,
    val categories: Set<SoundCategory>,
    val type: SoundType,
    val isProcedural: Boolean,
    val assetFileName: String? = null,
    val description: String,
    val requiresHeadphones: Boolean = false,
    val isBuiltIn: Boolean = true,
    val gradientStart: Long,
    val gradientEnd: Long,
    val licenseNotice: String = "CC0 / Public Domain"
)

data class MixLayer(
    val soundId: String,
    val volume: Float = 0.7f
)

data class SavedMix(
    val id: String,
    val title: String,
    val category: SoundCategory,
    val layers: List<MixLayer>,
    val createdAt: Long = System.currentTimeMillis()
)

data class LastPlaybackState(
    val soundIds: List<String> = listOf("gentle_rain"),
    val volumes: Map<String, Float> = mapOf("gentle_rain" to 0.75f),
    val mixTitle: String? = null,
    val category: SoundCategory = SoundCategory.SLEEP,
    val timerDurationMinutes: Int? = 60,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TimerMode {
    DURATION, // 시간 후 끄기 (예: 60분 후)
    END_TIME, // 종료 시각 지정 (예: 오전 2:30)
    INFINITE  // 끄지 않음
}

data class SleepTimerState(
    val isRunning: Boolean = false,
    val mode: TimerMode = TimerMode.DURATION,
    val endEpochMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val fadeOutDurationMillis: Long = 60_000L, // 기본 1분 페이드아웃
    val isFadeOutEnabled: Boolean = true
)
