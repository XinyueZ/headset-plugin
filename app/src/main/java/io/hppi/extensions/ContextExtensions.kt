package io.hppi.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import io.hppi.R
import io.hppi.services.HPPiService
import io.hppi.ui.MainActivity

const val NOTIFY_ID: Int = 0x12
const val NOTIFY_CHANNEL_ID: String = "hppi channel"
const val NOTIFY_CHANNEL_NAME: String = NOTIFY_CHANNEL_ID

fun Context.notifyHPPi(message: String) {
    val largeNotificationImage =
        BitmapFactory.decodeResource(resources, R.drawable.ic_headphone_state)

    val ii = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, ii, 0)

    val builder = NotificationCompat.Builder(this, NOTIFY_CHANNEL_ID)
    builder.setOngoing(true)
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setWhen(System.currentTimeMillis())
        .setLargeIcon(largeNotificationImage)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(R.drawable.ic_headphone_state)
        .setContentTitle(getString(R.string.app_name))

    if (VERSION.SDK_INT >= O) {
        val channel = NotificationChannel(
            NOTIFY_CHANNEL_ID,
            NOTIFY_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )
        getSystemService<NotificationManager>()?.createNotificationChannel(channel)
        builder.setChannelId(NOTIFY_CHANNEL_ID)
    }
    getSystemService<NotificationManager>()?.notify(NOTIFY_ID, builder.build())
}

fun Context.clearNotifyHPPI() {
    getSystemService<NotificationManager>()?.cancel(NOTIFY_ID)
}

fun Context.startHPPiService() {
    startService(Intent(this, HPPiService::class.java))
}