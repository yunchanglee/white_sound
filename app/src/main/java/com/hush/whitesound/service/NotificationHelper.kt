package com.hush.whitesound.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat as MediaNotificationCompat
import com.hush.whitesound.R
import com.hush.whitesound.ui.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "hush_playback_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_PLAY = "com.hush.whitesound.ACTION_PLAY"
        const val ACTION_PAUSE = "com.hush.whitesound.ACTION_PAUSE"
        const val ACTION_EXTEND_15 = "com.hush.whitesound.ACTION_EXTEND_15"
        const val ACTION_STOP = "com.hush.whitesound.ACTION_STOP"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_playback_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.channel_playback_desc)
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun buildNotification(
        title: String,
        subtitle: String,
        isPlaying: Boolean,
        hasActiveTimer: Boolean
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Select small status bar icon based on timer active state
        val smallIconRes = if (hasActiveTimer) {
            R.drawable.ic_stat_timer
        } else {
            R.drawable.ic_stat_playing
        }

        val playPauseAction = if (isPlaying) {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_pause,
                context.getString(R.string.action_pause),
                createServicePendingIntent(ACTION_PAUSE, 1)
            ).build()
        } else {
            NotificationCompat.Action.Builder(
                android.R.drawable.ic_media_play,
                context.getString(R.string.action_play),
                createServicePendingIntent(ACTION_PLAY, 2)
            ).build()
        }

        val extend15Action = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_recent_history,
            context.getString(R.string.action_extend_15),
            createServicePendingIntent(ACTION_EXTEND_15, 3)
        ).build()

        val stopAction = NotificationCompat.Action.Builder(
            android.R.drawable.ic_menu_close_clear_cancel,
            context.getString(R.string.action_stop),
            createServicePendingIntent(ACTION_STOP, 4)
        ).build()

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconRes)
            .setContentTitle(title)
            .setContentText(subtitle)
            .setContentIntent(contentIntent)
            .setOngoing(isPlaying)
            .setOnlyAlertOnce(true)
            .addAction(extend15Action)
            .addAction(playPauseAction)
            .addAction(stopAction)
            .setStyle(
                MediaNotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .build()
    }

    private fun createServicePendingIntent(actionStr: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, HushPlaybackService::class.java).apply {
            action = actionStr
        }
        return PendingIntent.getService(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
