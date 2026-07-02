package com.brunogp.minasdossons.audio

enum class TtsStatus {
    Initializing,
    PtPtOfflineAvailable,
    PtPtNetworkOnly,
    PortugueseFallbackAvailable,
    MissingLanguageData,
    LanguageUnsupported,
    EngineUnavailable,
    Disabled,
    Error,
}

enum class TtsLanguageAvailability {
    Available,
    MissingData,
    Unsupported,
    Unknown,
}

data class TtsVoiceInfo(
    val name: String,
    val localeTag: String,
    val requiresNetwork: Boolean,
) {
    val displayLanguage: String
        get() = when (localeTag.lowercase()) {
            "pt-pt" -> "Português de Portugal"
            "pt-br" -> "Português do Brasil"
            else -> localeTag
        }
}

data class TtsPreferences(
    val enabled: Boolean = true,
    val allowPortugueseFallback: Boolean = false,
)

data class TtsState(
    val status: TtsStatus = TtsStatus.Initializing,
    val selectedVoice: TtsVoiceInfo? = null,
    val message: String = "A verificar voz portuguesa.",
) {
    val canSpeak: Boolean
        get() = status == TtsStatus.PtPtOfflineAvailable || status == TtsStatus.PortugueseFallbackAvailable
}
