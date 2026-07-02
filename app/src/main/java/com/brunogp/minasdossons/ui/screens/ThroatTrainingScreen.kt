package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun ThroatTrainingScreen(vm: AppViewModel, nav: NavController) {
    val progress by vm.progress.collectAsState()
    MineScreen {
        ScreenTitle("Sente a garganta", "Põe dois dedos suavemente na garganta.")
        PixelCard {
            Text("Como funciona?", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Alguns sons fazem a garganta vibrar. Outros quase não fazem vibrar.", fontSize = 18.sp)
            Text("A vibração ajuda-nos a distinguir sons parecidos.", fontSize = 18.sp)
        }
        TutorialStep(
            title = "1. Põe a mão na garganta",
            sound = "SSSS",
            text = "Faz SSSS como a cobra. O som SSSS quase não faz a garganta vibrar.",
            button = { vm.playTextOrReference("SSSS") },
        )
        TutorialStep(
            title = "2. Agora experimenta ZZZZ",
            sound = "ZZZZ",
            text = "Faz ZZZZ como a abelha. O som ZZZZ faz a garganta vibrar.",
            button = { vm.playTextOrReference("ZZZZ") },
        )
        TutorialStep(
            title = "3. Compara",
            sound = "SSSS ou ZZZZ?",
            text = "SSSS não vibra. ZZZZ vibra. Ouve outra vez se precisares.",
            button = { vm.playTextOrReference("SSSS ZZZZ") },
        )
        TutorialStep(
            title = "4. Também acontece com CH e J",
            sound = "CHHHH ou JJJJ?",
            text = "CHHHH não vibra. JJJJ vibra.",
            button = { vm.playTextOrReference("CHHHH JJJJ") },
        )
        PixelCard {
            Text("Resumo", fontSize = 24.sp, fontWeight = FontWeight.Black)
            LocalGameData.targets.forEach { target ->
                Text(
                    "${target.displaySound}: ${if (target.throatVibrates) "vibra" else "não vibra"}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlockButton("Começar treino", { nav.navigate("game/${progress.currentWorld}/2") }, Modifier.weight(1f), color = Color(0xFFD6A22A))
            BlockButton("Ouvir S/Z", { vm.playTextOrReference("SSSS ZZZZ") }, Modifier.weight(1f), color = Color(0xFF4A90A4))
        }
        BackButton(nav)
    }
}

@Composable
private fun TutorialStep(title: String, sound: String, text: String, button: () -> Unit) {
    PixelCard {
        Text(title, fontSize = 22.sp, fontWeight = FontWeight.Black)
        Text(sound, fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color(0xFF4A90A4))
        Text(text, fontSize = 18.sp)
        BlockButton("Ouvir", button, color = Color(0xFF4A90A4))
    }
}
