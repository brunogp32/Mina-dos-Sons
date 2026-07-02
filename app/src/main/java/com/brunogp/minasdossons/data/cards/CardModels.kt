package com.brunogp.minasdossons.data.cards

enum class SoundFocus(
    val label: String,
    val displaySound: String,
    val vibrates: Boolean,
) {
    S("S", "SSSS", false),
    Z("Z", "ZZZZ", true),
    X_CH("X/CH", "CHHHH", false),
    J("J", "JJJJ", true),
    MIXED("Misto", "Sons", false),
}

enum class CardRarity(
    val label: String,
    val shortLabel: String,
    val stars: String,
    val symbol: String,
    val priceRange: IntRange,
) {
    BRONZE("Bronze - Comum", "Bronze", "*", "B", 15..35),
    SILVER("Prata - Invulgar", "Prata", "**", "P", 40..75),
    GOLD("Ouro - Rara", "Ouro", "***", "O", 90..160),
    CRYSTAL("Cristal - Épica", "Cristal", "****", "C", 190..320),
    RAINBOW("Arco-íris - Lendária", "Arco-íris", "*****", "A", 400..650),
}

enum class CardIllustrationType {
    SNAKE_SOUND,
    BEE_SOUND,
    RAIN_SOUND,
    LADYBIRD_SOUND,
    LETTER,
    SYLLABLE,
    WORD,
    WAVE,
    CRYSTAL,
    MINE_TREASURE,
    VOICE,
    LISTENING,
}

data class CardItem(
    val id: String,
    val name: String,
    val description: String,
    val collection: String,
    val cardNumber: Int,
    val rarity: CardRarity,
    val soundFocus: SoundFocus,
    val word: String,
    val illustrationType: CardIllustrationType,
    val primaryColor: Long,
    val secondaryColor: Long,
    val pattern: String,
    val power: Int,
    val educationalHint: String,
    val price: Int,
    val chestEligible: Boolean,
    val shopEligible: Boolean,
    val legacyRewardId: String? = null,
)

sealed interface CardPurchaseResult {
    data class Success(
        val progress: com.brunogp.minasdossons.data.GameProgress,
        val card: CardItem,
    ) : CardPurchaseResult

    data object AlreadyOwned : CardPurchaseResult

    data object InsufficientDiamonds : CardPurchaseResult

    data object InvalidCard : CardPurchaseResult

    data object InProgress : CardPurchaseResult

    data object Error : CardPurchaseResult
}

sealed interface CardPurchaseUiState {
    data object Idle : CardPurchaseUiState

    data class Confirming(
        val card: CardItem,
    ) : CardPurchaseUiState

    data class Processing(
        val card: CardItem,
    ) : CardPurchaseUiState

    data class Success(
        val card: CardItem,
    ) : CardPurchaseUiState

    data class Error(
        val message: String,
    ) : CardPurchaseUiState
}
