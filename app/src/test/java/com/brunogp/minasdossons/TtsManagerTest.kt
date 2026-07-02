package com.brunogp.minasdossons

import com.brunogp.minasdossons.audio.TtsEngineAdapter
import com.brunogp.minasdossons.audio.TtsLanguageAvailability
import com.brunogp.minasdossons.audio.TtsManager
import com.brunogp.minasdossons.audio.TtsPreferences
import com.brunogp.minasdossons.audio.TtsStatus
import com.brunogp.minasdossons.audio.TtsVoiceInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TtsManagerTest {
    @Test
    fun ptPtOfflineVoiceIsSelected() {
        val engine = FakeTtsEngine(voices = listOf(voice("pt-PT", network = false)))
        val manager = TtsManager(engine)

        val state = manager.refresh()

        assertEquals(TtsStatus.PtPtOfflineAvailable, state.status)
        assertEquals("pt-PT", state.selectedVoice?.localeTag)
        assertTrue(manager.speak("casa", slow = false))
    }

    @Test
    fun ptPtNetworkOnlyIsNotUsedByDefault() {
        val engine = FakeTtsEngine(voices = listOf(voice("pt-PT", network = true)))
        val manager = TtsManager(engine)

        val state = manager.refresh()

        assertEquals(TtsStatus.PtPtNetworkOnly, state.status)
        assertFalse(manager.speak("casa", slow = false))
        assertEquals(0, engine.speakCount)
    }

    @Test
    fun portugueseFallbackNeedsParentAuthorization() {
        val engine = FakeTtsEngine(voices = listOf(voice("pt-BR", network = false)))
        val manager = TtsManager(engine)

        val blocked = manager.refresh(TtsPreferences(allowPortugueseFallback = false))
        val allowed = manager.refresh(TtsPreferences(allowPortugueseFallback = true))

        assertEquals(TtsStatus.LanguageUnsupported, blocked.status)
        assertEquals(TtsStatus.PortugueseFallbackAvailable, allowed.status)
        assertEquals("pt-BR", allowed.selectedVoice?.localeTag)
    }

    @Test
    fun missingLanguageDataIsReported() {
        val manager = TtsManager(FakeTtsEngine(languageAvailability = TtsLanguageAvailability.MissingData))

        assertEquals(TtsStatus.MissingLanguageData, manager.refresh().status)
    }

    @Test
    fun unsupportedLanguageIsReported() {
        val manager = TtsManager(FakeTtsEngine(languageAvailability = TtsLanguageAvailability.Unsupported))

        assertEquals(TtsStatus.LanguageUnsupported, manager.refresh().status)
    }

    @Test
    fun disabledStatusDoesNotSpeak() {
        val engine = FakeTtsEngine(voices = listOf(voice("pt-PT", network = false)))
        val manager = TtsManager(engine)

        val state = manager.refresh(TtsPreferences(enabled = false))

        assertEquals(TtsStatus.Disabled, state.status)
        assertFalse(manager.speak("casa", slow = false))
        assertEquals(0, engine.speakCount)
    }

    @Test
    fun engineUnavailableIsReported() {
        val manager = TtsManager(FakeTtsEngine())

        manager.onEngineInitialized(false)

        assertEquals(TtsStatus.EngineUnavailable, manager.state.value.status)
    }

    @Test
    fun selectionFailureReportsError() {
        val manager = TtsManager(FakeTtsEngine(voices = listOf(voice("pt-PT", network = false)), selectSucceeds = false))

        assertEquals(TtsStatus.Error, manager.refresh().status)
    }

    private class FakeTtsEngine(
        private val voices: List<TtsVoiceInfo> = emptyList(),
        private val languageAvailability: TtsLanguageAvailability = TtsLanguageAvailability.Available,
        private val selectSucceeds: Boolean = true,
    ) : TtsEngineAdapter {
        var speakCount = 0
        private var selectedVoice: TtsVoiceInfo? = null

        override fun languageAvailability(localeTag: String): TtsLanguageAvailability = languageAvailability
        override fun voices(): List<TtsVoiceInfo> = voices

        override fun selectVoice(voice: TtsVoiceInfo): Boolean {
            if (selectSucceeds) selectedVoice = voice
            return selectSucceeds
        }

        override fun selectedVoice(): TtsVoiceInfo? = selectedVoice

        override fun speak(
            text: String,
            utteranceId: String,
            slow: Boolean,
        ): Boolean {
            speakCount += 1
            return true
        }

        override fun stop() = Unit
        override fun shutdown() = Unit
    }

    private fun voice(
        localeTag: String,
        network: Boolean,
    ): TtsVoiceInfo = TtsVoiceInfo(
        name = "voice-$localeTag-$network",
        localeTag = localeTag,
        requiresNetwork = network,
    )
}
