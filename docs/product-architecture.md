# EXAM PROTECT — Arsitektur Produk (Android Kotlin, iOS Swift)

## Tujuan
- Menjadi “secure exam shell” native yang membuka ujian web (Google Forms, Moodle, LMS kustom) di dalam aplikasi.
- Membatasi perilaku pengguna saat ujian sesuai kemampuan resmi masing-masing OS.
- Memisahkan mode operasi dan ekspektasi kemampuan:
  1) Android BYOD (tanpa device-owner)  
  2) Android managed kiosk / device-owner  
  3) iOS BYOD (App Store, non-supervised)  
  4) iOS supervised / managed device (MDM)

> Prinsip: realistis, tanpa mengklaim kemampuan yang tidak didukung OS.

## Gambaran Besar Arsitektur
- Mobile app (Android/iOS) berisi “Exam Web Shell” berbasis WebView/WKWebView dengan kontrol navigasi ketat.
- Backend menyediakan signed exam configuration, session lifecycle, dan pelaporan pelanggaran.
- Kebijakan (policy) dievaluasi on-device (runtime enforcement) dan divalidasi server-side lewat heartbeat & pelaporan.

Komponen utama:
- ExamShell (Android WebView / iOS WKWebView) dengan kontrol:
  - Whitelist domain (`allowedDomains`) dan blok navigasi eksternal.
  - Kontrol clipboard, download/upload sesuai kebijakan.
  - Deteksi event yang relevan: backgrounding, screen capture (jika tersedia), key events (Android).
- SessionManager: memulai, memelihara (heartbeat), menyelesaikan sesi.
- ComplianceMonitor: mengumpulkan sinyal pelanggaran (app switch, keluar fullscreen/kiosk, screen capture, overlay/tapjacking) sesuai dukungan OS.
- SecureConfig: menerima dan memverifikasi signed config (JWS/JWT) dari backend dengan key pinning.
- Telemetry: mengirim event pelanggaran/health ke backend.

## Alur Pengguna (MVP)
1. Siswa menempelkan link ujian (Google Forms/Moodle/URL kustom) atau memindai QR dari pengajar.
2. Aplikasi meminta signed exam configuration ke backend (mengandung `launchUrl`, `allowedDomains`, dll.).
3. Aplikasi memeriksa dukungan perangkat & mode (BYOD/managed), menampilkan peringatan kemampuan & residual risk.
4. App membuka WebView/WKWebView ke `launchUrl` dengan kebijakan dieksekusi.
5. Selama ujian: heartbeat ke server, pelaporan pelanggaran, dan enforcement lokal (mis. `FLAG_SECURE` di Android).
6. Ujian selesai: app menutup sesi, kirim ringkasan pelanggaran.

## Policy/Config Model (v1)
Contoh JSON policy terkirim dari backend (ditandatangani JWS RS256):
```json
{
  "version": 1,
  "examId": "EX-2026-01",
  "launchUrl": "https://forms.gle/abc123",
  "allowedDomains": ["forms.gle", "docs.google.com", "accounts.google.com"],
  "blockExternalNavigation": true,
  "enableCopyPaste": false,
  "enableDownloads": false,
  "enableUploads": true,
  "heartbeatSeconds": 15,
  "teacherPinRequired": false,
  "maxBackgroundSeconds": 0,
  "violationThreshold": 3,
  "managedMode": "auto|require|forbid",
  "branding": {
    "title": "EXAM PROTECT",
    "primaryColor": "#1144DD",
    "logoUrl": "https://cdn.example.com/logo.png"
  },
  "iat": 1710000000,
  "exp": 1710003600,
  "kid": "examprotect-key-1"
}
```
Catatan:
- `managedMode`: 
  - `auto` → gunakan kemampuan tertinggi yang tersedia (Android: aktifkan LockTask jika device-owner; iOS: sarankan Guided Access/supervised jika tersedia).  
  - `require` → tolak mulai jika tidak dalam mode managed (kiosk/device-owner atau iOS supervised single-app).  
  - `forbid` → paksa non-managed (mis. latihan/tryout).
- `maxBackgroundSeconds`: 0 artinya langsung pelanggaran saat app tidak aktif.
- Semua config ditandatangani; aplikasi memverifikasi tanda tangan (key pinning).

## Enforcement Per Platform & Mode (Ringkas)
### Android BYOD
- Tidak bisa memaksa LockTask. Gunakan:
  - `FLAG_SECURE` mencegah screenshot/recording pada app.
  - Non-resizable activity (meminimalisir split screen, tidak 100%).
  - Deteksi app background/switch → pelanggaran.
  - WebViewClient untuk memblokir domain non-whitelist & intent ke browser.
  - Nonaktifkan download manager, clipboard (best effort).

### Android Managed (Device Owner/Kiosk)
- LockTask Mode (COSU): blokir Home/Recent, batasi status bar (DevicePolicyManager).
- Nonaktifkan screen capture via DPM, atur daftar aplikasi diizinkan.
- Kontrol notifikasi/status bar (setStatusBarDisabled di versi yang didukung OEM/Android Enterprise).
- Enforcement jauh lebih kuat.

### iOS BYOD
- Tidak ada cara resmi memblokir Home/Recent atau notifikasi OS.
- WKWebView + `WKContentRuleList` untuk whitelist domain.
- Deteksi screen recording via `UIScreen.main.isCaptured` dan tanggapi (overlay blur); tidak mencegah screenshot sepenuhnya.
- Deteksi background (scene resign active) → pelanggaran sesuai `maxBackgroundSeconds`.
- Tidak bisa memaksa Single-App Mode; Guided Access perlu tindakan pengguna.

### iOS Supervised/Managed
- Dengan MDM: Single App Mode / Autonomous Single App Mode (ASAM) memungkinkan “kiosk” tingkat OS.
- Dapat memblokir screen capture pada tingkat kebijakan MDM untuk seluruh perangkat/app.
- Masih tidak bisa mencegah “second-device cheating”.

## Keputusan Teknis
- Native stack: Kotlin (Android), Swift (iOS).
- Web engine:
  - Android: `android.webkit.WebView` kustom; aktifkan Safe Browsing, matikan file access yang tidak perlu.
  - iOS: `WKWebView` + `WKNavigationDelegate` + `WKContentRuleList` untuk blocklist/allowlist.
- Security:
  - `FLAG_SECURE` (Android), `isCaptured` detection (iOS), `UIApplication.userDidTakeScreenshotNotification` logging.
  - Key pinning (public key) untuk verifikasi JWS.
  - Integrity (fase 2): Play Integrity API, Apple App Attest/DeviceCheck.
- State & arsitektur:
  - Android: Clean Architecture + MVVM, Coroutines/Flow, Hilt DI.
  - iOS: MVVM + Coordinator, Swift Concurrency, Combine optional, Dependency Injection ringan.

## Telemetri dan Pelanggaran
Event minimal yang dicatat:
- `APP_BACKGROUND`, `APP_FOCUS_LOSS`, `SCREEN_CAPTURE_DETECTED`, `SCREENSHOT_TAKEN`, `EXTERNAL_NAV_ATTEMPT`, `SPLIT_SCREEN_DETECTED` (Android best effort), `OVERLAY_DETECTED` (Android), `DEBUGGER_ATTACHED`, `ROOT_JAILBREAK_SUSPECTED`.
- Setiap event menaikkan counter; jika melewati `violationThreshold`, sesi diakhiri lokal dan dilaporkan.

## UX & Komunikasi Risiko
- Menampilkan mode kemampuan saat sebelum mulai (BYOD vs Managed).
- Pada iOS BYOD, jelaskan keterbatasan: tidak bisa blok notifikasi, screenshot masih mungkin; sarankan Guided Access.
- Pada Android BYOD, tekankan best-effort (tidak bisa blok Home), tapi screenshot/recording diblok oleh `FLAG_SECURE`.
- Pada mode managed, tampilkan indikator “Kiosk Mode aktif”.

## Logging & Privasi
- Simpan minimal yang diperlukan (timestamp, jenis pelanggaran, domain yang dikunjungi).
- Tidak menyimpan isi jawaban.
- Patuh pada kebijakan privasi dan ketentuan platform (App Store/Play).

## Keandalan
- Heartbeat dengan `heartbeatSeconds` untuk status hidup dan tingkat pelanggaran.
- Retry eksponensial pada jaringan; buffer event offline dan kirim saat jaringan tersedia.

