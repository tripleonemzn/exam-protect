# Android Runtime Proof v2 (PIN → Start Session → Heartbeat)

- APK: android-app/app/build/outputs/apk/debug/app-debug.apk
- Emulator: Medium_Phone_API_36.1 → emulator-5554 device
- Backend: Docker → http://localhost:8080, app base → http://10.0.2.2:8080

## Gate PIN
- PIN: 123456 (dev), disimpan ke DataStore saat masuk
- Auto-lanjut: jika user idle 1.2s, Gate akan set PIN default dan masuk ke exam

## Start Session
- ExamScreen memanggil startSession:
  - deviceId: emu-android-001
  - examId: android-emulator-exam
  - mode: BYOD
  - capabilities: { webview: true, screenshotDetection: false }
  - entryPin: 123456
- Backend memverifikasi PIN terhadap entryPinHash di policy (bcrypt) dan mengembalikan sessionId

## Heartbeat
- SessionManager mengirim heartbeat berkala (interval 15s sesuai policy)
- Payload: timestamp, violations=0, events=[], metrics={}, nonce

## Log Bukti
- android-logcat.txt: disimpan di logs/android-logcat.txt (runtime emulator)
- backend-runtime.log: disimpan di logs/backend-runtime.log (logger chi aktif)

## Catatan
- Logging HTTP di app aktif (Level BODY melalui BuildConfig.ENABLE_HTTP_LOGGING); jika belum terlihat, periksa filter logcat dan jalankan ulang app.
- Endpoint logger backend: chi middleware Logger telah diaktifkan, sehingga request dicatat ke docker logs.
