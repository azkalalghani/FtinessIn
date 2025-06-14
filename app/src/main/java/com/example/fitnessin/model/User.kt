package com.example.fitnessin.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val gender: Gender = Gender.PRIA,
    val height: Double = 0.0, // dalam cm
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val goalWeight: Double = 0.0, // dalam kg
    val currentWeight: Double = 0.0 // dalam kg
)

enum class Gender {
    PRIA, WANITA
}

enum class ActivityLevel(val description: String, val factor: Double) {
    SEDENTARY("Sangat Jarang", 1.2),
    LIGHTLY_ACTIVE("Aktivitas Ringan", 1.375),
    MODERATELY_ACTIVE("Aktivitas Sedang", 1.55),
    VERY_ACTIVE("Aktivitas Berat", 1.725),
    EXTREMELY_ACTIVE("Sangat Berat", 1.9)
}
