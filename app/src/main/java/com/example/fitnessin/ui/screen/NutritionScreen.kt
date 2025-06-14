package com.example.fitnessin.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.FoodRecommendation
import com.example.fitnessin.model.NutritionData
import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightGoal
import com.example.fitnessin.ui.components.MarkdownText

@Composable
fun NutritionScreen(
        user: User,
        nutritionData: NutritionData?,
        foodRecommendation: FoodRecommendation?,
        onRefreshRecommendation: () -> Unit,
        modifier: Modifier = Modifier
) {
    Column(
            modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with refresh button
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                    text = "Panduan Nutrisi",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
            )

            IconButton(
                    onClick = onRefreshRecommendation,
                    colors =
                            IconButtonDefaults.iconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
            ) {
                Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh berdasarkan data terbaru"
                )
            }
        }

        // User Current Data Card
        UserDataSummaryCard(user)

        // Nutrition Overview
        nutritionData?.let { data -> NutritionOverviewCard(data) }

        // Daily Macros
        nutritionData?.let { data -> DailyMacrosCard(data) }

        // Food Recommendations
        foodRecommendation?.let { recommendation -> FoodRecommendationCard(recommendation) }

        // Nutrition Tips
        NutritionTipsCard()

        // Meal Planning Guide
        MealPlanningCard(nutritionData)
    }
}

@Composable
private fun NutritionOverviewCard(nutritionData: NutritionData) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "Ringkasan Nutrisi Harian",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            val goalText =
                    when (nutritionData.goal) {
                        WeightGoal.SURPLUS -> "Menambah Berat Badan"
                        WeightGoal.DEFICIT -> "Menurunkan Berat Badan"
                        WeightGoal.MAINTENANCE -> "Mempertahankan Berat Badan"
                    }

            Text(
                    text = "Target: $goalText",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                NutritionMetric("BMR", "${nutritionData.bmr.toInt()} kcal")
                NutritionMetric("TDEE", "${nutritionData.tdee.toInt()} kcal")
                NutritionMetric("Target", "${nutritionData.targetCalories.toInt()} kcal")
            }
        }
    }
}

@Composable
private fun DailyMacrosCard(nutritionData: NutritionData) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                    text = "Kebutuhan Makronutrien Harian",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroNutrientDetail(
                        name = "Protein",
                        amount = "${nutritionData.proteinGrams.toInt()}g",
                        percentage = "30%",
                        color = Color(0xFFE91E63),
                        description = "Untuk pembentukan otot"
                )

                MacroNutrientDetail(
                        name = "Lemak",
                        amount = "${nutritionData.fatGrams.toInt()}g",
                        percentage = "25%",
                        color = Color(0xFFFF9800),
                        description = "Untuk energi & hormon"
                )

                MacroNutrientDetail(
                        name = "Karbohidrat",
                        amount = "${nutritionData.carbGrams.toInt()}g",
                        percentage = "45%",
                        color = Color(0xFF4CAF50),
                        description = "Untuk energi utama"
                )
            }
        }
    }
}

@Composable
private fun FoodRecommendationCard(recommendation: FoodRecommendation) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                        text = "🍽️ Rekomendasi Makanan",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                )
            }

            // Use MarkdownText component to format the content
            MarkdownText(text = recommendation.content, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun UserDataSummaryCard(user: User) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "📊 Data Anda Saat Ini",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                            text = "Berat Badan",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                            text = "${user.currentWeight} kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                    )
                }
                Column {
                    Text(
                            text = "Target",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                            text = "${user.goalWeight} kg",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                    )
                }
                Column {
                    Text(
                            text = "Tinggi",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                    )
                    Text(
                            text = "${user.height.toInt()} cm",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                        text = "Usia: ${user.age} tahun",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                        text = "Gender: ${if (user.gender.name == "PRIA") "Pria" else "Wanita"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Text(
                        text = user.activityLevel.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Text(
                    text =
                            "💡 Tekan tombol refresh untuk memperbarui rekomendasi berdasarkan data terbaru",
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun NutritionTipsCard() {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "💡 Tips Nutrisi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
            )

            val tips =
                    listOf(
                            "Minum air putih minimal 8 gelas per hari",
                            "Konsumsi protein dalam setiap makan",
                            "Pilih karbohidrat kompleks daripada sederhana",
                            "Makan buah dan sayuran beragam warna",
                            "Hindari makanan olahan berlebihan",
                            "Makan dengan porsi kecil tapi sering"
                    )

            tips.forEach { tip ->
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                    Text(text = "• ", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = tip, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun MealPlanningCard(nutritionData: NutritionData?) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = "🍽️ Panduan Pembagian Makan",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
            )

            nutritionData?.let { data ->
                val breakfastCal = (data.targetCalories * 0.25).toInt()
                val lunchCal = (data.targetCalories * 0.35).toInt()
                val dinnerCal = (data.targetCalories * 0.25).toInt()
                val snackCal = (data.targetCalories * 0.15).toInt()

                MealTimeCard("🌅 Sarapan", "$breakfastCal kcal", "25% dari total kalori")
                MealTimeCard("☀️ Makan Siang", "$lunchCal kcal", "35% dari total kalori")
                MealTimeCard("🌙 Makan Malam", "$dinnerCal kcal", "25% dari total kalori")
                MealTimeCard("🍎 Snack", "$snackCal kcal", "15% dari total kalori")
            }
        }
    }
}

@Composable
private fun NutritionMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun MacroNutrientDetail(
        name: String,
        amount: String,
        percentage: String,
        color: Color,
        description: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(100.dp)) {
        Text(
                text = name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
        )
        Text(text = amount, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(
                text = percentage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
                text = description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun MealTimeCard(title: String, calories: String, percentage: String) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                    CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                        text = percentage,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                    text = calories,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
