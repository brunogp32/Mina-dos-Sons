package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.game.QuestionType
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.BlockProgressBar
import com.brunogp.minasdossons.ui.components.FourSecondRecorder
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun GameSessionScreen(vm: AppViewModel, nav: NavController, world: Int, level: Int) {
    val progress by vm.progress.collectAsState()
    val allPairs = LocalGameData.minimalPairs + progress.customMinimalPairs
    val questions = remember(world, level, progress.easyMode, allPairs) {
        vm.gameEngine.generateSession(world, level, progress.easyMode, allPairs)
    }
    var index by remember { mutableIntStateOf(0) }
    var stars by remember { mutableIntStateOf(0) }
    var tried by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf("Ouve e constrói!") }
    val q = questions[index]

    DisposableEffect(Unit) {
        onDispose { vm.stopExerciseAudio() }
    }

    LaunchedEffect(index) {
        vm.stopExerciseAudio()
        tried = false
        feedback = "Boa escuta!"
    }

    fun answer(value: String) {
        if (value == q.correctAnswer) {
            if (!tried) stars++
            feedback = if (q.type == QuestionType.THROAT_VIBRATES) q.explanation else listOf("Boa!", "Muito bem!", "Grande trabalho!").random()
            if (index == questions.lastIndex) {
                vm.stopExerciseAudio()
                vm.completeSession(world, level, stars)
                nav.navigate("result/$stars/$world/$level") { popUpTo("home") }
            } else {
                vm.stopExerciseAudio()
                index++
            }
        } else {
            tried = true
            feedback = if (q.type == QuestionType.THROAT_VIBRATES) {
                "Vamos ouvir outra vez. Põe a mão no pescoço."
            } else {
                listOf("Quase!", "Tenta outra vez", "Vamos ouvir outra vez", "Estás a melhorar!").random()
            }
            vm.playTextOrReference(q.speakText)
        }
    }

    MineScreen {
        ScreenTitle("Missão $world.$level", "Pergunta ${index + 1} de 10")
        BlockProgressBar(index + 1, 10)
        PixelCard {
            Text(q.prompt, fontSize = 31.sp, fontWeight = FontWeight.Black)
            if (q.type == QuestionType.THROAT_VIBRATES) {
                Text("Põe a mão no pescoço.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Text(feedback, fontSize = 20.sp, color = Color(0xFF2F7D32), fontWeight = FontWeight.Bold)
            Text("Estrelas: $stars", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        BlockButton("Ouvir", { vm.playTextOrReference(q.speakText) }, color = Color(0xFF4A90A4))
        when (q.type) {
            QuestionType.RECORD_SOUND -> {
                Text("Ouve primeiro. Agora és tu!", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                FourSecondRecorder(
                    selectedText = q.speakText,
                    characterLabel = "Som de treino",
                    recorder = vm.recorder,
                    player = vm.player,
                    onListen = { vm.playTextOrReference(q.speakText) },
                    onSaved = { file -> vm.addRecording(q.targetSound, q.speakText, file.absolutePath) },
                    helperText = "Grava durante 4 segundos.",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    BlockButton("Ficou bem", { answer("Ficou bom") }, Modifier.weight(1f))
                    BlockButton("Tentar outra vez", { tried = true; feedback = "Vamos ouvir outra vez" }, Modifier.weight(1f), color = Color(0xFF607D8B))
                }
            }
            QuestionType.BUILD_SOUND -> {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    q.options.forEach { option ->
                        BlockButton(option, { answer(option) }, Modifier.weight(1f), color = Color(0xFF8B6BB1))
                    }
                }
            }
            QuestionType.THROAT_VIBRATES -> {
                q.options.forEach { option ->
                    BlockButton(option, { answer(option) }, color = if (option.startsWith("Sim")) Color(0xFF5AA469) else Color(0xFF607D8B))
                }
            }
            else -> q.options.forEach { option ->
                BlockButton(option, { answer(option) })
            }
        }
        BlockButton("Voltar", onClick = {
            vm.stopExerciseAudio()
            nav.popBackStack()
        }, color = Color(0xFF6F6F6F))
    }
}
