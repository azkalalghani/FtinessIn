# Firebase Setup Guide untuk Aplikasi Kebugaran Inti

## 📋 Prerequisites

- Akun Google
- Android Studio
- Aplikasi FitnessIn sudah ter-clone

## 🔥 Setup Firebase Project

### 1. Buat Firebase Project

1. Buka [Firebase Console](https://console.firebase.google.com)
2. Klik "Add project" atau "Create a project"
3. Masukkan nama project: **"Fitness In"**
4. Pilih apakah ingin menggunakan Google Analytics (opsional)
5. Klik "Create project"

### 2. Tambahkan Android App

1. Di dashboard Firebase, klik icon Android
2. Masukkan package name: `com.example.fitnessin`
3. Masukkan app nickname: **"Fitness In Android"**
4. SHA-1 certificate fingerprint (opsional untuk development)
5. Klik "Register app"

### 3. Download google-services.json

1. Download file `google-services.json`
2. Copy file tersebut ke folder: `app/` (menggantikan file dummy yang sudah ada)

### 4. Setup Authentication

1. Di Firebase Console, buka **Authentication**
2. Klik tab **"Sign-in method"**
3. Enable **"Email/Password"**
   - Klik Email/Password
   - Toggle "Enable"
   - Klik "Save"

### 5. Setup Cloud Firestore

1. Di Firebase Console, buka **Firestore Database**
2. Klik **"Create database"**
3. Pilih **"Start in production mode"** (untuk keamanan)
4. Pilih lokasi: **asia-southeast2 (Jakarta)**
5. Klik "Enable"

### 6. Setup Firestore Security Rules

Setelah database dibuat, update rules di tab **"Rules"**:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users dapat membaca dan menulis data mereka sendiri
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Progress dapat dibaca dan ditulis oleh user yang bersangkutan
    match /progress/{document} {
      allow read, write: if request.auth != null &&
        resource.data.userId == request.auth.uid;
      allow create: if request.auth != null &&
        request.resource.data.userId == request.auth.uid;
    }
  }
}
```

## 🔑 Setup Gemini AI

### 1. Dapatkan API Key

1. Buka [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Login dengan akun Google
3. Klik "Create API Key"
4. Copy API key yang dihasilkan

### 2. Update Konstanta API

Edit file `app/src/main/java/com/example/fitnessin/util/Constants.kt`:

```kotlin
// Ganti dengan API Key Gemini Anda
const val GEMINI_API_KEY = "YOUR_ACTUAL_API_KEY_HERE"
```

## 🧪 Testing Setup

### 1. Test Koneksi Firebase

Jalankan aplikasi dan coba:

- Daftar user baru melalui ProfileSetupScreen
- Periksa di Firebase Console > Authentication apakah user terdaftar
- Periksa di Firestore > users collection apakah data tersimpan

### 2. Test Gemini AI

- Setelah setup profil, lihat apakah rekomendasi makanan muncul di dashboard
- Klik tombol refresh untuk memuat ulang rekomendasi

## 🚨 Troubleshooting

### Error "google-services.json not found"

- Pastikan file `google-services.json` ada di folder `app/`
- Clean dan rebuild project

### Error "FirebaseApp initialization"

- Pastikan `google-services.json` valid dan sesuai package name
- Sync project dengan Gradle files

### Error "Permission denied" di Firestore

- Periksa Firestore Security Rules
- Pastikan user sudah authenticated

### Error Gemini API

- Pastikan API key valid dan aktif
- Periksa quota penggunaan API di Google Cloud Console

## 📱 Build Instructions

```bash
# Debug build
./gradlew assembleDebug

# Release build (perlu signing key)
./gradlew assembleRelease

# Install ke device
./gradlew installDebug
```

## 🔒 Security Notes

1. **Jangan commit API keys** ke version control
2. Gunakan **environment variables** untuk production
3. Setup **App Check** untuk production deployment
4. Review **Firestore security rules** sebelum production

## 📞 Support

Jika ada masalah:

1. Periksa log Android Studio
2. Periksa Firebase Console > Usage
3. Buat issue di repository ini
