package com.example.nottokeyword

data class Keyword(
    val word: String,
    val count: Int,
    val place: Int = 0,
    val state: PlaceState = PlaceState.New,
    val notch: Int? = 0,
    val hasChanged: Boolean = false
)
