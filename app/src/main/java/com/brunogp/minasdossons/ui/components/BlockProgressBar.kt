package com.brunogp.minasdossons.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BlockProgressBar(value: Int, max: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier.height(24.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(max) { index ->
            androidx.compose.foundation.layout.Box(
                Modifier
                    .size(24.dp)
                    .background(if (index < value) Color(0xFFF7D35C) else Color(0xFFB8A98B))
            )
        }
    }
}
