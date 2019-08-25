package io.hppi.repositories

import android.app.Application
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.FenceClient
import com.google.android.gms.awareness.SnapshotClient
import com.google.android.gms.awareness.fence.FenceState
import com.google.android.gms.awareness.fence.FenceUpdateRequest
import com.google.android.gms.awareness.fence.HeadphoneFence
import com.google.android.gms.awareness.state.HeadphoneState
import io.hppi.FENCE_HEADPHONE_ACTION
import io.hppi.FENCE_KEY_HEADPHONE

interface IHeadphoneStateRepository {
    val fenceClient: FenceClient
    val snapshotClient: SnapshotClient
}

class HeadphoneStateRepository(private val context: Context) : BroadcastReceiver(),
    IHeadphoneStateRepository {
    private val headphoneFence = HeadphoneFence.during(HeadphoneState.PLUGGED_IN)

    private val intent = Intent(FENCE_HEADPHONE_ACTION);
    private val pendingIntent: PendingIntent
        get() =
            PendingIntent.getBroadcast(context, 0, intent, 0);

    private val fenceHeadphoneActionFilter = IntentFilter(FENCE_HEADPHONE_ACTION)

    override val fenceClient: FenceClient
        get() = Awareness.getFenceClient(context)

    override val snapshotClient: SnapshotClient
        get() = Awareness.getSnapshotClient(context)

    fun setup() {
        context.registerReceiver(
            this,
            fenceHeadphoneActionFilter
        )

        snapshotClient.headphoneState.addOnSuccessListener { headphoneStateResponse ->
            val headphoneState = headphoneStateResponse.headphoneState
            val pluggedIn = headphoneState.state == HeadphoneState.PLUGGED_IN
            Toast.makeText(context, "HPPi pluggedIn: $pluggedIn", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exp ->
            Log.e("HPPi", "Fence could not get snapshot: $exp");
        }

        fenceClient.updateFences(
            FenceUpdateRequest.Builder()
                .addFence(FENCE_KEY_HEADPHONE, headphoneFence, pendingIntent)
                .build()
        ).addOnFailureListener { exp ->
            Log.e("HPPi", "Fence could not be registered: $exp");
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (!TextUtils.equals(
                FENCE_HEADPHONE_ACTION,
                intent.action
            )
        ) return

        val fenceState: FenceState = FenceState.extract(intent)

        if (TextUtils.equals(
                fenceState.fenceKey,
                FENCE_KEY_HEADPHONE
            )
        ) {
            val fenceStateStr: String = when (fenceState.currentState) {
                FenceState.TRUE -> "true"
                FenceState.FALSE -> "false"
                FenceState.UNKNOWN -> "unknown"
                else -> "unknown value"
            }
            Toast.makeText(context, "Headphone fence state: $fenceStateStr", Toast.LENGTH_SHORT)
                .show()
        }
    }
}