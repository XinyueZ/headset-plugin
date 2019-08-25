package io.hppi

import androidx.multidex.MultiDexApplication
import io.hppi.repositories.HeadphoneStateRepository

const val FENCE_KEY_HEADPHONE = "fence_key_headphone";
const val FENCE_HEADPHONE_ACTION = BuildConfig.APPLICATION_ID + "FENCE_HEADPHONE_ACTION"

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        HeadphoneStateRepository(this).setup()
    }
}