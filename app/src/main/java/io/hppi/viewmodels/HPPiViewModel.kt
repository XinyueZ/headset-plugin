package io.hppi.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.FenceClient
import com.google.android.gms.awareness.SnapshotClient

interface IHPPiViewModel {
    val fenceClient: FenceClient
    val snapshotClient: SnapshotClient
}

abstract class HPPiViewModel(app: Application) :
    AndroidViewModel(app),
    IHPPiViewModel {

    override val fenceClient: FenceClient
        get() = Awareness.getFenceClient(getApplication() as Application)

    override val snapshotClient: SnapshotClient
        get() = Awareness.getSnapshotClient(getApplication() as Application)
}
