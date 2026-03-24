# Android Build Proof (Windows, emulator backend 10.0.2.2)

## A. Audit Environment Android
- java -version → 17.0.12 (Oracle, LTS)
- where java → (path not listed; JDK terdeteksi via java -version)
- ANDROID SDK:
  - ANDROID_HOME not set
  - ANDROID_SDK_ROOT not set
  - Found SDK at C:\Users\tripleone\AppData\Local\Android\Sdk
- adb/emulator:
  - adb version → command not found
  - where adb → not found
  - emulator -list-avds → command not found
  - where emulator → not found

## B. Project Cleanup & Config
- Gradle wrapper di-root (generated via Docker Gradle): gradlew, gradlew.bat, gradle/wrapper/*
- local.properties:
  - sdk.dir=C:\Users\tripleone\AppData\Local\Android\Sdk
- AndroidManifest.xml:
  - hapus icon references yang hilang
  - networkSecurityConfig aktif
- network_security_config.xml:
  - cleartextTrafficPermitted untuk domain 10.0.2.2
- Sample policy (Android emulator):
  - assets/sample-policy.json → launchUrl=http://10.0.2.2:8080/mock/exam; allowedDomains=["10.0.2.2"]
- Build config:
  - BACKEND_BASE_URL= http://10.0.2.2:8080 (env var saat build)

## C. Build Debug
- Command:
  - .\\gradlew.bat :app:assembleDebug
- Result:
  - SUCCESS
  - APK: android-app/app/build/outputs/apk/debug/app-debug.apk

## D. Unit Tests
- Command:
  - .\\gradlew.bat :app:testDebugUnitTest
- Result:
  - SUCCESS (policy parsing test diperbaiki dengan KotlinJsonAdapterFactory)

## E. Catatan Emulator
- Emulator belum terdeteksi via CLI (adb/emulator tidak ada di PATH).
- APK tersedia untuk diinstall manual jika emulator/real device tersedia.

## F. Backend Verification (untuk Android)
- Backend Docker tetap berjalan di http://localhost:8080
- Endpoint verifikasi cepat:
  - GET /v1/health → 200
  - GET /mock/exam → 200

## Status Android
- Build: Hijau (APK debug berhasil)
- Runtime Emulator: Kuning (tools emulator/adb belum tersedia di PATH)
