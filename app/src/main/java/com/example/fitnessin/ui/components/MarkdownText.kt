package com.example.fitnessin.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MarkdownText(text: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        val lines = text.split("\n")
        var i = 0

        while (i < lines.size) {
            val line = lines[i].trim()

            when {
                // Header level 1 (###)
                line.startsWith("###") -> {
                    Text(
                            text = line.removePrefix("###").trim(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Header level 2 (**)
                line.startsWith("**") && line.endsWith("**") -> {
                    Text(
                            text = line.removePrefix("**").removeSuffix("**").trim(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                // Bold text with **text**
                line.contains("**") -> {
                    FormattedText(line)
                }

                // List items starting with - or •
                line.startsWith("-") || line.startsWith("•") -> {
                    Row(modifier = Modifier.padding(start = 8.dp)) {
                        Text(
                                text = "•",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                                text = line.removePrefix("-").removePrefix("•").trim(),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Menu sections (SARAPAN, MAKAN SIANG, etc.)
                line.contains("SARAPAN") ||
                        line.contains("MAKAN SIANG") ||
                        line.contains("MAKAN MALAM") ||
                        line.contains("SNACK") -> {
                    Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.primaryContainer
                                    ),
                            shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                                text = line,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Tips section
                line.contains("Tips:") || line.contains("💡") -> {
                    Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    MaterialTheme.colorScheme.secondaryContainer
                                    ),
                            shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                                text = line,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Regular text (not empty)
                line.isNotEmpty() -> {
                    Text(
                            text = line,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 20.sp
                    )
                }
            }
            i++
        }
    }
}

@Composable
private fun FormattedText(text: String) {
    val parts = mutableListOf<TextPart>()
    var currentText = text

    // Process bold text
    while (currentText.contains("**")) {
        val startIndex = currentText.indexOf("**")
        val endIndex = currentText.indexOf("**", startIndex + 2)

        if (endIndex != -1) {
            // Add text before bold
            if (startIndex > 0) {
                parts.add(TextPart(currentText.substring(0, startIndex), false))
            }

            // Add bold text
            val boldText = currentText.substring(startIndex + 2, endIndex)
            parts.add(TextPart(boldText, true))

            // Continue with remaining text
            currentText = currentText.substring(endIndex + 2)
        } else {
            // No closing **, treat as regular text
            parts.add(TextPart(currentText, false))
            break
        }
    }

    // Add remaining text
    if (currentText.isNotEmpty()) {
        parts.add(TextPart(currentText, false))
    }

    // Display formatted text
    Row(modifier = Modifier.fillMaxWidth()) {
        parts.forEach { part ->
            Text(
                    text = part.text,
                    fontSize = 14.sp,
                    fontWeight = if (part.isBold) FontWeight.Bold else FontWeight.Normal,
                    color =
                            if (part.isBold) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface,
                    lineHeight = 20.sp
            )
        }
    }
}

private data class TextPart(val text: String, val isBold: Boolean)

@Composable
fun MarkdownCard(title: String, content: String, modifier: Modifier = Modifier) {
    Card(
            modifier = modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
            )

            MarkdownText(text = content, modifier = Modifier.fillMaxWidth())
        }
    }
}
