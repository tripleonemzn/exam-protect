# Rencana Uji End-to-End (E2E)

## Tujuan
Memastikan alur ujian dari pembuatan policy di backend hingga pembukaan ujian di aplikasi mobile dapat berjalan dengan aman dan realistis.

## Prasyarat
- Backend & Postgres berjalan via Docker Compose
- Sample admin tersedia (seed)
- Sample policy dibuat (examId: sample-exam-001)
- Mock exam page tersedia di /mock/exam

## Skenario Utama
1) Health check backend (expect 200)
2) Admin login (expect token)
3) Create exam policy (expect 200)
4) Get signed config (expect 200 + jws/payload)
5) Start session (expect sessionId, heartbeatSeconds)
6) Heartbeat (expect status ok)
7) Report violation (expect 202)
8) Complete session (expect status completed)
9) Android: buka exam, verifikasi allowlist, blok skema eksternal, nonaktif selection (best-effort), FLAG_SECURE aktif
10) iOS: buka exam, verifikasi allowlist, blok skema eksternal, nonaktif selection (best-effort), deteksi screenshot/recording

## Data Uji
- Admin: username=admin, password=(lihat seed; ganti bila perlu)
- Policy: examId=sample-exam-001, launchUrl=http://localhost:8080/mock/exam, allowedDomains=["localhost"]

## Keberterimaan
- Semua endpoint backend sesuai kontrak
- Android build sukses (assembleDebug), unit tests lulus
- iOS: struktur dapat dibuka di Xcode; unit tests lulus (di macOS)
- Risiko residual BYOD dikomunikasikan jelas

