package io.hppi.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.hppi.BuildConfig
import io.hppi.R
import io.hppi.domains.AppWordingTranslator
import io.hppi.domains.IWordingTranslator
import io.hppi.events.Event
import io.hppi.repositories.HeadphoneStateListener

const val IS_ACTIVATE_USAGE = "io.hppi.activate.usage"

class MainViewModel(
    app: Application,
    wordingTranslator: IWordingTranslator = AppWordingTranslator
) : AndroidViewModel(app),
    HeadphoneStateListener,
    IWordingTranslator by wordingTranslator {

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

    private val preferences: SharedPreferences by lazy {
        getApplication<Application>().getSharedPreferences(
            IS_ACTIVATE_USAGE,
            Context.MODE_PRIVATE
        )
    }

    var isActivate: Boolean
        get() = preferences.getBoolean(IS_ACTIVATE_USAGE, false)
        set(value) {
            preferences.edit {
                putBoolean(IS_ACTIVATE_USAGE, value)
            }
        }

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

    fun setup(isActivate: Boolean) {
        when (isActivate) {
            true -> done()
            else -> abort()
        }
    }

    private fun done() {
        _onDone.value = Event(Unit)
        translateText(getApplication<Application>().getString(R.string.headphone_plug_in_test)) { translatedText ->
            _onTest.value = Event(translatedText)
        }
    }

    private fun abort() {
        isActivate = false
        _onAbort.value = Event(Unit)
    }

    override fun onPlugIn() {
        isActivate = true
        translateText(getApplication<Application>().getString(R.string.headphone_test_finished)) { translatedText ->
            _onTestFinished.value = Event(translatedText)
        }
    }

    override fun onPlugOut() {
        //TODO
    }

    fun shareApp(shareText: String) {
        _onShareApp.value = Event(shareText)
    }
}
