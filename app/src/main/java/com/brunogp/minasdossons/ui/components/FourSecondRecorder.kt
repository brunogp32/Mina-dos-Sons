package com.brunogp.minasdossons.ui.components

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.brunogp.minasdossons.audio.AudioPlayer
import com.brunogp.minasdossons.audio.AudioRecorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

const val FOUR_SECOND_RECORDING_MS = 4_000L

enum class RecorderUiState {
    Idle,
    Preparing,
    Recording,
    Saving,
    Recorded,
    Playing,
    Error,
}

@Composable
fun FourSecondRecorder(
    selectedText: String,
    characterLabel: String,
    recorder: AudioRecorder,
    player: AudioPlayer,
    onListen: () -> Unit,
    onSaved: (File) -> Unit,
    modifier: Modifier = Modifier,
    helperText: String = "Agora és tu!",
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var state by remember { mutableStateOf(RecorderUiState.Idle) }
    var countdown by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(4) }
    var currentFile by remember(selectedText) { mutableStateOf<File?>(recorder.fileForLabel(selectedText)) }
    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            hasPermission = granted
        }

    fun startFlow() {
        if (!hasPermission) {
            launcher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        scope.launch {
            state = RecorderUiState.Preparing
            countdown = 3
            repeat(3) {
                delay(700L)
                countdown--
            }
            state = RecorderUiState.Recording
            secondsLeft = 4
            val result =
                recorder.start(
                    scope = scope,
                    label = selectedText,
                    maxMillis = FOUR_SECOND_RECORDING_MS,
                    onFinished = { file ->
                        state = RecorderUiState.Saving
                        currentFile = file
                        onSaved(file)
                        scope.launch {
                            delay(350L)
                            state = RecorderUiState.Recorded
                        }
                    },
                )
            if (result.isFailure) {
                state = RecorderUiState.Error
                return@launch
            }
            repeat(4) {
                delay(1_000L)
                secondsLeft = (secondsLeft - 1).coerceAtLeast(0)
            }
        }
    }

    PixelCard(modifier = modifier, color = Color(0xFFFFE08A)) {
        Text("Selecionado", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2F7D32))
        Text(selectedText, fontSize = 34.sp, fontWeight = FontWeight.Black)
        Text(characterLabel, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        Text(helperText, fontSize = 17.sp)
        when (state) {
            RecorderUiState.Preparing -> {
                Text("Prepara-te! $countdown", fontSize = 30.sp, fontWeight = FontWeight.Black)
            }

            RecorderUiState.Recording -> {
                Text("A gravar... ${secondsLeft}s", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD24D57))
                LinearProgressIndicator(
                    progress = { (4 - secondsLeft).coerceIn(0, 4) / 4f },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFD24D57),
                    trackColor = Color(0xFFFFF7D7),
                )
                RecordingWaves()
            }

            RecorderUiState.Saving -> {
                Text("A guardar...", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            RecorderUiState.Recorded -> {
                Text("Gravação guardada!", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2F7D32))
            }

            RecorderUiState.Playing -> {
                Text("A ouvir...", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            RecorderUiState.Error -> {
                Text("Vamos tentar outra vez.", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD24D57))
            }

            RecorderUiState.Idle -> {
                Unit
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BlockButton("Ouvir", onListen, Modifier.weight(1f), color = Color(0xFF4A90A4))
            BlockButton(
                if (state == RecorderUiState.Recorded) "Gravar outra vez" else "Gravar a minha voz",
                {
                    if (state != RecorderUiState.Recording && state != RecorderUiState.Preparing &&
                        state != RecorderUiState.Saving
                    ) {
                        startFlow()
                    }
                },
                Modifier.weight(1f),
                enabled = state != RecorderUiState.Recording && state != RecorderUiState.Preparing && state != RecorderUiState.Saving,
                color = Color(0xFFD24D57),
            )
        }
        if (state == RecorderUiState.Recorded || currentFile?.exists() == true) {
            BlockButton("Ouvir a minha voz", {
                state = RecorderUiState.Playing
                if (player.play(currentFile)) {
                    scope.launch {
                        delay(800L)
                        if (state == RecorderUiState.Playing) state = RecorderUiState.Recorded
                    }
                } else {
                    state = RecorderUiState.Error
                }
            }, color = Color(0xFF8B6BB1))
        }
    }
}

@Composable
private fun RecordingWaves() {
    val transition = rememberInfiniteTransition(label = "recording-waves")
    val pulse by transition.animateFloat(
        initialValue = 0.25f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(520), repeatMode = RepeatMode.Reverse),
        label = "wave-pulse",
    )
    Canvas(Modifier.fillMaxWidth().height(44.dp)) {
        val centerY = size.height / 2f
        val spacing = size.width / 8f
        repeat(7) { index ->
            val height = (12.dp.toPx() + index % 3 * 8.dp.toPx()) * pulse
            drawLine(
                color = Color(0xFF4A90A4),
                start = Offset(spacing * (index + 1), centerY - height / 2),
                end = Offset(spacing * (index + 1), centerY + height / 2),
                strokeWidth = 6.dp.toPx(),
            )
        }
    }
}
