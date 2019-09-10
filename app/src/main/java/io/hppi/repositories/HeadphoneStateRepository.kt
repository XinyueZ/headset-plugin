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
import com.google.android.gms.awareness.snapshot.HeadphoneStateResponse
import com.google.android.gms.awareness.state.HeadphoneState
import com.google.android.gms.tasks.Task
import io.hppi.BuildConfig
import io.hppi.R
import io.hppi.domains.AppWordingTranslator
import io.hppi.domains.HeadphoneStateListener
import io.hppi.domains.IWordingTranslator
import io.hppi.extensions.clearNotification
import io.hppi.extensions.showNotification
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val FENCE_KEY_HEADPHONE = "fence_key_headphone"
const val FENCE_HEADPHONE_ACTION = BuildConfig.APPLICATION_ID + "FENCE_HEADPHONE_ACTION"

interface IHeadphoneStateRepository {
    val fenceClient: FenceClient
    val snapshotClient: SnapshotClient
    fun setup(context: Context): Job
    fun tearDown(context: Context)
}

class HeadphoneStateRepository private constructor(
    context: Context,
    wordingTranslator: IWordingTranslator = AppWordingTranslator
) :
    BroadcastReceiver(),
    IHeadphoneStateRepository,
    IWordingTranslator by wordingTranslator,
    CoroutineScope {

    private val job: CompletableJob = Job()
    override val coroutineContext: CoroutineContext get() = Dispatchers.IO + job

    var headphoneStateListener: HeadphoneStateListener? = null

    private var plugInMsg = context.getString(R.string.headphone_plug_in_message)

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

    override fun setup(context: Context) = launch {
        Log.i("HPPi", "setup()")
        context.registerReceiver(
            this@HeadphoneStateRepository,
            fenceHeadphoneActionFilter
        )

        val getHeadphoneState: Task<HeadphoneStateResponse> = snapshotClient.headphoneState
        while (!getHeadphoneState.isComplete) continue

        if (getHeadphoneState.isSuccessful) {
            val headphoneStateResponse: HeadphoneStateResponse =
                requireNotNull(getHeadphoneState.result)
            val headphoneState: HeadphoneState = headphoneStateResponse.headphoneState
            val pluggedIn = headphoneState.state == HeadphoneState.PLUGGED_IN
            when {
                pluggedIn -> {
                    val translated: String = translateText(plugInMsg)
                    context.showNotification(translated, R.drawable.ic_headphone_notification)

                    headphoneStateListener?.onPlugIn()
                }
                else -> {
                    context.clearNotification()
                    headphoneStateListener?.onPlugOut()
                }
            }
        } else {
            Log.e("HPPi", "Fence could not get snapshot: ${getHeadphoneState.exception}")
        }

        val fenceRequest: FenceUpdateRequest = FenceUpdateRequest.Builder()
            .addFence(FENCE_KEY_HEADPHONE, headphoneFence, pendingIntent)
            .build()
        val updateFences: Task<Void> = fenceClient.updateFences(fenceRequest)
        while (!updateFences.isComplete) continue

        if (!getHeadphoneState.isSuccessful) {
            Log.e("HPPi", "Fence could not be registered: ${updateFences.exception}")
        }
    }

    override fun tearDown(context: Context) = context.unregisterReceiver(this)

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
                fenceState.currentState == FenceState.TRUE -> {
                    launch {
                        val translated: String = translateText(plugInMsg)
                        withContext(Dispatchers.Main) {
                            context.showNotification(
                                translated,
                                R.drawable.ic_headphone_notification
                            )
                            headphoneStateListener?.onPlugIn()
                        }
                    }
                }
                fenceState.currentState == FenceState.FALSE -> {
                    context.clearNotification()
                    headphoneStateListener?.onPlugOut()
                }
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
