package io.hppi.services

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.Worker
import androidx.work.WorkerParameters
import io.hppi.repositories.HeadphoneStateRepository

class HPPiWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        HeadphoneStateRepository.getInstance(applicationContext).setup(applicationContext)
        return Result.success()
    }
}

fun Context.enqueueHPPiWorker(): HeadphoneStateRepository {
    val request: WorkRequest = OneTimeWorkRequest.Builder(HPPiWorker::class.java).build()
    WorkManager.getInstance(applicationContext).enqueue(request)
    return HeadphoneStateRepository.getInstance(applicationContext)
}