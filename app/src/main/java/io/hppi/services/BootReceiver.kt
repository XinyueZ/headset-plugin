package io.hppi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.hppi.extensions.startHPPiService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("HPPi", "HPPi detected boot")
        when {
            Intent.ACTION_BOOT_COMPLETED == intent.action -> {
                context.startHPPiService()
            }
        }
    }
}