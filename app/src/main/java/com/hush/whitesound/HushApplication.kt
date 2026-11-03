package com.hush.whitesound

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.hush.whitesound.data.repository.UserPreferencesRepository
import com.hush.whitesound.service.HushPlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HushApplication : Application() {

    companion object {
        lateinit var instance: HushApplication
            private set
    }

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    private val _serviceInstance = MutableStateFlow<HushPlaybackService?>(null)
    val serviceInstance: StateFlow<HushPlaybackService?> = _serviceInstance.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? HushPlaybackService.LocalBinder
            _serviceInstance.value = binder?.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _serviceInstance.value = null
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesRepository = UserPreferencesRepository(this)

        val intent = Intent(this, HushPlaybackService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
}
