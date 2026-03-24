param(
  [switch]$Rebuild
)
Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Write-Host "==> Memulai Docker Compose untuk Postgres + Backend"
if ($Rebuild) {
  docker compose build
}
docker compose up -d

Write-Host "==> Menunggu database siap (tcp:5432)..."
Start-Sleep -Seconds 3

Write-Host "==> Cek health endpoint"
try {
  Invoke-RestMethod -Method GET http://localhost:8080/v1/health -TimeoutSec 5 | Out-Host
} catch {
  Write-Warning "Backend belum siap. Tunggu beberapa detik lalu ulangi cek health."
}

Write-Host "Selesai. Backend seharusnya aktif di http://localhost:8080"
