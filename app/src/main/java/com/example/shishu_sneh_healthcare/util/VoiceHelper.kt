package com.example.shishu_sneh_healthcare.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class VoiceHelper(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var isReady = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("en", "IN") // Default to Indian English
            isReady = true
        }
    }

    fun speak(text: String, languageCode: String = "en") {
        if (!isReady) return
        
        val locale = when (languageCode) {
            "hi" -> Locale.forLanguageTag("hi-IN")
            "kn" -> Locale.forLanguageTag("kn-IN")
            else -> Locale.forLanguageTag("en-IN")
        }
        
        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.shutdown()
    }
}
