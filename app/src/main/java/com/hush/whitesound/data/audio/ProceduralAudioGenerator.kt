package com.hush.whitesound.data.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

class ProceduralAudioTrack(
    private val soundId: String,
    initialVolume: Float = 0.7f
) {
    private val sampleRate = 44100
    private val isRunning = AtomicBoolean(false)
    private var thread: Thread? = null
    private var track: AudioTrack? = null
    @Volatile private var volume: Float = initialVolume

    fun setVolume(vol: Float) {
        this.volume = vol.coerceIn(0f, 1f)
        track?.setVolume(this.volume)
    }

    fun start() {
        if (isRunning.getAndSet(true)) return

        val minBufSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_STEREO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val bufferSize = (minBufSize * 2).coerceAtLeast(sampleRate / 4)

        track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build().apply {
                setVolume(volume)
                play()
            }

        thread = Thread {
            generateAudioLoop(bufferSize / 2) // short buffer in samples
        }.apply {
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    fun stop() {
        isRunning.set(false)
        thread?.interrupt()
        thread = null
        try {
            track?.stop()
            track?.release()
        } catch (_: Exception) {}
        track = null
    }

    private fun generateAudioLoop(bufferSamples: Int) {
        val buffer = ShortArray(bufferSamples)
        var sampleIndex = 0L

        // Noise filter state
        var b0 = 0.0; var b1 = 0.0; var b2 = 0.0
        var b3 = 0.0; var b4 = 0.0; var b5 = 0.0; var b6 = 0.0
        var brownLast = 0.0

        while (isRunning.get()) {
            var i = 0
            while (i < bufferSamples) {
                val t = sampleIndex.toDouble() / sampleRate
                var leftSample = 0.0
                var rightSample = 0.0

                when (soundId) {
                    "white_noise" -> {
                        val white = Random.nextDouble(-1.0, 1.0) * 0.35
                        leftSample = white
                        rightSample = Random.nextDouble(-1.0, 1.0) * 0.35
                    }
                    "pink_noise" -> {
                        val white = Random.nextDouble(-1.0, 1.0)
                        b0 = 0.99886 * b0 + white * 0.0555179
                        b1 = 0.99332 * b1 + white * 0.0750759
                        b2 = 0.96900 * b2 + white * 0.1538520
                        b3 = 0.86650 * b3 + white * 0.3104856
                        b4 = 0.55000 * b4 + white * 0.5329522
                        b5 = -0.7616 * b5 - white * 0.0168980
                        val pink = b0 + b1 + b2 + b3 + b4 + b5 + b6 + white * 0.5362
                        b6 = white * 0.115926
                        leftSample = pink * 0.08
                        rightSample = leftSample
                    }
                    "brown_noise" -> {
                        val white = Random.nextDouble(-1.0, 1.0)
                        brownLast = (brownLast + (0.02 * white)) / 1.02
                        leftSample = brownLast * 1.5
                        rightSample = leftSample
                    }
                    "delta_binaural_2hz" -> {
                        // Left 100Hz, Right 102Hz (Binaural difference: 2Hz)
                        leftSample = sin(2 * PI * 100.0 * t) * 0.4
                        rightSample = sin(2 * PI * 102.0 * t) * 0.4
                    }
                    "alpha_binaural_10hz" -> {
                        // Left 200Hz, Right 210Hz (Binaural difference: 10Hz)
                        leftSample = sin(2 * PI * 200.0 * t) * 0.35
                        rightSample = sin(2 * PI * 210.0 * t) * 0.35
                    }
                    "beta_binaural_18hz" -> {
                        // Left 250Hz, Right 268Hz (Binaural difference: 18Hz)
                        leftSample = sin(2 * PI * 250.0 * t) * 0.3
                        rightSample = sin(2 * PI * 268.0 * t) * 0.3
                    }
                    "drone_432hz" -> {
                        // Pure warm 432Hz sine + gentle harmonic
                        val fundamental = sin(2 * PI * 432.0 * t) * 0.35
                        val harmonic = sin(2 * PI * 864.0 * t) * 0.07
                        leftSample = fundamental + harmonic
                        rightSample = leftSample
                    }
                    "gamma_tone_40hz" -> {
                        // Isochronic 40Hz amplitude modulation on 400Hz carrier
                        val carrier = sin(2 * PI * 400.0 * t)
                        val mod = (sin(2 * PI * 40.0 * t) + 1.0) * 0.5
                        leftSample = carrier * mod * 0.35
                        rightSample = leftSample
                    }
                    else -> {
                        leftSample = 0.0
                        rightSample = 0.0
                    }
                }

                buffer[i] = (leftSample.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
                buffer[i + 1] = (rightSample.coerceIn(-1.0, 1.0) * 32767).toInt().toShort()
                sampleIndex++
                i += 2
            }
            track?.write(buffer, 0, bufferSamples)
        }
    }
}
