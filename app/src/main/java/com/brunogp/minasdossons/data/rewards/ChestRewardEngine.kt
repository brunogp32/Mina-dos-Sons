package com.brunogp.minasdossons.data.rewards

import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.cards.CardCatalog
import com.brunogp.minasdossons.data.cards.CardRarity
import kotlin.math.ceil
import kotlin.random.Random

class ChestRewardEngine(
    private val random: Random = Random.Default,
) {
    fun openChest(
        progress: GameProgress,
        chestType: ChestType,
    ): Pair<ChestOpeningResult, GameProgress> {
        val forced = forcedRarity(progress)
        val rewards = mutableListOf<ChestReward>()
        var owned = progress.ownedRewardIds
        var duplicateDiamonds = 0
        val rarities = MutableList(chestType.prizeCount) { chooseRarity(chestType) }
        if (forced != null) rarities[0] = forced

        rarities.forEach { rarity ->
            val candidates =
                CardCatalog.cards
                    .filter { it.chestEligible && it.rarity == rarity }
                    .ifEmpty { CardCatalog.cards.filter { it.chestEligible } }
            val item = candidates.random(random)
            val duplicate = owned.contains(item.id)
            val conversion = if (duplicate) ceil(item.price * 0.25).toInt().coerceAtLeast(1) else 0
            if (!duplicate) owned = owned + item.id
            duplicateDiamonds += conversion
            rewards += ChestReward(item, duplicate, conversion)
        }

        val baseDiamonds = random.nextInt(chestType.diamondRange.first, chestType.diamondRange.last + 1)
        val chestDiamonds = baseDiamonds + duplicateDiamonds
        val obtained = rarities.toSet()
        val rareCounter = if (obtained.any { it.ordinal >= CardRarity.GOLD.ordinal }) 0 else progress.rarePityCounter + 1
        val epicCounter = if (obtained.any { it.ordinal >= CardRarity.CRYSTAL.ordinal }) 0 else progress.epicPityCounter + 1
        val legendaryCounter = if (obtained.contains(CardRarity.RAINBOW)) 0 else progress.legendaryPityCounter + 1
        val result = ChestOpeningResult(chestType, rewards, chestDiamonds, owned, rareCounter, epicCounter, legendaryCounter)
        var updated =
            progress.copy(
                ownedRewardIds = owned,
                newRewardIds = progress.newRewardIds + rewards.filterNot { it.isDuplicate }.map { it.item.id },
                openedChestCount = progress.openedChestCount + 1,
                rarePityCounter = rareCounter,
                epicPityCounter = epicCounter,
                legendaryPityCounter = legendaryCounter,
                unopenedChests = progress.unopenedChests.drop(1),
                chestHistory = (progress.chestHistory + "${System.currentTimeMillis()}:${chestType.name}").takeLast(50),
            )
        updated =
            DiamondRepository.addTransaction(
                updated,
                baseDiamonds,
                DiamondTransactionType.CHEST_REWARD,
                "${chestType.label}: $baseDiamonds diamantes",
            )
        duplicateDiamonds.takeIf { it > 0 }?.let {
            updated = DiamondRepository.addTransaction(updated, it, DiamondTransactionType.DUPLICATE_CONVERSION, "Duplicados convertidos")
        }
        return result to updated
    }

    private fun forcedRarity(progress: GameProgress): CardRarity? = when {
        progress.legendaryPityCounter >= 24 -> CardRarity.RAINBOW
        progress.epicPityCounter >= 11 -> CardRarity.CRYSTAL
        progress.rarePityCounter >= 4 -> CardRarity.GOLD
        else -> null
    }

    private fun chooseRarity(chestType: ChestType): CardRarity {
        val roll = random.nextInt(100)
        return when (chestType) {
            ChestType.WOOD -> {
                if (roll < 75) CardRarity.BRONZE else CardRarity.SILVER
            }

            ChestType.IRON -> {
                when {
                    roll < 50 -> CardRarity.BRONZE
                    roll < 85 -> CardRarity.SILVER
                    else -> CardRarity.GOLD
                }
            }

            ChestType.GOLD -> {
                when {
                    roll < 45 -> CardRarity.SILVER
                    roll < 85 -> CardRarity.GOLD
                    else -> CardRarity.CRYSTAL
                }
            }

            ChestType.CRYSTAL -> {
                when {
                    roll < 50 -> CardRarity.GOLD
                    roll < 85 -> CardRarity.CRYSTAL
                    else -> CardRarity.RAINBOW
                }
            }
        }
    }
}
