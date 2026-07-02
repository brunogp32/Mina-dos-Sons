package com.brunogp.minasdossons

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.brunogp.minasdossons.audio.AudioPlayer
import com.brunogp.minasdossons.audio.AudioRecorder
import com.brunogp.minasdossons.audio.ReferenceAudioPlayer
import com.brunogp.minasdossons.audio.ReferenceAudioRepository
import com.brunogp.minasdossons.audio.ReferenceSound
import com.brunogp.minasdossons.audio.SpeechHelper
import com.brunogp.minasdossons.data.cards.CardItem
import com.brunogp.minasdossons.data.cards.CardPurchaseResult
import com.brunogp.minasdossons.data.cards.CardRepository
import com.brunogp.minasdossons.data.GameProgress
import com.brunogp.minasdossons.data.MinimalPair
import com.brunogp.minasdossons.data.ProgressRepository
import com.brunogp.minasdossons.data.rewards.ChestRewardEngine
import com.brunogp.minasdossons.data.rewards.ChestOpeningResult
import com.brunogp.minasdossons.data.rewards.ChestType
import com.brunogp.minasdossons.data.rewards.DiamondRepository
import com.brunogp.minasdossons.data.rewards.DiamondTransactionType
import com.brunogp.minasdossons.game.GameEngine
import com.brunogp.minasdossons.game.RewardEngine
import com.brunogp.minasdossons.navigation.AppNavGraph
import com.brunogp.minasdossons.ui.theme.MinasDosSonsTheme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MinasDosSonsTheme {
                AppNavGraph()
            }
        }
    }
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ProgressRepository(application)
    val gameEngine = GameEngine()
    val rewardEngine = RewardEngine()
    val chestRewardEngine = ChestRewardEngine()
    val speech = SpeechHelper(application)
    val recorder = AudioRecorder(application)
    val player = AudioPlayer()
    val referenceAudioRepository = ReferenceAudioRepository(application)
    val referenceAudioPlayer = ReferenceAudioPlayer(application, referenceAudioRepository)
    private var referenceSequenceJob: Job? = null

    val progress: StateFlow<GameProgress> = repository.progress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        GameProgress(),
    )

    fun save(progress: GameProgress) {
        viewModelScope.launch { repository.saveProgress(progress) }
    }

    fun playTextOrReference(text: String) {
        referenceSequenceJob?.cancel()
        val reference = ReferenceSound.fromDisplayText(text)
        if (reference != null) {
            referenceAudioPlayer.playReferenceSound(reference)
            return
        }
        val soundsInText = ReferenceSound.entries
            .mapNotNull { sound ->
                val index = text.indexOf(sound.displayText, ignoreCase = true)
                if (index >= 0) index to sound else null
            }
            .sortedBy { it.first }
            .map { it.second }
        if (soundsInText.isNotEmpty()) {
            referenceSequenceJob = viewModelScope.launch {
                soundsInText.forEachIndexed { index, sound ->
                    if (index > 0) delay(1_050L)
                    referenceAudioPlayer.playReferenceSound(sound)
                }
            }
            return
        }
        speech.speak(text, progress.value.slowVoice, progress.value.ttsEnabled)
    }

    fun stopExerciseAudio() {
        referenceSequenceJob?.cancel()
        referenceSequenceJob = null
        referenceAudioPlayer.stop()
        player.stop()
        speech.stop()
    }

    fun completeSession(world: Int, level: Int, stars: Int) {
        viewModelScope.launch {
            val trained = repository.recordTrainingDay(progress.value)
            val sticker = rewardEngine.nextSticker(trained.stickersUnlocked)
            val progressed = gameEngine.progressAfterSession(trained, world, level, stars)
            val withDiamonds = DiamondRepository.rewardSession(progressed, stars, world, level)
            val updated = CardRepository.migrateLegacyRewards(
                withDiamonds.copy(
                    stickersUnlocked = trained.stickersUnlocked + listOfNotNull(sticker),
                    unopenedChests = withDiamonds.unopenedChests + ChestType.fromStars(stars),
                )
            )
            repository.saveProgress(updated)
        }
    }

    fun addRecording(targetId: String, recordingName: String = progress.value.lastRecordingName, recordingPath: String = progress.value.lastRecordingPath) {
        val p = progress.value
        val withRecording = p.copy(
                recordingsCount = p.recordingsCount + 1,
                soundStats = p.soundStats + (targetId to ((p.soundStats[targetId] ?: 0) + 1)),
                lastRecordingName = recordingName,
                lastRecordingPath = recordingPath,
                recordingsByName = if (recordingName.isNotBlank() && recordingPath.isNotBlank()) {
                    p.recordingsByName + (recordingName to recordingPath)
                } else {
                    p.recordingsByName
                },
            )
        save(DiamondRepository.rewardNewWordRecording(withRecording, recordingName))
    }

    fun rememberLastRecording(name: String, path: String) {
        val p = progress.value
        save(
            p.copy(
                lastRecordingName = name,
                lastRecordingPath = path,
                recordingsByName = if (name.isNotBlank() && path.isNotBlank()) p.recordingsByName + (name to path) else p.recordingsByName,
            )
        )
    }

    fun addCustomMinimalPair(wordA: String, wordB: String, targetA: String, targetB: String) {
        val cleanA = wordA.trim().lowercase()
        val cleanB = wordB.trim().lowercase()
        if (cleanA.isBlank() || cleanB.isBlank()) return
        val pair = MinimalPair(
            id = "custom-${targetA}-${targetB}-${cleanA}-${cleanB}-${System.currentTimeMillis()}",
            wordA = cleanA,
            wordB = cleanB,
            targetA = targetA,
            targetB = targetB,
            hint = "Par adicionado no Modo Pais",
            isCommonPortuguese = true,
            editable = true,
        )
        val p = progress.value
        save(p.copy(customMinimalPairs = p.customMinimalPairs + pair))
    }

    fun unlockAll() = save(progress.value.copy(unlockedWorlds = (1..10).toSet(), allUnlockedByParent = true))
    fun lockByProgress() = save(progress.value.copy(unlockedWorlds = setOf(1), allUnlockedByParent = false, currentWorld = 1, currentLevel = 1))
    fun reset() = viewModelScope.launch { repository.reset() }
    fun addParentDiamonds() = save(DiamondRepository.addTransaction(progress.value, 100, DiamondTransactionType.PARENT_ADJUSTMENT, "Ferramenta dos pais"))

    fun openNextChest(): ChestType? {
        val chest = progress.value.unopenedChests.firstOrNull() ?: return null
        val (_, updated) = chestRewardEngine.openChest(progress.value, chest)
        save(updated)
        return chest
    }

    fun openNextChestResult(): ChestOpeningResult? {
        val chest = progress.value.unopenedChests.firstOrNull() ?: return null
        val (result, updated) = chestRewardEngine.openChest(progress.value, chest)
        save(updated)
        return result
    }

    fun openChestPreview(chest: ChestType) = chestRewardEngine.openChest(progress.value, chest)

    fun buyCard(card: CardItem): Boolean {
        return purchaseCard(card.id) is CardPurchaseResult.Success
    }

    fun purchaseCard(cardId: String, inProgress: Boolean = false): CardPurchaseResult {
        val result = CardRepository.purchase(CardRepository.migrateLegacyRewards(progress.value), cardId, inProgress)
        if (result is CardPurchaseResult.Success) save(result.progress)
        return result
    }

    fun markCardViewed(cardId: String) {
        save(CardRepository.markViewed(CardRepository.migrateLegacyRewards(progress.value), cardId))
    }

    override fun onCleared() {
        referenceSequenceJob?.cancel()
        speech.shutdown()
        recorder.stop()
        player.stop()
        referenceAudioPlayer.stop()
        super.onCleared()
    }
}
