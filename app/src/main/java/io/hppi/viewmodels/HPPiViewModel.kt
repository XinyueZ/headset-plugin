package io.hppi.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.common.api.GoogleApiClient
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.apple.eawt.Application.getApplication



interface IHPPiViewModel {
    val googleApiClient: GoogleApiClient
}

abstract class HPPiViewModel(app: Application) : AndroidViewModel(app), IHPPiViewModel {
    private lateinit var _googleApiClient: GoogleApiClient

    override val googleApiClient: GoogleApiClient
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    init {
       _googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(
                this /* FragmentActivity */,
                this /* OnConnectionFailedListener */
            )
            .addApi(Awareness.API)
            .build()
    }
}