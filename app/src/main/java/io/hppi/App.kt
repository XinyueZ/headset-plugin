package io.hppi

import androidx.multidex.MultiDexApplication
import io.hppi.extensions.startHPPiService

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        startHPPiService()
    }
}