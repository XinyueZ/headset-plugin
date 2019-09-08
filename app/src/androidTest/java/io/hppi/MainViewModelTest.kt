package io.hppi

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.hppi.domains.ISetupApp
import io.hppi.domains.IWordingTranslator
import io.hppi.utils.getLiveDataValue
import io.hppi.utils.getRandomString
import io.hppi.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var context: Application
    private lateinit var setupApp: ISetupApp
    private lateinit var wordingTranslator: IWordingTranslator
    private lateinit var translated: String
    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)

        context =
            InstrumentationRegistry.getInstrumentation().targetContext.applicationContext as Application
        setupApp = mock()
        wordingTranslator = mock()

        translated = getRandomString()

        mainViewModel = MainViewModel(
            context,
            setupApp,
            wordingTranslator
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun shouldProcessDescriptionAndEchoUI() = runBlocking {
        whenever(
            wordingTranslator.translateText(any())
        ).thenReturn(translated)

        assertThat(mainViewModel.descriptionProcessed.get()).isFalse()
        mainViewModel.processDescription()

        delay(200)

        assertThat(mainViewModel.appDescription.get()).isEqualTo(translated)
        assertThat(mainViewModel.descriptionProcessed.get()).isTrue()
    }

    @Test
    fun shouldShareWithText() {
        val shareText = getRandomString()
        mainViewModel.shareApp(shareText)
        assertThat(getLiveDataValue(mainViewModel.onShareApp).peek())
            .isEqualTo(shareText)
    }

    @Test
    fun shouldBeDone() = runBlocking {
        whenever(
            wordingTranslator.translateText(any())
        ).thenReturn(translated)

        mainViewModel.setup(true)

        assertThat(getLiveDataValue(mainViewModel.onDone).peek())
            .isEqualTo(Unit)
        delay(200)
        assertThat(getLiveDataValue(mainViewModel.onTrying).peek())
            .isEqualTo(translated)
    }

    @Test
    fun shouldBeAbort() = runBlocking {
        mainViewModel.setup(false)

        assertThat(getLiveDataValue(mainViewModel.onAbort).peek())
            .isEqualTo(Unit)

        assertThat(getLiveDataValue(mainViewModel.onDone))
            .isNull()
        delay(200)
        assertThat(getLiveDataValue(mainViewModel.onTrying))
            .isNull()
    }

    @Test
    fun shouldFinishSetupAfterPlugin() = runBlocking {
        whenever(
            wordingTranslator.translateText(any())
        ).thenReturn(translated)

        mainViewModel.onPlugIn()
        delay(200)
        assertThat(getLiveDataValue(mainViewModel.onTestFinished).peek())
            .isEqualTo(translated)
    }
}
