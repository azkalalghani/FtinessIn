package com.example.fitnessin.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightEntry
import com.example.fitnessin.model.WeightGoal
import com.example.fitnessin.util.NutritionCalculator
import kotlin.math.abs

@Composable
fun GoalsScreen(
    user: User,
    weightHistory: List<WeightEntry>,
    onUpdateGoal: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditGoalDialog by remember { mutableStateOf(false) }
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
                text = "Target & Goal",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            IconButton(
                onClick = { showEditGoalDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Goal"
                )
            }
        }

        // Current Goal Card
        CurrentGoalCard(user, weightHistory, nutritionData.goal)

        // Goal Progress Card
        GoalProgressCard(user, weightHistory)

        // Timeline & Milestones
        TimelineCard(user, weightHistory)

        // Motivation Card
        MotivationCard(user, weightHistory)

        // Goal Tips
        GoalTipsCard()
    }

    // Edit Goal Dialog
    if (showEditGoalDialog) {
        EditGoalDialog(
            currentGoal = user.goalWeight,
            onDismiss = { showEditGoalDialog = false },
            onUpdateGoal = { newGoal ->
                onUpdateGoal(newGoal)
                showEditGoalDialog = false
            }
        )
    }
}

@Composable
private fun CurrentGoalCard(user: User, weightHistory: List<WeightEntry>, goal: WeightGoal) {
    val currentWeight = weightHistory.lastOrNull()?.weight ?: user.currentWeight
    val weightDifference = user.goalWeight - currentWeight
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (goal) {
                WeightGoal.SURPLUS -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                WeightGoal.DEFICIT -> Color(0xFF2196F3).copy(alpha = 0.1f)
                WeightGoal.MAINTENANCE -> Color(0xFFFF9800).copy(alpha = 0.1f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = when (goal) {
                        WeightGoal.SURPLUS -> Color(0xFF4CAF50)
                        WeightGoal.DEFICIT -> Color(0xFF2196F3)
                        WeightGoal.MAINTENANCE -> Color(0xFFFF9800)
                    },
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Target Utama",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val goalText = when (goal) {
                        WeightGoal.SURPLUS -> "Menambah Berat Badan"
                        WeightGoal.DEFICIT -> "Menurunkan Berat Badan"
                        WeightGoal.MAINTENANCE -> "Mempertahankan Berat Badan"
                    }
                    Text(
                        text = goalText,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GoalMetric("Berat Saat Ini", "${String.format("%.1f", currentWeight)} kg")
                GoalMetric("Target Berat", "${user.goalWeight} kg")
                GoalMetric(
                    if (weightDifference > 0) "Perlu Tambah" else "Perlu Kurangi",
                    "${String.format("%.1f", abs(weightDifference))} kg"
                )
            }
        }
    }
}

@Composable
private fun GoalProgressCard(user: User, weightHistory: List<WeightEntry>) {
    val currentWeight = weightHistory.lastOrNull()?.weight ?: user.currentWeight
    val startWeight = user.currentWeight
    val goalWeight = user.goalWeight
    
    val progress = if (goalWeight == startWeight) {
        100.0
    } else if (goalWeight > startWeight) {
        // Weight gain goal
        ((currentWeight - startWeight) / (goalWeight - startWeight)) * 100
    } else {
        // Weight loss goal
        ((startWeight - currentWeight) / (startWeight - goalWeight)) * 100
    }.coerceIn(0.0, 100.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Progress ke Target",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Progress Circle/Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format("%.1f", progress)}% Tercapai",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (progress >= 100) {
                        Text(
                            text = "🎉 Target Tercapai!",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LinearProgressIndicator(
                    progress = { (progress / 100).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = when {
                        progress >= 100 -> Color(0xFF4CAF50)
                        progress >= 75 -> Color(0xFFFF9800)
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            }

            if (progress < 100) {
                val remaining = abs(goalWeight - currentWeight)
                val timeEstimate = if (weightHistory.size >= 2) {
                    val recentEntries = weightHistory.takeLast(7)
                    if (recentEntries.size >= 2) {
                        val avgChange = abs(recentEntries.last().weight - recentEntries.first().weight) / recentEntries.size
                        if (avgChange > 0) {
                            (remaining / avgChange).toInt()
                        } else null
                    } else null
                } else null

                Text(
                    text = "Sisa: ${String.format("%.1f", remaining)} kg" + 
                           if (timeEstimate != null) " (Est. $timeEstimate hari)" else "",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun TimelineCard(user: User, weightHistory: List<WeightEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Timeline & Milestone",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            val milestones = generateMilestones(user, weightHistory)
            
            milestones.forEach { milestone ->
                MilestoneItem(milestone)
            }
        }
    }
}

@Composable
private fun MotivationCard(user: User, weightHistory: List<WeightEntry>) {
    val currentWeight = weightHistory.lastOrNull()?.weight ?: user.currentWeight
    val weightChange = currentWeight - user.currentWeight
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "💪 Motivasi Hari Ini",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )

            val motivationMessage = when {
                weightHistory.isEmpty() -> "Mulai perjalanan fitness Anda hari ini! Setiap langkah kecil adalah kemajuan."
                abs(weightChange) < 0.5 -> "Konsistensi adalah kunci! Tetap fokus pada target Anda."
                weightChange > 0 && user.goalWeight > user.currentWeight -> "Bagus! Anda sudah menambah ${String.format("%.1f", weightChange)} kg. Terus pertahankan!"
                weightChange < 0 && user.goalWeight < user.currentWeight -> "Luar biasa! Anda sudah menurunkan ${String.format("%.1f", abs(weightChange))} kg. Tetap semangat!"
                else -> "Perjalanan fitness adalah marathon, bukan sprint. Tetap konsisten!"
            }

            Text(
                text = motivationMessage,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun GoalTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "💡 Tips Mencapai Target",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            val tips = listOf(
                "Set target yang realistis dan dapat dicapai",
                "Catat progress secara rutin dan konsisten",
                "Fokus pada perubahan gaya hidup jangka panjang",
                "Rayakan setiap milestone kecil yang dicapai",
                "Jangan terlalu keras pada diri sendiri",
                "Konsultasi dengan ahli gizi jika diperlukan"
            )

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "• ",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = tip,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EditGoalDialog(
    currentGoal: Double,
    onDismiss: () -> Unit,
    onUpdateGoal: (Double) -> Unit
) {
    var newGoal by remember { mutableStateOf(currentGoal.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Target Berat") },
        text = {
            Column {
                Text("Target berat saat ini: $currentGoal kg")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newGoal,
                    onValueChange = { newGoal = it },
                    label = { Text("Target Berat Baru (kg)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    newGoal.toDoubleOrNull()?.let { goalValue ->
                        if (goalValue > 0) {
                            onUpdateGoal(goalValue)
                        }
                    }
                },
                enabled = newGoal.toDoubleOrNull() != null && newGoal.toDouble() > 0
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
private fun GoalMetric(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun MilestoneItem(milestone: Milestone) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.achieved) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (milestone.achieved) "✅" else "⏳",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = milestone.title,
                    fontSize = 14.sp,
                    fontWeight = if (milestone.achieved) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = milestone.target,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private data class Milestone(
    val title: String,
    val target: String,
    val achieved: Boolean
)

private fun generateMilestones(user: User, weightHistory: List<WeightEntry>): List<Milestone> {
    val currentWeight = weightHistory.lastOrNull()?.weight ?: user.currentWeight
    val startWeight = user.currentWeight
    val goalWeight = user.goalWeight
    val isGaining = goalWeight > startWeight
    
    return if (isGaining) {
        val quarter = startWeight + (goalWeight - startWeight) * 0.25
        val half = startWeight + (goalWeight - startWeight) * 0.5
        val threeQuarter = startWeight + (goalWeight - startWeight) * 0.75
        
        listOf(
            Milestone("Mulai Perjalanan", "${startWeight} kg", true),
            Milestone("25% Target", "${String.format("%.1f", quarter)} kg", currentWeight >= quarter),
            Milestone("50% Target", "${String.format("%.1f", half)} kg", currentWeight >= half),
            Milestone("75% Target", "${String.format("%.1f", threeQuarter)} kg", currentWeight >= threeQuarter),
            Milestone("Target Tercapai", "${goalWeight} kg", currentWeight >= goalWeight)
        )
    } else {
        val quarter = startWeight - (startWeight - goalWeight) * 0.25
        val half = startWeight - (startWeight - goalWeight) * 0.5
        val threeQuarter = startWeight - (startWeight - goalWeight) * 0.75
        
        listOf(
            Milestone("Mulai Perjalanan", "${startWeight} kg", true),
            Milestone("25% Target", "${String.format("%.1f", quarter)} kg", currentWeight <= quarter),
            Milestone("50% Target", "${String.format("%.1f", half)} kg", currentWeight <= half),
            Milestone("75% Target", "${String.format("%.1f", threeQuarter)} kg", currentWeight <= threeQuarter),
            Milestone("Target Tercapai", "${goalWeight} kg", currentWeight <= goalWeight)
        )
    }
}
