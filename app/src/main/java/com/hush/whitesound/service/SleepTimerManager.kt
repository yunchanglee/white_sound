package com.hush.whitesound.service

import com.hush.whitesound.data.model.SleepTimerState
import com.hush.whitesound.data.model.TimerMode
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar
import kotlin.math.ln

class SleepTimerManager(
    private val scope: CoroutineScope,
    private val onFadeMultiplierChanged: (Float) -> Unit,
    private val onTimerFinished: () -> Unit
) {
    private val _timerState = MutableStateFlow(SleepTimerState())
    val timerState: StateFlow<SleepTimerState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    fun startDurationTimer(
        durationMinutes: Int,
        isFadeOutEnabled: Boolean = true,
        fadeOutSeconds: Int = 60
    ) {
        val durationMillis = durationMinutes * 60 * 1000L
        val endEpoch = System.currentTimeMillis() + durationMillis
        startInternal(
            mode = TimerMode.DURATION,
            endEpoch = endEpoch,
            isFadeOutEnabled = isFadeOutEnabled,
            fadeOutDurationMillis = fadeOutSeconds * 1000L
        )
    }

    fun startEndTimeTimer(
        targetHour: Int, // 0..23
        targetMinute: Int, // 0..59
        isFadeOutEnabled: Boolean = true,
        fadeOutSeconds: Int = 60
    ) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1) // 내일로 자동 계산
        }
        startInternal(
            mode = TimerMode.END_TIME,
            endEpoch = target.timeInMillis,
            isFadeOutEnabled = isFadeOutEnabled,
            fadeOutDurationMillis = fadeOutSeconds * 1000L
        )
    }

    fun extend15Minutes() {
        val current = _timerState.value
        if (!current.isRunning) {
            startDurationTimer(15, current.isFadeOutEnabled, (current.fadeOutDurationMillis / 1000).toInt())
            return
        }
        val newEndEpoch = current.endEpochMillis + (15 * 60 * 1000L)
        _timerState.value = current.copy(
            endEpochMillis = newEndEpoch,
            remainingMillis = newEndEpoch - System.currentTimeMillis()
        )
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        onFadeMultiplierChanged(1.0f)
        _timerState.value = SleepTimerState(isRunning = false, mode = TimerMode.INFINITE)
    }

    private fun startInternal(
        mode: TimerMode,
        endEpoch: Long,
        isFadeOutEnabled: Boolean,
        fadeOutDurationMillis: Long
    ) {
        timerJob?.cancel()
        onFadeMultiplierChanged(1.0f)

        _timerState.value = SleepTimerState(
            isRunning = true,
            mode = mode,
            endEpochMillis = endEpoch,
            remainingMillis = (endEpoch - System.currentTimeMillis()).coerceAtLeast(0L),
            fadeOutDurationMillis = fadeOutDurationMillis,
            isFadeOutEnabled = isFadeOutEnabled
        )

        timerJob = scope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                val remaining = endEpoch - now

                if (remaining <= 0) {
                    _timerState.value = _timerState.value.copy(
                        isRunning = false,
                        remainingMillis = 0L
                    )
                    onFadeMultiplierChanged(0.0f)
                    onTimerFinished()
                    break
                }

                // Fade-out volume multiplier calculation (Logarithmic / smooth curve)
                if (isFadeOutEnabled && remaining <= fadeOutDurationMillis && fadeOutDurationMillis > 0) {
                    val progress = remaining.toFloat() / fadeOutDurationMillis.toFloat() // 1.0 down to 0.0
                    // Log curve feels more natural to human ears
                    val logMult = (ln(progress * 9f + 1f) / ln(10f)).coerceIn(0f, 1f)
                    onFadeMultiplierChanged(logMult)
                } else {
                    onFadeMultiplierChanged(1.0f)
                }

                _timerState.value = _timerState.value.copy(remainingMillis = remaining)
                delay(1000L)
            }
        }
    }
}
