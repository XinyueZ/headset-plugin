package io.hppi.viewmodels

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import io.hppi.BuildConfig

class MainViewModel(app: Application) : AndroidViewModel(app) {
    val appVersion =
        ObservableField("v${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}")

    fun setup() {
    }

    fun shareApp(shareText: String) {
    }
}
