package com.brunogp.minasdossons.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PixelCard(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFFFF7D7),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
        modifier
            .shadow(5.dp, RoundedCornerShape(8.dp))
            .border(3.dp, Color(0xFF5D4037), RoundedCornerShape(8.dp))
            .background(color, RoundedCornerShape(8.dp))
            .padding(14.dp)
            .fillMaxWidth(),
        content = content,
    )
}
