package com.example.fitnessin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitnessin.model.User
import com.example.fitnessin.navigation.Screen

@Composable
fun NavigationDrawer(
    user: User,
    currentScreen: Screen,
    onScreenSelected: (Screen) -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        // User Profile Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.take(2).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Navigation Menu Items
        Screen.values().forEach { screen ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentScreen == screen,
                onClick = { onScreenSelected(screen) },
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Logout Button
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            label = { 
                Text(
                    "Keluar",
                    color = MaterialTheme.colorScheme.error
                ) 
            },
            selected = false,
            onClick = onLogoutClick,
            modifier = Modifier.padding(vertical = 2.dp),
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = Color.Transparent,
                selectedContainerColor = MaterialTheme.colorScheme.errorContainer
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // App Version Info
        Text(
            text = "Aplikasi Kebugaran Inti\nv1.0.0",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
