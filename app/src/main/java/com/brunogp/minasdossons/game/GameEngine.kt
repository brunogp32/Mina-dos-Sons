package com.brunogp.minasdossons.game

import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.data.MinimalPair
import com.brunogp.minasdossons.data.SoundTarget
import kotlin.random.Random

class GameEngine(private val random: Random = Random.Default) {
    fun generateSession(
        world: Int,
        level: Int,
        easyMode: Boolean,
        minimalPairs: List<MinimalPair> = LocalGameData.minimalPairs,
    ): List<Question> {
        val targets = LocalGameData.targetsForWorld(world)
        val types = when (level) {
            1 -> listOf(QuestionType.WHO_MAKES_SOUND, QuestionType.CHOOSE_WORD)
            2 -> listOf(QuestionType.THROAT_VIBRATES)
            3 -> listOf(QuestionType.SAME_OR_DIFFERENT)
            4 -> listOf(QuestionType.FIND_SOUND_IN_WORD, QuestionType.BUILD_SOUND)
            5 -> listOf(QuestionType.CHOOSE_WORD, QuestionType.COMPLETE_SENTENCE)
            6 -> listOf(QuestionType.RECORD_SOUND, QuestionType.CHOOSE_WORD)
            7 -> listOf(QuestionType.WHO_MAKES_SOUND, QuestionType.THROAT_VIBRATES, QuestionType.FIND_SOUND_IN_WORD)
            8 -> listOf(QuestionType.SAME_OR_DIFFERENT, QuestionType.CHOOSE_WORD, QuestionType.COMPLETE_SENTENCE)
            9 -> listOf(QuestionType.BUILD_SOUND, QuestionType.CHOOSE_WORD, QuestionType.RECORD_SOUND)
            else -> QuestionType.entries
        }
        val questions = mutableListOf<Question>()
        var lastId = ""
        repeat(10) { index ->
            var question: Question
            var guard = 0
            do {
                val target = targets.random(random)
                question = buildQuestion(index, types.random(random), target, targets, easyMode, minimalPairs)
                guard++
            } while (question.id == lastId && guard < 6)
            questions += question
            lastId = question.id
        }
        return questions
    }

    fun isWorldUnlocked(progress: GameProgress, world: Int): Boolean {
        if (world == 1 || progress.allUnlockedByParent) return true
        return progress.unlockedWorlds.contains(world)
    }

    fun progressAfterSession(progress: GameProgress, world: Int, level: Int, stars: Int): GameProgress {
        val unlocked = progress.unlockedWorlds.toMutableSet()
        if (stars >= 7 && level >= 10 && world < 10) unlocked += world + 1
        val medal = if (stars == 10) setOf("Medalha perfeita mundo $world") else emptySet()
        return progress.copy(
            totalStars = progress.totalStars + stars,
            completedSessions = progress.completedSessions + 1,
            currentWorld = if (stars >= 7 && level >= 10) (world + 1).coerceAtMost(10) else world,
            currentLevel = if (stars >= 7) (level + 1).coerceAtMost(10) else level,
            unlockedWorlds = unlocked,
            bestScore = maxOf(progress.bestScore, stars),
            medals = progress.medals + medal,
        )
    }

    private fun buildQuestion(
        index: Int,
        type: QuestionType,
        target: SoundTarget,
        worldTargets: List<SoundTarget>,
        easyMode: Boolean,
        minimalPairs: List<MinimalPair>,
    ): Question = when (type) {
        QuestionType.WHO_MAKES_SOUND -> {
            val options = optionNames(target, easyMode)
            Question("who-${target.id}-$index", type, "Quem faz ${target.displaySound}?", options, target.characterName, target.id, target.shortHint, 1, target.displaySound)
        }
        QuestionType.THROAT_VIBRATES -> Question(
            "vibra-${target.id}-$index",
            type,
            "Este som faz a garganta vibrar?",
            listOf("Sim, vibra", "Não vibra"),
            if (target.throatVibrates) "Sim, vibra" else "Não vibra",
            target.id,
            if (target.throatVibrates) "Muito bem! O som ${target.displaySound} faz vibrar." else "Muito bem! O som ${target.displaySound} não faz vibrar.",
            1,
            target.displaySound,
        )
        QuestionType.SAME_OR_DIFFERENT -> {
            val second = if (random.nextBoolean()) target else worldTargets.filter { it.id != target.id }.ifEmpty { LocalGameData.targets }.random(random)
            val same = second.id == target.id
            Question("same-${target.id}-${second.id}-$index", type, "${target.displaySound} | ${second.displaySound}", listOf("Igual", "Diferente"), if (same) "Igual" else "Diferente", target.id, "Ouve com calma e compara os sons.", 2, "${target.displaySound}. ${second.displaySound}")
        }
        QuestionType.CHOOSE_WORD -> {
            val availablePairs = minimalPairs.ifEmpty { LocalGameData.minimalPairs }
            val pair = availablePairs.filter { it.targetA == target.id || it.targetB == target.id }.ifEmpty { availablePairs }.random(random)
            val heard = if (pair.targetA == target.id) pair.wordA else pair.wordB
            Question("word-${pair.id}-$heard-$index", type, "Qual palavra ouviste?", listOf(pair.wordA, pair.wordB).shuffled(random), heard, target.id, pair.hint, 2, heard)
        }
        QuestionType.BUILD_SOUND -> Question("build-${target.id}-$index", type, "Constrói ${target.displaySound}", listOf("S", "Z", "X", "J"), target.letter.take(1), target.id, "Escolhe a letra do som.", 2, target.displaySound)
        QuestionType.RECORD_SOUND -> Question("record-${target.id}-$index", type, "Faz o som: ${target.displaySound}", listOf("Ficou bom"), "Ficou bom", target.id, "Grava, ouve e decide com calma.", 1, target.displaySound)
        QuestionType.FIND_SOUND_IN_WORD -> {
            val word = target.exampleWords.random(random)
            val options = if (easyMode) {
                val other = worldTargets.filter { it.id != target.id }.ifEmpty { LocalGameData.targets }.random(random)
                listOf(target.displaySound, other.displaySound).shuffled(random)
            } else {
                LocalGameData.targets.map { it.displaySound }.shuffled(random)
            }
            Question("find-${target.id}-$word-$index", type, "Que som ouves em \"$word\"?", options, target.displaySound, target.id, "Ouve a palavra e procura o som.", 2, word)
        }
        QuestionType.COMPLETE_SENTENCE -> {
            val availablePairs = minimalPairs.ifEmpty { LocalGameData.minimalPairs }
            val pair = availablePairs.filter { it.targetA == target.id || it.targetB == target.id }.ifEmpty { availablePairs }.random(random)
            val heard = if (pair.targetA == target.id) pair.wordA else pair.wordB
            val sentence = LocalGameData.phraseHints[heard] ?: "Escolhe a palavra que ouviste: ____."
            Question("sentence-${pair.id}-$heard-$index", type, sentence, listOf(pair.wordA, pair.wordB).shuffled(random), heard, target.id, "A frase ajuda a ouvir o significado.", 3, heard)
        }
    }

    private fun optionNames(target: SoundTarget, easyMode: Boolean): List<String> {
        val all = LocalGameData.targets.map { it.characterName }
        if (!easyMode) return all.shuffled(random)
        val distractor = LocalGameData.targets.first { it.id != target.id }.characterName
        return listOf(target.characterName, distractor).shuffled(random)
    }
}
