package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.data.cards.CardCatalog
import com.brunogp.minasdossons.data.cards.CardItem
import com.brunogp.minasdossons.data.cards.CardPurchaseResult
import com.brunogp.minasdossons.data.cards.CardPurchaseUiState
import com.brunogp.minasdossons.data.cards.CardRarity
import com.brunogp.minasdossons.data.cards.SoundFocus
import com.brunogp.minasdossons.data.rewards.ChestType
import com.brunogp.minasdossons.ui.cards.CollectibleCard
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun CardCollectionScreen(vm: AppViewModel, nav: NavController) {
    val progress by vm.progress.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    var focus by remember { mutableStateOf<SoundFocus?>(null) }
    var rarity by remember { mutableStateOf<CardRarity?>(null) }
    var onlyNew by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<CardItem?>(null) }
    var buying by remember { mutableStateOf<CardItem?>(null) }
    var purchaseState by remember { mutableStateOf<CardPurchaseUiState>(CardPurchaseUiState.Idle) }

    val owned = CardCatalog.normalizeOwnedIds(progress.ownedRewardIds)
    val total = CardCatalog.cards.size
    val newCards = progress.newRewardIds.count { !progress.viewedRewardIds.contains(it) }
    val percent = ((owned.size * 100f) / total).toInt().coerceIn(0, 100)

    MineScreen {
        ScreenTitle("Cartas dos Sons", "${owned.size}/$total cartas - $percent%")
        PixelCard {
            Text("Treina os sons e completa a tua coleção de cartas.", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text("Novas: $newCards   Diamantes: ${progress.diamondBalance}", fontSize = 16.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Coleção", "Baús", "Loja").forEachIndexed { index, label ->
                BlockButton(label, { tab = index }, Modifier.weight(1f), color = if (tab == index) Color(0xFFD6A22A) else Color(0xFF4A90A4))
            }
        }
        when (tab) {
            0 -> {
                CollectionSummary(owned = owned, newCount = newCards)
                CardFilters(focus, rarity, onlyNew, onFocus = { focus = it }, onRarity = { rarity = it }, onOnlyNew = { onlyNew = !onlyNew })
                val ownedCards = CardCatalog.cards
                    .filter { owned.contains(it.id) }
                        .filter { focus == null || it.soundFocus == focus }
                        .filter { rarity == null || it.rarity == rarity }
                        .filter { !onlyNew || (progress.newRewardIds.contains(it.id) && !progress.viewedRewardIds.contains(it.id)) }
                    .sortedWith(compareBy<CardItem> { it.soundFocus.ordinal }.thenBy { it.rarity.ordinal }.thenBy { it.cardNumber })
                if (ownedCards.isEmpty()) {
                    PixelCard {
                        Text("Ainda não tens cartas neste grupo.", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Completa treinos, abre baús ou visita a loja para começar a coleção.", fontSize = 16.sp)
                    }
                } else {
                    CardGrid(
                        cards = ownedCards,
                        owned = owned,
                        newIds = progress.newRewardIds - progress.viewedRewardIds,
                        onOpen = {
                            selected = it
                            vm.markCardViewed(it.id)
                        },
                    )
                }
            }
            1 -> {
                PixelCard {
                    Text("Baús por abrir: ${progress.unopenedChests.size}", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    ChestType.entries.forEach { chest ->
                        Text("${chest.label}: ${chest.prizeCount} cartas + ${chest.diamondRange.first}-${chest.diamondRange.last} diamantes", fontSize = 16.sp)
                    }
                }
                if (progress.unopenedChests.isNotEmpty()) {
                    BlockButton("Abrir próximo baú", { nav.navigate("chest") }, color = Color(0xFFD6A22A))
                }
            }
            2 -> {
                PixelCard {
                    Text("Loja de cartas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text("As cartas que já tens ficam marcadas e não podem ser compradas outra vez.", fontSize = 16.sp)
                }
                CardGrid(
                    cards = CardCatalog.cards
                        .filter { it.shopEligible }
                        .sortedWith(compareBy<CardItem> { owned.contains(it.id) }.thenBy { it.price }.thenBy { it.cardNumber }),
                    owned = owned,
                    newIds = emptySet(),
                    onOpen = { selected = it },
                    onBuy = { if (!owned.contains(it.id)) buying = it },
                )
            }
        }
        BackButton(nav)
    }

    selected?.let { card ->
        CardDetailsDialog(
            card = card,
            owned = owned.contains(card.id),
            canBuy = progress.diamondBalance >= card.price,
            onDismiss = { selected = null },
            onBuy = {
                selected = null
                buying = card
            },
        )
    }

    buying?.let { card ->
        val alreadyOwned = owned.contains(card.id)
        AlertDialog(
            onDismissRequest = { buying = null },
            title = { Text(card.name) },
            text = {
                Text(
                    if (alreadyOwned) {
                        "Já tens esta carta na tua coleção."
                    } else if (progress.diamondBalance >= card.price) {
                        "Queres trocar ${card.price} diamantes por esta carta?"
                    } else {
                        "Faltam-te ${card.price - progress.diamondBalance} diamantes. Continua a treinar para conseguires mais."
                    }
                )
            },
            confirmButton = {
                BlockButton("Comprar", {
                    purchaseState = CardPurchaseUiState.Processing(card)
                    purchaseState = when (val result = vm.purchaseCard(card.id)) {
                        is CardPurchaseResult.Success -> {
                            buying = null
                            CardPurchaseUiState.Success(result.card)
                        }
                        CardPurchaseResult.AlreadyOwned -> CardPurchaseUiState.Error("Já tens esta carta.")
                        CardPurchaseResult.InsufficientDiamonds -> CardPurchaseUiState.Error("Faltam-te diamantes. Continua a treinar para conseguires mais.")
                        CardPurchaseResult.InvalidCard -> CardPurchaseUiState.Error("Esta carta não está disponível.")
                        CardPurchaseResult.InProgress -> CardPurchaseUiState.Processing(card)
                        CardPurchaseResult.Error -> CardPurchaseUiState.Error("Não foi possível guardar a compra.")
                    }
                }, enabled = !alreadyOwned && progress.diamondBalance >= card.price && purchaseState !is CardPurchaseUiState.Processing)
            },
            dismissButton = { BlockButton("Voltar", { buying = null }, color = Color(0xFF607D8B)) },
        )
    }

    when (val state = purchaseState) {
        is CardPurchaseUiState.Success -> AlertDialog(
            onDismissRequest = { purchaseState = CardPurchaseUiState.Idle },
            title = { Text("Novo cartão para a tua coleção!") },
            text = { CollectibleCard(state.card, Modifier.fillMaxWidth().aspectRatio(0.75f), locked = false) },
            confirmButton = { BlockButton("Ver carta", { selected = state.card; purchaseState = CardPurchaseUiState.Idle }) },
            dismissButton = { BlockButton("Continuar", { purchaseState = CardPurchaseUiState.Idle }, color = Color(0xFF607D8B)) },
        )
        is CardPurchaseUiState.Error -> AlertDialog(
            onDismissRequest = { purchaseState = CardPurchaseUiState.Idle },
            title = { Text("Compra não concluída") },
            text = { Text(state.message) },
            confirmButton = { BlockButton("Voltar", { purchaseState = CardPurchaseUiState.Idle }, color = Color(0xFF607D8B)) },
        )
        else -> Unit
    }
}

@Composable
private fun CollectionSummary(owned: Set<String>, newCount: Int) {
    val byFocus = CardCatalog.cards.groupBy { it.soundFocus }.mapValues { (_, cards) -> cards.count { owned.contains(it.id) } }
    PixelCard {
        Text("A minha coleção", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Cartas adquiridas: ${owned.size}/${CardCatalog.cards.size}", fontSize = 16.sp)
        Text("Cartas novas por ver: $newCount", fontSize = 16.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            SoundFocus.entries.take(4).forEach { focus ->
                Text("${focus.label}: ${byFocus[focus] ?: 0}", modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun CardFilters(
    focus: SoundFocus?,
    rarity: CardRarity?,
    onlyNew: Boolean,
    onFocus: (SoundFocus?) -> Unit,
    onRarity: (CardRarity?) -> Unit,
    onOnlyNew: () -> Unit,
) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        BlockButton("Todas", { onFocus(null); onRarity(null) }, Modifier.width(112.dp), color = if (focus == null && rarity == null) Color(0xFFD6A22A) else Color(0xFF607D8B))
        SoundFocus.entries.forEach { item ->
            BlockButton(item.label, { onFocus(item) }, Modifier.width(112.dp), color = if (focus == item) Color(0xFFD6A22A) else Color(0xFF4A90A4))
        }
        CardRarity.entries.forEach { item ->
            BlockButton(item.shortLabel, { onRarity(item) }, Modifier.width(132.dp), color = if (rarity == item) Color(0xFFD6A22A) else Color(0xFF8B6BB1))
        }
        BlockButton(if (onlyNew) "Novas ✓" else "Novas", onOnlyNew, Modifier.width(118.dp), color = if (onlyNew) Color(0xFFD6A22A) else Color(0xFF607D8B))
    }
}

@Composable
private fun CardGrid(
    cards: List<CardItem>,
    owned: Set<String>,
    newIds: Set<String>,
    onOpen: (CardItem) -> Unit,
    onBuy: ((CardItem) -> Unit)? = null,
) {
    cards.chunked(2).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            row.forEach { card ->
                val hasCard = owned.contains(card.id)
                PixelCard(modifier = Modifier.weight(1f)) {
                    CollectibleCard(
                        card = card,
                        modifier = Modifier.fillMaxWidth().aspectRatio(0.75f),
                        small = true,
                        locked = !hasCard && onBuy == null,
                        isNew = newIds.contains(card.id),
                    )
                    Text(if (hasCard) "Já tens" else "${card.price} diamantes", fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        BlockButton("Ver", { onOpen(card) }, Modifier.weight(1f), color = Color(0xFF4A90A4))
                        if (onBuy != null) {
                            BlockButton(if (hasCard) "Já tens" else "Comprar", { onBuy(card) }, Modifier.weight(1f), enabled = !hasCard, color = if (hasCard) Color(0xFF607D8B) else Color(0xFF8B6BB1))
                        }
                    }
                }
            }
            if (row.size == 1) Box(Modifier.weight(1f))
        }
    }
}

@Composable
private fun CardDetailsDialog(card: CardItem, owned: Boolean, canBuy: Boolean, onDismiss: () -> Unit, onBuy: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(card.name) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CollectibleCard(card, Modifier.size(210.dp, 280.dp), locked = !owned)
                Text(card.description, fontSize = 15.sp)
                Text(card.educationalHint, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Text("${card.rarity.label} - ${card.price} diamantes", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            if (!owned) {
                BlockButton("Comprar", onBuy, enabled = canBuy)
            }
        },
        dismissButton = { BlockButton("Voltar", onDismiss, color = Color(0xFF607D8B)) },
    )
}
