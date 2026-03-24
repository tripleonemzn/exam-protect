## Self-Hosted OTA Setup

1. Build APK debug/release:
   - Debug: android-app/app/build/outputs/apk/debug/app-debug.apk
   - Release (sign terlebih dulu): android-app/app/build/outputs/apk/release/app-release.apk
2. Salin APK ke backend/public/downloads dan beri nama `app-debug.apk` atau `app-release.apk`.
3. Update handler /v1/client/update jika menggunakan nama file berbeda atau domain produksi.
4. Jalankan ulang backend:
   - docker compose up -d --build backend
5. Buka app → notifikasi update akan muncul jika versionCode server lebih besar dari versi app saat ini.

Catatan:
- Untuk distribusi publik, gunakan domain produksi (ganti `http://localhost:8080` dengan domain Anda).
- Anda dapat mengelola beberapa channel rilis dengan beberapa file dan parameter di UpdateResp.
