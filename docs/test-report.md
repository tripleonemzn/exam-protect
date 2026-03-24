# Laporan Uji (Sementara)

## Backend
- Health: OK
- Login admin: OK (token diterima)
- Create policy: OK
- Get config: OK
- Start session: OK
- Heartbeat: OK
- Violation: OK
- Complete: OK

## Android
- Build: Siap, perlu Gradle wrapper/Android Studio Sync pada mesin penguji
- Unit tests: Siap dijalankan via Gradle
- Manual checks: domain allowlist, blokir skema eksternal, FLAG_SECURE, focus-loss logging

## iOS
- Build: memerlukan macOS + Xcode; struktur project tersedia
- Unit tests: tersedia

## Catatan
- BYOD iOS tidak full lockdown; screenshot/recording hanya terdeteksi
- Android BYOD tidak memaksa LockTask

