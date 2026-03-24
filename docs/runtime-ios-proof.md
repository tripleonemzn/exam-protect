# Bukti Runtime iOS (Lokal)

## Kondisi Lingkungan
- Build iOS memerlukan macOS + Xcode. Lingkungan ini tidak menyediakan Xcode/simulator.

## Status Kode
- Struktur UIKit + WKWebView tersedia, unit tests untuk parsing policy dan allowlist tersedia.
- Hardening: nonaktif selection via WKUserScript di atDocumentStart; pemblokiran skema tel/sms/mailto/itms-apps/itms/maps/file/data/blob/content/about; deteksi screenshot/recording/external display.

## Langkah Build di Mac
1) Buka ios-app/ di Xcode, buat target app (jika belum) lalu tambahkan folder ExamProtect/* ke target.
2) Build & run di simulator; jalankan tests di ExamProtectTests.
3) Gunakan backend lokal (http://localhost:8080) atau IP/10.0.2.2 sesuai kebutuhan; lihat docs/local-networking.md.

## Catatan
- Teacher PIN saat ini demo di iOS; rekomendasi: verifikasi server-side pada produksi.

