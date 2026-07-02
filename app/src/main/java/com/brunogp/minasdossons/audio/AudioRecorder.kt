package com.brunogp.minasdossons.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class AudioRecorder(private val context: Context) {
    private var recorder: MediaRecorder? = null
    private var stopJob: Job? = null
    var currentFile: File? = null
        private set

    fun fileForLabel(label: String): File = File(recordingDir(), "${visibleFileName(label)}.m4a")

    fun start(
        scope: CoroutineScope,
        label: String = "voz",
        maxMillis: Long = 4_000L,
        onFinished: (File) -> Unit = {},
    ): Result<File> = runCatching {
        stop()
        val file = fileForLabel(label)
        if (file.exists()) file.delete()
        val mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setOutputFile(file.absolutePath)
        mediaRecorder.prepare()
        mediaRecorder.start()
        recorder = mediaRecorder
        currentFile = file
        stopJob = scope.launch {
            delay(maxMillis)
            finishRecording()
            onFinished(file)
        }
        file
    }

    fun stop() {
        stopJob?.cancel()
        stopJob = null
        finishRecording()
    }

    private fun finishRecording() {
        recorder?.let {
            runCatching { it.stop() }
            it.release()
        }
        recorder = null
    }

    fun deleteCurrent() {
        stop()
        currentFile?.delete()
        currentFile = null
    }

    private fun recordingDir(): File = File(context.filesDir, "recordings").apply { mkdirs() }

    companion object {
        fun visibleFileName(label: String): String {
            return label.trim()
                .replace("[\\\\/:*?\"<>|]".toRegex(), "_")
                .ifBlank { "voz" }
        }
    }
}
