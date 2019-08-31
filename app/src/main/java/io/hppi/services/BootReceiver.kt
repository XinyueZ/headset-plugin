package io.hppi.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.hppi.extensions.showSingleActivity
import io.hppi.ui.MainActivity

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("HPPi", "HPPi detected boot")
        when {
            Intent.ACTION_BOOT_COMPLETED == intent.action -> {
                Intent(context, MainActivity::class.java).showSingleActivity(context)
            }
        }
    }
}