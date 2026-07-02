package com.brunogp.minasdossons.game

import com.brunogp.minasdossons.data.LocalGameData

class RewardEngine {
    fun nextSticker(current: Set<String>): String? {
        val missing = LocalGameData.stickers.filterNot { current.contains(it) }
        return (missing.ifEmpty { LocalGameData.stickers }).randomOrNull()
    }

    fun message(stars: Int): String = when {
        stars >= 10 -> "Grande construção!"
        stars >= 7 -> "Muito bem!"
        stars >= 4 -> "Estás a melhorar!"
        else -> "Vamos ouvir outra vez!"
    }
}
