package com.example.fitnessin.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.*
import com.example.fitnessin.util.NutritionCalculator
import javax.inject.Inject
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onSaveProfile: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    // Input states
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var currentWeight by remember { mutableStateOf("") }
    var goalWeight by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.PRIA) }
    var selectedActivityLevel by remember { mutableStateOf(ActivityLevel.SEDENTARY) }
    var expandedGender by remember { mutableStateOf(false) }
    var expandedActivity by remember { mutableStateOf(false) }
    
    // Calculation states
    var calculationResult by remember { mutableStateOf<NutritionData?>(null) }
    var showRecommendations by remember { mutableStateOf(false) }
    
    // Validation states
    var showValidationError by remember { mutableStateOf(false) }
    
    // Create nutrition calculator instance
    val nutritionCalculator = remember { NutritionCalculator() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Setup Profil Kebugaran",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )
        
        Text(
            text = "Masukkan data diri Anda untuk mendapatkan rekomendasi nutrisi yang akurat",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Personal Information Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Data Pribadi",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Lengkap") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Usia (tahun)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedGender,
                    onExpandedChange = { expandedGender = !expandedGender }
                ) {
                    OutlinedTextField(
                        value = selectedGender.name,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Jenis Kelamin") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedGender) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedGender,
                        onDismissRequest = { expandedGender = false }
                    ) {
                        Gender.values().forEach { gender ->
                            DropdownMenuItem(
                                text = { Text(gender.name) },
                                onClick = {
                                    selectedGender = gender
                                    expandedGender = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Physical Data Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Data Fisik & Target",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text("Tinggi (cm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = currentWeight,
                        onValueChange = { currentWeight = it },
                        label = { Text("Berat Saat Ini (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = goalWeight,
                    onValueChange = { goalWeight = it },
                    label = { Text("Target Berat Badan (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Activity Level Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedActivity,
                    onExpandedChange = { expandedActivity = !expandedActivity }
                ) {
                    OutlinedTextField(
                        value = selectedActivityLevel.description,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Tingkat Aktivitas") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedActivity) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedActivity,
                        onDismissRequest = { expandedActivity = false }
                    ) {
                        ActivityLevel.values().forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level.description) },
                                onClick = {
                                    selectedActivityLevel = level
                                    expandedActivity = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Calculate Button
        val isInputValid = name.isNotBlank() && email.isNotBlank() && age.isNotBlank() && 
                          height.isNotBlank() && currentWeight.isNotBlank() && goalWeight.isNotBlank()

        Button(
            onClick = {
                if (isInputValid) {
                    val user = User(
                        uid = "",
                        name = name,
                        email = email,
                        age = age.toIntOrNull() ?: 0,
                        gender = selectedGender,
                        height = height.toDoubleOrNull() ?: 0.0,
                        currentWeight = currentWeight.toDoubleOrNull() ?: 0.0,
                        goalWeight = goalWeight.toDoubleOrNull() ?: 0.0,
                        activityLevel = selectedActivityLevel
                    )
                    
                    // Calculate nutrition data
                    calculationResult = nutritionCalculator.calculateNutritionData(user)
                    showRecommendations = true
                    showValidationError = false
                } else {
                    showValidationError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = isInputValid
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Hitung Kebutuhan Nutrisi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Validation Error
        if (showValidationError) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = "Mohon lengkapi semua data yang diperlukan",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Calculation Results
        calculationResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val goalIcon = when (result.goal) {
                            WeightGoal.SURPLUS -> Icons.Default.KeyboardArrowUp
                            WeightGoal.DEFICIT -> Icons.Default.KeyboardArrowDown
                            WeightGoal.MAINTENANCE -> Icons.Default.KeyboardArrowRight
                        }
                        
                        val goalColor = when (result.goal) {
                            WeightGoal.SURPLUS -> Color(0xFF4CAF50)
                            WeightGoal.DEFICIT -> Color(0xFF2196F3)
                            WeightGoal.MAINTENANCE -> Color(0xFFFF9800)
                        }
                        
                        Icon(
                            imageVector = goalIcon,
                            contentDescription = null,
                            tint = goalColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hasil Kalkulasi Nutrisi",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

                    // Goal Status
                    val goalText = when (result.goal) {
                        WeightGoal.SURPLUS -> "Menambah Berat Badan"
                        WeightGoal.DEFICIT -> "Menurunkan Berat Badan"
                        WeightGoal.MAINTENANCE -> "Mempertahankan Berat Badan"
                    }
                    
                    Text(
                        text = "Target: $goalText",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    // Calories Information
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "BMR",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${result.bmr.roundToInt()} kcal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Column {
                            Text(
                                text = "TDEE",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${result.tdee.roundToInt()} kcal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        Column {
                            Text(
                                text = "Target Kalori",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${result.targetCalories.roundToInt()} kcal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

                    // Macronutrients
                    Text(
                        text = "Kebutuhan Makronutrien Harian",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Protein",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${result.proteinGrams.roundToInt()}g",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE91E63)
                            )
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Lemak",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${result.fatGrams.roundToInt()}g",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                        }
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Karbohidrat",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${result.carbGrams.roundToInt()}g",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }
        }

        // Recommendations Container
        if (showRecommendations && calculationResult != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Rekomendasi Untuk Anda",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val recommendations = generateRecommendations(calculationResult!!)
                    
                    recommendations.forEach { recommendation ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Text(
                                    text = recommendation.title,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = recommendation.content,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Save Profile Button
        if (calculationResult != null) {
            Button(
                onClick = {
                    val user = User(
                        uid = "",
                        name = name,
                        email = email,
                        age = age.toIntOrNull() ?: 0,
                        gender = selectedGender,
                        height = height.toDoubleOrNull() ?: 0.0,
                        currentWeight = currentWeight.toDoubleOrNull() ?: 0.0,
                        goalWeight = goalWeight.toDoubleOrNull() ?: 0.0,
                        activityLevel = selectedActivityLevel
                    )
                    onSaveProfile(user)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "Simpan Profil & Lanjutkan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun generateRecommendations(nutritionData: NutritionData): List<FoodRecommendation> {
    return when (nutritionData.goal) {
        WeightGoal.SURPLUS -> listOf(
            FoodRecommendation(
                title = "🥛 Tingkatkan Asupan Protein",
                content = "Konsumsi ${nutritionData.proteinGrams.roundToInt()}g protein per hari dari sumber seperti daging, telur, susu, dan kacang-kacangan.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            ),
            FoodRecommendation(
                title = "🍚 Karbohidrat Kompleks",
                content = "Pilih karbohidrat kompleks seperti nasi merah, oat, dan kentang untuk energi yang stabil.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            ),
            FoodRecommendation(
                title = "🥑 Lemak Sehat",
                content = "Tambahkan lemak sehat dari alpukat, kacang-kacangan, dan minyak zaitun sekitar ${nutritionData.fatGrams.roundToInt()}g per hari.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            )
        )
        WeightGoal.DEFICIT -> listOf(
            FoodRecommendation(
                title = "🥗 Prioritaskan Sayuran",
                content = "Perbanyak sayuran rendah kalori tapi tinggi serat untuk rasa kenyang lebih lama.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            ),
            FoodRecommendation(
                title = "🐟 Protein Tanpa Lemak",
                content = "Pilih protein rendah lemak seperti ikan, ayam tanpa kulit, dan tahu untuk ${nutritionData.proteinGrams.roundToInt()}g protein harian.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            ),
            FoodRecommendation(
                title = "💧 Hidrasi Optimal",
                content = "Minum air putih minimal 8 gelas per hari untuk mendukung metabolisme dan mengurangi rasa lapar palsu.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            )
        )
        WeightGoal.MAINTENANCE -> listOf(
            FoodRecommendation(
                title = "⚖️ Keseimbangan Nutrisi",
                content = "Jaga keseimbangan ${nutritionData.proteinGrams.roundToInt()}g protein, ${nutritionData.carbGrams.roundToInt()}g karbohidrat, dan ${nutritionData.fatGrams.roundToInt()}g lemak per hari.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            ),
            FoodRecommendation(
                title = "🕐 Waktu Makan Teratur",
                content = "Konsisten dengan waktu makan untuk menjaga metabolisme yang stabil.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            ),
            FoodRecommendation(
                title = "🏃‍♀️ Aktivitas Konsisten",
                content = "Pertahankan tingkat aktivitas fisik sesuai dengan ${nutritionData.targetCalories.roundToInt()} kalori target harian Anda.",
                targetCalories = nutritionData.targetCalories,
                goal = nutritionData.goal
            )
        )
    }
}
