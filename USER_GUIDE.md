# Aplikasi Kebugaran Inti - User Guide

## Deskripsi Aplikasi

Aplikasi Kebugaran Inti adalah aplikasi Android fitness yang membantu pengguna mencapai target berat badan ideal melalui:

- Perhitungan kalori dan makronutrien yang akurat
- Rekomendasi makanan menggunakan AI
- Tracking progress berat badan
- Arsitektur MVVM dengan Firebase & Gemini AI

## Flow Aplikasi

### 1. Authentication Screen (Halaman Login/Register)

**Lokasi**: `AuthScreen.kt`

Fitur:

- **Tab Login**: Masuk dengan email dan password
- **Tab Register**: Daftar dengan nama, email, password, dan konfirmasi password
- **Validasi Real-time**:
  - Email format validation
  - Password minimal 6 karakter
  - Nama minimal 2 karakter
  - Password confirmation matching
- **Mode Demo**: Untuk testing tanpa setup Firebase
- **Error Handling**: Tampilan error yang user-friendly

Cara Penggunaan:

1. Pilih tab "Masuk" atau "Daftar"
2. Isi form dengan data yang valid
3. Tekan tombol "Masuk" atau "Daftar"
4. Untuk demo, bisa langsung tekan "Masuk Mode Demo"

### 2. Profile Setup Screen

**Lokasi**: `ProfileSetupScreen.kt`

Fitur:

- Input data personal (nama, umur, gender)
- Input data fisik (tinggi, berat saat ini, target berat)
- Pilihan tingkat aktivitas
- Pilihan goal (turun/naik/maintain berat badan)
- Validasi comprehensive untuk semua input

Data yang diperlukan:

- **Nama**: Minimal 2 karakter
- **Umur**: 15-80 tahun
- **Gender**: Male/Female
- **Tinggi**: 100-250 cm
- **Berat Saat Ini**: 30-300 kg
- **Target Berat**: 30-300 kg
- **Tingkat Aktivitas**: Sedentary s/d Extremely Active
- **Goal**: Lose/Maintain/Gain weight

### 3. Dashboard Screen

**Lokasi**: `DashboardScreen.kt`

Fitur:

- **Overview Card**: Ringkasan kalori dan makronutrien harian
- **Progress Chart**: Grafik perkembangan berat badan
- **Food Recommendation**: Saran makanan dari Gemini AI
- **Quick Actions**: Input berat badan baru
- **Refresh**: Update rekomendasi makanan

## Komponen Teknis

### Architecture

- **MVVM Pattern**: Model-View-ViewModel
- **Dependency Injection**: Hilt
- **State Management**: StateFlow & Compose State
- **Navigation**: Single Activity with Compose

### Database (Firebase Firestore)

```
users/{userId}
├── uid: String
├── email: String
├── name: String
├── age: Int
├── gender: String
├── height: Int
├── currentWeight: Float
├── targetWeight: Float
├── activityLevel: String
├── weightGoal: String
└── createdAt: Timestamp

progress/{userId}/entries/{entryId}
├── weight: Float
├── date: String
├── notes: String
└── timestamp: Timestamp
```

### Calculations

**BMR** (Basal Metabolic Rate):

- Formula: Mifflin-St Jeor Equation
- Male: BMR = 10 × weight + 6.25 × height - 5 × age + 5
- Female: BMR = 10 × weight + 6.25 × height - 5 × age - 161

**TDEE** (Total Daily Energy Expenditure):

- BMR × Activity Factor
- Sedentary: ×1.2
- Lightly Active: ×1.375
- Moderately Active: ×1.55
- Very Active: ×1.725
- Extremely Active: ×1.9

**Macronutrients**:

- Protein: 1.8g per kg body weight
- Fat: 25% of total calories
- Carbs: Remaining calories

**Calorie Adjustment**:

- Weight Loss: TDEE - 400 calories
- Weight Gain: TDEE + 400 calories
- Maintain: TDEE

### AI Integration

**Gemini AI** untuk food recommendations:

- Input: User profile + nutrition requirements
- Output: Personalized food suggestions
- Fallback: Demo recommendations jika API tidak tersedia

## Mode Demo vs Production

### Demo Mode (Default)

- `Constants.DEMO_MODE = true`
- Firebase operations di-mock
- Data disimulasi tanpa server
- Gemini AI menggunakan content demo
- Cocok untuk testing dan development

### Production Mode

- `Constants.DEMO_MODE = false`
- Firebase project configuration diperlukan
- Gemini API key diperlukan
- Real authentication dan database
- Lihat `FIREBASE_PRODUCTION_SETUP.md` untuk setup

## Testing

### Unit Tests

Location: `app/src/test/java/`

- `NutritionCalculatorTest.kt`: Test perhitungan kalori dan makronutrien
- Coverage: BMR, TDEE, macronutrients, calorie adjustments

Run tests:

```bash
./gradlew test
```

### Manual Testing

1. **Authentication Flow**:

   - Test register user baru
   - Test login user existing
   - Test validation errors
   - Test demo mode

2. **Profile Setup**:

   - Test semua validasi input
   - Test save profile
   - Test different user scenarios

3. **Dashboard**:
   - Test nutrition calculations
   - Test weight entry
   - Test food recommendations
   - Test progress tracking

## Error Handling

### Network Errors

- Automatic retry untuk Firebase operations
- Fallback ke demo data jika Firebase gagal
- User-friendly error messages

### Validation Errors

- Real-time validation di form fields
- Clear error messages dalam bahasa Indonesia
- Visual feedback (red border, error text)

### App Crashes

- Try-catch blocks di critical operations
- Graceful degradation ke demo mode
- Logging untuk debugging

## Performance Optimization

### Compose Performance

- `remember` untuk expensive calculations
- `LaunchedEffect` untuk side effects
- State hoisting untuk reusability

### Firebase Performance

- Efficient queries dengan indexing
- Pagination untuk large datasets
- Offline capability dengan caching

### Memory Management

- Proper coroutine scope management
- StateFlow instead of LiveData
- Efficient image loading

## Security

### Authentication

- Firebase Auth untuk user management
- Email/password authentication
- Secure token management

### Database Security

- Firestore security rules (lihat `firestore.rules`)
- User dapat akses data sendiri saja
- Input validation di client dan server

### API Security

- API keys tidak di-hardcode di production
- Environment variables untuk sensitive data
- HTTPS only communications

## Deployment

### Debug Build

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### Play Store Deployment

1. Generate signed APK/Bundle
2. Update version code/name
3. Test di internal testing
4. Submit untuk review

## Troubleshooting

### Common Issues

**Build Errors**:

- Clean project: `./gradlew clean`
- Invalidate caches di Android Studio
- Check Kotlin/Gradle versions

**Firebase Issues**:

- Verify `google-services.json`
- Check Firebase project configuration
- Enable Authentication & Firestore

**UI Issues**:

- Check Compose version compatibility
- Verify Material Design 3 usage
- Test di different screen sizes

**Performance Issues**:

- Enable R8 minification
- Use ProGuard rules
- Profile dengan Android Studio

### Support

Jika ada masalah:

1. Check logs di Logcat
2. Verify configuration files
3. Test dengan demo mode
4. Check documentation di README.md

---

**Version**: 1.0.0  
**Last Updated**: June 2025  
**Minimum Android**: API 24 (Android 7.0)  
**Target Android**: API 34 (Android 14)
