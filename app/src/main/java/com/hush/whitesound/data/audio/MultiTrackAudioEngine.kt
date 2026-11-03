package com.hush.whitesound.data.audio

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.hush.whitesound.data.model.MixLayer
import com.hush.whitesound.data.repository.SoundCatalog

class MultiTrackAudioEngine(private val context: Context) {

    private val activePlayers = mutableMapOf<String, ExoPlayer>()
    private val activeProceduralTracks = mutableMapOf<String, ProceduralAudioTrack>()
    private val trackVolumes = mutableMapOf<String, Float>()
    @Volatile private var masterVolume: Float = 1.0f
    @Volatile private var fadeVolumeMultiplier: Float = 1.0f

    var isPlaying: Boolean = false
        private set

    fun playLayers(layers: List<MixLayer>) {
        // Stop removed layers
        val newSoundIds = layers.map { it.soundId }.toSet()
        val currentIds = activePlayers.keys + activeProceduralTracks.keys
        for (id in currentIds - newSoundIds) {
            removeTrack(id)
        }

        // Apply/Add new layers
        for (layer in layers) {
            trackVolumes[layer.soundId] = layer.volume
            val sound = SoundCatalog.getSoundById(layer.soundId) ?: continue
            val effectiveVol = layer.volume * masterVolume * fadeVolumeMultiplier

            if (sound.isProcedural) {
                val existing = activeProceduralTracks[layer.soundId]
                if (existing == null) {
                    val track = ProceduralAudioTrack(layer.soundId, effectiveVol)
                    activeProceduralTracks[layer.soundId] = track
                    track.start()
                } else {
                    existing.setVolume(effectiveVol)
                }
            } else {
                val existing = activePlayers[layer.soundId]
                if (existing == null) {
                    val player = createExoPlayerForAsset(sound.assetFileName ?: "")
                    player.volume = effectiveVol
                    activePlayers[layer.soundId] = player
                    player.play()
                } else {
                    existing.volume = effectiveVol
                    if (!existing.isPlaying) existing.play()
                }
            }
        }
        isPlaying = true
    }

    fun setTrackVolume(soundId: String, volume: Float) {
        trackVolumes[soundId] = volume
        val effectiveVol = volume * masterVolume * fadeVolumeMultiplier
        activePlayers[soundId]?.volume = effectiveVol
        activeProceduralTracks[soundId]?.setVolume(effectiveVol)
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
        updateAllVolumes()
    }

    fun setFadeVolumeMultiplier(multiplier: Float) {
        fadeVolumeMultiplier = multiplier.coerceIn(0f, 1f)
        updateAllVolumes()
    }

    private fun updateAllVolumes() {
        for ((soundId, baseVol) in trackVolumes) {
            val effective = baseVol * masterVolume * fadeVolumeMultiplier
            activePlayers[soundId]?.volume = effective
            activeProceduralTracks[soundId]?.setVolume(effective)
        }
    }

    fun pause() {
        for (player in activePlayers.values) {
            player.pause()
        }
        for (proc in activeProceduralTracks.values) {
            proc.stop()
        }
        isPlaying = false
    }

    fun resume() {
        for ((soundId, baseVol) in trackVolumes) {
            val sound = SoundCatalog.getSoundById(soundId) ?: continue
            val effective = baseVol * masterVolume * fadeVolumeMultiplier
            if (sound.isProcedural) {
                if (activeProceduralTracks[soundId] == null) {
                    val track = ProceduralAudioTrack(soundId, effective)
                    activeProceduralTracks[soundId] = track
                    track.start()
                }
            } else {
                activePlayers[soundId]?.let {
                    it.volume = effective
                    it.play()
                }
            }
        }
        isPlaying = true
    }

    fun stop() {
        for (player in activePlayers.values) {
            player.stop()
            player.release()
        }
        activePlayers.clear()

        for (proc in activeProceduralTracks.values) {
            proc.stop()
        }
        activeProceduralTracks.clear()
        trackVolumes.clear()
        isPlaying = false
    }

    private fun removeTrack(soundId: String) {
        activePlayers.remove(soundId)?.let {
            it.stop()
            it.release()
        }
        activeProceduralTracks.remove(soundId)?.stop()
        trackVolumes.remove(soundId)
    }

    private fun createExoPlayerForAsset(fileName: String): ExoPlayer {
        val player = ExoPlayer.Builder(context).build()
        val mediaUri = Uri.parse("asset:///sounds/$fileName")
        val mediaItem = MediaItem.fromUri(mediaUri)
        player.setMediaItem(mediaItem)
        player.repeatMode = Player.REPEAT_MODE_ONE
        player.prepare()
        return player
    }
}
