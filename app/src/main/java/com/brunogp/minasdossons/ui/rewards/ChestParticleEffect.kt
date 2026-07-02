package com.brunogp.minasdossons.ui.rewards

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@Composable
fun ChestParticleEffect(
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "chest-particles")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label = "chest-particle-progress",
    )
    Canvas(modifier) {
        if (!active) return@Canvas
        val center = Offset(size.width * 0.5f, size.height * 0.58f)
        repeat(18) { index ->
            val angle = index * 0.35f
            val radius = size.minDimension * (0.10f + progress * (0.22f + (index % 5) * 0.035f))
            val alpha = (1f - progress).coerceIn(0.05f, 0.95f)
            val start = Offset(center.x + kotlin.math.cos(angle) * radius * 0.72f, center.y + kotlin.math.sin(angle) * radius * 0.58f)
            val end = Offset(center.x + kotlin.math.cos(angle) * radius, center.y + kotlin.math.sin(angle) * radius)
            drawLine(
                color = if (index % 3 == 0) Color(0xFFFFD86B).copy(alpha = alpha) else Color(0xFF8BD3E6).copy(alpha = alpha),
                start = start,
                end = end,
                strokeWidth = 3.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}
