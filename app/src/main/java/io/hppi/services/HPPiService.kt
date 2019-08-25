package io.hppi.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import io.hppi.repositories.HeadphoneStateRepository

class HPPiService : LifecycleService() {
    private lateinit var headphoneStateRepository: HeadphoneStateRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!::headphoneStateRepository.isInitialized) {
            headphoneStateRepository = HeadphoneStateRepository(this).apply { setup() }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}