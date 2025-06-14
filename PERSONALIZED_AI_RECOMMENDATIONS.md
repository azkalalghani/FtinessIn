# Personalized AI Nutrition Recommendation System

## 📋 Overview

Sistem rekomendasi nutrisi AI yang dipersonalisasi berdasarkan profil lengkap user, menghasilkan saran makanan yang spesifik dan relevan dengan kebutuhan individual setiap pengguna.

## 🎯 Tujuan Personalisasi

### From Generic to Personal
**Before (Generic AI):**
- Rekomendasi umum tanpa konteks user
- Tidak mempertimbangkan gender, usia, aktivitas
- Output standard untuk semua user
- Porsi dan timing yang sama untuk semua

**After (Personalized AI):**
- Rekomendasi khusus berdasarkan profil lengkap
- Disesuaikan dengan gender, usia, aktivitas, dan goal
- Output spesifik untuk setiap individu
- Porsi dan timing yang disesuaikan

## 🔧 Input Data untuk Personalisasi

### User Profile Data
```kotlin
data class User(
    val name: String,           // Nama untuk personalisasi
    val age: Int,              // Usia untuk kebutuhan spesifik
    val gender: Gender,        // Gender untuk porsi dan kebutuhan
    val height: Double,        // Tinggi untuk BMR calculation
    val currentWeight: Double, // Berat saat ini
    val goalWeight: Double,    // Target berat badan
    val activityLevel: ActivityLevel // Level aktivitas
)
```

### Nutrition Data
```kotlin
data class NutritionData(
    val bmr: Double,           // Basal Metabolic Rate
    val tdee: Double,          // Total Daily Energy Expenditure
    val targetCalories: Double, // Target kalori harian
    val proteinGrams: Double,  // Kebutuhan protein (g)
    val fatGrams: Double,      // Kebutuhan lemak (g)
    val carbGrams: Double,     // Kebutuhan karbohidrat (g)
    val goal: WeightGoal       // Tujuan: surplus/deficit/maintenance
)
```

## 🤖 AI Prompt Engineering

### Enhanced Prompt Structure

#### 1. User Context
```
**PROFIL PENGGUNA:**
- Nama: ${user.name}
- Usia: ${user.age} tahun (${genderText})
- Tinggi: ${user.height} cm
- Berat saat ini: ${user.currentWeight} kg
- Target berat: ${user.goalWeight} kg (${progressText})
- Aktivitas: ${user.activityLevel.description}
- BMR: ${nutritionData.bmr} kkal
- TDEE: ${nutritionData.tdee} kkal
```

#### 2. Nutrition Requirements
```
**TARGET NUTRISI HARIAN:**
- Tujuan: ${goalText}
- Target Kalori: ${nutritionData.targetCalories} kkal
- Protein: ${nutritionData.proteinGrams}g (${proteinCalories} kkal)
- Lemak: ${nutritionData.fatGrams}g (${fatCalories} kkal)
- Karbohidrat: ${nutritionData.carbGrams}g (${carbCalories} kkal)
```

#### 3. Personalization Instructions
```
**INSTRUKSI KHUSUS:**
- Kebutuhan kalori spesifik untuk ${goalText}
- Usia dan gender untuk preferensi makanan
- Level aktivitas untuk timing nutrisi
- Makanan Indonesia yang mudah didapat
```

#### 4. Output Format Guidelines
```
**FORMAT RESPONSE:**
- Menu dengan breakdown kalori per waktu makan
- Porsi spesifik berdasarkan gender dan goal
- Tips personal berdasarkan profil user
- Ringkasan nutrisi harian
```

## 📊 Personalization Logic

### Gender-Based Adjustments
```kotlin
// Porsi disesuaikan dengan gender
val nasiPortion = if (user.gender.name == "PRIA") "1.5 centong" else "1 centong"
val proteinPortion = if (user.gender.name == "PRIA") "120g" else "100g"

// Tips berdasarkan gender
val timingTips = if (user.gender.name == "PRIA") 
    "Makan setiap 3-4 jam" else 
    "Porsi kecil tapi sering"
```

### Age-Based Recommendations
```kotlin
val ageSpecificTips = when {
    user.age < 25 -> "Fokus pada protein untuk pertumbuhan dan pembentukan massa otot"
    user.age > 40 -> "Perhatikan asupan kalsium dan vitamin D untuk kesehatan tulang"
    else -> "Maintain metabolisme dengan pola makan teratur"
}
```

### Activity Level Adaptations
```kotlin
val activityTips = when {
    user.activityLevel.factor >= 1.6 -> 
        "Konsumsi protein dalam 30 menit setelah olahraga untuk recovery optimal"
    user.activityLevel.factor >= 1.4 -> 
        "Tambahkan snack sebelum aktivitas fisik untuk energi extra"
    else -> 
        "Fokus pada makanan yang mudah dicerna dan tidak terlalu berat"
}
```

### Goal-Specific Menu Adjustments
```kotlin
// Sarapan disesuaikan dengan goal
val breakfastCarb = when (nutritionData.goal) {
    WeightGoal.DEFICIT -> "Nasi merah 3/4 centong"
    WeightGoal.SURPLUS -> "Nasi putih 1.5 centong"
    WeightGoal.MAINTENANCE -> "Nasi 1 centong"
}

// Snack disesuaikan dengan goal
val snackChoice = when (nutritionData.goal) {
    WeightGoal.SURPLUS -> "Pisang + selai kacang"
    WeightGoal.DEFICIT -> "Apel + teh hijau"
    WeightGoal.MAINTENANCE -> "Yogurt plain"
}
```

## 🍽️ Output Examples

### Example 1: Male, 25 years, Muscle Gain
```
### Rekomendasi Menu Personal untuk Ahmad

**PROFIL ANDA:**
- Usia: 25 tahun (pria) | Tinggi: 175 cm
- Berat: 65 kg → Target: 75 kg
- Aktivitas: Aktivitas Berat
- Tujuan: MENAMBAH BERAT BADAN

**SARAPAN (625 kkal)**
- Nasi merah 1.5 centong (270 kkal)
- Telur 2 butir (140 kkal)
- Tumis kangkung + tempe (140 kkal)
- Susu protein (75 kkal)

**💡 Tips Personal untuk Ahmad:**
- Target: Perlu 10.0 kg lagi
- Aktivitas: Konsumsi protein dalam 30 menit setelah olahraga
- Usia: Fokus pada protein untuk pembentukan massa otot
- Hidrasi: 12 gelas/hari
```

### Example 2: Female, 35 years, Weight Loss
```
### Rekomendasi Menu Personal untuk Sari

**PROFIL ANDA:**
- Usia: 35 tahun (wanita) | Tinggi: 160 cm
- Berat: 70 kg → Target: 60 kg
- Aktivitas: Aktivitas Ringan
- Tujuan: MENURUNKAN BERAT BADAN

**SARAPAN (400 kkal)**
- Nasi merah 1 centong (180 kkal)
- Telur 1 butir (70 kkal)
- Tumis sayuran (100 kkal)
- Teh hijau (5 kkal)

**💡 Tips Personal untuk Sari:**
- Target: Perlu 10.0 kg lagi
- Aktivitas: Fokus pada makanan yang mudah dicerna
- Hidrasi: 8-10 gelas/hari
- Timing: Porsi kecil tapi sering
```

## 🔄 Dynamic Adaptation System

### Real-time Personalization Flow
```
User Profile Changes → Nutrition Recalculation → AI Prompt Update → Personalized Output
```

### Adaptation Triggers
1. **Weight Updates** → Recalculate BMR/TDEE → Update calorie targets
2. **Activity Changes** → Adjust meal timing and portions
3. **Goal Modifications** → Change macro ratios and food choices
4. **Progress Updates** → Modify recommendations based on results

### Smart Recommendations
- **Progressive Adjustment**: Recommendations evolve as user progresses
- **Contextual Awareness**: Consider user's lifestyle and preferences
- **Cultural Relevance**: Focus on Indonesian foods and eating patterns
- **Practical Implementation**: Realistic portions and accessible ingredients

## 📈 Benefits of Personalization

### For Users
1. **Relevance**: Recommendations yang benar-benar sesuai dengan kebutuhan
2. **Motivation**: Merasa bahwa advice dibuat khusus untuk mereka
3. **Effectiveness**: Higher chance of achieving goals dengan plan yang personal
4. **Trust**: Increased confidence in the recommendations

### For App
1. **Engagement**: Users lebih sering menggunakan karena content yang relevan
2. **Retention**: Personal experience meningkatkan loyalitas user
3. **Success Rate**: Higher goal achievement rate
4. **Differentiation**: Unique value proposition dibanding kompetitor

## 🧪 Quality Assurance

### Validation Points
- [x] **Input Validation**: Semua data user divalidasi sebelum processing
- [x] **Output Consistency**: Format output selalu konsisten dan readable
- [x] **Cultural Appropriateness**: Makanan dan porsi sesuai dengan budaya Indonesia
- [x] **Nutritional Accuracy**: Kalori dan macro calculations akurat
- [x] **Personalization Logic**: Logic personalisasi berfungsi dengan benar

### Test Scenarios
1. **Different Profiles**: Test dengan berbagai kombinasi user profile
2. **Edge Cases**: Extreme values, unusual combinations
3. **Progress Simulation**: Test adaptasi saat user progress
4. **Demo Mode**: Ensure demo version works without API

## 🚀 Future Enhancements

### Advanced Personalization
- **Dietary Restrictions**: Support untuk vegetarian, halal, diabetes, dll
- **Food Preferences**: Learn dari user behavior dan preferences
- **Seasonal Recommendations**: Adjust berdasarkan musim dan availability
- **Budget Considerations**: Recommendations berdasarkan budget user

### AI Improvements
- **Learning Algorithm**: AI yang belajar dari user feedback
- **Regional Variations**: Different recommendations untuk different regions
- **Meal Planning**: Extended planning untuk weekly/monthly menus
- **Recipe Integration**: Detailed recipes dengan cooking instructions

### Integration Features
- **Shopping Lists**: Auto-generate shopping list dari recommendations
- **Nutrition Tracking**: Integration dengan food logging
- **Progress Monitoring**: Track success rate dari recommendations
- **Social Features**: Share personalized plans dengan friends/family

## ✅ Implementation Status

### Current Features
- 🟢 **Personalized Prompts** - AI prompts include full user context
- 🟢 **Dynamic Content** - Output adapts to user profile
- 🟢 **Cultural Relevance** - Indonesian foods and eating patterns
- 🟢 **Gender Adaptation** - Porsi dan tips disesuaikan dengan gender
- 🟢 **Age Considerations** - Recommendations berdasarkan usia
- 🟢 **Activity Integration** - Meal timing sesuai dengan activity level
- 🟢 **Goal Alignment** - Content sesuai dengan weight goals
- 🟢 **Demo Mode Support** - Personalized demo content

### Production Ready
- ✅ **API Integration** - Ready untuk production Gemini API
- ✅ **Fallback System** - Demo mode untuk testing dan backup
- ✅ **Error Handling** - Robust error handling dan fallbacks
- ✅ **Performance** - Efficient processing dan caching
- ✅ **User Experience** - Smooth integration dengan UI

## 📝 Technical Implementation

### Key Files Modified
1. **NutritionRepository.kt** - Enhanced dengan personalized AI calls
2. **DashboardViewModel.kt** - Pass user data ke recommendation service
3. **AI Prompt Engineering** - Comprehensive personalization logic

### API Contract
```kotlin
// New personalized method
suspend fun getFoodRecommendation(
    nutritionData: NutritionData,
    user: User
): Result<FoodRecommendation>

// Backward compatibility
suspend fun getFoodRecommendation(
    nutritionData: NutritionData
): Result<FoodRecommendation>
```

### Demo Mode Enhancement
- Personalized demo content berdasarkan user profile
- Same quality dan detail seperti production API
- Perfect untuk testing dan development

## 🎯 Summary

Sistem rekomendasi AI sekarang **fully personalized** dengan kemampuan:

- ✅ **Complete User Context** - Menggunakan semua data user untuk personalisasi
- ✅ **Adaptive Content** - Output yang berubah sesuai profil individual
- ✅ **Cultural Relevance** - Indonesian foods dan eating patterns
- ✅ **Smart Adjustments** - Gender, usia, aktivitas, dan goal considerations
- ✅ **Production Quality** - Ready untuk real API dan demo mode
- ✅ **User-Centric Design** - Experience yang benar-benar personal

User sekarang mendapatkan rekomendasi nutrisi yang benar-benar **dibuat khusus untuk mereka**, dengan mempertimbangkan semua aspek profil personal mereka, menghasilkan advice yang lebih relevan, praktis, dan efektif untuk mencapai goals fitness mereka.