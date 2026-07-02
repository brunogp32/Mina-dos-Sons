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
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.BlockProgressBar
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun SessionResultScreen(vm: AppViewModel, nav: NavController, stars: Int, world: Int, level: Int) {
    val progress by vm.progress.collectAsState()
    MineScreen {
        ScreenTitle("Treino completo", vm.rewardEngine.message(stars))
        PixelCard {
            Text("Ganhaste $stars estrelas", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            BlockProgressBar(stars, 10)
            Text(if (stars >= 7) "Nível desbloqueado!" else "Boa tentativa! Ganhaste um baú.", fontSize = 19.sp)
            if (stars == 10) Text("Sessão perfeita!", color = Color(0xFFD6A22A), fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Baús por abrir: ${progress.unopenedChests.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Diamantes: ${progress.diamondBalance}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        if (progress.unopenedChests.isNotEmpty()) {
            BlockButton("Abrir baú", { nav.navigate("chest") }, color = Color(0xFFD6A22A))
        }
        BlockButton("Jogar outra vez", { nav.navigate("game/$world/$level") })
        BlockButton("Mapa dos níveis", { nav.navigate("map") }, color = Color(0xFF4A90A4))
        BlockButton("Casa", { nav.navigate("home") { popUpTo("home") { inclusive = true } } }, color = Color(0xFF795548))
    }
}
