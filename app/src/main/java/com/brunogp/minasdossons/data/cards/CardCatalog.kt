package com.brunogp.minasdossons.data.cards

object CardCatalog {
    val cards: List<CardItem> by lazy { buildCatalog().also(::validate) }

    fun card(id: String): CardItem? = cards.firstOrNull { it.id == id || it.legacyRewardId == id }

    fun normalizeOwnedIds(ids: Set<String>): Set<String> = ids.map { old -> card(old)?.id ?: old }.filter { card(it) != null }.toSet()

    private fun buildCatalog(): List<CardItem> {
        val specs =
            listOf(
                FocusSpec(
                    SoundFocus.S,
                    "Reino da Cobra",
                    "S",
                    listOf("sapo", "saco", "selo", "sino", "sopa", "suma", "sala", "seda", "sombra", "serra"),
                ),
                FocusSpec(
                    SoundFocus.Z,
                    "Colmeia do Z",
                    "Z",
                    listOf("zebra", "zero", "azeite", "azul", "rosa", "casa", "mesa", "brasa", "tesoura", "vizinho"),
                ),
                FocusSpec(
                    SoundFocus.X_CH,
                    "Nuvens do X",
                    "X",
                    listOf("chá", "chave", "chuva", "peixe", "caixa", "baixo", "xarope", "mexer", "lixo", "fecho"),
                ),
                FocusSpec(
                    SoundFocus.J,
                    "Jardim do J",
                    "J",
                    listOf("janela", "jogo", "jardim", "joaninha", "jornal", "girafa", "gelado", "gelo", "viagem", "loja"),
                ),
                FocusSpec(
                    SoundFocus.MIXED,
                    "Mestres da Fala",
                    "M",
                    listOf("voz", "garganta", "escuta", "sílabas", "palavras", "frases", "eco", "som", "mina", "tesouro"),
                ),
            )
        val collections =
            listOf(
                "Cristais dos Sons",
                "Palavras Mágicas",
                "Sílabas Secretas",
                "Ondas da Voz",
                "Tesouros da Mina",
            )
        val result = mutableListOf<CardItem>()
        var global = 1
        specs.forEachIndexed { focusIndex, spec ->
            repeat(100) { localIndex ->
                val rarity = rarityFor(global)
                val word = spec.words[localIndex % spec.words.size]
                val collection = if (localIndex < 50) spec.collection else collections[(localIndex + focusIndex) % collections.size]
                val name = nameFor(spec, word, localIndex)
                val legacyId = if (global <= 250) legacyIdFor(global) else null
                result +=
                    CardItem(
                        id = "card_${global.toString().padStart(3, '0')}",
                        name = name,
                        description = descriptionFor(spec.focus, word, collection),
                        collection = collection,
                        cardNumber = global,
                        rarity = rarity,
                        soundFocus = spec.focus,
                        word = word,
                        illustrationType = illustrationFor(spec.focus, localIndex),
                        primaryColor = palette[(global + focusIndex) % palette.size],
                        secondaryColor = palette[(global + focusIndex + 5) % palette.size],
                        pattern = patterns[(global + localIndex) % patterns.size],
                        power = (35 + (global * 7 + localIndex * 3) % 66).coerceIn(35, 100),
                        educationalHint = hintFor(spec.focus),
                        price = priceFor(rarity, global),
                        chestEligible = true,
                        shopEligible = true,
                        legacyRewardId = legacyId,
                    )
                global += 1
            }
        }
        return result
    }

    private data class FocusSpec(
        val focus: SoundFocus,
        val collection: String,
        val prefix: String,
        val words: List<String>,
    )

    private val palette =
        listOf(
            0xFF2F7D32,
            0xFFD6A22A,
            0xFF4A90A4,
            0xFFD24D57,
            0xFF8B6BB1,
            0xFF795548,
            0xFF607D8B,
            0xFF43A047,
            0xFFFF8A65,
            0xFF26A69A,
        )
    private val patterns = listOf("ondas", "estrelas", "cristais", "riscas", "pontos", "letras", "folhas", "ecos")

    private fun rarityFor(index: Int): CardRarity = when {
        index <= 250 -> CardRarity.BRONZE
        index <= 375 -> CardRarity.SILVER
        index <= 450 -> CardRarity.GOLD
        index <= 485 -> CardRarity.CRYSTAL
        else -> CardRarity.RAINBOW
    }

    private fun priceFor(
        rarity: CardRarity,
        index: Int,
    ): Int {
        val range = rarity.priceRange
        return range.first + (index * 11 % (range.last - range.first + 1))
    }

    private fun legacyIdFor(index: Int): String {
        val buckets = listOf("character", "hat", "tool", "companion", "block", "crystal", "background", "effect", "sticker", "trophy")
        val bucket = buckets[(index - 1) / 25]
        val number = ((index - 1) % 25) + 1
        return "${bucket}_$number"
    }

    private fun illustrationFor(
        focus: SoundFocus,
        index: Int,
    ): CardIllustrationType = when (focus) {
        SoundFocus.S -> {
            if (index % 3 == 0) CardIllustrationType.SNAKE_SOUND else CardIllustrationType.LETTER
        }

        SoundFocus.Z -> {
            if (index % 3 == 0) CardIllustrationType.BEE_SOUND else CardIllustrationType.WAVE
        }

        SoundFocus.X_CH -> {
            if (index % 3 == 0) CardIllustrationType.RAIN_SOUND else CardIllustrationType.WORD
        }

        SoundFocus.J -> {
            if (index % 3 == 0) CardIllustrationType.LADYBIRD_SOUND else CardIllustrationType.SYLLABLE
        }

        SoundFocus.MIXED -> {
            listOf(
                CardIllustrationType.CRYSTAL,
                CardIllustrationType.MINE_TREASURE,
                CardIllustrationType.VOICE,
                CardIllustrationType.LISTENING,
            )[
                index %
                    4,
            ]
        }
    }

    private fun nameFor(
        spec: FocusSpec,
        word: String,
        index: Int,
    ): String {
        val themes =
            when (spec.focus) {
                SoundFocus.S -> {
                    listOf("Sopro da Cobra", "Eco do S", "Sapo Saltador", "Saco Surpresa", "Serpente Suave")
                }

                SoundFocus.Z -> {
                    listOf("Zumbido Dourado", "Eco do Z", "Zebra Azul", "Rosa Sonora", "Ziguezague da Voz")
                }

                SoundFocus.X_CH -> {
                    listOf("Chuva Suave", "Chave de Cristal", "Peixe do X", "Caixa Sonora", "Nuvem do CH")
                }

                SoundFocus.J -> {
                    listOf("Janela Brilhante", "Jardim das Palavras", "Joaninha do J", "Jogo da Voz", "Jóia Sonora")
                }

                SoundFocus.MIXED -> {
                    listOf(
                        "Mestre da Vibração",
                        "Guardião das Sílabas",
                        "Tesouro da Voz",
                        "Ouvinte Especial",
                        "Cristal dos Sons",
                    )
                }
            }
        return if (index < themes.size) themes[index] else "${themes[index % themes.size]} ${word.replaceFirstChar { it.uppercase() }}"
    }

    private fun descriptionFor(
        focus: SoundFocus,
        word: String,
        collection: String,
    ): String = "Carta da coleção $collection para treinar ${focus.label} com a palavra $word."

    private fun hintFor(focus: SoundFocus): String = when (focus) {
        SoundFocus.S -> "Faz SSSS sem sentir a garganta vibrar."
        SoundFocus.Z -> "Faz ZZZZ e sente a garganta vibrar."
        SoundFocus.X_CH -> "Faz CHHHH sem vibração forte na garganta."
        SoundFocus.J -> "Faz JJJJ e sente a garganta vibrar."
        SoundFocus.MIXED -> "Ouve com atenção e compara os sons."
    }

    private fun validate(cards: List<CardItem>) {
        check(cards.size >= 500) { "O catálogo deve ter pelo menos 500 cartas." }
        check(cards.map { it.id }.toSet().size == cards.size) { "IDs de cartas repetidos." }
        check(cards.groupBy { it.collection }.all { (_, values) -> values.map { it.cardNumber }.toSet().size == values.size }) {
            "Números repetidos dentro de uma coleção."
        }
        check(cards.all { it.name.isNotBlank() && it.price in it.rarity.priceRange && it.educationalHint.isNotBlank() })
    }
}
