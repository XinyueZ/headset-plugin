package io.hppi.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.hppi.domains.SetupApp

class MainViewModelFactory(app: Application) :
    ViewModelProvider.AndroidViewModelFactory(app) {

    private val viewModel = MainViewModel(app, SetupApp(app))

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModel as T
    }
}
