package com.brunogp.minasdossons.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RewardDialog(
    message: String,
    sticker: String?,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { BlockButton("Boa!", onDismiss) },
        title = { Text("🎁 Baú aberto") },
        text = { Text("$message\n${sticker?.let { "Autocolante: $it" } ?: ""}") },
    )
}
