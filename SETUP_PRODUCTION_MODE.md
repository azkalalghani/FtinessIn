# 🚀 Panduan Setup Firebase Production Mode

## Status Saat Ini

✅ **Demo Mode DINONAKTIFKAN** - Aplikasi sekarang dalam production mode  
✅ **Build berhasil** - Aplikasi siap untuk dikonfigurasi dengan Firebase real  
❌ **Firebase belum dikonfigurasi** - Perlu setup project Firebase yang sebenarnya

## Langkah 1: Setup Firebase Project

### 1.1 Buat Firebase Project

1. Buka [Firebase Console](https://console.firebase.google.com/)
2. Klik **"Add project"** atau **"Create a project"**
3. Masukkan nama project: `fitness-in-production`
4. Enable Google Analytics (opsional)
5. Tunggu project selesai dibuat

### 1.2 Setup Authentication

1. Di Firebase Console, masuk ke **Authentication**
2. Klik tab **"Sign-in method"**
3. Enable **"Email/Password"** provider
4. Pastikan "Email link (passwordless sign-in)" dinonaktifkan
5. Klik **Save**

### 1.3 Setup Firestore Database

1. Di Firebase Console, masuk ke **"Firestore Database"**
2. Klik **"Create database"**
3. Pilih **"Start in test mode"** (untuk development)
4. Pilih lokasi server: **"asia-southeast1 (Jakarta)"** untuk Indonesia
5. Klik **Done**

### 1.4 Add Android App

1. Di Project Overview, klik ikon **Android** untuk menambah app
2. Masukkan package name: `com.example.fitnessin`
3. App nickname: `Fitness In Production`
4. Debug signing certificate SHA-1 (opsional untuk development)
5. Klik **Register app**

### 1.5 Download Configuration

1. Download `google-services.json` yang baru
2. **Replace** file `app/google-services.json` yang ada dengan file yang baru
3. **PENTING**: Jangan commit file ini ke public repository

## Langkah 2: Update Firestore Security Rules

Setelah database dibuat, update **Rules** di Firestore Console:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users collection - users can only access their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    // Progress collection - weight entries for each user
    match /progress/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;

      match /entries/{entryId} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

## Langkah 3: Setup Gemini AI (Opsional)

### 3.1 Dapatkan API Key

1. Buka [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Klik **"Create API Key"**
3. Copy API key yang dihasilkan

### 3.2 Update Constants

Edit file `app/src/main/java/com/example/fitnessin/util/Constants.kt`:

```kotlin
// Ganti dengan API key yang sebenarnya
const val GEMINI_API_KEY = "YOUR_ACTUAL_GEMINI_API_KEY_HERE"
```

## Langkah 4: Test Aplikasi

### 4.1 Build dan Install

```bash
cd /Volumes/SSDGan/Coding-Project/Kotlin/FitnessIn
./gradlew assembleDebug
```

### 4.2 Test Flow

1. **Register User Baru**:

   - Buka aplikasi
   - Pilih tab "Daftar"
   - Masukkan nama, email valid, dan password
   - Tekan "Daftar"

2. **Setup Profile**:

   - Isi data personal lengkap
   - Pilih tingkat aktivitas dan goal
   - Tekan "Simpan Profil"

3. **Test Dashboard**:
   - Lihat nutrition calculations
   - Test input berat badan
   - Test refresh food recommendations

### 4.3 Monitoring di Firebase Console

- **Authentication**: Lihat user yang terdaftar
- **Firestore**: Lihat data yang tersimpan di collections
- **Usage**: Monitor quota dan penggunaan

## Troubleshooting

### Error: "Default FirebaseApp is not initialized"

**Solusi**:

1. Pastikan `google-services.json` ada di folder `app/`
2. Pastikan file bukan file demo (check project_id)
3. Clean dan rebuild project

### Error: "Permission denied" di Firestore

**Solusi**:

1. Cek Firestore Rules sudah diupdate
2. Pastikan user sudah login sebelum akses data
3. Cek UID user sesuai dengan document ID

### Error: "Authentication failed"

**Solusi**:

1. Cek Email/Password provider sudah di-enable
2. Pastikan email format valid
3. Password minimal 6 karakter

### Error: "Network request failed"

**Solusi**:

1. Cek koneksi internet
2. Cek Firebase project masih aktif
3. Cek quota belum terlampaui

## Fitur Production Mode

### ✅ Yang Sudah Aktif:

- **Real Authentication**: Email/password authentication dengan Firebase
- **Real Database**: Data tersimpan di Firestore
- **Input Validation**: Validasi form real-time
- **Error Handling**: Error messages yang informatif
- **Security**: Firestore rules untuk data protection

### 🔄 Yang Masih Demo (Sampai API Key diupdate):

- **Gemini AI**: Masih menggunakan demo recommendations
- **Food Suggestions**: Fallback ke content demo

## Struktur Database Production

### Collection: `users/{userId}`

```json
{
  "uid": "firebase_user_id",
  "email": "user@example.com",
  "name": "John Doe",
  "age": 25,
  "gender": "PRIA",
  "height": 175,
  "currentWeight": 70.0,
  "targetWeight": 75.0,
  "activityLevel": "MODERATELY_ACTIVE",
  "weightGoal": "GAIN",
  "createdAt": "2025-06-11T10:30:00Z"
}
```

### Collection: `progress/{userId}/entries/{entryId}`

```json
{
  "weight": 71.5,
  "date": "2025-06-11",
  "notes": "Feeling good",
  "timestamp": "2025-06-11T10:30:00Z"
}
```

## Next Steps

1. **Immediate** (Required):

   - [ ] Setup Firebase project di console
   - [ ] Download dan replace `google-services.json`
   - [ ] Update Firestore rules
   - [ ] Test register dan login

2. **Optional** (Enhanced Features):

   - [ ] Setup Gemini AI API key
   - [ ] Test food recommendations
   - [ ] Add more sophisticated error handling
   - [ ] Setup Firebase Analytics

3. **Production Ready**:
   - [ ] Generate signed APK
   - [ ] Setup production Firebase project
   - [ ] Review security rules
   - [ ] Setup monitoring dan alerts

---

## Status Aplikasi

| Feature        | Status      | Notes                 |
| -------------- | ----------- | --------------------- |
| Authentication | ⚠️ Ready    | Perlu Firebase config |
| Database       | ⚠️ Ready    | Perlu Firebase config |
| UI/UX          | ✅ Complete | Production ready      |
| Validation     | ✅ Complete | Real-time validation  |
| Calculations   | ✅ Complete | Accurate formulas     |
| Security       | ✅ Complete | Rules implemented     |
| AI Integration | 🔄 Partial  | Demo fallback active  |

**Overall Status**: 🟡 **Ready for Firebase Setup** - Aplikasi siap untuk production setelah Firebase dikonfigurasi.

---

Setelah mengikuti panduan ini, aplikasi akan berfungsi penuh dengan:

- ✅ Real user authentication
- ✅ Persistent data storage
- ✅ Secure database access
- ✅ Production-ready architecture
