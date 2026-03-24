# EXAM-PROTECT — Security Patch Plan

## Prioritas Perbaikan
1) Hapus PIN backdoor (critical)  
2) Perketat allowlist & skema (high)  
3) Hardening WebView/WKWebView (high)  
4) Validasi policy di backend (medium)  
5) Perkuat UX & penalti pelanggaran (medium)  
6) Siapkan migrasi launch token HS256→RS256 (medium)  
7) Granular rate limiting (low)

## Patch yang Diterapkan
- Android
  - WebView: aktifkan Safe Browsing (API≥26) dan mixed content “NEVER_ALLOW”.
  - Filter skema tambahan: about, data, blob, content, chrome.
  - Hapus backdoor teacher PIN “000000”; exit hanya jika PIN tidak diwajibkan.
- iOS
  - UserScript di atDocumentStart untuk nonaktif selection/callout.
  - Filter skema tambahan: blob, content, about.
- Backend
  - Validasi policy: launchUrl wajib http/https; allowedDomains tidak boleh kosong.
  - Rate limit global tetap; disarankan granuler di fase berikut.

## Sisa Risiko (BYOD)
- iOS: tidak bisa blok tombol Home/screenshot; hanya deteksi dan penalti.
- Android: tidak bisa force LockTask di BYOD; split screen dapat terjadi di beberapa OEM.
- Device kedua: tidak dapat dicegah OS-level.

## Langkah Lanjutan
- JWT RS256 untuk launch token + JWKS endpoint.
- Tambah endpoint verifikasi Teacher PIN (server-side) dan integrasi mobile.
- Tambah retry/backoff dan idempotency key untuk heartbeat/violations.
- Tambah audit logging pada semua mutasi admin, dan dashboard live sessions.

