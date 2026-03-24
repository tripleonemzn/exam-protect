Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Login-Admin {
  $body = @{ username = "admin"; password = "password" } | ConvertTo-Json
  try {
    $resp = Invoke-RestMethod -Method POST -Uri http://localhost:8080/v1/admin/login -Body $body -ContentType "application/json"
    return $resp.token
  } catch {
    Write-Warning "Login admin gagal. Pastikan seed admin sudah terpasang. (username=admin, password=?)"
    throw
  }
}

Write-Host "==> Health"
Invoke-RestMethod -Method GET http://localhost:8080/v1/health | Out-Host

$token = Login-Admin
Write-Host "==> Token: $token"
$hdr = @{ Authorization = "Bearer $token" }

Write-Host "==> Create exam policy"
$policy = @{
  examId = "sample-exam-001";
  title = "Sample Exam";
  launchUrl = "http://localhost:8080/mock/exam";
  allowedDomains = @("localhost");
  blockExternalNavigation = $true;
  enableCopyPaste = $false;
  enableDownloads = $false;
  enableUploads = $false;
  heartbeatSeconds = 15;
  teacherPinHash = "";
  maxBackgroundSeconds = 0;
  violationThreshold = 3;
  managedMode = $false;
  branding = @{ appName = "EXAM-PROTECT" };
} | ConvertTo-Json -Depth 6
Invoke-RestMethod -Method POST -Uri http://localhost:8080/v1/admin/exams -Body $policy -ContentType "application/json" -Headers $hdr | Out-Host

Write-Host "==> Get signed config"
Invoke-RestMethod -Method GET http://localhost:8080/v1/exams/sample-exam-001/config | Out-Host

Write-Host "==> Start session"
$start = @{
  deviceId = "dev-123";
  examId = "sample-exam-001";
  mode = "android-byod";
  appVersion = "0.1.0";
  capabilities = @{ screenshotBlocked = $true; kiosk = $false; domainWhitelist = $true };
  policyHash = "deadbeef";
} | ConvertTo-Json -Depth 6
$s = Invoke-RestMethod -Method POST -Uri http://localhost:8080/v1/sessions/start -Body $start -ContentType "application/json"
$sid = $s.sessionId
Write-Host "SessionID: $sid"

Write-Host "==> Heartbeat"
$hb = @{
  timestamp = (Get-Date).ToString("o");
  violations = 0;
  events = @();
  metrics = @{ network = "wifi" };
  nonce = [guid]::NewGuid().ToString();
} | ConvertTo-Json -Depth 6
Invoke-RestMethod -Method POST -Uri http://localhost:8080/v1/sessions/$sid/heartbeat -Body $hb -ContentType "application/json" | Out-Host

Write-Host "==> Violation report"
$vr = @{
  type = "APP_BACKGROUND";
  detail = "test";
  ts = (Get-Date).ToString("o");
} | ConvertTo-Json
Invoke-RestMethod -Method POST -Uri http://localhost:8080/v1/sessions/$sid/violations -Body $vr -ContentType "application/json" | Out-Host

Write-Host "==> Complete session"
$cp = @{
  endedReason = "user_submitted";
  violations = 1;
  summary = @{ durationSeconds = 60 };
} | ConvertTo-Json -Depth 6
Invoke-RestMethod -Method POST -Uri http://localhost:8080/v1/sessions/$sid/complete -Body $cp -ContentType "application/json" | Out-Host

Write-Host "Selesai test backend."
