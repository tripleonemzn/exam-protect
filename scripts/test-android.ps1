Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

if (Test-Path ".\android-app\app\gradlew.bat" -or Test-Path ".\android-app\gradlew.bat") {
  Push-Location android-app
  try {
    .\gradlew.bat :app:assembleDebug
    .\gradlew.bat :app:testDebugUnitTest
  } finally {
    Pop-Location
  }
} else {
  Write-Warning "Gradle wrapper belum tersedia. Buka folder 'android-app' di Android Studio dan jalankan Sync Project with Gradle Files, lalu build 'assembleDebug'."
}
