# 📊 Struktur Firestore Database - Aplikasi Kebugaran Inti

## 🎯 Important: Collections Dibuat Otomatis!

**Anda TIDAK perlu membuat collections atau documents secara manual**. Semua akan dibuat otomatis oleh aplikasi saat:

1. User pertama kali register
2. User setup profile
3. User menambah data berat badan

## 📁 Struktur Database Yang Akan Terbentuk

### Collection: `users`

Dibuat saat user menyelesaikan profile setup.

```
users/
├── {userId1}/
│   ├── uid: "firebase_auth_user_id_1"
│   ├── email: "user1@example.com"
│   ├── name: "John Doe"
│   ├── age: 25
│   ├── gender: "PRIA"
│   ├── height: 175
│   ├── currentWeight: 70.0
│   ├── targetWeight: 75.0
│   ├── activityLevel: "MODERATELY_ACTIVE"
│   ├── weightGoal: "GAIN"
│   └── createdAt: Timestamp
│
├── {userId2}/
│   ├── uid: "firebase_auth_user_id_2"
│   ├── email: "user2@example.com"
│   ├── name: "Jane Smith"
│   ├── age: 30
│   ├── gender: "WANITA"
│   ├── height: 160
│   ├── currentWeight: 60.0
│   ├── targetWeight: 55.0
│   ├── activityLevel: "LIGHTLY_ACTIVE"
│   ├── weightGoal: "LOSE"
│   └── createdAt: Timestamp
```

### Collection: `progress`

Dibuat saat user menambah entry berat badan pertama kali.

```
progress/
├── {userId1}/
│   └── entries/
│       ├── {entryId1}/
│       │   ├── weight: 70.5
│       │   ├── date: "2025-06-11"
│       │   ├── notes: "Feeling good today"
│       │   └── timestamp: Timestamp
│       │
│       ├── {entryId2}/
│       │   ├── weight: 71.0
│       │   ├── date: "2025-06-12"
│       │   ├── notes: "After workout"
│       │   └── timestamp: Timestamp
│
├── {userId2}/
│   └── entries/
│       ├── {entryId3}/
│       │   ├── weight: 59.8
│       │   ├── date: "2025-06-11"
│       │   ├── notes: "Good progress"
│       │   └── timestamp: Timestamp
```

## 🔄 Alur Pembuatan Data

### 1. User Register (Authentication)

- **Location**: Firebase Authentication
- **Action**: Aplikasi otomatis membuat user account
- **Result**: User bisa login dengan email/password

### 2. Profile Setup

- **Location**: Firestore `users/{userId}`
- **Action**: Aplikasi otomatis membuat document user
- **Code**: `FirebaseRepository.saveUserProfile()`

```kotlin
// Kode yang akan otomatis dijalankan
firestore.collection("users")
    .document(user.uid)
    .set(user)
```

### 3. Weight Entry

- **Location**: Firestore `progress/{userId}/entries/{entryId}`
- **Action**: Aplikasi otomatis menambah entry baru
- **Code**: `FirebaseRepository.saveWeightEntry()`

```kotlin
// Kode yang akan otomatis dijalankan
firestore.collection("progress")
    .document(userId)
    .collection("entries")
    .add(weightEntry)
```

## 🧪 Test Flow untuk Membuat Data

### Step 1: Register User Pertama

1. Buka aplikasi
2. Tab "Daftar"
3. Isi:
   - Nama: Test User
   - Email: test@example.com
   - Password: test123
4. Tekan "Daftar"

**Result**: User akan dibuat di Firebase Authentication

### Step 2: Setup Profile

1. Isi semua data profile:
   - Umur: 25
   - Gender: Pria
   - Tinggi: 175 cm
   - Berat saat ini: 70 kg
   - Target berat: 75 kg
   - Aktivitas: Moderately Active
   - Goal: Gain Weight
2. Tekan "Simpan Profil"

**Result**: Document akan dibuat di `users/{userId}`

### Step 3: Add Weight Entry

1. Di dashboard, klik "Tambah Berat Badan"
2. Input berat: 70.5 kg
3. Tekan "Simpan"

**Result**: Document akan dibuat di `progress/{userId}/entries/{entryId}`

## 📋 Yang Akan Anda Lihat di Firebase Console

### Sebelum Ada User:

```
Firestore Database > Data
(Empty - no collections)
```

### Setelah User Register + Profile Setup:

```
Firestore Database > Data
├── users (collection)
│   └── abc123xyz (document - user ID)
│       ├── uid: "abc123xyz"
│       ├── email: "test@example.com"
│       ├── name: "Test User"
│       ├── age: 25
│       ├── gender: "PRIA"
│       ├── height: 175
│       ├── currentWeight: 70.0
│       ├── targetWeight: 75.0
│       ├── activityLevel: "MODERATELY_ACTIVE"
│       ├── weightGoal: "GAIN"
│       └── createdAt: June 11, 2025 at 10:30:00 AM UTC+7
```

### Setelah Add Weight Entry:

```
Firestore Database > Data
├── users (collection)
│   └── abc123xyz (document)
│       └── [user data seperti di atas]
│
├── progress (collection)
│   └── abc123xyz (document)
│       └── entries (subcollection)
│           └── def456ghi (document - entry ID)
│               ├── weight: 70.5
│               ├── date: "2025-06-11"
│               ├── notes: ""
│               └── timestamp: June 11, 2025 at 10:35:00 AM UTC+7
```

## 🔐 Security Rules (Sudah Disiapkan)

Rules yang sudah Anda copy akan memastikan:

- User hanya bisa akses data mereka sendiri
- Tidak bisa akses data user lain
- Harus login untuk akses data apapun

```javascript
// Rules yang melindungi data
match /users/{userId} {
  allow read, write: if request.auth != null && request.auth.uid == userId;
}

match /progress/{userId} {
  allow read, write: if request.auth != null && request.auth.uid == userId;
}
```

## ✅ Checklist Firebase Setup

### Di Firebase Console:

- [ ] Authentication → Enable Email/Password ✅
- [ ] Firestore Database → Create database ✅
- [ ] Rules → Update dengan rules baru ✅
- [ ] **JANGAN** buat collections manual ❌

### Di Aplikasi:

- [ ] Register user baru
- [ ] Setup profile lengkap
- [ ] Add weight entry
- [ ] Verify data muncul di Firebase Console

## 🎯 Expected Result

Setelah test flow lengkap, Anda akan melihat:

1. **Authentication → Users**: Ada user dengan email test@example.com
2. **Firestore → Data**: Ada 2 collections:
   - `users` dengan 1 document
   - `progress` dengan 1 document yang memiliki subcollection `entries`

**Semua dibuat otomatis oleh aplikasi - Anda tidak perlu menambah data manual!** 🚀

---

## 🔍 Monitoring Tips

### Realtime Monitoring

Buka Firebase Console di browser, lalu:

1. Buka tab **Firestore Database > Data**
2. Refresh halaman setelah setiap aksi di aplikasi
3. Lihat collections dan documents bertambah otomatis

### Debug Logs

Jika ada masalah, check logs:

```bash
adb logcat | grep -E "(FirebaseRepository|Firestore|Auth)"
```
