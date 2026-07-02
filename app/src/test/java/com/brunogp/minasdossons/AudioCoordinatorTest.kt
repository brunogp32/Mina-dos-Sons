package com.brunogp.minasdossons

import com.brunogp.minasdossons.audio.AudioCoordinator
import com.brunogp.minasdossons.audio.RecordingPlaybackOutput
import com.brunogp.minasdossons.audio.ReferenceSound
import com.brunogp.minasdossons.audio.ReferenceSoundOutput
import com.brunogp.minasdossons.audio.SpeechOutput
import com.brunogp.minasdossons.audio.TtsPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioCoordinatorTest {
    @Test
    fun startingNewPlaybackStopsPreviousOutputs() = runBlocking {
        val reference = FakeReferenceSoundOutput()
        val speech = FakeSpeechOutput()
        val recording = FakeRecordingPlaybackOutput()
        val coordinator = AudioCoordinator(this, speech, reference, recording, sequenceDelayMillis = 1L)

        coordinator.playTextOrReference("SSSS", slowVoice = false, ttsPreferences = TtsPreferences())
        coordinator.playTextOrReference("ZZZZ", slowVoice = false, ttsPreferences = TtsPreferences())

        assertEquals(listOf(ReferenceSound.S, ReferenceSound.Z), reference.played)
        assertTrue(reference.stopCount >= 2)
        assertTrue(recording.stopCount >= 2)
        assertTrue(speech.stopCount >= 2)
    }

    @Test
    fun cancelledReferenceSequenceDoesNotStartNextSound() = runBlocking {
        val reference = FakeReferenceSoundOutput()
        val coordinator = AudioCoordinator(
            scope = this,
            speech = FakeSpeechOutput(),
            referenceOutput = reference,
            recordingOutput = FakeRecordingPlaybackOutput(),
            sequenceDelayMillis = 50L,
        )

        coordinator.playTextOrReference("SSSS ZZZZ", slowVoice = false, ttsPreferences = TtsPreferences())
        delay(5L)
        coordinator.stopAll()
        delay(80L)

        assertEquals(listOf(ReferenceSound.S), reference.played)
    }

    private class FakeReferenceSoundOutput : ReferenceSoundOutput {
        val played = mutableListOf<ReferenceSound>()
        var stopCount = 0

        override fun playReferenceSound(sound: ReferenceSound): Boolean {
            played += sound
            return true
        }

        override fun stop() {
            stopCount += 1
        }
    }

    private class FakeSpeechOutput : SpeechOutput {
        var stopCount = 0

        override fun speak(
            text: String,
            slow: Boolean,
            preferences: TtsPreferences,
        ): Boolean = true

        override fun stop() {
            stopCount += 1
        }

        override fun shutdown() = Unit
    }

    private class FakeRecordingPlaybackOutput : RecordingPlaybackOutput {
        var stopCount = 0

        override fun stop() {
            stopCount += 1
        }
    }
}
