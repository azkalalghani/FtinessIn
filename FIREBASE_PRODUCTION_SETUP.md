# Firebase Production Setup Guide

## Langkah-langkah Setup Firebase untuk Production

### 1. Buat Proyek Firebase

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Klik "Add project" atau "Tambah proyek"
3. Masukkan nama proyek: `fitness-in-production` (atau nama yang Anda inginkan)
4. Ikuti langkah-langkah setup hingga selesai

### 2. Setup Authentication

1. Di Firebase Console, masuk ke **Authentication**
2. Klik tab **Sign-in method**
3. Enable **Email/Password** provider
4. Pastikan "Email link (passwordless sign-in)" dinonaktifkan jika tidak diperlukan

### 3. Setup Firestore Database

1. Di Firebase Console, masuk ke **Firestore Database**
2. Klik **Create database**
3. Pilih **Start in test mode** (untuk development) atau **Start in production mode**
4. Pilih lokasi server (pilih yang terdekat dengan pengguna Anda)

### 4. Database Security Rules

Setelah database dibuat, update **Rules** dengan:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can only access their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Progress data (weight entries)
    match /progress/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;

      match /entries/{entryId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

### 5. Setup Android App

1. Di Firebase Console, klik ikon Android untuk menambah app
2. Masukkan package name: `com.example.fitnessin`
3. App nickname: `Fitness In`
4. Debug signing certificate SHA-1 (optional untuk development)

### 6. Download Configuration File

1. Download `google-services.json`
2. Replace file `app/google-services.json` yang ada dengan file yang baru
3. **PENTING**: Jangan commit file ini ke public repository karena berisi API keys

### 7. Update Constants

Edit file `Constants.kt`:

```kotlin
object Constants {
    // Set ke false untuk menggunakan Firebase yang sebenarnya
    const val DEMO_MODE = false

    // ... constants lainnya tetap sama

    // Ganti dengan Gemini API key yang benar
    const val GEMINI_API_KEY = "YOUR_ACTUAL_GEMINI_API_KEY"
}
```

### 8. Testing

1. Build dan install aplikasi
2. Test register user baru
3. Test login dengan user yang sudah ada
4. Test menyimpan dan membaca data profil
5. Test menyimpan data berat badan

### 9. Monitoring dan Analytics (Optional)

1. Enable **Google Analytics** untuk proyek Firebase
2. Enable **Crashlytics** untuk monitoring error
3. Setup **Performance Monitoring**

## Struktur Database

### Collection: `users`

```
users/{userId}
├── uid: String
├── email: String
├── name: String
├── age: Int
├── gender: String
├── height: Int (cm)
├── currentWeight: Float (kg)
├── targetWeight: Float (kg)
├── activityLevel: String
├── weightGoal: String
└── createdAt: Timestamp
```

### Collection: `progress/{userId}/entries`

```
progress/{userId}/entries/{entryId}
├── weight: Float
├── date: String
├── notes: String (optional)
└── timestamp: Timestamp
```

## Environment Variables

Untuk keamanan, pertimbangkan menggunakan environment variables:

```kotlin
// Constants.kt
object Constants {
    const val DEMO_MODE = BuildConfig.DEMO_MODE
    const val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY
}
```

Dan di `build.gradle.kts`:

```kotlin
android {
    buildTypes {
        debug {
            buildConfigField("Boolean", "DEMO_MODE", "true")
            buildConfigField("String", "GEMINI_API_KEY", "\"demo_key\"")
        }
        release {
            buildConfigField("Boolean", "DEMO_MODE", "false")
            buildConfigField("String", "GEMINI_API_KEY", "\"${project.findProperty("GEMINI_API_KEY")}\"")
        }
    }
}
```

## Troubleshooting

### Error: FirebaseApp is not initialized

- Pastikan `google-services.json` ada di folder `app/`
- Pastikan plugin `com.google.gms.google-services` ditambahkan di `build.gradle.kts`

### Error: Authentication failed

- Cek apakah Email/Password provider sudah di-enable di Firebase Console
- Cek apakah SHA-1 fingerprint sudah ditambahkan (untuk production)

### Error: Permission denied

- Cek Firestore security rules
- Pastikan user sudah login sebelum mengakses data

### Error: Network request failed

- Cek koneksi internet
- Cek apakah Firebase project masih aktif
- Cek quota dan billing (untuk production)

## Next Steps

1. Setup Gemini AI API key yang benar
2. Test dengan data real
3. Setup production signing certificate
4. Deploy ke Play Store
