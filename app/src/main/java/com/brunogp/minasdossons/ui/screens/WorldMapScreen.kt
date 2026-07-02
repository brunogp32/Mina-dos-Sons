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
fun WorldMapScreen(vm: AppViewModel, nav: NavController) {
    val progress by vm.progress.collectAsState()
    val worlds = listOf(
        "Floresta da Cobra" to "SSSS sem vibração: ouvir, tocar e escolher",
        "Colmeia da Abelha" to "ZZZZ com vibração: voz ligada",
        "Nuvem da Chuva" to "CHHHH / X sem vibração",
        "Jardim da Joaninha" to "JJJJ com vibração",
        "Mina S/Z" to "Pares reais S/Z: casa, caça, doce, doze",
        "Gruta CH/J" to "Pares reais CH/J: chá, já, queixo, queijo",
        "Oficina das Palavras" to "Som dentro da palavra e construção",
        "Castelo das Frases" to "Escolher pela frase e pelo significado",
        "Laboratório da Voz" to "Gravar, ouvir e comparar",
        "Portal dos Sons" to "Mistura final com todos os exercícios",
    )
    MineScreen {
        ScreenTitle("Mapa dos níveis")
        worlds.forEachIndexed { index, world ->
            val number = index + 1
            val unlocked = vm.gameEngine.isWorldUnlocked(progress, number)
            PixelCard(color = if (unlocked) Color(0xFFFFF7D7) else Color(0xFFD0C8B6)) {
                Text("Mundo $number — ${world.first}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(world.second, fontSize = 17.sp)
                if (number == 5) Text((LocalGameData.minimalPairs + progress.customMinimalPairs).joinToString("  •  ") { "${it.wordA}/${it.wordB}" }, fontSize = 14.sp)
                (1..10).chunked(5).forEach { levels ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        levels.forEach { level ->
                            BlockButton(if (unlocked) "N$level" else "🔒", { nav.navigate("game/$number/$level") }, Modifier.weight(1f), enabled = unlocked, color = if (unlocked) Color(0xFF5AA469) else Color.Gray)
                        }
                    }
                }
            }
        }
        BackButton(nav)
    }
}
