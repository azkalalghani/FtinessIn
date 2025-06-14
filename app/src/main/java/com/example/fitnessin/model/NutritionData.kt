package com.example.fitnessin.model

data class NutritionData(
    val bmr: Double,
    val tdee: Double,
    val targetCalories: Double,
    val proteinGrams: Double,
    val fatGrams: Double,
    val carbGrams: Double,
    val goal: WeightGoal
)

enum class WeightGoal {
    SURPLUS, // Naik berat badan
    DEFICIT, // Turun berat badan  
    MAINTENANCE // Menjaga berat badan
}

data class FoodRecommendation(
    val title: String,
    val content: String,
    val targetCalories: Double,
    val goal: WeightGoal
)
