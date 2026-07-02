package com.brunogp.minasdossons

import com.brunogp.minasdossons.audio.AudioRecorder
import com.brunogp.minasdossons.audio.ReferenceAudioRepository
import com.brunogp.minasdossons.audio.ReferenceSound
import com.brunogp.minasdossons.ui.components.FOUR_SECOND_RECORDING_MS
import com.brunogp.minasdossons.ui.components.RecorderUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioAndRecordingTest {
    @Test
    fun referenceSoundsHaveBundledRawResources() {
        ReferenceSound.entries.forEach {
            assertTrue(it.bundledRawResourceName.isNotBlank())
            assertTrue(it.bundledRawResourceName.startsWith("som_"))
        }
    }

    @Test
    fun vibrationRulesAreCorrectForReferenceSounds() {
        assertFalse(ReferenceSound.S.vibrates)
        assertFalse(ReferenceSound.CH.vibrates)
        assertTrue(ReferenceSound.Z.vibrates)
        assertTrue(ReferenceSound.J.vibrates)
    }

    @Test
    fun repositoryPrefersBundledRawThenFallback() {
        assertEquals("raw", ReferenceAudioRepository.preferredSourceKind(rawResourceId = 12))
        assertEquals("fallback", ReferenceAudioRepository.preferredSourceKind(rawResourceId = 0))
    }

    @Test
    fun longSoundsAreDetectedBeforeTts() {
        assertEquals(ReferenceSound.S, ReferenceSound.fromDisplayText("SSSS"))
        assertEquals(ReferenceSound.Z, ReferenceSound.fromDisplayText("ZZZZ"))
        assertEquals(ReferenceSound.CH, ReferenceSound.fromDisplayText("CHHHH"))
        assertEquals(ReferenceSound.J, ReferenceSound.fromDisplayText("JJJJ"))
    }

    @Test
    fun recordingPolicyUsesFourSecondsAndStableNames() {
        assertEquals(4_000L, FOUR_SECOND_RECORDING_MS)
        assertEquals("sapo", AudioRecorder.visibleFileName("sapo"))
        assertEquals("caça", AudioRecorder.visibleFileName("caça"))
        assertEquals("SSSS", AudioRecorder.visibleFileName("SSSS"))
        assertEquals("cha_", AudioRecorder.visibleFileName("cha?"))
        assertEquals(AudioRecorder.visibleFileName("sapo"), AudioRecorder.visibleFileName("sapo"))
        assertFalse(AudioRecorder.visibleFileName("sapo").any { it.isDigit() })
    }

    @Test
    fun recorderStatesExistInExpectedFlow() {
        val flow =
            listOf(
                RecorderUiState.Idle,
                RecorderUiState.Preparing,
                RecorderUiState.Recording,
                RecorderUiState.Saving,
                RecorderUiState.Recorded,
            )
        assertEquals(RecorderUiState.Idle, flow.first())
        assertEquals(RecorderUiState.Recorded, flow.last())
    }
}
