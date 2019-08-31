package io.hppi.repositories

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.FenceClient
import com.google.android.gms.awareness.SnapshotClient
import com.google.android.gms.awareness.fence.FenceState
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.HeadphoneFence
import com.google.android.gms.awareness.state.HeadphoneState
import io.hppi.BuildConfig
import io.hppi.R
import io.hppi.extensions.clearNotifyHPPI
import io.hppi.extensions.notifyHPPi

const val FENCE_KEY_HEADPHONE = "fence_key_headphone"
const val FENCE_HEADPHONE_ACTION = BuildConfig.APPLICATION_ID + "FENCE_HEADPHONE_ACTION"

interface IHeadphoneStateRepository {
    val fenceClient: FenceClient
    val snapshotClient: SnapshotClient
}

class HeadphoneStateRepository(context: Context) : BroadcastReceiver(),
    IHeadphoneStateRepository {

    private var notifyMsg = context.getString(R.string.headphone_plug_in_message)

    private val headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN)

    private val intent = Intent(FENCE_HEADPHONE_ACTION)
    private val pendingIntent: PendingIntent by lazy {
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            0
        )
    }

    private val fenceHeadphoneActionFilter = IntentFilter(FENCE_HEADPHONE_ACTION)

    override val fenceClient: FenceClient by lazy { Awareness.getFenceClient(context) }

    override val snapshotClient: SnapshotClient by lazy { Awareness.getSnapshotClient(context) }

    fun setup(context: Context) {
        Log.i("HPPi", "setup()")
        context.registerReceiver(
            this,
            fenceHeadphoneActionFilter
        )

        snapshotClient.headphoneState.addOnSuccessListener { headphoneStateResponse ->
            val headphoneState = headphoneStateResponse.headphoneState
            val pluggedIn = headphoneState.state == HeadphoneState.PLUGGED_IN
            when {
                pluggedIn -> context.notifyHPPi(notifyMsg)
                else -> context.clearNotifyHPPI()
            }
        }.addOnFailureListener { exp ->
            Log.e("HPPi", "Fence could not get snapshot: $exp")
        }

        fenceClient.updateFences(
            FenceUpdateRequest.Builder()
                .addFence(FENCE_KEY_HEADPHONE, headphoneFence, pendingIntent)
                .build()
        ).addOnFailureListener { exp ->
            Log.e("HPPi", "Fence could not be registered: $exp")
        }
    }

    fun tearDown(context: Context) = context.unregisterReceiver(this)

    override fun onReceive(context: Context, intent: Intent) {
        if (!TextUtils.equals(
                FENCE_HEADPHONE_ACTION,
                intent.action
            )
        ) return

        Log.i("HPPi", "receive()")
        val fenceState: FenceState = FenceState.extract(intent)

        if (TextUtils.equals(
                fenceState.fenceKey,
                FENCE_KEY_HEADPHONE
            )
        ) {
            when {
                fenceState.currentState == FenceState.TRUE -> context.notifyHPPi(notifyMsg)
                fenceState.currentState == FenceState.FALSE -> context.clearNotifyHPPI()
                else -> {
                    Log.e("HPPi", "Headphone fence state: unknown")
                }
            }
        }
    }

    companion object {

        @Volatile
        private var instance: HeadphoneStateRepository? = null

        fun getInstance(context: Context): HeadphoneStateRepository {
            return instance ?: synchronized(this) {
                instance ?: HeadphoneStateRepository(context).also { instance = it }
            }
        }
    }
}