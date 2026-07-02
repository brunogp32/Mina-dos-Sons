package com.brunogp.minasdossons.data

data class MinimalPair(
    val id: String,
    val wordA: String,
    val wordB: String,
    val targetA: String,
    val targetB: String,
    val hint: String,
    val isCommonPortuguese: Boolean,
    val editable: Boolean,
)
