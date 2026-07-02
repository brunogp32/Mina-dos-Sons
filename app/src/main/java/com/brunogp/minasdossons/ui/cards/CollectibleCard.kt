package com.brunogp.minasdossons.ui.cards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunogp.minasdossons.data.cards.CardIllustrationType
import com.brunogp.minasdossons.data.cards.CardItem
import com.brunogp.minasdossons.data.cards.CardRarity

@Composable
fun CollectibleCard(
    card: CardItem,
    modifier: Modifier = Modifier,
    small: Boolean = false,
    locked: Boolean = false,
    isNew: Boolean = false,
    selected: Boolean = false,
) {
    val border = rarityColor(card.rarity)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(if (selected) 4.dp else 2.dp, border, RoundedCornerShape(8.dp)),
    ) {
        Canvas(Modifier.matchParentSize()) {
            drawCardBackground(card, locked)
            drawCardIllustration(card, locked)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (small) 6.dp else 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("#${card.cardNumber}", fontSize = if (small) 10.sp else 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF263238))
            Text(
                if (locked) "Carta por descobrir" else card.name,
                fontSize = if (small) 12.sp else 18.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = if (small) 2 else 3,
            )
            Text(card.soundFocus.displaySound, fontSize = if (small) 15.sp else 24.sp, fontWeight = FontWeight.Black, color = border)
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(if (small) 6.dp else 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("${card.rarity.symbol} ${card.rarity.shortLabel} ${card.rarity.stars}", fontSize = if (small) 10.sp else 13.sp, fontWeight = FontWeight.Bold)
            if (!small) {
                Text("Poder do Som ${card.power}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(card.educationalHint, fontSize = 12.sp, textAlign = TextAlign.Center, maxLines = 2)
            }
        }
        if (isNew) {
            Text(
                "NOVA",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp),
                color = Color(0xFFD24D57),
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
            )
        }
    }
}

fun rarityColor(rarity: CardRarity): Color = when (rarity) {
    CardRarity.BRONZE -> Color(0xFF9A6A3A)
    CardRarity.SILVER -> Color(0xFF7A8793)
    CardRarity.GOLD -> Color(0xFFD6A22A)
    CardRarity.CRYSTAL -> Color(0xFF26A69A)
    CardRarity.RAINBOW -> Color(0xFFD24D57)
}

private fun DrawScope.drawCardBackground(card: CardItem, locked: Boolean) {
    val base = if (locked) Color(0xFFD7D1C5) else Color(0xFFFFF7D7)
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(base, Color(card.secondaryColor).copy(alpha = if (locked) 0.18f else 0.35f))),
        size = size,
        cornerRadius = CornerRadius(size.minDimension * 0.06f),
    )
    repeat(7) { index ->
        drawLine(
            Color(card.primaryColor).copy(alpha = if (locked) 0.08f else 0.16f),
            Offset(size.width * -0.1f, size.height * (0.18f + index * 0.12f)),
            Offset(size.width * 1.1f, size.height * (0.08f + index * 0.12f)),
            2.dp.toPx(),
            StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawCardIllustration(card: CardItem, locked: Boolean) {
    val primary = Color(card.primaryColor).copy(alpha = if (locked) 0.28f else 0.92f)
    val secondary = Color(card.secondaryColor).copy(alpha = if (locked) 0.20f else 0.82f)
    val center = Offset(size.width * 0.5f, size.height * 0.50f)
    val radius = size.minDimension * 0.18f
    when (card.illustrationType) {
        CardIllustrationType.SNAKE_SOUND -> {
            drawArc(primary, 200f, 260f, false, Offset(size.width * 0.24f, size.height * 0.36f), Size(size.width * 0.52f, size.height * 0.22f), style = Stroke(9.dp.toPx(), cap = StrokeCap.Round))
            drawCircle(secondary, radius * 0.34f, Offset(size.width * 0.70f, size.height * 0.45f))
        }
        CardIllustrationType.BEE_SOUND -> {
            drawCircle(primary, radius, center)
            repeat(3) { drawLine(secondary, Offset(center.x - radius + it * radius * 0.7f, center.y - radius), Offset(center.x - radius + it * radius * 0.7f, center.y + radius), 4.dp.toPx()) }
            drawCircle(secondary.copy(alpha = 0.35f), radius * 0.7f, Offset(center.x - radius * 0.9f, center.y - radius * 0.45f))
            drawCircle(secondary.copy(alpha = 0.35f), radius * 0.7f, Offset(center.x + radius * 0.9f, center.y - radius * 0.45f))
        }
        CardIllustrationType.RAIN_SOUND -> {
            repeat(5) { drawLine(primary, Offset(size.width * (0.30f + it * 0.10f), size.height * 0.38f), Offset(size.width * (0.25f + it * 0.10f), size.height * 0.62f), 5.dp.toPx(), StrokeCap.Round) }
            drawArc(secondary, 180f, 180f, false, Offset(size.width * 0.26f, size.height * 0.30f), Size(size.width * 0.48f, size.height * 0.22f), style = Stroke(8.dp.toPx(), cap = StrokeCap.Round))
        }
        CardIllustrationType.LADYBIRD_SOUND -> {
            drawCircle(primary, radius, center)
            drawLine(Color(0xFF263238), Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 2.dp.toPx())
            repeat(4) { drawCircle(Color(0xFF263238).copy(alpha = 0.65f), radius * 0.14f, Offset(center.x + if (it % 2 == 0) -radius * 0.42f else radius * 0.42f, center.y + if (it < 2) -radius * 0.25f else radius * 0.25f)) }
        }
        CardIllustrationType.LETTER, CardIllustrationType.SYLLABLE, CardIllustrationType.WORD -> {
            drawCircle(primary.copy(alpha = 0.18f), radius * 1.45f, center)
            drawLine(primary, Offset(center.x - radius, center.y), Offset(center.x + radius, center.y), 7.dp.toPx(), StrokeCap.Round)
            drawLine(secondary, Offset(center.x, center.y - radius), Offset(center.x, center.y + radius), 7.dp.toPx(), StrokeCap.Round)
        }
        CardIllustrationType.WAVE, CardIllustrationType.VOICE, CardIllustrationType.LISTENING -> {
            repeat(4) { index ->
                drawArc(primary.copy(alpha = 0.25f + index * 0.14f), -35f, 70f, false, Offset(size.width * (0.22f + index * 0.05f), size.height * (0.37f - index * 0.02f)), Size(size.width * (0.34f + index * 0.08f), size.height * (0.24f + index * 0.08f)), style = Stroke(4.dp.toPx(), cap = StrokeCap.Round))
            }
            drawCircle(secondary, radius * 0.36f, Offset(size.width * 0.36f, size.height * 0.52f))
        }
        CardIllustrationType.CRYSTAL, CardIllustrationType.MINE_TREASURE -> {
            drawPath(Path().apply {
                moveTo(center.x, center.y - radius * 1.3f)
                lineTo(center.x + radius, center.y)
                lineTo(center.x + radius * 0.22f, center.y + radius * 1.35f)
                lineTo(center.x - radius, center.y)
                close()
            }, primary)
            drawLine(secondary, Offset(center.x, center.y - radius * 1.2f), Offset(center.x + radius * 0.18f, center.y + radius * 1.2f), 2.dp.toPx())
        }
    }
}
