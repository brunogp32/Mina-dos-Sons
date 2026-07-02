package com.brunogp.minasdossons.data.cards

import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.rewards.DiamondRepository

object CardRepository {
    fun migrateLegacyRewards(progress: GameProgress): GameProgress {
        val owned = CardCatalog.normalizeOwnedIds(progress.ownedRewardIds)
        val fresh = CardCatalog.normalizeOwnedIds(progress.newRewardIds)
        val viewed = CardCatalog.normalizeOwnedIds(progress.viewedRewardIds)
        return progress.copy(
            ownedRewardIds = owned,
            newRewardIds = fresh,
            viewedRewardIds = viewed,
        )
    }

    fun purchase(
        progress: GameProgress,
        cardId: String?,
        inProgress: Boolean = false,
    ): CardPurchaseResult {
        val card = cardId?.let(CardCatalog::card)
        return when {
            inProgress -> CardPurchaseResult.InProgress
            card == null || !card.shopEligible -> CardPurchaseResult.InvalidCard
            progress.ownedRewardIds.contains(card.id) -> CardPurchaseResult.AlreadyOwned
            progress.diamondBalance < card.price -> CardPurchaseResult.InsufficientDiamonds
            else -> purchaseAvailableCard(progress, card)
        }
    }

    private fun purchaseAvailableCard(
        progress: GameProgress,
        card: CardItem,
    ): CardPurchaseResult {
        val spent =
            DiamondRepository.spend(
                progress = progress,
                price = card.price,
                description = "Compra de carta: ${card.name}",
            ) ?: return CardPurchaseResult.InsufficientDiamonds
        val updated =
            spent.copy(
                ownedRewardIds = spent.ownedRewardIds + card.id,
                newRewardIds = spent.newRewardIds + card.id,
            )
        return CardPurchaseResult.Success(updated, card)
    }

    fun markViewed(
        progress: GameProgress,
        cardId: String,
    ): GameProgress = if (!progress.newRewardIds.contains(cardId) || progress.viewedRewardIds.contains(cardId)) {
        progress
    } else {
        progress.copy(viewedRewardIds = progress.viewedRewardIds + cardId)
    }
}
