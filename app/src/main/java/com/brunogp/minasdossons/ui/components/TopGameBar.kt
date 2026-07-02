package com.brunogp.minasdossons.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.brunogp.minasdossons.data.GameProgress

@Composable
fun TopGameBar(
    progress: GameProgress,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        StarCounter(progress.totalStars)
        Text("M${progress.currentWorld} N${progress.currentLevel}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("💎 ${progress.diamondBalance}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}
