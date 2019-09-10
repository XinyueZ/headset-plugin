package io.hppi.domains

import com.google.android.gms.tasks.Task
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.Locale

const val UND = -1

interface IWordingTranslator {
    var sourceLanguageId: Int

    fun close()
    suspend fun translateText(text: String): String
}

object AppWordingTranslator : IWordingTranslator {

    private var _sourceLanguageId: Int = UND

    private val translator: FirebaseTranslator by lazy {
        val options = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(sourceLanguageId)
            .build()
        FirebaseNaturalLanguage.getInstance().getTranslator(options)
    }

    override var sourceLanguageId: Int
        get() = _sourceLanguageId
        set(value) {
            _sourceLanguageId = value
        }

    override fun close() {
        if (sourceLanguageId > 0) {
            translator.close()
        }
    }

    override suspend fun translateText(text: String): String {
        if (Locale.getDefault().displayLanguage == "English") {
            return text
        }

        sourceLanguageId =
            FirebaseTranslateLanguage.languageForLanguageCode(Locale.getDefault().language) ?: UND

        val download: Task<Void> = translator.downloadModelIfNeeded()
        while (!download.isComplete) continue

        val translate: Task<String> = translator.translate(text)
        while (!translate.isComplete) continue

        return if (translate.isSuccessful) {
            translate.result ?: text
        } else {
            text
        }
    }
}
