package com.example.fitnessin.util

object Constants {
    // Demo Mode untuk testing tanpa Firebase
    const val DEMO_MODE = false // Set false untuk production dengan Firebase

    // Firebase Collections
    const val USERS_COLLECTION = "users"
    const val PROGRESS_COLLECTION = "progress"

    // Nutrition Constants
    const val PROTEIN_PER_KG = 1.8
    const val FAT_PERCENTAGE = 0.25
    const val CALORIES_PER_GRAM_PROTEIN = 4
    const val CALORIES_PER_GRAM_FAT = 9
    const val CALORIES_PER_GRAM_CARB = 4

    // Weight Goal Adjustments
    const val SURPLUS_CALORIES = 400
    const val DEFICIT_CALORIES = 400

    // Activity Level Factors
    const val SEDENTARY_FACTOR = 1.2
    const val LIGHTLY_ACTIVE_FACTOR = 1.375
    const val MODERATELY_ACTIVE_FACTOR = 1.55
    const val VERY_ACTIVE_FACTOR = 1.725
    const val EXTREMELY_ACTIVE_FACTOR = 1.9

    // UI Constants
    const val WEIGHT_HISTORY_LIMIT = 30
    const val WEIGHT_HISTORY_DISPLAY_LIMIT = 5

    // API Keys - Replace with your actual keys
    const val GEMINI_API_KEY = "AIzaSyAtPpiLMERQznakvetk3a2ivVKT3e6tsSE"
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/"

    // Date Formats
    const val DATE_FORMAT_DISPLAY = "dd/MM/yyyy"
    const val DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss'Z'"
}
