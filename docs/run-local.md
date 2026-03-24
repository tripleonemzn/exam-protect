# Menjalankan Sistem Secara Lokal

## Persyaratan
- Docker Desktop (untuk backend + Postgres)
- Windows PowerShell 5/7 atau bash (WSL/macOS/Linux)
- Untuk Android: Android Studio (JDK 17, SDK 34)
- Untuk iOS: macOS + Xcode (opsional di Windows)

## Langkah Cepat (Windows PowerShell)
1) dari root repo:
```
.\scripts\dev-backend.ps1
```
2) uji endpoint backend:
```
.\scripts\test-backend.ps1
```
3) build Android (jika gradlew tersedia):
```
.\scripts\test-android.ps1
```
4) iOS (placeholder, jalankan di Mac):
```
.\scripts\test-ios.ps1
```
5) jalankan semua:
```
.\scripts\test-all.ps1
```

## Langkah Cepat (bash)
```
./scripts/dev-backend.sh
./scripts/test-backend.sh
```

## Catatan
- Backend berjalan di http://localhost:8080
- Mock exam page: http://localhost:8080/mock/exam
- Sample policy: dibuat oleh test-backend script (examId: sample-exam-001)

