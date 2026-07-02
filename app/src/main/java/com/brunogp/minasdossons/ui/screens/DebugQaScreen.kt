package com.brunogp.minasdossons.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.data.cards.CardCatalog
import com.brunogp.minasdossons.data.rewards.ChestType
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun DebugQaScreen(vm: AppViewModel, nav: NavController) {
    val progress by vm.progress.collectAsState()
    val owned = CardCatalog.normalizeOwnedIds(progress.ownedRewardIds)
    MineScreen {
        ScreenTitle("QA debug", "Apenas para builds de teste")
        PixelCard {
            Text("Verificação rápida", fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text("Cartas: ${CardCatalog.cards.size}", fontSize = 17.sp)
            Text("Cartas adquiridas: ${owned.size}", fontSize = 17.sp)
            Text("Diamantes: ${progress.diamondBalance}", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Baús por abrir: ${progress.unopenedChests.size}", fontSize = 17.sp)
        }
        BlockButton("Executar verificação rápida", { }, color = Color(0xFF4A90A4))
        BlockButton("Dar 100 diamantes de teste", { vm.addParentDiamonds() }, color = Color(0xFFD6A22A))
        ChestType.entries.forEach { chest ->
            BlockButton("Testar ${chest.label}", { vm.save(progress.copy(unopenedChests = progress.unopenedChests + chest)) }, color = Color(0xFF8B6BB1))
        }
        BlockButton("Abrir cartas", { nav.navigate("cards") }, color = Color(0xFFD6A22A))
        BackButton(nav)
    }
}
