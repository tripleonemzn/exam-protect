# Bukti Runtime Android (Lokal)

## Kondisi Lingkungan
- Build Android membutuhkan Gradle wrapper, JDK 17, dan Android SDK 34 (Android Studio).
- Di lingkungan ini, wrapper belum tersedia dan Android SDK tidak terdeteksi via CLI.

## Status Kode
- Masalah linter "Unresolved reference: androidx/Composable" pada ExamScreen.kt telah diperbaiki.
- Hardening WebView (Safe Browsing, Mixed Content NEVER_ALLOW, filter skema) sudah diaktifkan.

## Langkah Build di Mesin Dev
1) Buka android-app/ di Android Studio.
2) Sync Project with Gradle Files (menghasilkan gradle wrapper).
3) Build → assembleDebug (artefak: app/build/outputs/apk/debug/app-debug.apk).
4) Jalankan unit tests: testDebugUnitTest.

## Catatan
- Skrip Windows untuk build & tests: scripts/test-android.ps1 (menjalankan gradlew jika tersedia).
- Jika emulator digunakan, gunakan 10.0.2.2 untuk mengakses backend lokal (lihat docs/local-networking.md).

