package com.example.fitnessin.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.window.Dialog
import com.example.fitnessin.model.FoodRecommendation
import com.example.fitnessin.model.NutritionData
import com.example.fitnessin.model.WeightEntry
import com.example.fitnessin.model.WeightGoal
import com.example.fitnessin.ui.components.MarkdownText
import com.example.fitnessin.util.Constants
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
        nutritionData: NutritionData?,
        foodRecommendation: FoodRecommendation?,
        weightHistory: List<WeightEntry>,
        onAddWeight: (Double) -> Unit,
        onRefreshRecommendation: () -> Unit,
        modifier: Modifier = Modifier
) {
    var showAddWeightDialog by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var lastAddedWeight by remember { mutableStateOf(0.0) }

    LazyColumn(
            modifier = modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                    text = "Dashboard Kebugaran",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
            )
        }

        // Nutrition Summary Card
        item { nutritionData?.let { nutrition -> NutritionSummaryCard(nutritionData = nutrition) } }

        // Weight Progress Card
        item {
            WeightProgressCard(
                    weightHistory = weightHistory,
                    onAddWeight = { showAddWeightDialog = true }
            )
        }

        // Food Recommendation Card
        item {
            FoodRecommendationCard(
                    recommendation = foodRecommendation,
                    onRefresh = onRefreshRecommendation
            )
        }

        // Success message
        if (showSuccessMessage) {
            item {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = "✅ Berat badan ${lastAddedWeight}kg berhasil ditambahkan!",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                        )
                    }
                }

                LaunchedEffect(showSuccessMessage) {
                    kotlinx.coroutines.delay(3000)
                    showSuccessMessage = false
                }
            }
        }
    }

    if (showAddWeightDialog) {
        AddWeightDialog(
                onDismiss = { showAddWeightDialog = false },
                onConfirm = { weight ->
                    onAddWeight(weight)
                    lastAddedWeight = weight
                    showAddWeightDialog = false
                    showSuccessMessage = true
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NutritionSummaryCard(nutritionData: NutritionData) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "Ringkasan Nutrisi", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            val goalText =
                    when (nutritionData.goal) {
                        WeightGoal.SURPLUS -> "Menambah Berat Badan"
                        WeightGoal.DEFICIT -> "Menurunkan Berat Badan"
                        WeightGoal.MAINTENANCE -> "Mempertahankan Berat Badan"
                    }

            Text(text = "Tujuan: $goalText")
            Text(text = "BMR: ${nutritionData.bmr.toInt()} kkal")
            Text(text = "TDEE: ${nutritionData.tdee.toInt()} kkal")
            Text(text = "Target Kalori: ${nutritionData.targetCalories.toInt()} kkal")

            Text(text = "Kebutuhan Makronutrien:")
            Text(text = "• Protein: ${nutritionData.proteinGrams.toInt()}g")
            Text(text = "• Lemak: ${nutritionData.fatGrams.toInt()}g")
            Text(text = "• Karbohidrat: ${nutritionData.carbGrams.toInt()}g")
        }
    }
}

@Composable
private fun WeightProgressCard(weightHistory: List<WeightEntry>, onAddWeight: () -> Unit) {
    Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Progress Berat Badan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                IconButton(onClick = onAddWeight) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah Berat Badan")
                }
            }

            if (weightHistory.isNotEmpty()) {
                val latest = weightHistory.first()

                // Latest weight with better formatting
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.secondaryContainer
                                )
                ) {
                    Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                                text = "Berat Saat Ini",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                                text = "${latest.weight} kg",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Progress comparison
                if (weightHistory.size > 1) {
                    val previous = weightHistory[1]
                    val difference = latest.weight - previous.weight
                    val changeText =
                            if (difference > 0) "+${String.format("%.1f", difference)}"
                            else "${String.format("%.1f", difference)}"
                    val changeColor =
                            when {
                                difference > 0 -> Color(0xFF4CAF50) // Green for gain
                                difference < 0 -> Color(0xFFFF5722) // Red for loss
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                    Text(
                            text = "Perubahan: $changeText kg dari data sebelumnya",
                            fontSize = 14.sp,
                            color = changeColor,
                            fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Riwayat Terakhir:", fontSize = 16.sp, fontWeight = FontWeight.Medium)

                weightHistory.take(Constants.WEIGHT_HISTORY_DISPLAY_LIMIT).forEach { entry ->
                    val dateFormat =
                            SimpleDateFormat(Constants.DATE_FORMAT_DISPLAY, Locale.getDefault())
                    val date = dateFormat.format(entry.timestamp.toDate())

                    Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.surfaceVariant
                                    )
                    ) {
                        Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                    text = date,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                    text = "${entry.weight} kg",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                ) {
                    Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "📊", fontSize = 32.sp)
                        Text(
                                text = "Belum ada data berat badan",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                                text = "Tambahkan berat badan pertama Anda!",
                                fontSize = 14.sp,
                                color =
                                        MaterialTheme.colorScheme.onErrorContainer.copy(
                                                alpha = 0.8f
                                        )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FoodRecommendationCard(recommendation: FoodRecommendation?, onRefresh: () -> Unit) {
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
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = onRefresh) {
                    Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh berdasarkan data terbaru",
                            tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            recommendation?.let { rec ->
                // Use MarkdownText component to format the content
                MarkdownText(text = rec.content, modifier = Modifier.fillMaxWidth())
            }
                    ?: run {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                    text = "Memuat rekomendasi makanan...",
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
        }
    }
}

@Composable
private fun AddWeightDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var weight by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(text = "Tambah Berat Badan", fontSize = 18.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Berat Badan (kg)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                )

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                            onClick = {
                                weight.toDoubleOrNull()?.let { weightValue ->
                                    onConfirm(weightValue)
                                }
                            },
                            enabled = weight.toDoubleOrNull() != null
                    ) { Text("Simpan") }
                }
            }
        }
    }
}
