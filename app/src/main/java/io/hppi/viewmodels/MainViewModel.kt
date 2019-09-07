package io.hppi.viewmodels

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.hppi.BuildConfig
import io.hppi.R
import io.hppi.domains.AppWordingTranslator
import io.hppi.domains.ISetupApp
import io.hppi.domains.IWordingTranslator
import io.hppi.events.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val IS_ACTIVATE_USAGE = "io.hppi.activate.usage"

class MainViewModel(app: Application, private val defaultSetup: ISetupApp) :
    AndroidViewModel(app),
    ISetupApp,
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

        viewModelScope.launch(Dispatchers.IO) {
            val translated: String =
                translateText(getApplication<Application>().getString(R.string.headphone_plug_in_description))

            withContext(Dispatchers.Main) {
                appDescription.set(translated)
                descriptionProcessed.set(true)
            }
        }
    }

    fun shareApp(shareText: String) {
        _onShareApp.value = Event(shareText)
    }

    override var isActivate: Boolean = defaultSetup.isActivate

    override fun done() {
        defaultSetup.done()
        _onDone.value = Event(Unit)

        viewModelScope.launch(Dispatchers.IO) {
            val translated: String =
                translateText(getApplication<Application>().getString(R.string.headphone_plug_in_test))
            _onTest.postValue(Event(translated))
        }
    }

    override fun abort() {
        defaultSetup.abort()
        _onAbort.value = Event(Unit)
    }

    override fun onPlugIn() {
        defaultSetup.onPlugIn()

        viewModelScope.launch(Dispatchers.IO) {
            val translated: String =
                translateText(getApplication<Application>().getString(R.string.headphone_test_finished))
            _onTestFinished.postValue(Event(translated))
        }
    }
}
