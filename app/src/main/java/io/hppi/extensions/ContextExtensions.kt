package io.hppi.extensions

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.ShareCompat
import androidx.core.content.getSystemService
import io.hppi.ui.MainActivity

const val NOTIFY_ID: Int = 0x12
const val NOTIFY_CHANNEL_ID: String = "hppi channel"
const val NOTIFY_CHANNEL_NAME: String = NOTIFY_CHANNEL_ID

fun Context.showNotification(message: String, @DrawableRes icon: Int, isOngoing: Boolean = true) {
    val drawable: Drawable? = AppCompatResources.getDrawable(this, icon)
    val largeNotificationImage: Bitmap? = drawable?.let { dr ->
        Bitmap.createBitmap(
            dr.intrinsicWidth,
            dr.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        ).apply {
            val canvas = Canvas(this)
            dr.setBounds(0, 0, canvas.width, canvas.height)
            dr.draw(canvas)
        }
    }

    val ii = Intent(this, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(this, 0, ii, 0)

    val builder = NotificationCompat.Builder(this, NOTIFY_CHANNEL_ID)
    builder
        .setContentText(message)
        .setContentIntent(pendingIntent)
        .setWhen(System.currentTimeMillis())
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(icon)
        .setContentTitle(getString(io.hppi.R.string.app_name))

    if (largeNotificationImage != null) {
        builder.setLargeIcon(largeNotificationImage)
    }

    if (isOngoing) {
        builder.setOngoing(isOngoing)
    } else {
        builder.setOngoing(false)
    }

    if (VERSION.SDK_INT >= VERSION_CODES.O) {
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

fun Context.clearNotification() {
    getSystemService<NotificationManager>()?.cancel(NOTIFY_ID)
}

fun Context.shareCompat(shareText: String) {
    val shareIntent = ShareCompat.IntentBuilder.from(this as Activity)
        .setText(shareText)
        .setType("text/plain")
        .createChooserIntent()
        .apply {
            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                // If we're on Lollipop, we can open the intent as a document
                addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            } else {
                // Else, we will use the old CLEAR_WHEN_TASK_RESET flag
                @Suppress("DEPRECATION")
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
            }
        }
    startActivity(shareIntent)
}
