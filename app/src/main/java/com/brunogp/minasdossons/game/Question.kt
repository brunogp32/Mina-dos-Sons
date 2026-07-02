package com.brunogp.minasdossons.game

data class Question(
    val id: String,
    val type: QuestionType,
    val prompt: String,
    val options: List<String>,
    val correctAnswer: String,
    val targetSound: String,
    val explanation: String,
    val difficulty: Int,
    val speakText: String = prompt,
)
