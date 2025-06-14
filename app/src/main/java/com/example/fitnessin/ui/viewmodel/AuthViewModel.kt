package com.example.fitnessin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessin.model.User
import com.example.fitnessin.repository.FirebaseRepository
import com.example.fitnessin.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val firebaseRepository: FirebaseRepository) :
        ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        checkAuthState()
    }

    fun checkAuthState() {
        val firebaseUser = firebaseRepository.getCurrentUser()
        if (firebaseUser != null) {
            loadUserProfile(firebaseUser.uid)
        } else {
            _uiState.value = AuthUiState.NotAuthenticated
        }
    }

    fun loginWithEmail(email: String) {
        viewModelScope.launch {
            android.util.Log.d("AuthViewModel", "Login attempt for email: $email")
            _uiState.value = AuthUiState.Loading

            // Untuk demo mode, langsung berhasil
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(1000) // Simulasi network delay
                val demoUserId = "demo_user_${email.hashCode()}"

                // Cek apakah user sudah ada
                firebaseRepository
                        .getUserProfile(demoUserId)
                        .onSuccess { user ->
                            android.util.Log.d("AuthViewModel", "Demo user found: ${user.name}")
                            _currentUser.value = user
                            _uiState.value = AuthUiState.Authenticated(user)
                        }
                        .onFailure {
                            android.util.Log.d(
                                    "AuthViewModel",
                                    "Demo user not found, redirect to profile setup"
                            )
                            _uiState.value = AuthUiState.RequiresProfileSetup(demoUserId, email)
                        }
                return@launch
            }

            // Firebase authentication logic akan ditambahkan di sini
            firebaseRepository
                    .signInWithEmail(email, "demopassword")
                    .onSuccess { userId -> loadUserProfile(userId) }
                    .onFailure { error ->
                        _uiState.value = AuthUiState.Error(error.message ?: "Login gagal")
                    }
        }
    }

    fun registerWithEmail(email: String) {
        viewModelScope.launch {
            android.util.Log.d("AuthViewModel", "Register attempt for email: $email")
            _uiState.value = AuthUiState.Loading

            // Untuk demo mode
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(1000)
                val demoUserId = "demo_user_${email.hashCode()}"
                android.util.Log.d("AuthViewModel", "Demo registration successful for: $email")
                _uiState.value = AuthUiState.RequiresProfileSetup(demoUserId, email)
                return@launch
            }

            // Firebase registration logic akan ditambahkan di sini
            firebaseRepository
                    .createUserWithEmail(email, "demopassword")
                    .onSuccess { userId ->
                        _uiState.value = AuthUiState.RequiresProfileSetup(userId, email)
                    }
                    .onFailure { error ->
                        _uiState.value = AuthUiState.Error(error.message ?: "Registrasi gagal")
                    }
        }
    }

    fun loadUserProfile(uid: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading

            firebaseRepository
                    .getUserProfile(uid)
                    .onSuccess { user ->
                        // Load user dengan weight terbaru dari weight history
                        val userWithLatestWeight = getUserWithLatestWeight(user, uid)
                        _currentUser.value = userWithLatestWeight
                        _uiState.value = AuthUiState.Authenticated(userWithLatestWeight)
                        android.util.Log.d(
                                "AuthViewModel",
                                "User loaded with latest weight: ${userWithLatestWeight.currentWeight}kg"
                        )
                    }
                    .onFailure { error ->
                        _uiState.value =
                                AuthUiState.Error(error.message ?: "Gagal memuat profil user")
                    }
        }
    }

    /** Mendapatkan user dengan weight terbaru dari weight history */
    private suspend fun getUserWithLatestWeight(user: User, userId: String): User {
        return try {
            val latestWeight = firebaseRepository.getLatestWeightEntry(userId).getOrNull()?.weight
            if (latestWeight != null && latestWeight != user.currentWeight) {
                android.util.Log.d(
                        "AuthViewModel",
                        "Updating user weight from ${user.currentWeight}kg to ${latestWeight}kg"
                )
                user.copy(currentWeight = latestWeight)
            } else {
                user
            }
        } catch (e: Exception) {
            android.util.Log.e(
                    "AuthViewModel",
                    "Error getting latest weight, using profile weight",
                    e
            )
            user
        }
    }

    fun saveUserProfile(user: User) {
        viewModelScope.launch {
            android.util.Log.d("AuthViewModel", "Starting saveUserProfile for user: ${user.name}")
            _uiState.value = AuthUiState.Loading

            firebaseRepository
                    .saveUserProfile(user)
                    .onSuccess {
                        android.util.Log.d("AuthViewModel", "User profile saved successfully")
                        _currentUser.value = user
                        _uiState.value = AuthUiState.Authenticated(user)
                    }
                    .onFailure { error ->
                        android.util.Log.e("AuthViewModel", "Failed to save user profile", error)
                        _uiState.value =
                                AuthUiState.Error(error.message ?: "Gagal menyimpan profil user")
                    }
        }
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = AuthUiState.NotAuthenticated
    }
}

sealed class AuthUiState {
    object Initial : AuthUiState()
    object Loading : AuthUiState()
    object NotAuthenticated : AuthUiState()
    data class RequiresProfileSetup(val userId: String, val email: String) : AuthUiState()
    data class Authenticated(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
