package com.example.fitnessin.util

import com.example.fitnessin.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionCalculator @Inject constructor() {

    /**
     * Menentukan tujuan berdasarkan perbandingan berat badan saat ini dengan target
     */
    fun determineWeightGoal(currentWeight: Double, goalWeight: Double): WeightGoal {
        return when {
            goalWeight > currentWeight -> WeightGoal.SURPLUS
            goalWeight < currentWeight -> WeightGoal.DEFICIT
            else -> WeightGoal.MAINTENANCE
        }
    }

    /**
     * Menghitung BMR menggunakan formula Mifflin-St Jeor
     */
    fun calculateBMR(
        weight: Double, // kg
        height: Double, // cm
        age: Int,
        gender: Gender
    ): Double {
        val baseCalculation = (10 * weight) + (6.25 * height) - (5 * age)
        return when (gender) {
            Gender.PRIA -> baseCalculation + 5
            Gender.WANITA -> baseCalculation - 161
        }
    }

    /**
     * Menghitung TDEE (Total Daily Energy Expenditure)
     */
    fun calculateTDEE(bmr: Double, activityLevel: ActivityLevel): Double {
        return bmr * activityLevel.factor
    }

    /**
     * Menentukan target kalori berdasarkan tujuan
     */
    fun calculateTargetCalories(tdee: Double, goal: WeightGoal): Double {
        return when (goal) {
            WeightGoal.SURPLUS -> tdee + Constants.SURPLUS_CALORIES
            WeightGoal.DEFICIT -> tdee - Constants.DEFICIT_CALORIES
            WeightGoal.MAINTENANCE -> tdee
        }
    }

    /**
     * Menghitung distribusi makronutrien
     */
    fun calculateMacronutrients(
        targetCalories: Double,
        weight: Double
    ): Triple<Double, Double, Double> { // Protein, Fat, Carbs (dalam gram)
        
        // Protein: 1.8g per kg berat badan
        val proteinGrams = weight * Constants.PROTEIN_PER_KG
        val proteinCalories = proteinGrams * Constants.CALORIES_PER_GRAM_PROTEIN
        
        // Lemak: 25% dari total kalori
        val fatCalories = targetCalories * Constants.FAT_PERCENTAGE
        val fatGrams = fatCalories / Constants.CALORIES_PER_GRAM_FAT
        
        // Karbohidrat: sisanya
        val carbCalories = targetCalories - proteinCalories - fatCalories
        val carbGrams = carbCalories / Constants.CALORIES_PER_GRAM_CARB
        
        return Triple(proteinGrams, fatGrams, carbGrams)
    }

    /**
     * Menghitung semua data nutrisi untuk user
     */
    fun calculateNutritionData(user: User): NutritionData {
        val goal = determineWeightGoal(user.currentWeight, user.goalWeight)
        val bmr = calculateBMR(user.currentWeight, user.height, user.age, user.gender)
        val tdee = calculateTDEE(bmr, user.activityLevel)
        val targetCalories = calculateTargetCalories(tdee, goal)
        val (protein, fat, carbs) = calculateMacronutrients(targetCalories, user.currentWeight)
        
        return NutritionData(
            bmr = bmr,
            tdee = tdee,
            targetCalories = targetCalories,
            proteinGrams = protein,
            fatGrams = fat,
            carbGrams = carbs,
            goal = goal
        )
    }
}
