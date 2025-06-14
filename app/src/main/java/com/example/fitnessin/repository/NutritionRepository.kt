package com.example.fitnessin.repository

import com.example.fitnessin.model.FoodRecommendation
import com.example.fitnessin.model.NutritionData
import com.example.fitnessin.model.User
import com.example.fitnessin.model.WeightGoal
import com.example.fitnessin.network.GeminiApiService
import com.example.fitnessin.util.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutritionRepository @Inject constructor(private val geminiApiService: GeminiApiService) {

    /** Mendapatkan rekomendasi makanan dari Gemini AI berdasarkan data user */
    suspend fun getFoodRecommendation(
            nutritionData: NutritionData,
            user: User
    ): Result<FoodRecommendation> {
        return try {
            if (Constants.DEMO_MODE || Constants.GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE") {
                // Demo recommendation jika API key belum dikonfigurasi
                kotlinx.coroutines.delay(1000)
                val demoRecommendation =
                        FoodRecommendation(
                                title =
                                        "Rekomendasi Personal untuk ${user.name} (${nutritionData.targetCalories.toInt()} kkal)",
                                content = getPersonalizedDemoRecommendation(nutritionData, user),
                                targetCalories = nutritionData.targetCalories,
                                goal = nutritionData.goal
                        )
                return Result.success(demoRecommendation)
            }

            val prompt = createPersonalizedPrompt(nutritionData, user)
            val response = geminiApiService.generateContent(prompt)

            val recommendation =
                    FoodRecommendation(
                            title =
                                    "Rekomendasi Personal untuk ${user.name} (${nutritionData.targetCalories.toInt()} kkal)",
                            content = response,
                            targetCalories = nutritionData.targetCalories,
                            goal = nutritionData.goal
                    )

            Result.success(recommendation)
        } catch (e: Exception) {
            android.util.Log.e("NutritionRepository", "Error getting food recommendation", e)
            // Fallback ke demo recommendation
            val demoRecommendation =
                    FoodRecommendation(
                            title =
                                    "Rekomendasi Personal untuk ${user.name} (${nutritionData.targetCalories.toInt()} kkal)",
                            content = getPersonalizedDemoRecommendation(nutritionData, user),
                            targetCalories = nutritionData.targetCalories,
                            goal = nutritionData.goal
                    )
            Result.success(demoRecommendation)
        }
    }

    /** Overload untuk backward compatibility */
    suspend fun getFoodRecommendation(nutritionData: NutritionData): Result<FoodRecommendation> {
        // Default user untuk backward compatibility
        val defaultUser =
                User(
                        name = "User",
                        age = 25,
                        gender = com.example.fitnessin.model.Gender.PRIA,
                        height = 170.0,
                        currentWeight = 70.0,
                        goalWeight = 70.0,
                        activityLevel = com.example.fitnessin.model.ActivityLevel.MODERATELY_ACTIVE
                )
        return getFoodRecommendation(nutritionData, defaultUser)
    }

    /** Membuat prompt personal untuk Gemini AI berdasarkan data user lengkap */
    private fun createPersonalizedPrompt(nutritionData: NutritionData, user: User): String {
        val goalText =
                when (nutritionData.goal) {
                    WeightGoal.SURPLUS -> "menambah berat badan"
                    WeightGoal.DEFICIT -> "menurunkan berat badan"
                    WeightGoal.MAINTENANCE -> "mempertahankan berat badan"
                }

        val genderText = if (user.gender.name == "PRIA") "pria" else "wanita"

        val currentProgress = user.currentWeight - user.goalWeight
        val progressText =
                when {
                    nutritionData.goal == WeightGoal.SURPLUS ->
                            "perlu menambah ${String.format("%.1f", kotlin.math.abs(currentProgress))} kg"
                    nutritionData.goal == WeightGoal.DEFICIT ->
                            "perlu menurunkan ${String.format("%.1f", kotlin.math.abs(currentProgress))} kg"
                    else -> "sudah mendekati target ideal"
                }

        return """
            Anda adalah ahli gizi profesional Indonesia. Buat rekomendasi menu makanan harian yang personal untuk:

            **PROFIL PENGGUNA:**
            - Nama: ${user.name}
            - Usia: ${user.age} tahun ($genderText)
            - Tinggi: ${user.height.toInt()} cm
            - Berat saat ini: ${user.currentWeight} kg
            - Target berat: ${user.goalWeight} kg ($progressText)
            - Aktivitas: ${user.activityLevel.description}
            - BMR: ${nutritionData.bmr.toInt()} kkal
            - TDEE: ${nutritionData.tdee.toInt()} kkal

            **TARGET NUTRISI HARIAN:**
            - Tujuan: $goalText
            - Target Kalori: ${nutritionData.targetCalories.toInt()} kkal
            - Protein: ${nutritionData.proteinGrams.toInt()}g (${(nutritionData.proteinGrams * 4).toInt()} kkal)
            - Lemak: ${nutritionData.fatGrams.toInt()}g (${(nutritionData.fatGrams * 9).toInt()} kkal)
            - Karbohidrat: ${nutritionData.carbGrams.toInt()}g (${(nutritionData.carbGrams * 4).toInt()} kkal)

            **INSTRUKSI KHUSUS:**
            Buatlah rekomendasi yang disesuaikan dengan profil di atas. Pertimbangkan:
            - Kebutuhan kalori spesifik untuk ${goalText}
            - Usia dan gender untuk preferensi makanan
            - Level aktivitas untuk timing nutrisi
            - Makanan Indonesia yang mudah didapat dan disiapkan

            **FORMAT RESPONSE:**
            Gunakan format Markdown dengan struktur:

            ### Rekomendasi Menu Personal untuk ${user.name}

            **SARAPAN (25% - ${(nutritionData.targetCalories * 0.25).toInt()} kkal)**
            - [Menu dengan kalori spesifik]
            - [Sumber protein, karbo, lemak yang jelas]

            **SNACK PAGI (10% - ${(nutritionData.targetCalories * 0.1).toInt()} kkal)**
            - [Snack sehat sesuai target]

            **MAKAN SIANG (35% - ${(nutritionData.targetCalories * 0.35).toInt()} kkal)**
            - [Menu utama dengan porsi detail]

            **SNACK SORE (10% - ${(nutritionData.targetCalories * 0.1).toInt()} kkal)**
            - [Snack pre/post workout jika aktif]

            **MAKAN MALAM (20% - ${(nutritionData.targetCalories * 0.2).toInt()} kkal)**
            - [Menu ringan tapi nutritious]

            **💡 Tips Personal:**
            - Tips spesifik untuk mencapai target ${user.goalWeight} kg
            - Saran timing makan sesuai aktivitas ${user.activityLevel.description}
            - Rekomendasi hidrasi dan suplemen jika perlu
            - Alternatif makanan untuk variasi

            **📊 Ringkasan Harian:**
            Total: ${nutritionData.targetCalories.toInt()} kkal | P: ${nutritionData.proteinGrams.toInt()}g | L: ${nutritionData.fatGrams.toInt()}g | K: ${nutritionData.carbGrams.toInt()}g

            Pastikan semua rekomendasi realistis, terjangkau, dan mudah diikuti untuk gaya hidup Indonesia.
        """.trimIndent()
    }

    /** Demo food recommendation yang dipersonalisasi untuk testing */
    private fun getPersonalizedDemoRecommendation(
            nutritionData: NutritionData,
            user: User
    ): String {
        val goalText =
                when (nutritionData.goal) {
                    WeightGoal.SURPLUS -> "menambah berat badan"
                    WeightGoal.DEFICIT -> "menurunkan berat badan"
                    WeightGoal.MAINTENANCE -> "mempertahankan berat badan"
                }

        val genderText = if (user.gender.name == "PRIA") "pria" else "wanita"
        val currentProgress = kotlin.math.abs(user.currentWeight - user.goalWeight)

        // Sesuaikan porsi berdasarkan gender dan goal
        val nasiPortion = if (user.gender.name == "PRIA") "1.5 centong" else "1 centong"
        val proteinPortion = if (user.gender.name == "PRIA") "120g" else "100g"

        // Sesuaikan rekomendasi berdasarkan aktivitas
        val activityTips =
                when {
                    user.activityLevel.factor >= 1.6 ->
                            "Konsumsi protein dalam 30 menit setelah olahraga untuk recovery optimal"
                    user.activityLevel.factor >= 1.4 ->
                            "Tambahkan snack sebelum aktivitas fisik untuk energi extra"
                    else -> "Fokus pada makanan yang mudah dicerna dan tidak terlalu berat"
                }

        // Adjust recommendations based on age
        val ageSpecificTips =
                when {
                    user.age < 25 ->
                            "Fokus pada protein untuk pertumbuhan dan pembentukan massa otot"
                    user.age > 40 ->
                            "Perhatikan asupan kalsium dan vitamin D untuk kesehatan tulang"
                    else -> "Maintain metabolisme dengan pola makan teratur"
                }

        return """
            ### Rekomendasi Menu Personal untuk ${user.name}

            **PROFIL ANDA:**
            - Usia: ${user.age} tahun ($genderText) | Tinggi: ${user.height.toInt()} cm
            - Berat: ${user.currentWeight} kg → Target: ${user.goalWeight} kg
            - Aktivitas: ${user.activityLevel.description}
            - Tujuan: ${goalText.uppercase()}

            **TARGET NUTRISI HARIAN:**
            - Kalori: ${nutritionData.targetCalories.toInt()} kkal
            - Protein: ${nutritionData.proteinGrams.toInt()}g | Lemak: ${nutritionData.fatGrams.toInt()}g | Karbohidrat: ${nutritionData.carbGrams.toInt()}g

            **SARAPAN (${(nutritionData.targetCalories * 0.25).toInt()} kkal)**
            - Nasi merah $nasiPortion (180 kkal)
            - Telur ${if (user.gender.name == "PRIA") "2 butir" else "1-2 butir"} (140 kkal)
            - Tumis kangkung + tempe (120 kkal)
            - Teh/kopi tanpa gula (5 kkal)

            **SNACK PAGI (${(nutritionData.targetCalories * 0.1).toInt()} kkal)**
            - ${if (nutritionData.goal == WeightGoal.SURPLUS) "Pisang + selai kacang" else "Apel + teh hijau"}
            - ${if (user.activityLevel.factor >= 1.6) "Protein shake ringan" else "Yogurt plain"}

            **MAKAN SIANG (${(nutritionData.targetCalories * 0.35).toInt()} kkal)**
            - Nasi ${if (nutritionData.goal == WeightGoal.DEFICIT) "merah" else "putih"} $nasiPortion
            - ${if (user.gender.name == "PRIA") "Ayam/ikan" else "Ikan/tahu"} $proteinPortion
            - Sayur (gado-gado/sop/capcay)
            - Lalapan + sambal

            **SNACK SORE (${(nutritionData.targetCalories * 0.1).toInt()} kkal)**
            - ${if (user.activityLevel.factor >= 1.6) "Kurma + kacang" else "Buah potong"}
            - Air kelapa muda

            **MAKAN MALAM (${(nutritionData.targetCalories * 0.2).toInt()} kkal)**
            - ${if (nutritionData.goal == WeightGoal.DEFICIT) "Nasi merah 3/4 centong" else "Nasi 1 centong"}
            - Protein rendah lemak (ikan bakar/rebus)
            - Sayur bening/sup
            - Buah sebagai penutup

            **💡 Tips Personal untuk ${user.name}:**
            - **Target**: ${if (currentProgress > 0) "Perlu ${String.format("%.1f", currentProgress)} kg lagi" else "Maintain berat ideal"}
            - **Aktivitas**: $activityTips
            - **Usia**: $ageSpecificTips
            - **Hidrasi**: ${if (user.activityLevel.factor >= 1.6) "10-12 gelas/hari" else "8-10 gelas/hari"}
            - **Timing**: ${if (user.gender.name == "PRIA") "Makan setiap 3-4 jam" else "Porsi kecil tapi sering"}

            **📊 Ringkasan Harian:**
            Total: ${nutritionData.targetCalories.toInt()} kkal | P: ${nutritionData.proteinGrams.toInt()}g (${((nutritionData.proteinGrams * 4 / nutritionData.targetCalories) * 100).toInt()}%) | L: ${nutritionData.fatGrams.toInt()}g (${((nutritionData.fatGrams * 9 / nutritionData.targetCalories) * 100).toInt()}%) | K: ${nutritionData.carbGrams.toInt()}g (${((nutritionData.carbGrams * 4 / nutritionData.targetCalories) * 100).toInt()}%)

            *Rekomendasi ini disesuaikan dengan profil ${user.name}. Sesuaikan porsi sesuai toleransi dan preferensi personal.*
        """.trimIndent()
    }
}
