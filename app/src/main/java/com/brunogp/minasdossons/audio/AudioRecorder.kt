package com.brunogp.minasdossons.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import com.brunogp.minasdossons.data.GameProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class AudioRecorder(
    private val context: Context,
) {
    private var recorder: MediaRecorder? = null
    private var stopJob: Job? = null
    private var temporaryFile: File? = null
    private var finalFile: File? = null
    var currentFile: File? = null
        private set

    fun fileForLabel(label: String): File = File(recordingDir(), "${visibleFileName(label)}.m4a")

    fun migrateProgress(progress: GameProgress): GameProgress = RecordingFileMigration(
        legacyDir = File(context.filesDir, "recordings"),
        targetDir = recordingDir(),
    ).migrate(progress)

    fun start(
        scope: CoroutineScope,
        label: String = "voz",
        maxMillis: Long = 4_000L,
        onFinished: (File) -> Unit = {},
    ): Result<File> = runCatching {
        stop()
        val visibleName = visibleFileName(label)
        val file = fileForLabel(visibleName)
        val pendingFile = File(recordingDir(), "$visibleName.recording.tmp")
        if (pendingFile.exists()) pendingFile.delete()
        val mediaRecorder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setOutputFile(pendingFile.absolutePath)
        mediaRecorder.prepare()
        mediaRecorder.start()
        recorder = mediaRecorder
        temporaryFile = pendingFile
        finalFile = file
        currentFile = file
        stopJob =
            scope.launch {
                delay(maxMillis)
                completeRecording()?.let(onFinished)
            }
        file
    }

    fun stop() {
        stopJob?.cancel()
        stopJob = null
        finishRecording()
        temporaryFile?.delete()
        temporaryFile = null
        finalFile = null
    }

    private fun finishRecording() {
        recorder?.let {
            runCatching { it.stop() }
            it.release()
        }
        recorder = null
    }

    private fun completeRecording(): File? {
        finishRecording()
        val pendingFile = temporaryFile
        val completedFile = finalFile
        temporaryFile = null
        finalFile = null
        if (pendingFile == null || completedFile == null || !pendingFile.exists()) return null
        if (completedFile.exists()) completedFile.delete()
        if (!pendingFile.renameTo(completedFile)) {
            pendingFile.copyTo(completedFile, overwrite = true)
            pendingFile.delete()
        }
        currentFile = completedFile
        return completedFile
    }

    fun deleteCurrent() {
        stop()
        currentFile?.delete()
        currentFile = null
    }

    private fun recordingDir(): File {
        val target = File(context.noBackupFilesDir, "recordings").apply { mkdirs() }
        migrateLegacyRecordings(target)
        return target
    }

    private fun migrateLegacyRecordings(target: File) {
        val legacy = File(context.filesDir, "recordings")
        if (!legacy.exists() || legacy.absolutePath == target.absolutePath) return
        legacy.listFiles().orEmpty().forEach { legacyFile ->
            val destination = File(target, legacyFile.name)
            if (!destination.exists()) {
                if (!legacyFile.renameTo(destination)) {
                    legacyFile.copyTo(destination, overwrite = false)
                    legacyFile.delete()
                }
            }
        }
    }

    companion object {
        fun visibleFileName(label: String): String = label
            .trim()
            .replace("[\\\\/:*?\"<>|]".toRegex(), "_")
            .ifBlank { "voz" }
    }
}
