package com.brunogp.minasdossons.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brunogp.minasdossons.ui.components.BlockButton

@Composable
fun MineScreen(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF8BD3E6), Color(0xFFA8D16D), Color(0xFF7A5638))))
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
fun ScreenTitle(
    title: String,
    subtitle: String? = null,
) {
    Text(title, fontSize = 34.sp, fontWeight = FontWeight.Black, color = Color(0xFF2C241B))
    subtitle?.let { Text(it, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun BackButton(nav: NavController) {
    BlockButton("Voltar", onClick = { nav.popBackStack() }, color = Color(0xFF6F6F6F))
}
