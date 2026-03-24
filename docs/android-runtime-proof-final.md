# Android Runtime Proof (Emulator → Backend Docker)

## Emulator & Tools
- adb version → OK (platform-tools aktif)
- emulator -list-avds → Medium_Phone_API_36.1
- adb devices → emulator-5554 device

## Install & Launch
- Install APK:
  - adb install -r android-app/app/build/outputs/apk/debug/app-debug.apk → Success
- Start app:
  - adb shell am start -n com.examprotect.app/.MainActivity → Started

## Policy & Networking
- ExamScreen policy (debug) disetel untuk emulator:
  - launchUrl: http://10.0.2.2:8080/mock/exam
  - allowedDomains: ["10.0.2.2"]
- network_security_config: cleartextTrafficPermitted untuk 10.0.2.2
- Backend Docker di host: http://localhost:8080 (publish port 8080)

## Bukti Log
- adb logcat (ringkas): proses com.examprotect.app diluncurkan, WebView update aktif.
- file log:
  - logs/android-logcat.txt
  - logs/backend-runtime.log

## Status Runtime
- App terinstall dan diluncurkan di emulator.
- Exam page diarahkan ke backend Docker melalui 10.0.2.2.
- Catatan: heartbeat/violation ke backend akan diaktifkan saat integrasi network di Session layer digunakan; saat ini fokus pembuktian pemuatan exam page dari backend dan konfigurasi networking emulator.
