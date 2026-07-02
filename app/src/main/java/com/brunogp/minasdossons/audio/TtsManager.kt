package com.brunogp.minasdossons.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

class TtsManager(
    private val engine: TtsEngineAdapter,
) {
    private val _state = MutableStateFlow(TtsState())
    val state: StateFlow<TtsState> = _state
    private var preferences = TtsPreferences()

    fun onEngineInitialized(success: Boolean) {
        if (!success) {
            _state.value = TtsState(TtsStatus.EngineUnavailable, message = "Motor de voz indisponível.")
            return
        }
        refresh(preferences)
    }

    fun refresh(newPreferences: TtsPreferences = preferences): TtsState {
        preferences = newPreferences
        val state = resolveState(newPreferences)
        _state.value = state
        return state
    }

    fun speak(
        text: String,
        slow: Boolean,
    ): Boolean {
        val current = refresh(preferences)
        if (!current.canSpeak) return false
        stop()
        return engine.speak(text, "mina-dos-sons-${UUID.randomUUID()}", slow)
    }

    fun stop() = engine.stop()

    fun shutdown() = engine.shutdown()

    private fun resolveState(preferences: TtsPreferences): TtsState {
        if (!preferences.enabled) {
            return TtsState(TtsStatus.Disabled, message = "Som TTS desligado nas opções.")
        }

        val voices = engine.voices()
        val exactPtPt = voices.filter { it.localeTag.equals("pt-PT", ignoreCase = true) }
        val exactOffline = exactPtPt.firstOrNull { !it.requiresNetwork }
        val fallback = voices.firstOrNull { voice ->
            voice.localeTag.startsWith("pt-", ignoreCase = true) &&
                !voice.localeTag.equals("pt-PT", ignoreCase = true) &&
                !voice.requiresNetwork
        }

        return when {
            exactOffline != null -> selectVoice(
                voice = exactOffline,
                status = TtsStatus.PtPtOfflineAvailable,
                message = "Voz portuguesa de Portugal instalada e disponível offline.",
            )

            fallback != null && preferences.allowPortugueseFallback -> selectVoice(
                voice = fallback,
                status = TtsStatus.PortugueseFallbackAvailable,
                message = "A usar ${fallback.displayLanguage}; não é português de Portugal.",
            )

            exactPtPt.any { it.requiresNetwork } -> TtsState(
                status = TtsStatus.PtPtNetworkOnly,
                selectedVoice = null,
                message = "Só foi encontrada voz pt-PT que precisa de rede. A app mantém-se offline.",
            )

            else -> unavailableState()
        }
    }

    private fun unavailableState(): TtsState = when (engine.languageAvailability("pt-PT")) {
        TtsLanguageAvailability.MissingData ->
            TtsState(TtsStatus.MissingLanguageData, message = "Faltam dados de voz portuguesa de Portugal.")

        TtsLanguageAvailability.Unsupported ->
            TtsState(TtsStatus.LanguageUnsupported, message = "Este motor de voz não suporta português de Portugal.")

        TtsLanguageAvailability.Available,
        TtsLanguageAvailability.Unknown,
        -> TtsState(TtsStatus.LanguageUnsupported, message = "Não há voz offline pt-PT instalada.")
    }

    private fun selectVoice(
        voice: TtsVoiceInfo,
        status: TtsStatus,
        message: String,
    ): TtsState = if (engine.selectVoice(voice)) {
        TtsState(status = status, selectedVoice = engine.selectedVoice() ?: voice, message = message)
    } else {
        TtsState(status = TtsStatus.Error, selectedVoice = null, message = "Não foi possível selecionar a voz.")
    }
}
