package com.brunogp.minasdossons.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.brunogp.minasdossons.data.rewards.ChestType
import com.brunogp.minasdossons.data.rewards.DiamondTransaction
import com.brunogp.minasdossons.data.rewards.DiamondTransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDate

private val Context.progressDataStore by preferencesDataStore("game_progress")

class ProgressRepository(private val context: Context) {
    private object Keys {
        val totalStars = intPreferencesKey("total_stars")
        val completedSessions = intPreferencesKey("completed_sessions")
        val currentWorld = intPreferencesKey("current_world")
        val currentLevel = intPreferencesKey("current_level")
        val unlockedWorlds = stringPreferencesKey("unlocked_worlds")
        val bestScore = intPreferencesKey("best_score")
        val trainingDays = intPreferencesKey("training_days")
        val lastTrainingDate = stringPreferencesKey("last_training_date")
        val stickersUnlocked = stringPreferencesKey("stickers_unlocked")
        val recordingsCount = intPreferencesKey("recordings_count")
        val medals = stringPreferencesKey("medals")
        val soundStats = stringPreferencesKey("sound_stats")
        val customMinimalPairs = stringPreferencesKey("custom_minimal_pairs")
        val lastRecordingName = stringPreferencesKey("last_recording_name")
        val lastRecordingPath = stringPreferencesKey("last_recording_path")
        val recordingsByName = stringPreferencesKey("recordings_by_name")
        val recordingRewardDates = stringPreferencesKey("recording_reward_dates")
        val diamondBalance = intPreferencesKey("diamond_balance")
        val ownedRewardIds = stringPreferencesKey("owned_reward_ids")
        val newRewardIds = stringPreferencesKey("new_reward_ids")
        val viewedRewardIds = stringPreferencesKey("viewed_reward_ids")
        val unopenedChests = stringPreferencesKey("unopened_chests")
        val openedChestCount = intPreferencesKey("opened_chest_count")
        val chestHistory = stringPreferencesKey("chest_history")
        val rarePityCounter = intPreferencesKey("rare_pity_counter")
        val epicPityCounter = intPreferencesKey("epic_pity_counter")
        val legendaryPityCounter = intPreferencesKey("legendary_pity_counter")
        val lastDailyDiamondDate = stringPreferencesKey("last_daily_diamond_date")
        val uniqueRecordedWordsRewarded = stringPreferencesKey("unique_recorded_words_rewarded")
        val diamondTransactions = stringPreferencesKey("diamond_transactions")
        val completedRewardMissions = stringPreferencesKey("completed_reward_missions")
        val completedLevelKeys = stringPreferencesKey("completed_level_keys")
        val completedWorldsForRewards = stringPreferencesKey("completed_worlds_for_rewards")
        val allUnlockedByParent = booleanPreferencesKey("all_unlocked_by_parent")
        val ttsEnabled = booleanPreferencesKey("tts_enabled")
        val allowPortugueseVoiceFallback = booleanPreferencesKey("allow_portuguese_voice_fallback")
        val slowVoice = booleanPreferencesKey("slow_voice")
        val largeText = booleanPreferencesKey("large_text")
        val easyMode = booleanPreferencesKey("easy_mode")
    }

    val progress: Flow<GameProgress> = context.progressDataStore.data.map { prefs ->
        GameProgress(
            totalStars = prefs[Keys.totalStars] ?: 0,
            completedSessions = prefs[Keys.completedSessions] ?: 0,
            currentWorld = prefs[Keys.currentWorld] ?: 1,
            currentLevel = prefs[Keys.currentLevel] ?: 1,
            unlockedWorlds = decodeInts(prefs[Keys.unlockedWorlds]).ifEmpty { setOf(1) },
            bestScore = prefs[Keys.bestScore] ?: 0,
            trainingDays = prefs[Keys.trainingDays] ?: 0,
            lastTrainingDate = prefs[Keys.lastTrainingDate] ?: "",
            stickersUnlocked = decodeStrings(prefs[Keys.stickersUnlocked]),
            recordingsCount = prefs[Keys.recordingsCount] ?: 0,
            medals = decodeStrings(prefs[Keys.medals]),
            soundStats = decodeStats(prefs[Keys.soundStats]),
            customMinimalPairs = decodePairs(prefs[Keys.customMinimalPairs]),
            lastRecordingName = prefs[Keys.lastRecordingName] ?: "",
            lastRecordingPath = prefs[Keys.lastRecordingPath] ?: "",
            recordingsByName = decodeStringMap(prefs[Keys.recordingsByName]),
            recordingRewardDates = decodeStringMap(prefs[Keys.recordingRewardDates]),
            diamondBalance = prefs[Keys.diamondBalance] ?: 0,
            ownedRewardIds = decodeStrings(prefs[Keys.ownedRewardIds]),
            newRewardIds = decodeStrings(prefs[Keys.newRewardIds]),
            viewedRewardIds = decodeStrings(prefs[Keys.viewedRewardIds]),
            unopenedChests = decodeChests(prefs[Keys.unopenedChests]),
            openedChestCount = prefs[Keys.openedChestCount] ?: 0,
            chestHistory = decodeStrings(prefs[Keys.chestHistory]).toList(),
            rarePityCounter = prefs[Keys.rarePityCounter] ?: 0,
            epicPityCounter = prefs[Keys.epicPityCounter] ?: 0,
            legendaryPityCounter = prefs[Keys.legendaryPityCounter] ?: 0,
            lastDailyDiamondDate = prefs[Keys.lastDailyDiamondDate] ?: "",
            uniqueRecordedWordsRewarded = decodeStrings(prefs[Keys.uniqueRecordedWordsRewarded]),
            diamondTransactions = decodeTransactions(prefs[Keys.diamondTransactions]),
            completedRewardMissions = decodeStrings(prefs[Keys.completedRewardMissions]),
            completedLevelKeys = decodeStrings(prefs[Keys.completedLevelKeys]),
            completedWorldsForRewards = decodeInts(prefs[Keys.completedWorldsForRewards]),
            allUnlockedByParent = prefs[Keys.allUnlockedByParent] ?: false,
            ttsEnabled = prefs[Keys.ttsEnabled] ?: true,
            allowPortugueseVoiceFallback = prefs[Keys.allowPortugueseVoiceFallback] ?: false,
            slowVoice = prefs[Keys.slowVoice] ?: false,
            largeText = prefs[Keys.largeText] ?: false,
            easyMode = prefs[Keys.easyMode] ?: true,
        )
    }

    suspend fun saveProgress(progress: GameProgress) {
        context.progressDataStore.edit { prefs ->
            prefs[Keys.totalStars] = progress.totalStars
            prefs[Keys.completedSessions] = progress.completedSessions
            prefs[Keys.currentWorld] = progress.currentWorld
            prefs[Keys.currentLevel] = progress.currentLevel
            prefs[Keys.unlockedWorlds] = encode(progress.unlockedWorlds)
            prefs[Keys.bestScore] = progress.bestScore
            prefs[Keys.trainingDays] = progress.trainingDays
            prefs[Keys.lastTrainingDate] = progress.lastTrainingDate
            prefs[Keys.stickersUnlocked] = encode(progress.stickersUnlocked)
            prefs[Keys.recordingsCount] = progress.recordingsCount
            prefs[Keys.medals] = encode(progress.medals)
            prefs[Keys.soundStats] = progress.soundStats.entries.joinToString("|") { "${it.key}:${it.value}" }
            prefs[Keys.customMinimalPairs] = encodePairs(progress.customMinimalPairs)
            prefs[Keys.lastRecordingName] = progress.lastRecordingName
            prefs[Keys.lastRecordingPath] = progress.lastRecordingPath
            prefs[Keys.recordingsByName] = encodeStringMap(progress.recordingsByName)
            prefs[Keys.recordingRewardDates] = encodeStringMap(progress.recordingRewardDates)
            prefs[Keys.diamondBalance] = progress.diamondBalance.coerceAtLeast(0)
            prefs[Keys.ownedRewardIds] = encode(progress.ownedRewardIds)
            prefs[Keys.newRewardIds] = encode(progress.newRewardIds)
            prefs[Keys.viewedRewardIds] = encode(progress.viewedRewardIds)
            prefs[Keys.unopenedChests] = progress.unopenedChests.joinToString("|") { it.name }
            prefs[Keys.openedChestCount] = progress.openedChestCount
            prefs[Keys.chestHistory] = encode(progress.chestHistory)
            prefs[Keys.rarePityCounter] = progress.rarePityCounter
            prefs[Keys.epicPityCounter] = progress.epicPityCounter
            prefs[Keys.legendaryPityCounter] = progress.legendaryPityCounter
            prefs[Keys.lastDailyDiamondDate] = progress.lastDailyDiamondDate
            prefs[Keys.uniqueRecordedWordsRewarded] = encode(progress.uniqueRecordedWordsRewarded)
            prefs[Keys.diamondTransactions] = encodeTransactions(progress.diamondTransactions.takeLast(100))
            prefs[Keys.completedRewardMissions] = encode(progress.completedRewardMissions)
            prefs[Keys.completedLevelKeys] = encode(progress.completedLevelKeys)
            prefs[Keys.completedWorldsForRewards] = encode(progress.completedWorldsForRewards)
            prefs[Keys.allUnlockedByParent] = progress.allUnlockedByParent
            prefs[Keys.ttsEnabled] = progress.ttsEnabled
            prefs[Keys.allowPortugueseVoiceFallback] = progress.allowPortugueseVoiceFallback
            prefs[Keys.slowVoice] = progress.slowVoice
            prefs[Keys.largeText] = progress.largeText
            prefs[Keys.easyMode] = progress.easyMode
        }
    }

    suspend fun recordTrainingDay(progress: GameProgress): GameProgress {
        val today = LocalDate.now().toString()
        if (progress.lastTrainingDate == today) return progress
        val yesterday = LocalDate.now().minusDays(1).toString()
        return progress.copy(
            trainingDays = if (progress.lastTrainingDate == yesterday) progress.trainingDays + 1 else 1,
            lastTrainingDate = today,
        )
    }

    suspend fun reset() = saveProgress(GameProgress())

    private fun encode(values: Iterable<Any>): String = values.joinToString("|")
    private fun decodeStrings(value: String?): Set<String> = value?.split("|")?.filter { it.isNotBlank() }?.toSet().orEmpty()
    private fun decodeInts(value: String?): Set<Int> = decodeStrings(value).mapNotNull { it.toIntOrNull() }.toSet()
    private fun decodeStats(value: String?): Map<String, Int> = value?.split("|").orEmpty().mapNotNull {
        val parts = it.split(":")
        if (parts.size == 2) parts[0] to (parts[1].toIntOrNull() ?: 0) else null
    }.toMap()

    private fun encodeStringMap(values: Map<String, String>): String = values.entries.joinToString("|") {
        "${enc(it.key)}~${enc(it.value)}"
    }

    private fun decodeStringMap(value: String?): Map<String, String> = value?.split("|").orEmpty().mapNotNull {
        val parts = it.split("~")
        if (parts.size == 2) dec(parts[0]) to dec(parts[1]) else null
    }.toMap()

    private fun encodePairs(pairs: List<MinimalPair>): String = pairs.joinToString("|") {
        listOf(it.id, it.wordA, it.wordB, it.targetA, it.targetB, it.hint).joinToString("~") { part -> enc(part) }
    }

    private fun decodePairs(value: String?): List<MinimalPair> = value?.split("|").orEmpty().mapNotNull { item ->
        val parts = item.split("~").map { dec(it) }
        if (parts.size == 6) {
            MinimalPair(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], isCommonPortuguese = true, editable = true)
        } else {
            null
        }
    }

    private fun decodeChests(value: String?): List<ChestType> = value?.split("|").orEmpty().mapNotNull {
        runCatching { ChestType.valueOf(it) }.getOrNull()
    }

    private fun encodeTransactions(transactions: List<DiamondTransaction>): String = transactions.joinToString("|") {
        listOf(it.id, it.amount.toString(), it.type.name, it.description, it.timestamp.toString()).joinToString("~") { part -> enc(part) }
    }

    private fun decodeTransactions(value: String?): List<DiamondTransaction> = value?.split("|").orEmpty().mapNotNull { item ->
        val parts = item.split("~").map { dec(it) }
        if (parts.size == 5) {
            val type = runCatching {
                DiamondTransactionType.valueOf(parts[2])
            }.getOrNull() ?: return@mapNotNull null
            DiamondTransaction(
                id = parts[0],
                amount = parts[1].toIntOrNull() ?: 0,
                type = type,
                description = parts[3],
                timestamp = parts[4].toLongOrNull() ?: 0L,
            )
        } else {
            null
        }
    }.takeLast(100)

    private fun enc(value: String): String = URLEncoder.encode(value, StandardCharsets.UTF_8.name())
    private fun dec(value: String): String = URLDecoder.decode(value, StandardCharsets.UTF_8.name())
}
