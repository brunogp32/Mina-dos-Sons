package com.brunogp.minasdossons

import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.game.GameEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {
    private val engine = GameEngine(Random(7))

    @Test
    fun sessionHasTenQuestions() {
        assertEquals(10, engine.generateSession(world = 1, level = 1, easyMode = true).size)
    }

    @Test
    fun everyQuestionHasCorrectAnswer() {
        val questions = engine.generateSession(world = 5, level = 6, easyMode = false)
        assertTrue(questions.all { it.correctAnswer.isNotBlank() && it.options.contains(it.correctAnswer) })
    }

    @Test
    fun sevenStarsUnlockNextWorldAfterFinalLevel() {
        val progress = GameProgress(currentWorld = 1, currentLevel = 10, unlockedWorlds = setOf(1))
        val updated = engine.progressAfterSession(progress, world = 1, level = 10, stars = 7)
        assertTrue(updated.unlockedWorlds.contains(2))
    }

    @Test
    fun vibrationRulesMatchTargets() {
        assertFalse(LocalGameData.target("s").throatVibrates)
        assertFalse(LocalGameData.target("x").throatVibrates)
        assertTrue(LocalGameData.target("z").throatVibrates)
        assertTrue(LocalGameData.target("j").throatVibrates)
    }
}
