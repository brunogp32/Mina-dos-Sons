package com.brunogp.minasdossons.game

import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.data.MinimalPair
import com.brunogp.minasdossons.data.SoundTarget
import kotlin.random.Random

class GameEngine(
    private val random: Random = Random.Default,
) {
    fun generateSession(
        world: Int,
        level: Int,
        easyMode: Boolean,
        minimalPairs: List<MinimalPair> = LocalGameData.minimalPairs,
    ): List<Question> {
        val targets = LocalGameData.targetsForWorld(world)
        val types =
            when (level) {
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

    fun isWorldUnlocked(
        progress: GameProgress,
        world: Int,
    ): Boolean {
        if (world == 1 || progress.allUnlockedByParent) return true
        return progress.unlockedWorlds.contains(world)
    }

    fun progressAfterSession(
        progress: GameProgress,
        world: Int,
        level: Int,
        stars: Int,
    ): GameProgress {
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
            Question(
                id = "who-${target.id}-$index",
                type = type,
                prompt = "Quem faz ${target.displaySound}?",
                options = options,
                correctAnswer = target.characterName,
                targetSound = target.id,
                explanation = target.shortHint,
                difficulty = 1,
                speakText = target.displaySound,
            )
        }

        QuestionType.THROAT_VIBRATES -> {
            Question(
                "vibra-${target.id}-$index",
                type,
                "Este som faz a garganta vibrar?",
                listOf("Sim, vibra", "Não vibra"),
                if (target.throatVibrates) "Sim, vibra" else "Não vibra",
                target.id,
                if (target.throatVibrates) {
                    "Muito bem! O som ${target.displaySound} faz vibrar."
                } else {
                    "Muito bem! O som ${target.displaySound} não faz vibrar."
                },
                1,
                target.displaySound,
            )
        }

        QuestionType.SAME_OR_DIFFERENT -> {
            val alternatives =
                worldTargets
                    .filter { it.id != target.id }
                    .ifEmpty { LocalGameData.targets }
            val second = if (random.nextBoolean()) target else alternatives.random(random)
            val same = second.id == target.id
            Question(
                "same-${target.id}-${second.id}-$index",
                type,
                "${target.displaySound} | ${second.displaySound}",
                listOf("Igual", "Diferente"),
                if (same) "Igual" else "Diferente",
                target.id,
                "Ouve com calma e compara os sons.",
                2,
                "${target.displaySound}. ${second.displaySound}",
            )
        }

        QuestionType.CHOOSE_WORD -> {
            val availablePairs = minimalPairs.ifEmpty { LocalGameData.minimalPairs }
            val pair = pairForTarget(availablePairs, target)
            val heard = if (pair.targetA == target.id) pair.wordA else pair.wordB
            Question(
                id = "word-${pair.id}-$heard-$index",
                type = type,
                prompt = "Qual palavra ouviste?",
                options = listOf(pair.wordA, pair.wordB).shuffled(random),
                correctAnswer = heard,
                targetSound = target.id,
                explanation = pair.hint,
                difficulty = 2,
                speakText = heard,
            )
        }

        QuestionType.BUILD_SOUND -> {
            Question(
                id = "build-${target.id}-$index",
                type = type,
                prompt = "Constrói ${target.displaySound}",
                options = listOf("S", "Z", "X", "J"),
                correctAnswer = target.letter.take(1),
                targetSound = target.id,
                explanation = "Escolhe a letra do som.",
                difficulty = 2,
                speakText = target.displaySound,
            )
        }

        QuestionType.RECORD_SOUND -> {
            Question(
                id = "record-${target.id}-$index",
                type = type,
                prompt = "Faz o som: ${target.displaySound}",
                options = listOf("Ficou bom"),
                correctAnswer = "Ficou bom",
                targetSound = target.id,
                explanation = "Grava, ouve e decide com calma.",
                difficulty = 1,
                speakText = target.displaySound,
            )
        }

        QuestionType.FIND_SOUND_IN_WORD -> {
            val word = target.exampleWords.random(random)
            val options =
                if (easyMode) {
                    val other = worldTargets.filter { it.id != target.id }.ifEmpty { LocalGameData.targets }.random(random)
                    listOf(target.displaySound, other.displaySound).shuffled(random)
                } else {
                    LocalGameData.targets.map { it.displaySound }.shuffled(random)
                }
            Question(
                id = "find-${target.id}-$word-$index",
                type = type,
                prompt = "Que som ouves em \"$word\"?",
                options = options,
                correctAnswer = target.displaySound,
                targetSound = target.id,
                explanation = "Ouve a palavra e procura o som.",
                difficulty = 2,
                speakText = word,
            )
        }

        QuestionType.COMPLETE_SENTENCE -> {
            val availablePairs = minimalPairs.ifEmpty { LocalGameData.minimalPairs }
            val pair = pairForTarget(availablePairs, target)
            val heard = if (pair.targetA == target.id) pair.wordA else pair.wordB
            val sentence = LocalGameData.phraseHints[heard] ?: "Escolhe a palavra que ouviste: ____."
            Question(
                id = "sentence-${pair.id}-$heard-$index",
                type = type,
                prompt = sentence,
                options = listOf(pair.wordA, pair.wordB).shuffled(random),
                correctAnswer = heard,
                targetSound = target.id,
                explanation = "A frase ajuda a ouvir o significado.",
                difficulty = 3,
                speakText = heard,
            )
        }
    }

    private fun pairForTarget(
        availablePairs: List<MinimalPair>,
        target: SoundTarget,
    ): MinimalPair = availablePairs
        .filter { it.targetA == target.id || it.targetB == target.id }
        .ifEmpty { availablePairs }
        .random(random)

    private fun optionNames(
        target: SoundTarget,
        easyMode: Boolean,
    ): List<String> {
        val all = LocalGameData.targets.map { it.characterName }
        if (!easyMode) return all.shuffled(random)
        val distractor = LocalGameData.targets.first { it.id != target.id }.characterName
        return listOf(target.characterName, distractor).shuffled(random)
    }
}
