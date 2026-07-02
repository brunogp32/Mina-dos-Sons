package com.brunogp.minasdossons.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.data.rewards.ChestOpeningResult
import com.brunogp.minasdossons.ui.cards.CardRevealRow
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard
import com.brunogp.minasdossons.ui.rewards.ChestParticleEffect
import com.brunogp.minasdossons.ui.rewards.ChestSpriteAnimation

@Composable
fun ChestOpeningScreen(vm: AppViewModel, nav: NavController) {
    val progress by vm.progress.collectAsState()
    var result by remember { mutableStateOf<ChestOpeningResult?>(null) }
    var opened by remember { mutableStateOf(false) }

    MineScreen {
        ScreenTitle("Baú surpresa", "${progress.diamondBalance} diamantes")
        val nextChest = progress.unopenedChests.firstOrNull()
        if (nextChest == null && result == null) {
            PixelCard { Text("Não há baús por abrir.", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
            BackButton(nav)
            return@MineScreen
        }
        PixelCard {
            Text(result?.chestType?.label ?: nextChest!!.label, fontSize = 24.sp, fontWeight = FontWeight.Black)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
                    .clickable {
                        if (result == null) {
                            result = vm.openNextChestResult()
                            opened = true
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                ChestParticleEffect(opened, Modifier.fillMaxWidth().height(190.dp))
                ChestSpriteAnimation(opened, result?.chestType ?: nextChest!!, Modifier.fillMaxWidth().height(180.dp))
            }
            if (result == null) {
                Text("Toca para abrir!", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                BlockButton("Abrir agora", {
                    result = vm.openNextChestResult()
                    opened = true
                }, color = Color(0xFFD6A22A))
            }
        }
        AnimatedVisibility(result != null) {
            PixelCard {
                val openedResult = result ?: return@PixelCard
                Text("Descobriste cartas!", fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text("+${openedResult.diamonds} diamantes", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                openedResult.rewards.forEach { reward -> CardRevealRow(reward) }
                BlockButton("Ver cartas", { nav.navigate("cards") { popUpTo("home") } })
                BlockButton("Continuar", { nav.navigate("home") { popUpTo("home") { inclusive = true } } }, color = Color(0xFF607D8B))
            }
        }
        BackButton(nav)
    }
}
