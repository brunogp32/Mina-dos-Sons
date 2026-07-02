package com.brunogp.minasdossons.ui.rewards

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.brunogp.minasdossons.R
import com.brunogp.minasdossons.data.rewards.ChestType

object ChestAnimationSpec {
    const val CLOSED_FRAME = 0
    const val SHAKE_FRAME = 1
    const val OPEN_FRAME = 2
    const val GLOW_FRAME = 3
    const val FRAME_COUNT = 4
}

@Composable
fun ChestSpriteAnimation(
    opened: Boolean,
    chestType: ChestType,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "chest-assets")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(850, easing = LinearEasing), RepeatMode.Reverse),
        label = "chest-pulse",
    )
    val frames = framesFor(chestType)
    val frame =
        if (opened) {
            if (pulse < 0.33f) {
                1
            } else if (pulse < 0.66f) {
                2
            } else {
                3
            }
        } else if (pulse > 0.55f) {
            ChestAnimationSpec.SHAKE_FRAME
        } else {
            ChestAnimationSpec.CLOSED_FRAME
        }.coerceIn(0, frames.lastIndex)

    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            if (opened) {
                val center = Offset(size.width / 2f, size.height * 0.52f)
                repeat(14) { index ->
                    val angle = index * 0.45f + pulse
                    drawCircle(
                        Color(0xFFFFD86B).copy(alpha = 0.28f + pulse * 0.22f),
                        radius = size.minDimension * 0.017f,
                        center =
                        Offset(
                            center.x + kotlin.math.cos(angle) * size.minDimension * 0.38f,
                            center.y + kotlin.math.sin(angle) * size.minDimension * 0.25f,
                        ),
                    )
                }
            }
        }
        Image(
            painter = painterResource(frames[frame]),
            contentDescription = chestType.label,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit,
        )
    }
}

private fun framesFor(type: ChestType): List<Int> = when (type) {
    ChestType.WOOD -> {
        listOf(R.drawable.chest_wood_00, R.drawable.chest_wood_01, R.drawable.chest_wood_02, R.drawable.chest_wood_03)
    }

    ChestType.IRON -> {
        listOf(R.drawable.chest_iron_00, R.drawable.chest_iron_01, R.drawable.chest_iron_02, R.drawable.chest_iron_03)
    }

    ChestType.GOLD -> {
        listOf(R.drawable.chest_gold_00, R.drawable.chest_gold_01, R.drawable.chest_gold_02, R.drawable.chest_gold_03)
    }

    ChestType.CRYSTAL -> {
        listOf(
            R.drawable.chest_crystal_00,
            R.drawable.chest_crystal_01,
            R.drawable.chest_crystal_02,
            R.drawable.chest_crystal_03,
        )
    }
}
