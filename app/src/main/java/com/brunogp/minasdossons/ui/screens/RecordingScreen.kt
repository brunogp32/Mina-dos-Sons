package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.FourSecondRecorder
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun RecordingScreen(vm: AppViewModel, nav: NavController) {
    val progress by vm.progress.collectAsState()
    var selected by remember { mutableStateOf(LocalGameData.targets.first()) }
    var prompt by remember { mutableStateOf(selected.displaySound) }

    MineScreen {
        ScreenTitle("Gravar a minha voz")
        PixelCard {
            Text("A gravação fica só neste telemóvel.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Escolhe, ouve e grava durante 4 segundos.", fontSize = 17.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            LocalGameData.targets.forEach { target ->
                BlockButton(
                    target.displaySound,
                    {
                        selected = target
                        prompt = target.displaySound
                    },
                    Modifier.weight(1f),
                    color = if (selected.id == target.id) Color(0xFFD6A22A) else Color(0xFF5AA469),
                )
            }
        }
        selected.exampleWords.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { word ->
                    BlockButton(
                        word,
                        { prompt = word },
                        Modifier.weight(1f),
                        color = if (prompt == word) Color(0xFFD6A22A) else Color(0xFF8B6BB1),
                    )
                }
            }
        }
        FourSecondRecorder(
            selectedText = prompt,
            characterLabel = "${selected.emoji} ${selected.characterName}",
            recorder = vm.recorder,
            player = vm.player,
            onListen = { vm.playTextOrReference(prompt) },
            onSaved = { file -> vm.addRecording(selected.id, prompt, file.absolutePath) },
            helperText = if (progress.largeText) "Agora grava a tua voz." else "Agora és tu!",
        )
        BackButton(nav)
    }
}
