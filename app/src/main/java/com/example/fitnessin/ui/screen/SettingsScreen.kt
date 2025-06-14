package com.example.fitnessin.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.User

@Composable
fun SettingsScreen(
    user: User,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Pengaturan",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        // Profile Section
        ProfileSettingsCard(user, onEditProfile)

        // App Settings
        AppSettingsCard()

        // Data & Privacy
        DataPrivacyCard()

        // Support & About
        SupportAboutCard()

        // Logout Section
        LogoutCard(onLogout)
    }
}

@Composable
private fun ProfileSettingsCard(user: User, onEditProfile: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Profil",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingsItem(
                icon = Icons.Default.Person,
                title = "Edit Profil",
                subtitle = "Ubah data pribadi dan target",
                onClick = onEditProfile
            )

            SettingsItem(
                icon = Icons.Default.AccountCircle,
                title = "Foto Profil",
                subtitle = "Tambah atau ubah foto profil",
                onClick = { /* TODO: Implement photo upload */ }
            )
        }
    }
}

@Composable
private fun AppSettingsCard() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Aplikasi",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Notifikasi",
                subtitle = "Pengingat untuk input berat dan makan",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )

            SettingsToggleItem(
                icon = Icons.Default.Settings,
                title = "Mode Gelap",
                subtitle = "Ubah tema aplikasi",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )

            SettingsItem(
                icon = Icons.Default.Settings,
                title = "Bahasa",
                subtitle = "Bahasa Indonesia",
                onClick = { /* TODO: Implement language selection */ }
            )

            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Pengingat",
                subtitle = "Atur waktu pengingat harian",
                onClick = { /* TODO: Implement reminder settings */ }
            )
        }
    }
}

@Composable
private fun DataPrivacyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Data & Privasi",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingsItem(
                icon = Icons.Default.Share,
                title = "Ekspor Data",
                subtitle = "Download data pribadi Anda",
                onClick = { /* TODO: Implement data export */ }
            )

            SettingsItem(
                icon = Icons.Default.Refresh,
                title = "Sinkronisasi",
                subtitle = "Kelola data cloud dan backup",
                onClick = { /* TODO: Implement sync settings */ }
            )

            SettingsItem(
                icon = Icons.Default.Lock,
                title = "Privasi",
                subtitle = "Pengaturan keamanan dan privasi",
                onClick = { /* TODO: Implement privacy settings */ }
            )

            SettingsItem(
                icon = Icons.Default.Delete,
                title = "Hapus Semua Data",
                subtitle = "Hapus permanen semua data",
                onClick = { /* TODO: Implement data deletion */ },
                isDestructive = true
            )
        }
    }
}

@Composable
private fun SupportAboutCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Bantuan & Tentang",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Bantuan",
                subtitle = "FAQ dan panduan penggunaan",
                onClick = { /* TODO: Implement help screen */ }
            )

            SettingsItem(
                icon = Icons.Default.Send,
                title = "Kirim Masukan",
                subtitle = "Beri saran untuk perbaikan aplikasi",
                onClick = { /* TODO: Implement feedback */ }
            )

            SettingsItem(
                icon = Icons.Default.Star,
                title = "Beri Rating",
                subtitle = "Rating aplikasi di Play Store",
                onClick = { /* TODO: Implement rating */ }
            )

            SettingsItem(
                icon = Icons.Default.Info,
                title = "Tentang Aplikasi",
                subtitle = "Versi 1.0.0 - Aplikasi Kebugaran Inti",
                onClick = { /* TODO: Implement about dialog */ }
            )

            SettingsItem(
                icon = Icons.Default.List,
                title = "Syarat & Ketentuan",
                subtitle = "Kebijakan privasi dan ketentuan",
                onClick = { /* TODO: Implement terms */ }
            )
        }
    }
}

@Composable
private fun LogoutCard(onLogout: () -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SettingsItem(
                icon = Icons.Default.ExitToApp,
                title = "Keluar",
                subtitle = "Logout dari akun Anda",
                onClick = { showLogoutDialog = true },
                isDestructive = true
            )
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Konfirmasi Logout") },
            text = { Text("Apakah Anda yakin ingin keluar dari aplikasi?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Ya, Keluar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive) 
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isDestructive) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.error 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isDestructive) 
                        MaterialTheme.colorScheme.error.copy(alpha = 0.7f) 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
