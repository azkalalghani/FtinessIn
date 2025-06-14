package com.example.fitnessin.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightEntry
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@Composable
fun ProgressScreen(
    user: User,
    weightHistory: List<WeightEntry>,
    onAddWeight: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddWeightDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Add Weight button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Progress Saya",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            FloatingActionButton(
                onClick = { showAddWeightDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Weight"
                )
            }
        }

        // Progress Summary
        ProgressSummaryCard(user, weightHistory)

        // Weight Chart
        if (weightHistory.isNotEmpty()) {
            WeightChartCard(weightHistory, user.goalWeight)
        }

        // Recent Entries
        RecentEntriesCard(weightHistory)

        // Goals Progress
        GoalsProgressCard(user, weightHistory)
    }

    // Add Weight Dialog
    if (showAddWeightDialog) {
        AddWeightDialog(
            onDismiss = { showAddWeightDialog = false },
            onAddWeight = { weight ->
                onAddWeight(weight)
                showAddWeightDialog = false
            }
        )
    }
}

@Composable
private fun ProgressSummaryCard(user: User, weightHistory: List<WeightEntry>) {
    val currentWeight = weightHistory.lastOrNull()?.weight ?: user.currentWeight
    val startWeight = weightHistory.firstOrNull()?.weight ?: user.currentWeight
    val weightChange = currentWeight - startWeight
    val goalProgress = if (user.goalWeight > user.currentWeight) {
        // Weight gain goal
        ((currentWeight - user.currentWeight) / (user.goalWeight - user.currentWeight)) * 100
    } else {
        // Weight loss goal
        ((user.currentWeight - currentWeight) / (user.currentWeight - user.goalWeight)) * 100
    }.coerceIn(0.0, 100.0)

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
                text = "Ringkasan Progress",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ProgressMetric(
                    "Berat Saat Ini",
                    "${String.format("%.1f", currentWeight)} kg",
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                ProgressMetric(
                    "Target Berat",
                    "${user.goalWeight} kg",
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
                ProgressMetric(
                    "Perubahan",
                    "${if (weightChange >= 0) "+" else ""}${String.format("%.1f", weightChange)} kg",
                    MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f))

            // Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Progress ke Target",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${String.format("%.1f", goalProgress)}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { (goalProgress / 100).toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun WeightChartCard(weightHistory: List<WeightEntry>, goalWeight: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Grafik Berat Badan",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            // Simple line chart
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                if (weightHistory.isEmpty()) return@Canvas

                val weights = weightHistory.map { it.weight.toFloat() }
                val minWeight = (weights.minOrNull() ?: 0f) - 2f
                val maxWeight = (weights.maxOrNull() ?: 100f) + 2f
                val weightRange = maxWeight - minWeight

                val width = size.width
                val height = size.height
                val stepX = width / (weights.size - 1).coerceAtLeast(1)

                // Draw goal line
                val goalY = height - ((goalWeight.toFloat() - minWeight) / weightRange) * height
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, goalY),
                    end = Offset(width, goalY),
                    strokeWidth = 3.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f)
                    )
                )

                // Draw weight line
                val path = Path()
                weights.forEachIndexed { index, weight ->
                    val x = index * stepX
                    val y = height - ((weight - minWeight) / weightRange) * height
                    
                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    
                    // Draw points
                    drawCircle(
                        color = Color.Blue,
                        radius = 6.dp.toPx(),
                        center = Offset(x, y)
                    )
                }

                drawPath(
                    path = path,
                    color = Color.Blue,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                LegendItem("Berat Badan", Color.Blue)
                LegendItem("Target Berat", Color.Red)
            }
        }
    }
}

@Composable
private fun RecentEntriesCard(weightHistory: List<WeightEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Entri Terbaru",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            if (weightHistory.isEmpty()) {
                Text(
                    text = "Belum ada data berat badan. Tambahkan entri pertama Anda!",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(weightHistory.takeLast(5).reversed()) { entry ->
                        WeightEntryItem(entry)
                    }
                }
            }
        }
    }
}

@Composable
private fun GoalsProgressCard(user: User, weightHistory: List<WeightEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Target & Pencapaian",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            val currentWeight = weightHistory.lastOrNull()?.weight ?: user.currentWeight
            val remainingWeight = abs(user.goalWeight - currentWeight)
            val isGaining = user.goalWeight > user.currentWeight

            GoalItem(
                "🎯 Target Berat",
                "${user.goalWeight} kg",
                if (currentWeight == user.goalWeight) "Tercapai!" else "Target utama"
            )

            GoalItem(
                "📈 Sisa Target",
                "${String.format("%.1f", remainingWeight)} kg",
                if (isGaining) "Perlu ditambah" else "Perlu dikurangi"
            )

            GoalItem(
                "📅 Total Entri",
                "${weightHistory.size} kali",
                "Konsistensi pencatatan"
            )

            if (weightHistory.size >= 2) {
                val daysBetween = weightHistory.size // Simplified calculation
                val avgWeightChange = (currentWeight - user.currentWeight) / daysBetween
                GoalItem(
                    "📊 Rata-rata Perubahan",
                    "${String.format("%.2f", avgWeightChange)} kg/hari",
                    "Trend perubahan berat"
                )
            }
        }
    }
}

@Composable
private fun AddWeightDialog(
    onDismiss: () -> Unit,
    onAddWeight: (Double) -> Unit
) {
    var weight by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Berat Badan") },
        text = {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Berat Badan (kg)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    weight.toDoubleOrNull()?.let { weightValue ->
                        if (weightValue > 0) {
                            onAddWeight(weightValue)
                        }
                    }
                },
                enabled = weight.toDoubleOrNull() != null && weight.toDouble() > 0
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
private fun ProgressMetric(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color.copy(alpha = 0.8f)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(color = color)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun WeightEntryItem(entry: WeightEntry) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${entry.weight} kg",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Entri berat badan",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(
                    entry.timestamp.toDate()
                ),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun GoalItem(title: String, value: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
