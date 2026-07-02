package com.brunogp.minasdossons.audio

import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.StateFlow

class SpeechHelper(
    context: Context,
) : SpeechOutput {
    private var manager: TtsManager? = null
    private val androidAdapter = AndroidTtsEngineAdapter(context.applicationContext) { success ->
        manager?.onEngineInitialized(success)
    }
    private val ttsManager = TtsManager(androidAdapter.asAdapter()).also { manager = it }

    val ttsState: StateFlow<TtsState> = ttsManager.state

    fun refresh(preferences: TtsPreferences): TtsState = ttsManager.refresh(preferences)

    fun installVoiceDataIntent(): Intent = Intent(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)

    override fun speak(
        text: String,
        slow: Boolean,
        preferences: TtsPreferences,
    ): Boolean {
        ttsManager.refresh(preferences)
        return ttsManager.speak(text, slow)
    }

    override fun stop() = ttsManager.stop()

    override fun shutdown() = ttsManager.shutdown()
}
