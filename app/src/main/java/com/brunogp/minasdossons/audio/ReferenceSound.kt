package com.brunogp.minasdossons.audio

enum class ReferenceSound(
    val id: String,
    val targetId: String,
    val displayText: String,
    val phoneme: String,
    val characterName: String,
    val description: String,
    val vibrates: Boolean,
    val bundledRawResourceName: String,
    val instruction: String,
) {
    S(
        id = "s",
        targetId = "s",
        displayText = "SSSS",
        phoneme = "/s/",
        characterName = "Cobra",
        description = "Som da cobra",
        vibrates = false,
        bundledRawResourceName = "som_s",
        instruction = "Faz SSSS como uma cobra, de forma contínua.",
    ),
    Z(
        id = "z",
        targetId = "z",
        displayText = "ZZZZ",
        phoneme = "/z/",
        characterName = "Abelha",
        description = "Som da abelha",
        vibrates = true,
        bundledRawResourceName = "som_z",
        instruction = "Faz ZZZZ como uma abelha, de forma contínua.",
    ),
    CH(
        id = "ch",
        targetId = "x",
        displayText = "CHHHH",
        phoneme = "/ʃ/",
        characterName = "Chuva",
        description = "Som da chuva",
        vibrates = false,
        bundledRawResourceName = "som_x",
        instruction = "Faz CHHHH como o som da chuva, de forma contínua.",
    ),
    J(
        id = "j",
        targetId = "j",
        displayText = "JJJJ",
        phoneme = "/ʒ/",
        characterName = "Joaninha",
        description = "Som da joaninha",
        vibrates = true,
        bundledRawResourceName = "som_j",
        instruction = "Faz JJJJ como no início de janela, de forma contínua.",
    ),
    ;

    companion object {
        fun fromDisplayText(text: String): ReferenceSound? {
            val clean = text.trim().uppercase()
            return entries.firstOrNull { it.displayText == clean }
        }

        fun fromTargetId(targetId: String): ReferenceSound? = entries.firstOrNull { it.targetId == targetId }
    }
}
