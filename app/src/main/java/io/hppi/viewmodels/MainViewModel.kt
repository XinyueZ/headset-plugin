package io.hppi.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hppi.BuildConfig
import io.hppi.R
import io.hppi.domains.AppWordingTranslator
import io.hppi.domains.ISetupApp
import io.hppi.domains.IWordingTranslator
import io.hppi.domains.SetupApp
import io.hppi.events.Event

const val IS_ACTIVATE_USAGE = "io.hppi.activate.usage"

class MainViewModel(app: Application, private val setupAppDelegate: SetupApp) :
    AndroidViewModel(app),
    ISetupApp by setupAppDelegate,
    IWordingTranslator by AppWordingTranslator {

    val appVersion = "v${BuildConfig.VERSION_NAME}+${BuildConfig.VERSION_CODE}"
    val appDescription =
        ObservableField<String>(getApplication<Application>().getString(R.string.headphone_plug_in_description))
    val descriptionProcessed = ObservableBoolean()

    private val _onAbort = MutableLiveData<Event<Unit>>()
    val onAbort: LiveData<Event<Unit>> = _onAbort
    private val _onDone = MutableLiveData<Event<Unit>>()
    val onDone: LiveData<Event<Unit>> = _onDone
    private val _onShareApp = MutableLiveData<Event<String>>()
    val onShareApp: LiveData<Event<String>> = _onShareApp
    private val _onTest = MutableLiveData<Event<String>>()
    val onTest: LiveData<Event<String>> = _onTest
    private val _onTestFinished = MutableLiveData<Event<String>>()
    val onTestFinished: LiveData<Event<String>> = _onTestFinished

    override fun onCleared() {
        close()
        super.onCleared()
    }

    fun processDescription() {
        descriptionProcessed.set(false)
        translateText(getApplication<Application>().getString(R.string.headphone_plug_in_description)) { translatedText ->
            appDescription.set(translatedText)
            descriptionProcessed.set(true)
        }
    }

    override fun done() {
        setupAppDelegate.done()

        _onDone.value = Event(Unit)
        translateText(getApplication<Application>().getString(R.string.headphone_plug_in_test)) { translatedText ->
            _onTest.value = Event(translatedText)
        }
    }

    override fun abort() {
        setupAppDelegate.abort()

        _onAbort.value = Event(Unit)
    }

    override fun onPlugIn() {
        setupAppDelegate.onPlugIn()

        translateText(getApplication<Application>().getString(R.string.headphone_test_finished)) { translatedText ->
            _onTestFinished.value = Event(translatedText)
        }
    }

    fun shareApp(shareText: String) {
        _onShareApp.value = Event(shareText)
    }
}
