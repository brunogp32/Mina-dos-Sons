package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.BuildConfig
import com.brunogp.minasdossons.audio.ReferenceSound
import com.brunogp.minasdossons.data.LocalGameData
import com.brunogp.minasdossons.data.cards.CardCatalog
import com.brunogp.minasdossons.data.cards.CardRarity
import com.brunogp.minasdossons.data.rewards.ChestType
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard
import java.io.File
import java.time.Instant
import java.time.ZoneId
import kotlin.random.Random

@Composable
fun ParentModeScreen(
    vm: AppViewModel,
    nav: NavController,
) {
    val progress by vm.progress.collectAsState()
    val challenge = remember { Random.nextInt(2, 10) to Random.nextInt(2, 10) }
    val challengeAnswer = challenge.first * challenge.second
    var answer by remember { mutableStateOf("") }
    var unlocked by remember { mutableStateOf(false) }
    val activeSounds = remember { mutableStateMapOf("s" to true, "z" to true, "x" to true, "j" to true) }
    var wordA by remember { mutableStateOf("") }
    var wordB by remember { mutableStateOf("") }
    var pairType by remember { mutableStateOf("s-z") }
    var pairStatus by remember { mutableStateOf("") }
    var recordingFilter by remember { mutableStateOf("Todas") }
    var recordingSearch by remember { mutableStateOf("") }
    val allPairs = LocalGameData.minimalPairs + progress.customMinimalPairs

    MineScreen {
        ScreenTitle("Modo Pais")
        if (!unlocked) {
            PixelCard {
                Text("Quanto é ${challenge.first} × ${challenge.second}?", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                OutlinedTextField(answer, {
                    answer = it
                }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), label = { Text("Resposta") })
                BlockButton("Entrar", { unlocked = answer.trim() == challengeAnswer.toString() })
                if (answer.isNotBlank() &&
                    answer.trim() != challengeAnswer.toString()
                ) {
                    Text("Quase! Tenta outra vez.", color = Color(0xFF2F7D32))
                }
            }
            BackButton(nav)
            return@MineScreen
        }
        PixelCard {
            Text("Objetivo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            if (BuildConfig.DEBUG) {
                BlockButton("Abrir QA debug", { nav.navigate("debugqa") }, color = Color(0xFF8B6BB1))
            }
            Text("Treinar primeiro o som e só depois a letra, com atenção à vibração da garganta.", fontSize = 17.sp)
            Text("S /s/ e CH /ʃ/ são sons sem voz. Z /z/ e J /ʒ/ são sons com voz.", fontSize = 16.sp)
            Text("Neste jogo, X representa o som /ʃ/, como em xarope e peixe.", fontSize = 16.sp)
            Text(
                "Esta aplicação apoia o treino, mas não substitui um terapeuta da fala.",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        PixelCard {
            Text("Sons oficiais", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Os quatro sons modelo usam os áudios preparados na aplicação. Não podem ser editados no Modo Pais.", fontSize = 16.sp)
            ReferenceSound.entries.forEach { sound ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${sound.displayText} ${sound.phoneme}", fontSize = 17.sp, modifier = Modifier.weight(1f))
                    BlockButton(
                        "Ouvir",
                        { vm.referenceAudioPlayer.playReferenceSound(sound) },
                        Modifier.weight(1f),
                        color = Color(0xFF4A90A4),
                    )
                }
            }
        }
        PixelCard {
            Text("Progresso detalhado", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                "Estrelas: ${progress.totalStars} | Sessões: ${progress.completedSessions} | Gravações: ${progress.recordingsCount}",
                fontSize = 17.sp,
            )
            Text("Mundo ${progress.currentWorld}, nível ${progress.currentLevel}", fontSize = 17.sp)
            Text("Cartas: ${CardCatalog.normalizeOwnedIds(progress.ownedRewardIds).size}/${CardCatalog.cards.size}", fontSize = 17.sp)
            Text("Diamantes: ${progress.diamondBalance}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            BlockButton("Adicionar 100 diamantes para teste", { vm.addParentDiamonds() }, color = Color(0xFFD6A22A))
        }
        RecordingListCard(progress.recordingsByName, progress.recordingRewardDates, recordingFilter, recordingSearch, vm) {
            recordingFilter = it
        }
        OutlinedTextField(recordingSearch, { recordingSearch = it }, label = { Text("Pesquisar gravação") })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlockButton("Desbloquear tudo", { vm.unlockAll() }, Modifier.weight(1f), color = Color(0xFFD6A22A))
            BlockButton("Bloquear por progresso", { vm.lockByProgress() }, Modifier.weight(1f), color = Color(0xFF607D8B))
        }
        BlockButton("Reiniciar progresso", { vm.reset() }, color = Color(0xFFD24D57))
        PixelCard {
            Text("Sons a treinar", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            LocalGameData.targets.forEach { target ->
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${target.displaySound} ${target.characterName}", fontSize = 18.sp, modifier = Modifier.weight(1f))
                    Switch(activeSounds[target.id] == true, { activeSounds[target.id] = it })
                }
            }
        }
        PixelCard {
            Text("Adicionar par mínimo", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Estes pares entram aleatoriamente nas perguntas do jogo.", fontSize = 16.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BlockButton(
                    "S / Z",
                    { pairType = "s-z" },
                    Modifier.weight(1f),
                    color =
                    if (pairType ==
                        "s-z"
                    ) {
                        Color(0xFFD6A22A)
                    } else {
                        Color(0xFF5AA469)
                    },
                )
                BlockButton(
                    "CH / J",
                    { pairType = "x-j" },
                    Modifier.weight(1f),
                    color =
                    if (pairType ==
                        "x-j"
                    ) {
                        Color(0xFFD6A22A)
                    } else {
                        Color(0xFF4A90A4)
                    },
                )
            }
            OutlinedTextField(wordA, { wordA = it }, label = { Text(if (pairType == "s-z") "Palavra com S" else "Palavra com CH/X") })
            OutlinedTextField(wordB, { wordB = it }, label = { Text(if (pairType == "s-z") "Palavra com Z" else "Palavra com J") })
            BlockButton("Adicionar par", {
                val parts = pairType.split("-")
                vm.addCustomMinimalPair(wordA, wordB, parts[0], parts[1])
                pairStatus = "Par adicionado: ${wordA.trim()} / ${wordB.trim()}"
                wordA = ""
                wordB = ""
            })
            if (pairStatus.isNotBlank()) Text(pairStatus, color = Color(0xFF2F7D32), fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        PixelCard {
            Text("Pares no jogo (${allPairs.size})", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            allPairs.forEach { Text("${it.wordA} / ${it.wordB}${if (it.editable) " (editável)" else ""}", fontSize = 16.sp) }
        }
        PixelCard {
            Text("Probabilidades dos baús", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("${ChestType.WOOD.label}: Bronze e Prata", fontSize = 15.sp)
            Text("${ChestType.IRON.label}: Bronze, Prata e Ouro", fontSize = 15.sp)
            Text("${ChestType.GOLD.label}: Prata, Ouro e Cristal", fontSize = 15.sp)
            Text("${ChestType.CRYSTAL.label}: Ouro, Cristal e Arco-íris", fontSize = 15.sp)
            CardRarity.entries.forEach {
                Text(
                    "${it.shortLabel}: ${it.priceRange.first}-${it.priceRange.last} diamantes",
                    fontSize = 14.sp,
                )
            }
        }
        PixelCard {
            Text("Últimas transações", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            val earned = progress.diamondTransactions.filter { it.amount > 0 }.sumOf { it.amount }
            val spent = progress.diamondTransactions.filter { it.amount < 0 }.sumOf { -it.amount }
            Text("Ganhos: $earned | Gastos: $spent", fontSize = 17.sp)
            progress.diamondTransactions.takeLast(8).reversed().forEach {
                Text("${if (it.amount >= 0) "+" else ""}${it.amount} - ${it.description}", fontSize = 14.sp)
            }
        }
        BackButton(nav)
    }
}

@Composable
private fun RecordingListCard(
    recordings: Map<String, String>,
    recordingDates: Map<String, String>,
    filter: String,
    search: String,
    vm: AppViewModel,
    onFilter: (String) -> Unit,
) {
    PixelCard {
        Text("Gravações guardadas", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Todas", "S", "Z", "X/CH", "J").forEach {
                BlockButton(it, { onFilter(it) }, Modifier.weight(1f), color = if (filter == it) Color(0xFFD6A22A) else Color(0xFF607D8B))
            }
        }
        val filtered =
            recordings.filter { (name, _) ->
                (search.isBlank() || name.contains(search, ignoreCase = true)) && (filter == "Todas" || groupForRecording(name) == filter)
            }
        if (filtered.isEmpty()) {
            Text("Ainda não há gravações neste filtro.", fontSize = 17.sp)
        } else {
            filtered.toSortedMap().forEach { (name, path) ->
                val storedFile = File(path)
                val file = if (storedFile.exists()) storedFile else vm.recorder.fileForLabel(name)
                val date =
                    recordingDates[name] ?: runCatching {
                        Instant
                            .ofEpochMilli(file.lastModified())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                            .toString()
                    }.getOrDefault("")
                Text("$name - ${groupForRecording(name)} - Gravado", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(date, fontSize = 13.sp)
                BlockButton("Ouvir $name", { vm.player.play(file) }, color = Color(0xFF4A90A4))
            }
        }
    }
}

private fun groupForRecording(name: String): String {
    val referenceGroup = when (ReferenceSound.fromDisplayText(name)) {
        ReferenceSound.S -> "S"
        ReferenceSound.Z -> "Z"
        ReferenceSound.CH -> "X/CH"
        ReferenceSound.J -> "J"
        null -> null
    }
    val targetId = LocalGameData.targets
        .firstOrNull { target -> target.exampleWords.any { it.equals(name, ignoreCase = true) } }
        ?.id
    return referenceGroup ?: groupForTargetId(targetId)
}

private fun groupForTargetId(targetId: String?): String = when (targetId) {
    "s" -> "S"
    "z" -> "Z"
    "x" -> "X/CH"
    "j" -> "J"
    else -> "Todas"
}
