package com.example.fitnessin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.User
import com.example.fitnessin.util.NutritionCalculator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User,
    onEditProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutritionCalculator = remember { NutritionCalculator() }
    val nutritionData = remember(user) { nutritionCalculator.calculateNutritionData(user) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profil Saya",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            IconButton(
                onClick = onEditProfile,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile"
                )
            }
        }

        // Profile Picture and Basic Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(2).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = user.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = user.email,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }

        // Personal Information
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Informasi Pribadi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                ProfileInfoRow("Usia", "${user.age} tahun")
                ProfileInfoRow("Jenis Kelamin", user.gender.name)
                ProfileInfoRow("Tinggi Badan", "${user.height.toInt()} cm")
                ProfileInfoRow("Berat Saat Ini", "${user.currentWeight} kg")
                ProfileInfoRow("Target Berat", "${user.goalWeight} kg")
                ProfileInfoRow("Tingkat Aktivitas", user.activityLevel.description)
            }
        }

        // Nutrition Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ringkasan Nutrisi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                val goalText = when (nutritionData.goal) {
                    com.example.fitnessin.model.WeightGoal.SURPLUS -> "Menambah Berat Badan"
                    com.example.fitnessin.model.WeightGoal.DEFICIT -> "Menurunkan Berat Badan"
                    com.example.fitnessin.model.WeightGoal.MAINTENANCE -> "Mempertahankan Berat Badan"
                }

                ProfileInfoRow(
                    "Target", 
                    goalText, 
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                ProfileInfoRow(
                    "BMR", 
                    "${nutritionData.bmr.toInt()} kcal", 
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                ProfileInfoRow(
                    "TDEE", 
                    "${nutritionData.tdee.toInt()} kcal", 
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                ProfileInfoRow(
                    "Target Kalori", 
                    "${nutritionData.targetCalories.toInt()} kcal", 
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )
                
                Text(
                    text = "Kebutuhan Makronutrien",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MacroNutrientCard(
                        "Protein",
                        "${nutritionData.proteinGrams.toInt()}g",
                        androidx.compose.ui.graphics.Color(0xFFE91E63)
                    )
                    MacroNutrientCard(
                        "Lemak",
                        "${nutritionData.fatGrams.toInt()}g",
                        androidx.compose.ui.graphics.Color(0xFFFF9800)
                    )
                    MacroNutrientCard(
                        "Karbohidrat",
                        "${nutritionData.carbGrams.toInt()}g",
                        androidx.compose.ui.graphics.Color(0xFF4CAF50)
                    )
                }
            }
        }

        // BMI Information
        val bmi = user.currentWeight / ((user.height / 100) * (user.height / 100))
        val bmiCategory = when {
            bmi < 18.5 -> "Underweight"
            bmi < 25.0 -> "Normal"
            bmi < 30.0 -> "Overweight"
            else -> "Obesitas"
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Body Mass Index (BMI)",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = String.format("%.1f", bmi),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = bmiCategory,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    Text(
                        text = "BMI dihitung dari\nberat dan tinggi badan",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String, 
    value: String,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = textColor.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}

@Composable
private fun MacroNutrientCard(
    name: String,
    value: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
