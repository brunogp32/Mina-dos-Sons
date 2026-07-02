package com.brunogp.minasdossons.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun StarCounter(stars: Int) {
    Text("⭐ $stars", fontSize = 22.sp, fontWeight = FontWeight.Bold)
}
