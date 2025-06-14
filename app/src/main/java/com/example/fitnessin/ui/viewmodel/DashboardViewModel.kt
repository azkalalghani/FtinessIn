package com.example.fitnessin.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitnessin.model.FoodRecommendation
import com.example.fitnessin.model.NutritionData
import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightEntry
import com.example.fitnessin.repository.FirebaseRepository
import com.example.fitnessin.repository.NutritionRepository
import com.example.fitnessin.util.NutritionCalculator
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel
@Inject
constructor(
        private val firebaseRepository: FirebaseRepository,
        private val nutritionRepository: NutritionRepository,
        private val nutritionCalculator: NutritionCalculator
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _nutritionData = MutableStateFlow<NutritionData?>(null)
    val nutritionData: StateFlow<NutritionData?> = _nutritionData.asStateFlow()

    private val _foodRecommendation = MutableStateFlow<FoodRecommendation?>(null)
    val foodRecommendation: StateFlow<FoodRecommendation?> = _foodRecommendation.asStateFlow()

    private val _weightHistory = MutableStateFlow<List<WeightEntry>>(emptyList())
    val weightHistory: StateFlow<List<WeightEntry>> = _weightHistory.asStateFlow()

    fun loadDashboardData(user: User) {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            try {
                // Simpan user data untuk reference
                _currentUser.value = user

                // Load weight history terlebih dahulu untuk mendapatkan weight terbaru
                loadWeightHistory(user.uid)

                // Setelah weight history loaded, update user dengan weight terbaru
                val latestUser = getUserWithLatestWeight(user)
                _currentUser.value = latestUser

                // Hitung data nutrisi berdasarkan data terbaru (termasuk weight terbaru)
                val nutrition = nutritionCalculator.calculateNutritionData(latestUser)
                _nutritionData.value = nutrition

                // Load food recommendation dengan data terbaru
                loadFoodRecommendation(nutrition)

                android.util.Log.d(
                        "DashboardViewModel",
                        "Dashboard loaded with latest weight: ${latestUser.currentWeight}kg"
                )

                _uiState.value = DashboardUiState.Success
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Error loading dashboard data", e)
                _uiState.value = DashboardUiState.Error(e.message ?: "Gagal memuat data dashboard")
            }
        }
    }

    private suspend fun loadWeightHistory(userId: String) {
        android.util.Log.d("DashboardViewModel", "Loading weight history for user: $userId")
        firebaseRepository
                .getWeightHistory(userId)
                .onSuccess { history ->
                    android.util.Log.d(
                            "DashboardViewModel",
                            "Weight history loaded: ${history.size} entries"
                    )
                    _weightHistory.value = history

                    // Update user dengan weight terbaru jika ada
                    if (history.isNotEmpty()) {
                        val latestWeight = history.first().weight
                        _currentUser.value?.let { user ->
                            if (user.currentWeight != latestWeight) {
                                val updatedUser = user.copy(currentWeight = latestWeight)
                                _currentUser.value = updatedUser
                                android.util.Log.d(
                                        "DashboardViewModel",
                                        "User weight updated to latest: ${latestWeight}kg"
                                )
                            }
                        }
                    }
                }
                .onFailure { error ->
                    // Log error but don't fail the whole dashboard
                    android.util.Log.e(
                            "DashboardViewModel",
                            "Error loading weight history: ${error.message}"
                    )
                }
    }

    private suspend fun loadFoodRecommendation(nutritionData: NutritionData) {
        val user = _currentUser.value
        if (user != null) {
            nutritionRepository
                    .getFoodRecommendation(nutritionData, user)
                    .onSuccess { recommendation -> _foodRecommendation.value = recommendation }
                    .onFailure { error ->
                        // Log error but don't fail the whole dashboard
                        println("Error loading food recommendation: ${error.message}")
                    }
        } else {
            // Fallback ke versi tanpa user data
            nutritionRepository
                    .getFoodRecommendation(nutritionData)
                    .onSuccess { recommendation -> _foodRecommendation.value = recommendation }
                    .onFailure { error ->
                        println("Error loading food recommendation: ${error.message}")
                    }
        }
    }

    fun addWeightEntry(userId: String, weight: Double) {
        viewModelScope.launch {
            try {
                val weightEntry =
                        WeightEntry(userId = userId, weight = weight, timestamp = Timestamp.now())

                android.util.Log.d(
                        "DashboardViewModel",
                        "Adding weight entry: ${weight}kg for user: $userId"
                )

                firebaseRepository
                        .saveWeightEntry(weightEntry)
                        .onSuccess {
                            android.util.Log.d(
                                    "DashboardViewModel",
                                    "Weight entry saved successfully"
                            )

                            // LANGSUNG update current user dengan weight terbaru
                            _currentUser.value?.let { user ->
                                val updatedUser = user.copy(currentWeight = weight)
                                _currentUser.value = updatedUser

                                // Recalculate nutrition dengan weight terbaru
                                val updatedNutrition =
                                        nutritionCalculator.calculateNutritionData(updatedUser)
                                _nutritionData.value = updatedNutrition

                                // Update food recommendation dengan data terbaru
                                loadFoodRecommendation(updatedNutrition)

                                android.util.Log.d(
                                        "DashboardViewModel",
                                        "User profile updated with new weight: ${weight}kg"
                                )
                            }

                            // Update di database dan reload weight history
                            firebaseRepository.updateCurrentWeight(userId, weight)
                            loadWeightHistory(userId)
                        }
                        .onFailure { error ->
                            android.util.Log.e(
                                    "DashboardViewModel",
                                    "Failed to save weight entry",
                                    error
                            )
                            _uiState.value =
                                    DashboardUiState.Error(
                                            error.message ?: "Gagal menyimpan berat badan"
                                    )
                        }
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Error in addWeightEntry", e)
                _uiState.value =
                        DashboardUiState.Error(
                                e.message ?: "Terjadi kesalahan saat menambah berat badan"
                        )
            }
        }
    }

    fun refreshFoodRecommendation() {
        viewModelScope.launch {
            _currentUser.value?.let { user ->
                // Refresh berdasarkan data user terbaru
                refreshAllData(user.uid)
            }
        }
    }

    /** Refresh semua data berdasarkan user data terbaru */
    fun refreshAllData(userId: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("DashboardViewModel", "Refreshing all data for user: $userId")

                // Reload weight history untuk mendapatkan weight terbaru
                loadWeightHistory(userId)

                // Update user dengan weight terbaru dari history
                _currentUser.value?.let { user ->
                    val latestUser = getUserWithLatestWeight(user)
                    _currentUser.value = latestUser

                    // Recalculate nutrition dengan data terbaru
                    val updatedNutrition = nutritionCalculator.calculateNutritionData(latestUser)
                    _nutritionData.value = updatedNutrition

                    // Refresh food recommendation dengan nutrition data baru
                    loadFoodRecommendation(updatedNutrition)

                    android.util.Log.d(
                            "DashboardViewModel",
                            "All data refreshed with latest weight: ${latestUser.currentWeight}kg"
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("DashboardViewModel", "Error refreshing data", e)
            }
        }
    }

    /** Mendapatkan user dengan weight terbaru dari weight history */
    private fun getUserWithLatestWeight(user: User): User {
        val weightHistory = _weightHistory.value
        return if (weightHistory.isNotEmpty()) {
            val latestWeight = weightHistory.first().weight
            if (latestWeight != user.currentWeight) {
                android.util.Log.d(
                        "DashboardViewModel",
                        "Updating user weight from ${user.currentWeight}kg to ${latestWeight}kg"
                )
                user.copy(currentWeight = latestWeight)
            } else {
                user
            }
        } else {
            user
        }
    }

    /** Ambil data user terbaru dari repository dengan weight terbaru */
    private suspend fun getLatestUserData(userId: String): User? {
        return try {
            firebaseRepository.getUserProfile(userId).getOrNull()?.let { user ->
                // Update dengan weight terbaru dari weight history jika ada
                val latestWeight =
                        firebaseRepository.getLatestWeightEntry(userId).getOrNull()?.weight
                if (latestWeight != null && latestWeight != user.currentWeight) {
                    android.util.Log.d(
                            "DashboardViewModel",
                            "Found newer weight in database: ${latestWeight}kg vs profile: ${user.currentWeight}kg"
                    )
                    user.copy(currentWeight = latestWeight)
                } else {
                    user
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("DashboardViewModel", "Error getting latest user data", e)
            null
        }
    }
}

sealed class DashboardUiState {
    object Loading : DashboardUiState()
    object Success : DashboardUiState()
    data class Error(val message: String) : DashboardUiState()
}
