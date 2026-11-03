package com.hush.whitesound.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.hush.whitesound.data.audio.MultiTrackAudioEngine
import com.hush.whitesound.data.model.MixLayer
import com.hush.whitesound.data.model.SleepTimerState
import com.hush.whitesound.data.repository.SoundCatalog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HushPlaybackService : MediaSessionService() {

    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    lateinit var audioEngine: MultiTrackAudioEngine
        private set
    lateinit var timerManager: SleepTimerManager
        private set
    private lateinit var notificationHelper: NotificationHelper
    private var dummyPlayer: ExoPlayer? = null
    private var mediaSession: MediaSession? = null

    private val _currentLayers = MutableStateFlow<List<MixLayer>>(emptyList())
    val currentLayers: StateFlow<List<MixLayer>> = _currentLayers.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var currentTitle: String = "쉼"
    private var currentSubtitle: String = "소리를 재생하는 중"

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
                pausePlayback()
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): HushPlaybackService = this@HushPlaybackService
    }

    override fun onCreate() {
        super.onCreate()
        audioEngine = MultiTrackAudioEngine(this)
        notificationHelper = NotificationHelper(this)

        dummyPlayer = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, dummyPlayer!!).build()

        timerManager = SleepTimerManager(
            scope = serviceScope,
            onFadeMultiplierChanged = { multiplier ->
                audioEngine.setFadeVolumeMultiplier(multiplier)
            },
            onTimerFinished = {
                stopPlayback()
            }
        )

        registerReceiver(
            becomingNoisyReceiver,
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        )

        serviceScope.launch {
            timerManager.timerState.collect { state ->
                updateNotification(state)
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        when (intent?.action) {
            NotificationHelper.ACTION_PLAY -> resumePlayback()
            NotificationHelper.ACTION_PAUSE -> pausePlayback()
            NotificationHelper.ACTION_EXTEND_15 -> timerManager.extend15Minutes()
            NotificationHelper.ACTION_STOP -> stopPlayback()
        }
        return START_NOT_STICKY
    }

    fun playLayers(layers: List<MixLayer>, title: String? = null) {
        _currentLayers.value = layers
        audioEngine.playLayers(layers)
        _isPlaying.value = true

        val soundNames = layers.mapNotNull { SoundCatalog.getSoundById(it.soundId)?.nameKo }
        currentTitle = title ?: soundNames.joinToString(" + ")
        currentSubtitle = if (layers.size > 1) "${layers.size}개 레이어 믹스" else "화이트 노이즈"

        val notification = notificationHelper.buildNotification(
            title = currentTitle,
            subtitle = currentSubtitle,
            isPlaying = true,
            hasActiveTimer = timerManager.timerState.value.isRunning
        )
        startForeground(NotificationHelper.NOTIFICATION_ID, notification)
    }

    fun setTrackVolume(soundId: String, volume: Float) {
        audioEngine.setTrackVolume(soundId, volume)
        _currentLayers.value = _currentLayers.value.map {
            if (it.soundId == soundId) it.copy(volume = volume) else it
        }
    }

    fun setMasterVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
    }

    fun pausePlayback() {
        audioEngine.pause()
        _isPlaying.value = false
        updateNotification(timerManager.timerState.value)
    }

    fun resumePlayback() {
        audioEngine.resume()
        _isPlaying.value = true
        updateNotification(timerManager.timerState.value)
    }

    fun stopPlayback() {
        audioEngine.stop()
        timerManager.cancelTimer()
        _currentLayers.value = emptyList()
        _isPlaying.value = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(timerState: SleepTimerState) {
        if (!_isPlaying.value && _currentLayers.value.isEmpty()) return

        var sub = currentSubtitle
        if (timerState.isRunning) {
            val mins = (timerState.remainingMillis / 60000).coerceAtLeast(1)
            sub = "$currentSubtitle · ${mins}분 후 꺼짐"
        }

        val notification = notificationHelper.buildNotification(
            title = currentTitle,
            subtitle = sub,
            isPlaying = _isPlaying.value,
            hasActiveTimer = timerState.isRunning
        )
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        nm.notify(NotificationHelper.NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        try {
            unregisterReceiver(becomingNoisyReceiver)
        } catch (_: Exception) {}
        audioEngine.stop()
        dummyPlayer?.release()
        mediaSession?.release()
    }
}
