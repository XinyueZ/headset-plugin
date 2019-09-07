package io.hppi

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestDriver
import androidx.work.testing.TestWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.google.common.truth.Truth.assertThat
import io.hppi.services.HPPiWorker
import io.hppi.services.enqueueHPPiWorker
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class HPPiWorkerTest {

    private lateinit var context: Context
    private lateinit var executor: Executor

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        executor = Executors.newSingleThreadExecutor()

        Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build().apply {
                WorkManagerTestInitHelper.initializeTestWorkManager(context, this)
            }
    }

    @Test
    fun shouldHPPiWorkerDoSuccessfully() {
        val worker: HPPiWorker = TestWorkerBuilder<HPPiWorker>(
            context = context,
            executor = executor
        ).build()

        val result: ListenableWorker.Result = worker.doWork()
        assertThat(result).isInstanceOf(ListenableWorker.Result.Success::class.java)
    }

    @Test
    fun testHppiPeriodicWork() {
        val reqAndOps: Pair<WorkRequest, Operation> = context.enqueueHPPiWorker()

        val testDriver: TestDriver? = WorkManagerTestInitHelper.getTestDriver(context)
        reqAndOps.second.result.get()
        testDriver?.setPeriodDelayMet(reqAndOps.first.id)
        testDriver?.setAllConstraintsMet(reqAndOps.first.id)

        val workInfo: WorkInfo =
            WorkManager.getInstance(context).getWorkInfoById(reqAndOps.first.id).get()

        assertThat(workInfo.state).isEqualTo(WorkInfo.State.ENQUEUED)
    }
}
