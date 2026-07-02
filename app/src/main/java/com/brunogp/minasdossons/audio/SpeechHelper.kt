package com.brunogp.minasdossons.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class SpeechHelper(
    context: Context,
) : TextToSpeech.OnInitListener {
    private var ready = false
    private val tts = TextToSpeech(context.applicationContext, this)

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val pt = Locale("pt", "PT")
            val result = tts.setLanguage(pt)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.language = Locale.getDefault()
            }
            ready = true
        }
    }

    fun speak(
        text: String,
        slow: Boolean = false,
        enabled: Boolean = true,
    ) {
        if (!ready || !enabled) return
        tts.setSpeechRate(if (slow) 0.72f else 0.95f)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "minas-dos-sons")
    }

    fun stop() = tts.stop()

    fun shutdown() = tts.shutdown()
}
