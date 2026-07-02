package com.brunogp.minasdossons

import com.brunogp.minasdossons.audio.ReferenceSound
import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.cards.CardCatalog
import com.brunogp.minasdossons.data.cards.CardPurchaseResult
import com.brunogp.minasdossons.data.cards.CardRarity
import com.brunogp.minasdossons.data.cards.CardRepository
import com.brunogp.minasdossons.data.cards.SoundFocus
import com.brunogp.minasdossons.data.rewards.ChestRewardEngine
import com.brunogp.minasdossons.data.rewards.ChestType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class CardSystemTest {
    @Test
    fun catalogHasFiveHundredValidCards() {
        val cards = CardCatalog.cards
        assertEquals(500, cards.size)
        assertEquals(cards.size, cards.map { it.id }.toSet().size)
        assertTrue(cards.all { it.name.isNotBlank() && it.educationalHint.isNotBlank() })
        assertTrue(cards.all { it.price in it.rarity.priceRange })
        assertTrue(SoundFocus.entries.all { focus -> cards.any { it.soundFocus == focus } })
    }

    @Test
    fun rarityDistributionMatchesRevisionTarget() {
        val counts = CardCatalog.cards.groupingBy { it.rarity }.eachCount()
        assertEquals(250, counts[CardRarity.BRONZE])
        assertEquals(125, counts[CardRarity.SILVER])
        assertEquals(75, counts[CardRarity.GOLD])
        assertEquals(35, counts[CardRarity.CRYSTAL])
        assertEquals(15, counts[CardRarity.RAINBOW])
    }

    @Test
    fun cardNumbersAreUniqueInsideCollection() {
        CardCatalog.cards.groupBy { it.collection }.forEach { (_, cards) ->
            assertEquals(cards.size, cards.map { it.cardNumber }.toSet().size)
        }
    }

    @Test
    fun purchaseIsAtomicAndNeverNegative() {
        val card = CardCatalog.cards.first { it.shopEligible }
        val result = CardRepository.purchase(GameProgress(diamondBalance = card.price), card.id)
        assertTrue(result is CardPurchaseResult.Success)
        val updated = (result as CardPurchaseResult.Success).progress
        assertEquals(0, updated.diamondBalance)
        assertTrue(updated.ownedRewardIds.contains(card.id))
        assertTrue(updated.newRewardIds.contains(card.id))
    }

    @Test
    fun purchaseRejectsMissingOwnedAndInsufficientCases() {
        val card = CardCatalog.cards.first { it.price > 20 }
        assertEquals(
            CardPurchaseResult.InsufficientDiamonds,
            CardRepository.purchase(GameProgress(diamondBalance = card.price - 1), card.id),
        )
        assertEquals(
            CardPurchaseResult.AlreadyOwned,
            CardRepository.purchase(GameProgress(diamondBalance = 999, ownedRewardIds = setOf(card.id)), card.id),
        )
        assertEquals(CardPurchaseResult.InvalidCard, CardRepository.purchase(GameProgress(diamondBalance = 999), "missing"))
        assertEquals(CardPurchaseResult.InProgress, CardRepository.purchase(GameProgress(diamondBalance = 999), card.id, inProgress = true))
    }

    @Test
    fun duplicateChestCardConvertsToDiamonds() {
        val card = CardCatalog.cards.first { it.rarity == CardRarity.BRONZE }
        val progress = GameProgress(ownedRewardIds = setOf(card.id))
        val engine = ChestRewardEngine(Random(3))
        val (_, updated) = engine.openChest(progress, ChestType.WOOD)
        assertTrue(updated.diamondBalance >= 2)
        assertFalse(updated.diamondBalance < 0)
    }

    @Test
    fun legacyRewardsMigrateToCards() {
        val migrated = CardRepository.migrateLegacyRewards(GameProgress(ownedRewardIds = setOf("character_1", "hat_2")))
        assertTrue(migrated.ownedRewardIds.contains("card_001"))
        assertTrue(migrated.ownedRewardIds.contains("card_027"))
        assertNotNull(CardCatalog.card("character_1"))
    }

    @Test
    fun referenceSoundsUseCorrectVibrationMapping() {
        assertFalse(ReferenceSound.S.vibrates)
        assertTrue(ReferenceSound.Z.vibrates)
        assertFalse(ReferenceSound.CH.vibrates)
        assertTrue(ReferenceSound.J.vibrates)
        assertEquals("som_s", ReferenceSound.S.bundledRawResourceName)
        assertEquals("som_z", ReferenceSound.Z.bundledRawResourceName)
        assertEquals("som_x", ReferenceSound.CH.bundledRawResourceName)
        assertEquals("som_j", ReferenceSound.J.bundledRawResourceName)
    }
}
