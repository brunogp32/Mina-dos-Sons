package com.brunogp.minasdossons.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BlockButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = Color(0xFF5AA469),
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier =
        modifier
            .heightIn(min = 58.dp)
            .fillMaxWidth()
            .semantics { contentDescription = text },
        colors = ButtonDefaults.buttonColors(containerColor = color, contentColor = Color.White),
        border = BorderStroke(3.dp, Color(0xFF3C2F2F)),
        contentPadding = PaddingValues(12.dp),
        shape = MaterialTheme.shapes.small,
    ) {
        Text(text, fontSize = 21.sp, fontWeight = FontWeight.Bold)
    }
}
