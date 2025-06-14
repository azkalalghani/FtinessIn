# 🔧 Panduan Konfigurasi Firebase Project: fitnessin-c7cfc

## Status Saat Ini

✅ Firebase Project: `fitnessin-c7cfc` sudah dibuat  
✅ google-services.json sudah didownload  
❌ **Perlu konfigurasi Authentication dan Firestore**

## Langkah 1: Setup Authentication

### 1.1 Buka Firebase Console

- URL: https://console.firebase.google.com/project/fitnessin-c7cfc
- Pastikan Anda sudah login dengan akun Google yang sama

### 1.2 Enable Authentication

1. Di sidebar kiri, klik **"Authentication"**
2. Jika belum disetup, klik **"Get started"**
3. Klik tab **"Sign-in method"** di bagian atas
4. Cari **"Email/Password"** di daftar providers
5. Klik **"Email/Password"**
6. Toggle **"Enable"** menjadi ON
7. Pastikan **"Email link (passwordless sign-in)"** tetap OFF
8. Klik **"Save"**

### Screenshot yang diharapkan:

```
Sign-in providers:
☑️ Email/Password    Enabled
☐ Email link         Disabled
☐ Google             Disabled
☐ Phone              Disabled
```

## Langkah 2: Setup Firestore Database

### 2.1 Buka Firestore

1. Di sidebar kiri, klik **"Firestore Database"**
2. Jika belum ada database, klik **"Create database"**

### 2.2 Pilih Security Rules

- Pilih **"Start in test mode"** untuk development
- Klik **"Next"**

### 2.3 Pilih Location

- Pilih **"asia-southeast1 (Jakarta)"** untuk performa terbaik di Indonesia
- Klik **"Done"**

### 2.4 Tunggu Database Dibuat

- Proses ini memakan waktu 1-2 menit
- Setelah selesai, Anda akan melihat Firestore console

## Langkah 3: Update Security Rules

### 3.1 Buka Rules Tab

1. Di Firestore console, klik tab **"Rules"**
2. Anda akan melihat rules default yang mengizinkan akses untuk 30 hari

### 3.2 Update Rules

Ganti rules yang ada dengan rules berikut:

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

### 3.3 Publish Rules

1. Klik **"Publish"**
2. Konfirmasi dengan klik **"Publish"** lagi

## Langkah 4: Verifikasi Setup

### 4.1 Cek Authentication

- Masuk ke **Authentication > Users**
- Seharusnya kosong (belum ada user)
- Ini normal karena belum ada yang register

### 4.2 Cek Firestore

- Masuk ke **Firestore Database > Data**
- Seharusnya kosong (belum ada data)
- Struktur collections akan dibuat otomatis saat ada data pertama

### 4.3 Cek Project Settings

- Masuk ke **Project Settings** (gear icon)
- Di tab **General**, pastikan:
  - Project ID: `fitnessin-c7cfc`
  - Project number: `740665265100`
  - Web API Key: `AIzaSyDF0y4v98rNyHeDQjPemw9KM1q3t3ksD6o`

## Langkah 5: Test Koneksi

Sekarang mari kita test apakah aplikasi bisa connect ke Firebase:

### 5.1 Build Aplikasi

```bash
cd /Volumes/SSDGan/Coding-Project/Kotlin/FitnessIn
./gradlew clean
./gradlew assembleDebug
```

### 5.2 Install dan Test

1. Install APK ke device/emulator
2. Buka aplikasi
3. Coba register user baru dengan:
   - Nama: Test User
   - Email: test@example.com
   - Password: test123

### 5.3 Verifikasi di Firebase Console

Setelah register berhasil:

1. Cek **Authentication > Users** - seharusnya ada user baru
2. Cek **Firestore Database > Data** - seharusnya ada collection `users`

## Troubleshooting

### Jika Error "FirebaseError: Missing or insufficient permissions"

**Penyebab**: Security rules belum diupdate  
**Solusi**: Pastikan rules sudah dipublish dengan benar

### Jika Error "Auth domain not whitelisted"

**Penyebab**: Domain authorization issue  
**Solusi**:

1. Masuk ke **Authentication > Settings**
2. Cek **Authorized domains**
3. Pastikan domain yang diperlukan sudah ada

### Jika Error "API key not valid"

**Penyebab**: google-services.json tidak sinkron  
**Solusi**:

1. Download ulang google-services.json dari Firebase Console
2. Replace file di folder `app/`
3. Clean dan rebuild project

## Checklist Setup

- [ ] Authentication enabled (Email/Password)
- [ ] Firestore Database created
- [ ] Security rules updated dan published
- [ ] google-services.json di folder app/
- [ ] DEMO_MODE = false di Constants.kt
- [ ] Build berhasil tanpa error
- [ ] Test register user baru
- [ ] Verifikasi data di Firebase Console

## Status Firebase Project

| Service        | Status          | Location                                                                                   |
| -------------- | --------------- | ------------------------------------------------------------------------------------------ |
| Authentication | ⚠️ Perlu enable | [Go to Auth](https://console.firebase.google.com/project/fitnessin-c7cfc/authentication)   |
| Firestore      | ⚠️ Perlu setup  | [Go to Firestore](https://console.firebase.google.com/project/fitnessin-c7cfc/firestore)   |
| Rules          | ⚠️ Perlu update | [Go to Rules](https://console.firebase.google.com/project/fitnessin-c7cfc/firestore/rules) |

---

## Quick Links

- 🔗 [Firebase Console - Project Overview](https://console.firebase.google.com/project/fitnessin-c7cfc)
- 🔗 [Authentication Setup](https://console.firebase.google.com/project/fitnessin-c7cfc/authentication)
- 🔗 [Firestore Database](https://console.firebase.google.com/project/fitnessin-c7cfc/firestore)
- 🔗 [Project Settings](https://console.firebase.google.com/project/fitnessin-c7cfc/settings/general)

Setelah semua langkah ini selesai, aplikasi Anda akan berfungsi penuh dengan authentication dan database yang real!
