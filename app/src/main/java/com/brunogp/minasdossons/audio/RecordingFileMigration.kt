package com.brunogp.minasdossons.audio

import com.brunogp.minasdossons.data.GameProgress
import java.io.File

class RecordingFileMigration(
    private val legacyDir: File,
    private val targetDir: File,
) {
    fun migrate(progress: GameProgress): GameProgress {
        targetDir.mkdirs()
        val migratedRecordings = progress.recordingsByName.mapValues { (name, path) ->
            migratePath(name, path).absolutePath
        }
        val migratedLastPath =
            if (progress.lastRecordingName.isNotBlank() && progress.lastRecordingPath.isNotBlank()) {
                migratePath(progress.lastRecordingName, progress.lastRecordingPath).absolutePath
            } else {
                progress.lastRecordingPath
            }

        return progress.copy(
            recordingsByName = migratedRecordings,
            lastRecordingPath = migratedLastPath,
        )
    }

    private fun migratePath(
        label: String,
        storedPath: String,
    ): File {
        val storedFile = File(storedPath)
        val targetFile = File(targetDir, "${AudioRecorder.visibleFileName(label)}.m4a")
        return when {
            isInsideTarget(storedFile) && storedFile.exists() -> storedFile
            targetFile.exists() -> targetFile
            isInsideLegacy(storedFile) && storedFile.exists() -> moveSafely(storedFile, targetFile)
            else -> targetFile
        }
    }

    private fun moveSafely(
        source: File,
        target: File,
    ): File {
        target.parentFile?.mkdirs()
        var result = target
        if (!target.exists()) {
            val temporaryTarget = File(target.parentFile, "${target.name}.migration.tmp")
            if (temporaryTarget.exists()) temporaryTarget.delete()
            source.copyTo(temporaryTarget, overwrite = false)
            if (!temporaryTarget.renameTo(target)) {
                temporaryTarget.delete()
                result = source
            }
        }
        if (result == target && target.exists() && target.length() == source.length()) {
            source.delete()
        }
        return result
    }

    private fun isInsideLegacy(file: File): Boolean = file.normalizePath().startsWith(legacyDir.normalizePath())

    private fun isInsideTarget(file: File): Boolean = file.normalizePath().startsWith(targetDir.normalizePath())

    private fun File.normalizePath(): String = canonicalFile.absolutePath.trimEnd(File.separatorChar) + File.separator
}
