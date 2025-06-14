package com.example.fitnessin.util

import com.example.fitnessin.model.ActivityLevel
import com.example.fitnessin.model.Gender
import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightGoal
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class NutritionCalculatorTest {

    private lateinit var nutritionCalculator: NutritionCalculator

    @Before
    fun setUp() {
        nutritionCalculator = NutritionCalculator()
    }

    @Test
    fun `test BMR calculation for male`() {
        // Given
        val weight = 70.0 // kg
        val height = 175.0 // cm
        val age = 25
        val gender = Gender.PRIA

        // When
        val bmr = nutritionCalculator.calculateBMR(weight, height, age, gender)

        // Then
        val expected = (10 * weight) + (6.25 * height) - (5 * age) + 5
        assertEquals(expected, bmr, 0.1)
    }

    @Test
    fun `test BMR calculation for female`() {
        // Given
        val weight = 60.0 // kg
        val height = 165.0 // cm
        val age = 30
        val gender = Gender.WANITA

        // When
        val bmr = nutritionCalculator.calculateBMR(weight, height, age, gender)

        // Then
        val expected = (10 * weight) + (6.25 * height) - (5 * age) - 161
        assertEquals(expected, bmr, 0.1)
    }

    @Test
    fun `test TDEE calculation`() {
        // Given
        val bmr = 1500.0
        val activityLevel = ActivityLevel.MODERATELY_ACTIVE

        // When
        val tdee = nutritionCalculator.calculateTDEE(bmr, activityLevel)

        // Then
        val expected = bmr * activityLevel.factor
        assertEquals(expected, tdee, 0.1)
    }

    @Test
    fun `test weight goal determination - surplus`() {
        // Given
        val currentWeight = 70.0
        val goalWeight = 75.0

        // When
        val goal = nutritionCalculator.determineWeightGoal(currentWeight, goalWeight)

        // Then
        assertEquals(WeightGoal.SURPLUS, goal)
    }

    @Test
    fun `test weight goal determination - deficit`() {
        // Given
        val currentWeight = 80.0
        val goalWeight = 70.0

        // When
        val goal = nutritionCalculator.determineWeightGoal(currentWeight, goalWeight)

        // Then
        assertEquals(WeightGoal.DEFICIT, goal)
    }

    @Test
    fun `test weight goal determination - maintenance`() {
        // Given
        val currentWeight = 70.0
        val goalWeight = 70.0

        // When
        val goal = nutritionCalculator.determineWeightGoal(currentWeight, goalWeight)

        // Then
        assertEquals(WeightGoal.MAINTENANCE, goal)
    }

    @Test
    fun `test target calories calculation - surplus`() {
        // Given
        val tdee = 2000.0
        val goal = WeightGoal.SURPLUS

        // When
        val targetCalories = nutritionCalculator.calculateTargetCalories(tdee, goal)

        // Then
        val expected = tdee + Constants.SURPLUS_CALORIES
        assertEquals(expected, targetCalories, 0.1)
    }

    @Test
    fun `test target calories calculation - deficit`() {
        // Given
        val tdee = 2000.0
        val goal = WeightGoal.DEFICIT

        // When
        val targetCalories = nutritionCalculator.calculateTargetCalories(tdee, goal)

        // Then
        val expected = tdee - Constants.DEFICIT_CALORIES
        assertEquals(expected, targetCalories, 0.1)
    }

    @Test
    fun `test macronutrients calculation`() {
        // Given
        val targetCalories = 2500.0
        val weight = 70.0

        // When
        val (protein, fat, carbs) = nutritionCalculator.calculateMacronutrients(targetCalories, weight)

        // Then
        val expectedProtein = weight * Constants.PROTEIN_PER_KG
        val expectedFat = (targetCalories * Constants.FAT_PERCENTAGE) / Constants.CALORIES_PER_GRAM_FAT
        val expectedCarbs = (targetCalories - (expectedProtein * Constants.CALORIES_PER_GRAM_PROTEIN) - (expectedFat * Constants.CALORIES_PER_GRAM_FAT)) / Constants.CALORIES_PER_GRAM_CARB

        assertEquals(expectedProtein, protein, 0.1)
        assertEquals(expectedFat, fat, 0.1)
        assertEquals(expectedCarbs, carbs, 0.1)
    }

    @Test
    fun `test complete nutrition data calculation`() {
        // Given
        val user = User(
            uid = "test123",
            name = "Test User",
            email = "test@example.com",
            age = 25,
            gender = Gender.PRIA,
            height = 175.0,
            currentWeight = 70.0,
            goalWeight = 75.0,
            activityLevel = ActivityLevel.MODERATELY_ACTIVE
        )

        // When
        val nutritionData = nutritionCalculator.calculateNutritionData(user)

        // Then
        assertTrue(nutritionData.bmr > 0)
        assertTrue(nutritionData.tdee > nutritionData.bmr)
        assertTrue(nutritionData.targetCalories > 0)
        assertTrue(nutritionData.proteinGrams > 0)
        assertTrue(nutritionData.fatGrams > 0)
        assertTrue(nutritionData.carbGrams > 0)
        assertEquals(WeightGoal.SURPLUS, nutritionData.goal)
    }
}
