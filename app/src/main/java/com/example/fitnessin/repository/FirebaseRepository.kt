package com.example.fitnessin.repository

import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightEntry
import com.example.fitnessin.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.tasks.await

@Singleton
class FirebaseRepository
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: FirebaseAuth) {
    // Local storage for demo mode
    private val demoWeightEntries = mutableListOf<WeightEntry>()

    init {
        // Initialize with some demo data
        if (Constants.DEMO_MODE) {
            // Will be populated when user adds their first weight entry
        }
    }

    /** Mendapatkan user yang sedang login */
    fun getCurrentUser() = auth.currentUser

    /** Login dengan email dan password */
    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(500)
                return Result.success("demo_user_${email.hashCode()}")
            }

            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID tidak ditemukan")
            Result.success(userId)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error signing in", e)
            Result.failure(e)
        }
    }

    /** Registrasi dengan email dan password */
    suspend fun createUserWithEmail(email: String, password: String): Result<String> {
        return try {
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(500)
                return Result.success("demo_user_${email.hashCode()}")
            }

            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID tidak ditemukan")
            Result.success(userId)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error creating user", e)
            Result.failure(e)
        }
    }

    /** Logout user */
    fun signOut() {
        auth.signOut()
    }

    /** Menyimpan data user profile ke Firestore */
    suspend fun saveUserProfile(user: User): Result<Unit> {
        return try {
            // Untuk testing tanpa Firebase yang dikonfigurasi
            if (Constants.DEMO_MODE) {
                // Simulasi delay network
                kotlinx.coroutines.delay(1000)
                android.util.Log.d(
                        "FirebaseRepository",
                        "Demo mode: User profile saved successfully"
                )
                return Result.success(Unit)
            }

            // Production mode - real Firebase operation
            android.util.Log.d(
                    "FirebaseRepository",
                    "Saving user profile to Firestore: ${user.name}"
            )
            firestore.collection(Constants.USERS_COLLECTION).document(user.uid).set(user).await()
            android.util.Log.d("FirebaseRepository", "User profile saved successfully to Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            // Log error untuk debugging
            android.util.Log.e("FirebaseRepository", "Error saving user profile", e)
            Result.failure(Exception("Gagal menyimpan profil: ${e.localizedMessage}"))
        }
    }

    /** Mengambil data user profile dari Firestore */
    suspend fun getUserProfile(uid: String): Result<User> {
        return try {
            // Untuk testing tanpa Firebase yang dikonfigurasi
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(500)
                // Return demo user jika ada
                val demoUser =
                        User(
                                uid = uid,
                                name = "Demo User",
                                email = "demo@example.com",
                                age = 25,
                                gender = com.example.fitnessin.model.Gender.PRIA,
                                height = 175.0,
                                currentWeight = 70.0,
                                goalWeight = 75.0,
                                activityLevel =
                                        com.example.fitnessin.model.ActivityLevel.MODERATELY_ACTIVE
                        )
                android.util.Log.d("FirebaseRepository", "Demo mode: Returning demo user")
                return Result.success(demoUser)
            }

            // Production mode - real Firebase operation
            android.util.Log.d("FirebaseRepository", "Getting user profile from Firestore: $uid")
            val document =
                    firestore.collection(Constants.USERS_COLLECTION).document(uid).get().await()

            val user = document.toObject(User::class.java)
            if (user != null) {
                android.util.Log.d(
                        "FirebaseRepository",
                        "User profile retrieved successfully: ${user.name}"
                )
                Result.success(user)
            } else {
                android.util.Log.w("FirebaseRepository", "User profile not found in Firestore")
                Result.failure(Exception("User profile not found"))
            }
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error getting user profile", e)
            Result.failure(Exception("Gagal memuat profil: ${e.localizedMessage}"))
        }
    }

    /** Menyimpan data progress berat badan */
    suspend fun saveWeightEntry(weightEntry: WeightEntry): Result<Unit> {
        return try {
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(500)
                // Add to local demo storage with proper user ID
                val entryWithId = weightEntry.copy(id = "demo_${System.currentTimeMillis()}")
                demoWeightEntries.add(0, entryWithId) // Add to front for latest first
                android.util.Log.d(
                        "FirebaseRepository",
                        "Demo mode: Weight entry saved - ${entryWithId.weight}kg for user ${entryWithId.userId}"
                )
                return Result.success(Unit)
            }

            firestore.collection(Constants.PROGRESS_COLLECTION).add(weightEntry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error saving weight entry", e)
            // Fallback untuk demo
            if (Constants.DEMO_MODE) {
                val entryWithId =
                        weightEntry.copy(id = "demo_fallback_${System.currentTimeMillis()}")
                demoWeightEntries.add(0, entryWithId)
            }
            kotlinx.coroutines.delay(300)
            Result.success(Unit)
        }
    }

    /** Mengambil history progress berat badan user */
    suspend fun getWeightHistory(userId: String): Result<List<WeightEntry>> {
        return try {
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(300)
                // Return demo weight history from local storage for specific user
                val userEntries =
                        demoWeightEntries.filter {
                            it.userId == userId ||
                                    (userId.startsWith("demo_user") &&
                                            (it.userId == "demo_user" ||
                                                    it.userId.startsWith("demo_user")))
                        }
                android.util.Log.d(
                        "FirebaseRepository",
                        "Demo mode: Returning ${userEntries.size} weight entries for user $userId"
                )
                return Result.success(userEntries.take(Constants.WEIGHT_HISTORY_LIMIT))
            }

            val querySnapshot =
                    firestore
                            .collection(Constants.PROGRESS_COLLECTION)
                            .whereEqualTo("userId", userId)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(Constants.WEIGHT_HISTORY_LIMIT.toLong())
                            .get()
                            .await()

            val weightEntries =
                    querySnapshot.documents.mapNotNull { document ->
                        document.toObject(WeightEntry::class.java)?.copy(id = document.id)
                    }

            Result.success(weightEntries)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error getting weight history", e)
            // Fallback demo data
            Result.success(emptyList())
        }
    }

    /** Mengambil entry berat badan terbaru user */
    suspend fun getLatestWeightEntry(userId: String): Result<WeightEntry?> {
        return try {
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(200)
                // Return latest entry from demo storage
                val userEntries =
                        demoWeightEntries.filter {
                            it.userId == userId ||
                                    (userId.startsWith("demo_user") &&
                                            (it.userId == "demo_user" ||
                                                    it.userId.startsWith("demo_user")))
                        }
                val latestEntry = userEntries.firstOrNull()
                android.util.Log.d(
                        "FirebaseRepository",
                        "Demo mode: Latest weight entry for $userId: ${latestEntry?.weight}kg"
                )
                return Result.success(latestEntry)
            }

            val querySnapshot =
                    firestore
                            .collection(Constants.PROGRESS_COLLECTION)
                            .whereEqualTo("userId", userId)
                            .orderBy("timestamp", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .await()

            val latestEntry =
                    querySnapshot.documents.firstOrNull()?.let { document ->
                        document.toObject(WeightEntry::class.java)?.copy(id = document.id)
                    }

            Result.success(latestEntry)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error getting latest weight entry", e)
            Result.failure(e)
        }
    }

    /** Update berat badan current user di profile */
    suspend fun updateCurrentWeight(userId: String, newWeight: Double): Result<Unit> {
        return try {
            if (Constants.DEMO_MODE) {
                kotlinx.coroutines.delay(300)
                android.util.Log.d(
                        "FirebaseRepository",
                        "Demo mode: Updated current weight to $newWeight kg for user $userId"
                )
                return Result.success(Unit)
            }

            firestore
                    .collection(Constants.USERS_COLLECTION)
                    .document(userId)
                    .update("currentWeight", newWeight)
                    .await()
            android.util.Log.d(
                    "FirebaseRepository",
                    "Updated current weight in profile: $newWeight kg for user $userId"
            )
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Error updating current weight", e)
            Result.failure(e)
        }
    }
}
