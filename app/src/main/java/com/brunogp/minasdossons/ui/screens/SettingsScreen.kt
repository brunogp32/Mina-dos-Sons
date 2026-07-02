package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.AppViewModel
import com.brunogp.minasdossons.ui.components.BlockButton
import com.brunogp.minasdossons.ui.components.PixelCard

@Composable
fun SettingsScreen(
    vm: AppViewModel,
    nav: NavController,
) {
    val p by vm.progress.collectAsState()
    MineScreen {
        ScreenTitle("Opções")
        SettingRow("Som TTS", p.ttsEnabled) { vm.save(p.copy(ttsEnabled = it)) }
        SettingRow("Voz lenta", p.slowVoice) { vm.save(p.copy(slowVoice = it)) }
        SettingRow("Permitir outra voz portuguesa", p.allowPortugueseVoiceFallback) {
            vm.save(p.copy(allowPortugueseVoiceFallback = it))
            vm.refreshPortugueseVoice()
        }
        SettingRow("Texto grande", p.largeText) { vm.save(p.copy(largeText = it)) }
        SettingRow("Modo fácil", p.easyMode) { vm.save(p.copy(easyMode = it)) }
        val tts by vm.ttsState.collectAsState()
        PixelCard {
            Text("Voz portuguesa", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(tts.message, fontSize = 16.sp)
            tts.selectedVoice?.let { voice ->
                Text("${voice.displayLanguage} - ${voice.name}", fontSize = 15.sp)
                Text(if (voice.requiresNetwork) "Precisa de rede" else "Disponível offline", fontSize = 15.sp)
            }
            BlockButton("Verificar voz portuguesa", { vm.refreshPortugueseVoice() })
            BlockButton("Instalar voz portuguesa", { vm.installPortugueseVoice() }, color = androidx.compose.ui.graphics.Color(0xFFD6A22A))
            BlockButton("Testar voz", { vm.testPortugueseVoice() }, color = androidx.compose.ui.graphics.Color(0xFF4A90A4))
        }
        PixelCard {
            Text("Modo misto", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Quando o modo fácil está desligado, aparecem mais opções e sons misturados.", fontSize = 17.sp)
        }
        BlockButton("Reiniciar sessão", { nav.navigate("game/${p.currentWorld}/${p.currentLevel}") })
        BackButton(nav)
    }
}

@Composable
private fun SettingRow(
    title: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit,
) {
    PixelCard {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Switch(checked, onChecked)
        }
    }
}
