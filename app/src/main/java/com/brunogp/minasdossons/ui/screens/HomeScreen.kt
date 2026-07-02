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
import com.brunogp.minasdossons.data.cards.CardCatalog
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard
import com.brunogp.minasdossons.ui.components.TopGameBar

@Composable
fun HomeScreen(
    vm: AppViewModel,
    nav: NavController,
) {
    val progress by vm.progress.collectAsState()
    val ownedCards = CardCatalog.normalizeOwnedIds(progress.ownedRewardIds).size
    val totalCards = CardCatalog.cards.size
    val percent = ((ownedCards * 100f) / totalCards).toInt().coerceIn(0, 100)

    MineScreen {
        TopGameBar(progress)
        ScreenTitle("Mina dos Sons", "Treina os sons e completa a tua coleção de cartas.")
        PixelCard {
            Text("Treino dos sons", fontSize = 24.sp, fontWeight = FontWeight.Black)
            Text("Sons, palavras, baús, cartas e diamantes", fontSize = 16.sp)
        }
        PixelCard {
            Text("Minha coleção", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Nível ${progress.currentWorld}.${progress.currentLevel} - $ownedCards/$totalCards cartas ($percent%)", fontSize = 16.sp)
            Text("Cartas novas: ${(progress.newRewardIds - progress.viewedRewardIds).size}", fontSize = 16.sp)
            Text("Diamantes: ${progress.diamondBalance}", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            BlockButton("Abrir coleção", { nav.navigate("cards") }, color = Color(0xFF8B6BB1))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BlockButton("Jogar", { nav.navigate("game/${progress.currentWorld}/${progress.currentLevel}") }, Modifier.weight(1f))
            BlockButton("Mapa", { nav.navigate("map") }, Modifier.weight(1f), color = Color(0xFF4A90A4))
        }
        BlockButton("Cartas dos Sons", { nav.navigate("cards") }, color = Color(0xFFD6A22A))
        BlockButton("Treinar garganta", { nav.navigate("throat") })
        BlockButton("Gravar a minha voz", { nav.navigate("recording") })
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BlockButton("Coleção", { nav.navigate("cards") }, Modifier.weight(1f), color = Color(0xFFD6A22A))
            BlockButton("Progresso", { nav.navigate("progress") }, Modifier.weight(1f), color = Color(0xFF8B6BB1))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BlockButton("Pais", { nav.navigate("parents") }, Modifier.weight(1f), color = Color(0xFF795548))
            BlockButton("Opções", { nav.navigate("settings") }, Modifier.weight(1f), color = Color(0xFF607D8B))
        }
    }
}
