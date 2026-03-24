# EXAM PROTECT — Implementation Roadmap

## Prinsip
- Native only untuk inti secure shell: Android (Kotlin), iOS (Swift).
- Realistis terhadap kemampuan OS; tidak mengklaim blokir yang tidak didukung.
- Fase bertahap: MVP usable, lalu penguatan managed mode & integrasi.

## MVP (Fase 1)
Sasaran: Exam shell berfungsi untuk BYOD dengan batasan realistis, plus jalur menuju managed mode.

Android (BYOD)
- WebView shell dengan:
  - Whitelist domain (`allowedDomains`) dan blok navigasi eksternal.
  - Nonaktifkan download (intersep), kontrol upload (opsional).
  - Nonaktifkan copy/paste (JS/CSS best-effort).
  - `FLAG_SECURE` untuk blokir screen capture.
  - Non-resizable activity untuk mengurangi split screen.
- Session lifecycle:
  - Start/heartbeat/complete + pelaporan pelanggaran (background, external nav attempt).
  - Penegakan `violationThreshold`.
- Config:
  - Verifikasi JWS (key pinning), cache singkat, expiry.

iOS (BYOD)
- WKWebView shell dengan:
  - `WKContentRuleList` untuk whitelist domain.
  - `WKNavigationDelegate` cegah eksternal.
  - Deteksi screen recording (`isCaptured`) → blur/strike; log screenshot notification.
  - Deteksi resign active → strike/end sesuai `maxBackgroundSeconds`.
- Session lifecycle dan config verifikasi identik.

Backend
- Endpoint sesuai `api-contract.md` untuk config, session, heartbeat, violation, complete.
- Admin CRUD policy sederhana (in-memory/DB ringan).
- JWKS publishing untuk key pinning klien.

Rilis dan Operasional
- Logging dasar, observabilitas (request-id).
- Rate limit dasar, retry eksponensial di klien.
- Dokumentasi penggunaan untuk pengajar (membuat kebijakan, menyalin link).

Acceptance Criteria
- Dapat membuka Google Forms/Moodle via whitelist domain tanpa keluar app.
- Android BYOD tidak bisa screenshot konten app; iOS BYOD minimal mendeteksi recording/screenshot dan menindak.
- Pelanggaran app switch terdeteksi dan mempengaruhi skor/penutupan sesi.
- Heartbeat stabil, laporan pelanggaran tersimpan.

## Fase 2
Android Managed (Device Owner/Kiosk)
- Penyediaan provisioning (QR/afw#) untuk Device Owner.
- LockTask Mode, daftar aplikasi diizinkan, disable status bar.
- Always-On VPN opsional, screen capture global off via DPM.

iOS Supervised/Managed
- Integrasi MDM untuk Single App Mode / ASAM di perangkat supervised.
- Kebijakan MDM untuk meminimalkan notifikasi dan screen capture.

Keamanan Lanjut
- Android Play Integrity API dan iOS App Attest/DeviceCheck.
- Tamper detection tambahan, obfuscation, runtime protection.
- Telemetry lebih detail (perubahan ukuran window, network anomalies).

Integrasi LMS
- Template kebijakan untuk Google Forms, Moodle, dan LMS kustom.
- Opsi SSO token exchange agar login tetap di dalam shell.

Non-functional
- Pengukuran performa WebView/WKWebView dan ketersediaan.
- Uji kompatibilitas perangkat dan versi OS populer.

## Fase 3 (Opsional)
- Proctoring kamera/mikrofon opsional (memperhitungkan privasi dan App Store review).
- Integrasi analitik kecurangan berbasis sinyal perilaku.
- Portal pengajar lanjutan dan dashboard pelanggaran.

