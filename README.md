# EXAM-PROTECT (Android MVP)

Instruksi singkat:
- Buka folder `android-app/` di Android Studio (Hedgehog atau lebih baru).
- Pastikan JDK 17 dan Android SDK 34 terpasang.
- Secara default, build debug menggunakan `assets/sample-policy.json`. Untuk backend, set environment variable `BACKEND_BASE_URL` sebelum build.

Fitur utama (MVP):
- Kotlin + Jetpack Compose + Hilt + Coroutines/Flow.
- WebView shell dengan allowlist domain, blok eksternal (intent://, market://, tel:, sms:, mailto:, file:// dll).
- `FLAG_SECURE` di layar ujian, nonaktifkan long-press/copy-paste (best effort).
- Deteksi background/focus via lifecycle; pencatatan pelanggaran ke Room.
- Abstraksi kiosk: `KioskController` akan memanggil `startLockTask` hanya jika perangkat telah diprovisikan sebagai device-owner dan aplikasi di-allowlist. (Perlu provisioning terpisah).
- Heartbeat placeholder (siap dihubungkan ke backend sesuai `docs/api-contract.md`).

Catatan BYOD vs Managed:
- BYOD tidak bisa memaksa LockTask/Home/Recent. Pencegahan bersifat best-effort.
- Managed (Device Owner/Kiosk) memungkinkan LockTask dan kebijakan lebih ketat; memerlukan provisioning perangkat via Android Enterprise/MDM.

---

# EXAM-PROTECT (iOS MVP)

Struktur:
- ios-app/ExamProtect/ — Sumber aplikasi UIKit + WKWebView
- ios-app/ExamProtectTests/ — Unit tests
- Policy lokal debug: ios-app/ExamProtect/Resources/sample-policy.json

Cara membuka:
- Buat project Xcode baru atau gunakan project generator (opsional). Kode sumber sudah dipisah per layer:
  - AppDelegate.swift, Presentation/*, Networking/*, Domain/*, Security/*, Resources/*

Fitur utama (MVP):
- WKWebView secure shell: whitelist domain, blok skema eksternal (tel:, sms:, mailto:, itms-apps:, file:, dll), blok popup/window.open.
- Best-effort nonaktif selection/copy via CSS injection, nonaktif link preview.
- Deteksi screenshot (UIApplication.userDidTakeScreenshotNotification), screen recording (UIScreen.isCaptured), app inactive/background, external display.
- Guided Access UX (requestGuidedAccessSession) — membutuhkan tindakan pengguna pada BYOD.
- Teacher PIN dialog (demo PIN: 000000).
- Policy parsing (Codable) dan fallback JSON lokal; API client siap untuk BACKEND_BASE_URL.

Keterbatasan iOS BYOD:
- Tidak bisa memblokir tombol Home/App Switcher/Notifikasi OS.
- Screenshot tidak bisa diblokir, hanya dideteksi; screen recording terdeteksi via isCaptured.
- Single-App Mode memerlukan perangkat supervised + MDM (di luar kemampuan app App Store biasa).
