package com.brunogp.minasdossons.audio

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface ReferenceSoundOutput {
    fun playReferenceSound(sound: ReferenceSound): Boolean
    fun stop()
}

interface SpeechOutput {
    fun speak(text: String, slow: Boolean = false, enabled: Boolean = true)
    fun stop()
    fun shutdown()
}

interface RecordingPlaybackOutput {
    fun stop()
}

class AudioCoordinator(
    private val scope: CoroutineScope,
    private val speech: SpeechOutput,
    private val referenceOutput: ReferenceSoundOutput,
    private val recordingOutput: RecordingPlaybackOutput,
    private val sequenceDelayMillis: Long = 1_050L,
) {
    private var generation = 0L
    private var sequenceJob: Job? = null

    fun playTextOrReference(
        text: String,
        slowVoice: Boolean,
        ttsEnabled: Boolean,
    ) {
        stopAll()
        val playbackGeneration = nextGeneration()
        val reference = ReferenceSound.fromDisplayText(text)
        if (reference != null) {
            referenceOutput.playReferenceSound(reference)
            return
        }

        val soundsInText = ReferenceSound.entries
            .mapNotNull { sound ->
                val index = text.indexOf(sound.displayText, ignoreCase = true)
                if (index >= 0) index to sound else null
            }
            .sortedBy { it.first }
            .map { it.second }

        if (soundsInText.isNotEmpty()) {
            sequenceJob = scope.launch {
                soundsInText.forEachIndexed { index, sound ->
                    if (playbackGeneration != generation) return@launch
                    if (index > 0) delay(sequenceDelayMillis)
                    if (playbackGeneration == generation) {
                        referenceOutput.playReferenceSound(sound)
                    }
                }
            }
            return
        }

        speech.speak(text, slowVoice, ttsEnabled)
    }

    fun stopAll() {
        nextGeneration()
        sequenceJob?.cancel()
        sequenceJob = null
        referenceOutput.stop()
        recordingOutput.stop()
        speech.stop()
    }

    fun release() {
        stopAll()
        speech.shutdown()
    }

    private fun nextGeneration(): Long {
        generation += 1
        return generation
    }
}
