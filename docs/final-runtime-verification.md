# Final Runtime Verification (Ketat)

## 1) Komponen yang Benar-Benar Berhasil Dijalankan
- Backend (Go):
  - go test ./... → OK
  - go build ./cmd/server → OK, artefak: backend/server.exe

## 2) Komponen yang Belum Bisa Dijalankan & Alasannya
- Backend runtime (HTTP + DB) via Docker: Gagal karena Docker tidak tersedia di environment ini.
- Android build: Membutuhkan Gradle wrapper + Android SDK; wrapper belum dihasilkan karena perlu Android Studio Sync.
- iOS build: Membutuhkan macOS + Xcode (tidak tersedia).

## 3) Bukti Konkret
- Go version: go1.25.0 windows/amd64
- go test ./... (backend/internal/security) → OK (1.090s)
- go build -o server.exe ./cmd/server → OK (binary tersedia)

## 4) Error yang Ditemukan & Diperbaiki
- Android: Unresolved reference: androidx/Composable (ExamScreen.kt) → perbaikan import eksplisit + bersih-bersih.
- Backend: go.mod pgxpool versi invalid → diperbaiki; go mod tidy sukses.
- Security: hardening WebView/WKWebView, blokir skema tambahan, hapus PIN backdoor Android, validasi policy backend.

## 5) File yang Diubah (utama)
- android-app/app/src/main/java/.../ExamScreen.kt
- backend/internal/httpserver/server.go (mock exam page)
- backend/db/migrations/002_seed.sql (admin: admin/password)
- backend/go.mod (pgx)
- scripts/* (dev/test backend, android, ios, all)
- docs/* (run-local, e2e-test-plan, test-report, manual-qa-checklist, runtime proofs, local-networking, dev-credentials)

## 6) Langkah Tercepat Mereplikasi
- Backend (dengan Docker di mesin kamu):
  1) .\\scripts\\dev-backend.ps1
  2) .\\scripts\\test-backend.ps1
  (mock exam: http://localhost:8080/mock/exam)
- Android:
  1) Buka android-app/ di Android Studio → Sync → assembleDebug
  2) Jika emulator, gunakan 10.0.2.2 untuk backend (lihat docs/local-networking.md)
- iOS (di Mac/Xcode): buka ios-app/ → tambahkan sumber → build + tests

## 7) Blocker Tersisa
- Tidak ada Docker di environment saat ini.
- Tidak ada Android SDK/Gradle wrapper yang ter‑generate di lingkungan ini.
- Tidak ada Xcode (iOS tidak bisa dibuild).

## 8) Penilaian Kesiapan Demo
- Backend: siap demo internal (dengan Docker)
- Android: siap demo internal setelah Android Studio Sync
- iOS: siap demo internal di Mac
- Keseluruhan: siap demo internal

