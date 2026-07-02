package com.brunogp.minasdossons

import com.brunogp.minasdossons.audio.RecordingFileMigration
import com.brunogp.minasdossons.data.GameProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class RecordingFileMigrationTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun migratesLegacyFilesAndUpdatesPersistedPaths() {
        val legacy = temporaryFolder.newFolder("files", "recordings")
        val target = temporaryFolder.newFolder("no-backup", "recordings")
        val legacyFile = File(legacy, "sapo.m4a").apply { writeText("voice") }
        val progress = GameProgress(
            lastRecordingName = "sapo",
            lastRecordingPath = legacyFile.absolutePath,
            recordingsByName = mapOf("sapo" to legacyFile.absolutePath),
            recordingRewardDates = mapOf("sapo" to "2026-07-02"),
        )

        val migrated = RecordingFileMigration(legacy, target).migrate(progress)
        val targetFile = File(target, "sapo.m4a")

        assertTrue(targetFile.exists())
        assertFalse(legacyFile.exists())
        assertEquals(targetFile.absolutePath, migrated.lastRecordingPath)
        assertEquals(targetFile.absolutePath, migrated.recordingsByName["sapo"])
        assertEquals(progress.recordingRewardDates, migrated.recordingRewardDates)
    }

    @Test
    fun migrationIsIdempotentWhenTargetAlreadyExists() {
        val legacy = temporaryFolder.newFolder("legacy-recordings")
        val target = temporaryFolder.newFolder("target-recordings")
        val targetFile = File(target, "zebra.m4a").apply { writeText("voice") }
        val progress = GameProgress(
            lastRecordingName = "zebra",
            lastRecordingPath = targetFile.absolutePath,
            recordingsByName = mapOf("zebra" to targetFile.absolutePath),
        )

        val migration = RecordingFileMigration(legacy, target)
        val first = migration.migrate(progress)
        val second = migration.migrate(first)

        assertEquals(first, second)
        assertTrue(targetFile.exists())
    }

    @Test
    fun collisionKeepsExistingTargetAndDoesNotDeleteLegacySource() {
        val legacy = temporaryFolder.newFolder("legacy")
        val target = temporaryFolder.newFolder("target")
        val legacyFile = File(legacy, "casa.m4a").apply { writeText("old") }
        val targetFile = File(target, "casa.m4a").apply { writeText("new") }
        val progress = GameProgress(
            lastRecordingName = "casa",
            lastRecordingPath = legacyFile.absolutePath,
            recordingsByName = mapOf("casa" to legacyFile.absolutePath),
        )

        val migrated = RecordingFileMigration(legacy, target).migrate(progress)

        assertTrue(legacyFile.exists())
        assertEquals("new", targetFile.readText())
        assertEquals(targetFile.absolutePath, migrated.recordingsByName["casa"])
    }
}
