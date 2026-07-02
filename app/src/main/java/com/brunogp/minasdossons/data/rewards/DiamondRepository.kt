package com.brunogp.minasdossons.data.rewards

import com.brunogp.minasdossons.data.GameProgress
import java.time.LocalDate
import kotlin.math.max

object DiamondRepository {
    fun addTransaction(
        progress: GameProgress,
        amount: Int,
        type: DiamondTransactionType,
        description: String,
        now: Long = System.currentTimeMillis(),
    ): GameProgress {
        val transaction = DiamondTransaction(
            id = "${now}_${type.name}_${progress.diamondTransactions.size}",
            amount = amount,
            type = type,
            description = description,
            timestamp = now,
        )
        return progress.copy(
            diamondBalance = max(0, progress.diamondBalance + amount),
            diamondTransactions = (progress.diamondTransactions + transaction).takeLast(100),
        )
    }

    fun spend(progress: GameProgress, price: Int, description: String): GameProgress? {
        if (price < 0 || progress.diamondBalance < price) return null
        return addTransaction(progress, -price, DiamondTransactionType.SHOP_PURCHASE, description)
    }

    fun rewardSession(progress: GameProgress, stars: Int, world: Int, level: Int, today: LocalDate = LocalDate.now()): GameProgress {
        var updated = progress
        updated = addTransaction(updated, 2, DiamondTransactionType.SESSION_COMPLETION, "Sessão concluída")
        if (stars > 0) {
            updated = addTransaction(updated, stars, DiamondTransactionType.FIRST_TRY_CORRECT, "$stars acertos à primeira")
        }
        if (stars >= 7) {
            updated = addTransaction(updated, 3, DiamondTransactionType.SCORE_BONUS, "Bónus de 7 ou mais estrelas")
        }
        if (stars == 10) {
            updated = addTransaction(updated, 5, DiamondTransactionType.PERFECT_SCORE, "Pontuação perfeita")
        }
        val todayText = today.toString()
        if (updated.lastDailyDiamondDate != todayText) {
            updated = addTransaction(updated, 3, DiamondTransactionType.DAILY_BONUS, "Primeira sessão do dia")
            updated = updated.copy(lastDailyDiamondDate = todayText)
        }
        val levelKey = "$world-$level"
        if (!updated.completedLevelKeys.contains(levelKey)) {
            updated = addTransaction(updated, 8, DiamondTransactionType.LEVEL_COMPLETION, "Primeira conclusão do nível $world.$level")
            updated = updated.copy(completedLevelKeys = updated.completedLevelKeys + levelKey)
        }
        val worldKeys = (1..10).map { "$world-$it" }.toSet()
        if (updated.completedLevelKeys.containsAll(worldKeys) && !updated.completedWorldsForRewards.contains(world)) {
            updated = addTransaction(updated, 20, DiamondTransactionType.WORLD_COMPLETION, "Mundo $world completo")
            updated = updated.copy(completedWorldsForRewards = updated.completedWorldsForRewards + world)
        }
        return updated
    }

    fun rewardNewWordRecording(progress: GameProgress, word: String, today: LocalDate = LocalDate.now()): GameProgress {
        val clean = word.trim()
        if (clean.isBlank() || progress.uniqueRecordedWordsRewarded.contains(clean)) return progress
        val todayText = today.toString()
        val todayCount = progress.recordingRewardDates.values.count { it == todayText }
        if (todayCount >= 5) return progress.copy(
            uniqueRecordedWordsRewarded = progress.uniqueRecordedWordsRewarded + clean,
            recordingRewardDates = progress.recordingRewardDates + (clean to todayText),
        )
        val updated = addTransaction(progress, 1, DiamondTransactionType.NEW_WORD_RECORDING, "Nova gravação: $clean")
        return updated.copy(
            uniqueRecordedWordsRewarded = updated.uniqueRecordedWordsRewarded + clean,
            recordingRewardDates = updated.recordingRewardDates + (clean to todayText),
        )
    }
}
