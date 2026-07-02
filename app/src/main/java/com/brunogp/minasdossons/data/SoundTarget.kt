package com.brunogp.minasdossons.data

data class SoundTarget(
    val id: String,
    val displaySound: String,
    val letter: String,
    val phoneme: String,
    val characterName: String,
    val shortHint: String,
    val throatVibrates: Boolean,
    val emoji: String,
    val worldName: String,
    val exampleWords: List<String>,
)
