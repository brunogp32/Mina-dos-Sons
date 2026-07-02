package com.brunogp.minasdossons.audio

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

interface TtsEngineAdapter {
    fun languageAvailability(localeTag: String): TtsLanguageAvailability
    fun voices(): List<TtsVoiceInfo>
    fun selectVoice(voice: TtsVoiceInfo): Boolean
    fun selectedVoice(): TtsVoiceInfo?
    fun speak(
        text: String,
        utteranceId: String,
        slow: Boolean,
    ): Boolean
    fun stop()
    fun shutdown()
}

class AndroidTtsEngineAdapter(
    context: Context,
    private val onInitialized: (Boolean) -> Unit,
) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        Handler(Looper.getMainLooper()).post {
            val engine = tts
            if (engine == null) {
                onInitialized(false)
                return@post
            }
            engine.setOnUtteranceProgressListener(
                object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) = Unit
                    override fun onDone(utteranceId: String?) = Unit

                    @Deprecated("Deprecated by Android")
                    override fun onError(utteranceId: String?) = Unit
                },
            )
            onInitialized(status == TextToSpeech.SUCCESS)
        }
    }

    fun asAdapter(): TtsEngineAdapter = object : TtsEngineAdapter {
        override fun languageAvailability(localeTag: String): TtsLanguageAvailability {
            val result = tts?.isLanguageAvailable(Locale.forLanguageTag(localeTag))
            return when (result) {
                TextToSpeech.LANG_MISSING_DATA -> TtsLanguageAvailability.MissingData

                TextToSpeech.LANG_NOT_SUPPORTED -> TtsLanguageAvailability.Unsupported

                TextToSpeech.LANG_AVAILABLE,
                TextToSpeech.LANG_COUNTRY_AVAILABLE,
                TextToSpeech.LANG_COUNTRY_VAR_AVAILABLE,
                -> TtsLanguageAvailability.Available

                else -> TtsLanguageAvailability.Unknown
            }
        }

        override fun voices(): List<TtsVoiceInfo> = tts?.voices.orEmpty().map { voice ->
            TtsVoiceInfo(
                name = voice.name,
                localeTag = voice.locale.toLanguageTag(),
                requiresNetwork = voice.isNetworkConnectionRequired,
            )
        }

        override fun selectVoice(voice: TtsVoiceInfo): Boolean {
            val engine = tts
            val androidVoice = engine?.voices.orEmpty().firstOrNull { it.name == voice.name }
            return engine != null &&
                androidVoice != null &&
                engine.setVoice(androidVoice) == TextToSpeech.SUCCESS &&
                selectedVoice()?.name == voice.name
        }

        override fun selectedVoice(): TtsVoiceInfo? = tts?.voice?.let { voice ->
            TtsVoiceInfo(
                name = voice.name,
                localeTag = voice.locale.toLanguageTag(),
                requiresNetwork = voice.isNetworkConnectionRequired,
            )
        }

        override fun speak(
            text: String,
            utteranceId: String,
            slow: Boolean,
        ): Boolean {
            val engine = tts ?: return false
            engine.setSpeechRate(if (slow) 0.72f else 0.95f)
            return engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId) == TextToSpeech.SUCCESS
        }

        override fun stop() {
            tts?.stop()
        }

        override fun shutdown() {
            tts?.shutdown()
            tts = null
        }
    }
}
