package com.brunogp.minasdossons

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ManifestPolicyTest {
    @Test
    fun manifestUsesOnlyMicrophonePermissionAndNoInternet() {
        val manifest = parseXml(projectFile("src/main/AndroidManifest.xml"))
        val permissions = manifest.elements("uses-permission").mapNotNull { it.androidName() }

        assertEquals(listOf("android.permission.RECORD_AUDIO"), permissions)
        assertTrue("INTERNET permission must not be declared.", "android.permission.INTERNET" !in permissions)
    }

    @Test
    fun manifestDisablesBackupAndDeclaresExtractionRules() {
        val manifest = parseXml(projectFile("src/main/AndroidManifest.xml"))
        val application = manifest.elements("application").single()

        assertEquals("false", application.getAttribute("android:allowBackup"))
        assertEquals("@xml/backup_rules", application.getAttribute("android:fullBackupContent"))
        assertEquals("@xml/data_extraction_rules", application.getAttribute("android:dataExtractionRules"))
    }

    @Test
    fun backupRulesExcludeRecordings() {
        val backupRules = parseXml(projectFile("src/main/res/xml/backup_rules.xml"))
        val excludes = backupRules.elements("exclude").map { it.getAttribute("domain") to it.getAttribute("path") }

        assertTrue("recordings/ must be excluded from full backup.", "file" to "recordings/" in excludes)
    }

    @Test
    fun dataExtractionRulesExcludeRecordingsFromCloudAndTransfer() {
        val extractionRules = parseXml(projectFile("src/main/res/xml/data_extraction_rules.xml"))

        val cloudBackup = extractionRules.elements("cloud-backup").single()
        val deviceTransfer = extractionRules.elements("device-transfer").single()

        assertTrue("recordings/ must be excluded from cloud backup.", cloudBackup.hasRecordingExclude())
        assertTrue("recordings/ must be excluded from device transfer.", deviceTransfer.hasRecordingExclude())
    }

    private fun org.w3c.dom.Element.hasRecordingExclude(): Boolean = elements("exclude").any { it.getAttribute("domain") == "file" && it.getAttribute("path") == "recordings/" }

    private fun org.w3c.dom.Element.androidName(): String? = getAttribute("android:name").ifBlank { null }

    private fun Document.elements(name: String): List<org.w3c.dom.Element> = documentElement.elements(name)

    private fun org.w3c.dom.Element.elements(name: String): List<org.w3c.dom.Element> {
        val nodes = getElementsByTagName(name)
        return List(nodes.length) { index -> nodes.item(index) as org.w3c.dom.Element }
    }

    private fun parseXml(file: File): Document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)

    private fun projectFile(path: String): File {
        val workingDir = File(System.getProperty("user.dir"))
        val direct = File(workingDir, path)
        if (direct.exists()) return direct
        return File(workingDir, "app/$path")
    }
}
