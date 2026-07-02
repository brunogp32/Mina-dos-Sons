package com.brunogp.minasdossons.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.ui.components.BlockProgressBar
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun ProgressScreen(
    vm: AppViewModel,
    nav: NavController,
) {
    val p by vm.progress.collectAsState()
    MineScreen {
        ScreenTitle("Progresso")
        PixelCard {
            Text("⭐ Estrelas totais: ${p.totalStars}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Sessões concluídas: ${p.completedSessions}", fontSize = 18.sp)
            Text("Dias treinados: ${p.trainingDays}", fontSize = 18.sp)
            Text("Melhor pontuação: ${p.bestScore}/10", fontSize = 18.sp)
            Text("Mundo atual: ${p.currentWorld}", fontSize = 18.sp)
            Text("Nível atual: ${p.currentLevel}", fontSize = 18.sp)
            Text("Gravações feitas: ${p.recordingsCount}", fontSize = 18.sp)
        }
        PixelCard {
            Text("Sons mais treinados", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            LocalGameData.targets.forEach {
                Text("${it.displaySound}: ${p.soundStats[it.id] ?: 0}", fontSize = 17.sp)
                BlockProgressBar((p.soundStats[it.id] ?: 0).coerceAtMost(10), 10)
            }
        }
        PixelCard {
            Text("Medalhas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(p.medals.ifEmpty { setOf("Ainda por descobrir") }.joinToString("\n"), fontSize = 17.sp)
        }
        BackButton(nav)
    }
}
