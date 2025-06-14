package com.example.fitnessin.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    onLoginSuccess: (String) -> Unit,
    onRegisterSuccess: (String) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }

    // Validation functions
    fun validateEmail(email: String): String {
        return when {
            email.isBlank() -> "Email tidak boleh kosong"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Format email tidak valid"
            else -> ""
        }
    }

    fun validatePassword(password: String): String {
        return when {
            password.isBlank() -> "Password tidak boleh kosong"
            password.length < 6 -> "Password minimal 6 karakter"
            else -> ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        Text(
            text = "Aplikasi Kebugaran Inti",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Raih target berat badan ideal Anda",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        // Tab Selection
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { isLoginMode = true },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLoginMode) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Masuk",
                    color = if (isLoginMode) MaterialTheme.colorScheme.onPrimary 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Button(
                onClick = { isLoginMode = false },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!isLoginMode) MaterialTheme.colorScheme.primary 
                                   else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = "Daftar",
                    color = if (!isLoginMode) MaterialTheme.colorScheme.onPrimary 
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Form Fields
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { 
                        email = it 
                        emailError = validateEmail(it)
                    },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = emailError.isNotEmpty(),
                    supportingText = if (emailError.isNotEmpty()) {
                        { Text(emailError, color = MaterialTheme.colorScheme.error) }
                    } else null
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it 
                        passwordError = validatePassword(it)
                    },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = passwordError.isNotEmpty(),
                    supportingText = if (passwordError.isNotEmpty()) {
                        { Text(passwordError, color = MaterialTheme.colorScheme.error) }
                    } else null
                )

                // Confirm Password field (only for register)
                if (!isLoginMode) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Konfirmasi Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = confirmPassword.isNotEmpty() && confirmPassword != password,
                        supportingText = if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                            { Text("Password tidak sama", color = MaterialTheme.colorScheme.error) }
                        } else null
                    )
                }

                // Error message
                errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }

                // Action Button
                Button(
                    onClick = {
                        // Clear previous errors
                        emailError = validateEmail(email)
                        passwordError = validatePassword(password)
                        
                        when {
                            isLoginMode -> {
                                if (emailError.isEmpty() && passwordError.isEmpty()) {
                                    onLoginSuccess(email)
                                }
                            }
                            else -> {
                                val passwordsMatch = password == confirmPassword
                                
                                if (emailError.isEmpty() && passwordError.isEmpty() && passwordsMatch) {
                                    onRegisterSuccess(email)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !isLoading && when {
                        isLoginMode -> email.isNotBlank() && password.isNotBlank()
                        else -> email.isNotBlank() && password.isNotBlank() && password == confirmPassword
                    }
                ) {
                    if (isLoading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Text("Memproses...")
                        }
                    } else {
                        Text(
                            text = if (isLoginMode) "Masuk" else "Daftar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mode Info
        if (com.example.fitnessin.util.Constants.DEMO_MODE) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Mode Demo Aktif",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Anda dapat login dengan email apapun. Data akan disimulasi tanpa Firebase.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Contoh: demo@example.com",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Quick Demo Button
            OutlinedButton(
                onClick = {
                    onLoginSuccess("demo@example.com")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Masuk Mode Demo")
            }
        } else {
            // Production Mode Info
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Mode Produksi",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Terhubung dengan Firebase Authentication dan Database real. Gunakan email dan password yang valid.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Additional info
        Text(
            text = if (isLoginMode) {
                "Belum punya akun? Klik tombol Daftar di atas"
            } else {
                "Sudah punya akun? Klik tombol Masuk di atas"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
