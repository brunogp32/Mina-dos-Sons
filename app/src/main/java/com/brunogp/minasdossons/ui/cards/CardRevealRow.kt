package com.brunogp.minasdossons.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brunogp.minasdossons.data.rewards.ChestReward

@Composable
fun CardRevealRow(
    reward: ChestReward,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CollectibleCard(reward.item, Modifier.size(72.dp, 96.dp), small = true)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(reward.item.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${reward.item.rarity.symbol} ${reward.item.rarity.label} ${reward.item.rarity.stars}", fontSize = 14.sp)
            if (reward.isDuplicate) {
                Text(
                    "Já tinhas esta carta. Recebeste ${reward.duplicateDiamonds} diamantes.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
