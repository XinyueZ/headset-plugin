package io.hppi.domains

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import io.hppi.viewmodels.IS_ACTIVATE_USAGE

interface ISetupApp : HeadphoneStateListener {
    var isActivate: Boolean
    fun setup(isActivate: Boolean)
    fun abort()
    fun done()
}

class SetupApp(context: Context) :
    ISetupApp,
    HeadphoneStateListener by SimpleHeadphoneStateListener {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            IS_ACTIVATE_USAGE,
            Context.MODE_PRIVATE
        )
    }

    override var isActivate: Boolean
        get() = preferences.getBoolean(IS_ACTIVATE_USAGE, false)
        set(newValue) {
            if (newValue == preferences.getBoolean(IS_ACTIVATE_USAGE, false)) return
            preferences.edit {
                putBoolean(IS_ACTIVATE_USAGE, newValue)
            }
        }

    override fun setup(isActivate: Boolean) {
        when (isActivate) {
            true -> done()
            else -> abort()
        }
    }

    override fun abort() {
        isActivate = false
    }

    override fun onPlugIn() {
        isActivate = true
    }

    override fun done() = Unit
}