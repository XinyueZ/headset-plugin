package io.hppi.domains

import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import java.util.Locale

typealias TranslatedCallback = (translatedText: String) -> Unit

const val UND = -1

interface IWordingTranslator {
    var sourceLanguageId: Int

    fun close()
    fun translateText(text: String, callback: TranslatedCallback)
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
        translator.close()
    }

    override fun translateText(text: String, callback: TranslatedCallback) {
        sourceLanguageId =
            FirebaseTranslateLanguage.languageForLanguageCode(Locale.getDefault().language) ?: UND
        translator.downloadModelIfNeeded().addOnSuccessListener {
            translator.translate(text)
                .addOnSuccessListener { translatedDescription ->
                    callback(translatedDescription)
                }
        }
    }
}
