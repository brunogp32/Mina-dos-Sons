package com.brunogp.minasdossons.data.rewards

import com.brunogp.minasdossons.data.cards.CardItem

enum class ChestType(
    val label: String,
    val prizeCount: Int,
    val diamondRange: IntRange,
    val message: String,
) {
    WOOD("Baú de Madeira", 1, 2..4, "Boa tentativa! Ganhaste um baú!"),
    IRON("Baú de Ferro", 2, 4..7, "Muito bem! Ganhaste um baú de ferro!"),
    GOLD("Baú Dourado", 3, 7..12, "Excelente! Ganhaste um baú dourado!"),
    CRYSTAL("Baú de Cristal", 4, 12..20, "Perfeito! Descobriste um baú de cristal!");

    companion object {
        fun fromStars(stars: Int): ChestType = when {
            stars <= 4 -> WOOD
            stars <= 7 -> IRON
            stars <= 9 -> GOLD
            else -> CRYSTAL
        }
    }
}

data class ChestReward(
    val item: CardItem,
    val isDuplicate: Boolean,
    val duplicateDiamonds: Int,
)

data class ChestOpeningResult(
    val chestType: ChestType,
    val rewards: List<ChestReward>,
    val diamonds: Int,
    val updatedOwnedIds: Set<String>,
    val rarePityCounter: Int,
    val epicPityCounter: Int,
    val legendaryPityCounter: Int,
)

enum class DiamondTransactionType {
    SESSION_COMPLETION,
    FIRST_TRY_CORRECT,
    SCORE_BONUS,
    PERFECT_SCORE,
    DAILY_BONUS,
    LEVEL_COMPLETION,
    WORLD_COMPLETION,
    NEW_WORD_RECORDING,
    MISSION_REWARD,
    CHEST_REWARD,
    SHOP_PURCHASE,
    DUPLICATE_CONVERSION,
    PARENT_ADJUSTMENT,
}

data class DiamondTransaction(
    val id: String,
    val amount: Int,
    val type: DiamondTransactionType,
    val description: String,
    val timestamp: Long,
)
