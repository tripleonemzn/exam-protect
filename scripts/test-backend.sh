#!/usr/bin/env bash
set -euo pipefail
echo "==> Health"
curl -fsS http://localhost:8080/v1/health
echo

echo "==> Admin login (sesuaikan password)"
TOKEN=$(curl -fsS -X POST http://localhost:8080/v1/admin/login -H 'Content-Type: application/json' -d '{"username":"admin","password":"password"}' | jq -r .token)
echo "Token: $TOKEN"

echo "==> Create exam policy"
curl -fsS -X POST http://localhost:8080/v1/admin/exams -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d @- <<'JSON'
{
  "examId": "sample-exam-001",
  "title": "Sample Exam",
  "launchUrl": "http://localhost:8080/mock/exam",
  "allowedDomains": ["localhost"],
  "blockExternalNavigation": true,
  "enableCopyPaste": false,
  "enableDownloads": false,
  "enableUploads": false,
  "heartbeatSeconds": 15,
  "teacherPinHash": "",
  "maxBackgroundSeconds": 0,
  "violationThreshold": 3,
  "managedMode": false,
  "branding": { "appName": "EXAM-PROTECT" }
}
JSON
echo

echo "==> Get signed config"
curl -fsS http://localhost:8080/v1/exams/sample-exam-001/config
echo
