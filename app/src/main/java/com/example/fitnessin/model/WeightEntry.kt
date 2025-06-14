package com.example.fitnessin.model

import com.google.firebase.Timestamp

data class WeightEntry(
    val id: String = "",
    val userId: String = "",
    val weight: Double = 0.0, // dalam kg
    val timestamp: Timestamp = Timestamp.now()
)
