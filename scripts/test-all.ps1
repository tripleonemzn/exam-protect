Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "==> Start backend (Docker Compose)"
.\scripts\dev-backend.ps1

Write-Host "==> Test backend endpoints"
.\scripts\test-backend.ps1

Write-Host "==> Build & test Android (jika gradlew tersedia)"
.\scripts\test-android.ps1

Write-Host "==> iOS test (placeholder, butuh macOS)"
.\scripts\test-ios.ps1

Write-Host "Selesai menjalankan test-all.ps1"
