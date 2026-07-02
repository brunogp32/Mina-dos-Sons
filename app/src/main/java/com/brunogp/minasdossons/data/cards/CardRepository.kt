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

    fun purchase(progress: GameProgress, cardId: String?, inProgress: Boolean = false): CardPurchaseResult {
        if (inProgress) return CardPurchaseResult.InProgress
        val card = cardId?.let(CardCatalog::card) ?: return CardPurchaseResult.InvalidCard
        if (!card.shopEligible) return CardPurchaseResult.InvalidCard
        if (progress.ownedRewardIds.contains(card.id)) return CardPurchaseResult.AlreadyOwned
        if (progress.diamondBalance < card.price) return CardPurchaseResult.InsufficientDiamonds
        val spent = DiamondRepository.spend(progress, card.price, "Compra de carta: ${card.name}") ?: return CardPurchaseResult.InsufficientDiamonds
        val updated = spent.copy(
            ownedRewardIds = spent.ownedRewardIds + card.id,
            newRewardIds = spent.newRewardIds + card.id,
        )
        return CardPurchaseResult.Success(updated, card)
    }

    fun markViewed(progress: GameProgress, cardId: String): GameProgress =
        if (!progress.newRewardIds.contains(cardId) || progress.viewedRewardIds.contains(cardId)) {
            progress
        } else {
            progress.copy(viewedRewardIds = progress.viewedRewardIds + cardId)
        }
}
