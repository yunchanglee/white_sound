package com.hush.whitesound.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.hush.whitesound.data.model.LastPlaybackState
import com.hush.whitesound.data.model.SavedMix
import com.hush.whitesound.data.model.SoundCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hush_user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val WIFI_ONLY_DOWNLOAD = booleanPreferencesKey("wifi_only_download")
        val LAST_SOUND_IDS = stringPreferencesKey("last_sound_ids")
        val LAST_VOLUMES = stringPreferencesKey("last_volumes")
        val LAST_MIX_TITLE = stringPreferencesKey("last_mix_title")
        val LAST_CATEGORY = stringPreferencesKey("last_category")
        val LAST_TIMER_MINUTES = intPreferencesKey("last_timer_minutes")
        val SAVED_MIXES_JSON = stringPreferencesKey("saved_mixes_json")
        val TODAY_FOCUS_MILLIS = longPreferencesKey("today_focus_millis")
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ONBOARDING_COMPLETED] ?: false }

    val isWifiOnlyDownload: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.WIFI_ONLY_DOWNLOAD] ?: true }

    val lastPlaybackState: Flow<LastPlaybackState> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { pref ->
            val soundIdsStr = pref[Keys.LAST_SOUND_IDS] ?: "gentle_rain"
            val soundIds = soundIdsStr.split(",").filter { it.isNotBlank() }
            
            val volumesStr = pref[Keys.LAST_VOLUMES] ?: ""
            val volumeMap = mutableMapOf<String, Float>()
            if (volumesStr.isNotBlank()) {
                volumesStr.split(";").forEach { pair ->
                    val parts = pair.split(":")
                    if (parts.size == 2) {
                        parts[0] to (parts[1].toFloatOrNull() ?: 0.7f)
                        volumeMap[parts[0]] = parts[1].toFloatOrNull() ?: 0.7f
                    }
                }
            } else {
                soundIds.forEach { volumeMap[it] = 0.75f }
            }

            val categoryStr = pref[Keys.LAST_CATEGORY] ?: SoundCategory.SLEEP.name
            val category = try { SoundCategory.valueOf(categoryStr) } catch (_: Exception) { SoundCategory.SLEEP }

            LastPlaybackState(
                soundIds = if (soundIds.isEmpty()) listOf("gentle_rain") else soundIds,
                volumes = volumeMap,
                mixTitle = pref[Keys.LAST_MIX_TITLE],
                category = category,
                timerDurationMinutes = pref[Keys.LAST_TIMER_MINUTES] ?: 60
            )
        }

    val savedMixes: Flow<List<SavedMix>> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { pref ->
            val jsonStr = pref[Keys.SAVED_MIXES_JSON] ?: "[]"
            parseMixesJson(jsonStr)
        }

    val todayFocusMillis: Flow<Long> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.TODAY_FOCUS_MILLIS] ?: 0L }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    suspend fun setWifiOnlyDownload(wifiOnly: Boolean) {
        context.dataStore.edit { it[Keys.WIFI_ONLY_DOWNLOAD] = wifiOnly }
    }

    suspend fun saveLastPlaybackState(state: LastPlaybackState) {
        context.dataStore.edit { pref ->
            pref[Keys.LAST_SOUND_IDS] = state.soundIds.joinToString(",")
            val volumesStr = state.volumes.entries.joinToString(";") { "${it.key}:${it.value}" }
            pref[Keys.LAST_VOLUMES] = volumesStr
            if (state.mixTitle != null) {
                pref[Keys.LAST_MIX_TITLE] = state.mixTitle
            } else {
                pref.remove(Keys.LAST_MIX_TITLE)
            }
            pref[Keys.LAST_CATEGORY] = state.category.name
            pref[Keys.LAST_TIMER_MINUTES] = state.timerDurationMinutes ?: 60
        }
    }

    suspend fun saveMix(mix: SavedMix) {
        context.dataStore.edit { pref ->
            val current = parseMixesJson(pref[Keys.SAVED_MIXES_JSON] ?: "[]").toMutableList()
            current.removeAll { it.id == mix.id }
            current.add(0, mix)
            pref[Keys.SAVED_MIXES_JSON] = serializeMixesJson(current)
        }
    }

    suspend fun deleteMix(mixId: String) {
        context.dataStore.edit { pref ->
            val current = parseMixesJson(pref[Keys.SAVED_MIXES_JSON] ?: "[]").toMutableList()
            current.removeAll { it.id == mixId }
            pref[Keys.SAVED_MIXES_JSON] = serializeMixesJson(current)
        }
    }

    suspend fun addFocusMillis(millis: Long) {
        context.dataStore.edit { pref ->
            val current = pref[Keys.TODAY_FOCUS_MILLIS] ?: 0L
            pref[Keys.TODAY_FOCUS_MILLIS] = current + millis
        }
    }

    private fun parseMixesJson(jsonStr: String): List<SavedMix> {
        val list = mutableListOf<SavedMix>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val id = obj.getString("id")
                val title = obj.getString("title")
                val catStr = obj.optString("category", SoundCategory.SLEEP.name)
                val cat = try { SoundCategory.valueOf(catStr) } catch (_: Exception) { SoundCategory.SLEEP }
                val layersArr = obj.getJSONArray("layers")
                val layers = mutableListOf<com.hush.whitesound.data.model.MixLayer>()
                for (j in 0 until layersArr.length()) {
                    val lObj = layersArr.getJSONObject(j)
                    layers.add(
                        com.hush.whitesound.data.model.MixLayer(
                            soundId = lObj.getString("soundId"),
                            volume = lObj.getDouble("volume").toFloat()
                        )
                    )
                }
                list.add(SavedMix(id = id, title = title, category = cat, layers = layers))
            }
        } catch (_: Exception) {}
        return list
    }

    private fun serializeMixesJson(mixes: List<SavedMix>): String {
        val arr = JSONArray()
        for (m in mixes) {
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("title", m.title)
            obj.put("category", m.category.name)
            val layersArr = JSONArray()
            for (l in m.layers) {
                val lObj = JSONObject()
                lObj.put("soundId", l.soundId)
                lObj.put("volume", l.volume.toDouble())
                layersArr.put(lObj)
            }
            obj.put("layers", layersArr)
            arr.put(obj)
        }
        return arr.toString()
    }
}
