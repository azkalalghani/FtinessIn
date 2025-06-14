# Aplikasi Kebugaran Inti

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)]() [![Tests](https://img.shields.io/badge/tests-passing-brightgreen)]() [![Kotlin](https://img.shields.io/badge/kotlin-2.0.21-blue)](https://kotlinlang.org/) [![Firebase](https://img.shields.io/badge/firebase-enabled-orange)](https://firebase.google.com/) [![API](https://img.shields.io/badge/min_sdk-26-green)]()

Aplikasi Android untuk membantu pengguna mencapai tujuan berat badan mereka melalui perhitungan kalori dan makronutrien yang akurat, serta rekomendasi makanan yang cerdas.

## 🎯 Fitur Utama

- **Kalkulasi Nutrisi Akurat**: BMR, TDEE, dan target kalori berdasarkan profil pengguna
- **Tracking Berat Badan**: Mencatat dan memantau progress berat badan
- **Rekomendasi Makanan Cerdas**: Menggunakan Gemini AI untuk saran menu harian
- **Profil Personal**: Menyimpan data pengguna di Firebase Firestore

## 🏗️ Arsitektur

Aplikasi ini menggunakan arsitektur **MVVM (Model-View-ViewModel)** dengan:

- **View Layer**: Jetpack Compose UI
- **ViewModel Layer**: State management dan UI logic
- **Repository Layer**: Data access abstraction
- **Model Layer**: Data structures

## 🛠️ Tech Stack

### Core

- **Kotlin** - Bahasa pemrograman utama
- **Jetpack Compose** - Modern UI toolkit
- **MVVM Architecture** - Pemisahan concerns
- **Kotlin Coroutines & Flow** - Asynchronous programming

### Dependencies & Libraries

- **Hilt** - Dependency Injection
- **Firebase Auth** - Authentication
- **Firebase Firestore** - Cloud database
- **Retrofit** - HTTP client untuk API calls
- **Gemini AI** - Rekomendasi makanan

### Specifications

- **Target SDK**: 34 (Android 14)
- **Minimum SDK**: 26 (Android 8.0 Oreo)
- **Gradle**: 8.5+

## 📊 Kalkulasi Nutrisi

### Formula BMR (Mifflin-St Jeor)

- **Pria**: BMR = (10 × berat_kg) + (6.25 × tinggi_cm) - (5 × usia) + 5
- **Wanita**: BMR = (10 × berat_kg) + (6.25 × tinggi_cm) - (5 × usia) - 161

### Target Kalori

- **SURPLUS** (naik berat): TDEE + 400 kkal
- **DEFICIT** (turun berat): TDEE - 400 kkal
- **MAINTENANCE** (jaga berat): TDEE

### Makronutrien

- **Protein**: 1.8g per kg berat badan
- **Lemak**: 25% dari total kalori
- **Karbohidrat**: Sisanya

## 🔧 Setup Development

### Prerequisites

- Android Studio Hedgehog atau lebih baru
- JDK 11 atau lebih baru
- Android SDK 34
- Firebase Project dengan Firestore dan Auth enabled
- Gemini API Key

### Installation

1. **Clone repository**

   ```bash
   git clone <repository-url>
   cd FitnessIn
   ```

2. **Setup Firebase**

   - Buat project baru di [Firebase Console](https://console.firebase.google.com)
   - Aktifkan Authentication (Email/Password)
   - Aktifkan Cloud Firestore
   - Download `google-services.json` dan letakkan di folder `app/`

3. **Setup Gemini AI**

   - Dapatkan API key dari [Google AI Studio](https://makersuite.google.com)
   - Ganti `YOUR_GEMINI_API_KEY_HERE` di `GeminiApiService.kt`

4. **Build & Run**
   ```bash
   ./gradlew assembleDebug
   ```

## 📁 Struktur Project

```
app/src/main/java/com/example/fitnessin/
├── di/                     # Dependency Injection
│   └── AppModule.kt
├── model/                  # Data Models
│   ├── User.kt
│   ├── WeightEntry.kt
│   └── NutritionData.kt
├── network/                # Network Layer
│   └── GeminiApiService.kt
├── repository/             # Data Layer
│   ├── FirebaseRepository.kt
│   └── NutritionRepository.kt
├── ui/
│   ├── screen/            # UI Screens
│   │   ├── ProfileSetupScreen.kt
│   │   └── DashboardScreen.kt
│   ├── viewmodel/         # ViewModels
│   │   ├── AuthViewModel.kt
│   │   └── DashboardViewModel.kt
│   └── theme/             # UI Theme
├── util/                  # Utilities
│   └── NutritionCalculator.kt
└── MainActivity.kt
```

## 🔒 Database Schema

### Collection: `users`

```json
{
  "uid": "String",
  "name": "String",
  "email": "String",
  "age": "Number",
  "gender": "String", // "PRIA" atau "WANITA"
  "height": "Number", // cm
  "currentWeight": "Number", // kg
  "goalWeight": "Number", // kg
  "activityLevel": "String"
}
```

### Collection: `progress`

```json
{
  "userId": "String",
  "weight": "Number", // kg
  "timestamp": "Timestamp"
}
```

## 🤖 Integrasi Gemini AI

Aplikasi menggunakan Gemini Pro untuk menghasilkan rekomendasi makanan berdasarkan:

- Target kalori pengguna
- Kebutuhan makronutrien
- Tujuan berat badan (surplus/deficit/maintenance)
- Preferensi makanan lokal Indonesia

## 📱 Cara Penggunaan

1. **Setup Profil**: Masukkan data personal (nama, usia, tinggi, berat, dll)
2. **Lihat Dashboard**: Pantau kalori target dan kebutuhan nutrisi
3. **Track Progress**: Catat berat badan secara berkala
4. **Dapatkan Rekomendasi**: Lihat saran menu makanan harian

## 🧪 Testing

```bash
# Unit Tests
./gradlew test

# Instrumented Tests
./gradlew connectedAndroidTest
```

## 📝 License

Copyright © 2024 Aplikasi Kebugaran Inti. All rights reserved.

## 🤝 Contributing

1. Fork project
2. Create feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Open Pull Request

## 📞 Support

Untuk pertanyaan dan dukungan, silakan buat issue di repository ini.
