package io.hppi.services

import android.content.Context
import androidx.work.Constraints
import androidx.work.Operation
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.hppi.repositories.HeadphoneStateRepository
import java.util.concurrent.TimeUnit

const val WORKER_REPEAT_MIN = 20L
const val WORKER_REPEAT_FLEX = 10L

class HPPiWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        HeadphoneStateRepository.getInstance(applicationContext).setup(applicationContext)
        return Result.success()
    }
}

fun Context.enqueueHPPiWorker(): Pair<WorkRequest, Operation> {
    HeadphoneStateRepository.getInstance(applicationContext).setup(applicationContext)
    val request: WorkRequest = PeriodicWorkRequest.Builder(
        HPPiWorker::class.java,
        WORKER_REPEAT_MIN, TimeUnit.MINUTES,
        WORKER_REPEAT_FLEX, TimeUnit.MINUTES
    ).setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
        .build()
    return request to WorkManager.getInstance(applicationContext).enqueue(request)
}
