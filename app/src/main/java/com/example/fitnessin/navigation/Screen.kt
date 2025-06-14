package com.example.fitnessin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Default.Home),
    PROFILE("profile", "Profil", Icons.Default.Person),
    NUTRITION("nutrition", "Nutrisi", Icons.Default.Favorite),
    PROGRESS("progress", "Progress", Icons.Default.KeyboardArrowUp),
    GOALS("goals", "Target", Icons.Default.Star),
    SETTINGS("settings", "Pengaturan", Icons.Default.Settings)
}
