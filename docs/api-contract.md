# EXAM PROTECT — Kontrak API Backend (v1)

Semua endpoint berada di bawah prefix `/v1`. Semua response menggunakan JSON. Gunakan HTTPS, dan terapkan certificate pinning pada mobile app untuk domain backend EXAM PROTECT.

Autentikasi:
- Mobile → Backend: `Authorization: Bearer <token>` (MVP: token perangkat/sesi sederhana; Fase 2: OIDC short-lived token + attestation).
- Admin → Backend: `Authorization: Bearer <admin-jwt>`.

## Model: Exam Policy/Config
Schema ringkas:
```json
{
  "version": 1,
  "examId": "string",
  "launchUrl": "https://...",
  "allowedDomains": ["example.com"],
  "blockExternalNavigation": true,
  "enableCopyPaste": false,
  "enableDownloads": false,
  "enableUploads": true,
  "heartbeatSeconds": 15,
  "teacherPinRequired": false,
  "maxBackgroundSeconds": 0,
  "violationThreshold": 3,
  "managedMode": "auto|require|forbid",
  "branding": {
    "title": "string",
    "primaryColor": "#RRGGBB",
    "logoUrl": "https://..."
  },
  "iat": 0,
  "exp": 0,
  "kid": "key-id"
}
```
Semua payload policy dikemas sebagai JWS (RS256) dengan header `kid` dan klien memverifikasi terhadap public key yang dipin.

## 1) Mengambil Signed Exam Configuration
- GET `/v1/exams/{examId}/config?ssoToken=...`  
  - Header: `Authorization: Bearer <token>` (opsional tergantung integrasi)  
  - Response 200:
```json
{
  "examId": "EX-2026-01",
  "jws": "eyJhbGciOiJSUzI1NiIsImtpZCI6I...<JWS compact>..."
}
```
  - Error: 401/403 jika token tidak valid, 404 jika examId tidak ada.

Catatan: Untuk Google Forms/Moodle, admin dapat membuat policy dengan `launchUrl` yang sesuai; allowedDomains perlu memasukkan domain terkait login/SSO (mis. `accounts.google.com`).

## 2) Memulai Session
- POST `/v1/sessions/start`
  - Body:
```json
{
  "deviceId": "uuid",
  "examId": "EX-2026-01",
  "mode": "android-byod|android-managed|ios-byod|ios-supervised",
  "appVersion": "1.0.0",
  "capabilities": {
    "screenshotBlocked": true,
    "kiosk": false,
    "domainWhitelist": true
  },
  "policyHash": "sha256-of-jws-payload"
}
```
  - Response 200:
```json
{
  "sessionId": "SESS-abc123",
  "heartbeatSeconds": 15,
  "serverTime": "2026-03-22T10:00:00Z"
}
```

## 3) Heartbeat
- POST `/v1/sessions/{sessionId}/heartbeat`
  - Body:
```json
{
  "timestamp": "2026-03-22T10:00:15Z",
  "violations": 1,
  "events": [
    {"type": "APP_BACKGROUND", "ts": "2026-03-22T10:00:10Z"}
  ],
  "metrics": {
    "latencyMs": 120,
    "battery": 0.75,
    "network": "wifi"
  },
  "nonce": "random-uuid"
}
```
  - Response 200:
```json
{
  "status": "ok",
  "action": "none|warn|terminate",
  "serverTime": "2026-03-22T10:00:15Z"
}
```

## 4) Pelaporan Pelanggaran
- POST `/v1/sessions/{sessionId}/violations`
  - Body:
```json
{
  "type": "SCREEN_CAPTURE_DETECTED|APP_SWITCH|EXTERNAL_NAV_ATTEMPT|OVERLAY|DEBUGGER|ROOT_JAILBREAK|SPLIT_SCREEN",
  "detail": "optional string",
  "ts": "2026-03-22T10:02:00Z"
}
```
  - Response 202:
```json
{"accepted": true}
```

## 5) Penyelesaian Session / Submit
- POST `/v1/sessions/{sessionId}/complete`
  - Body:
```json
{
  "endedReason": "user_submitted|violation_threshold|timeout|teacher_pin",
  "violations": 3,
  "summary": {
    "durationSeconds": 1800,
    "domainAccessed": ["forms.gle", "accounts.google.com"]
  }
}
```
  - Response 200:
```json
{
  "status": "completed",
  "certificate": "optional-proof-or-checksum"
}
```

## 6) CRUD Admin Exam Policy
- POST `/v1/admin/exams`
  - Body: objek kebijakan (tanpa `iat/exp/kid`); server menambah metadata dan menandatangani.
  - Response 201: `{ "examId": "EX-2026-01" }`
- PUT `/v1/admin/exams/{examId}`
  - Body: patch kebijakan.
  - Response 200: `{ "updated": true }`
- GET `/v1/admin/exams/{examId}`
  - Response 200: kebijakan terbaru (payload + header JWS).
- DELETE `/v1/admin/exams/{examId}`
  - Response 204.
- GET `/v1/admin/exams?search=...&page=1`
  - Response 200: daftar ringkas exam policy.

## Keselamatan & Keandalan
- Semua request harus menyertakan `request-id` untuk trace.
- Rate limit per perangkat/sesi.
- Replay protection: nonce pada heartbeat; tolak duplikasi.
- Waktu server dikembalikan pada tiap response untuk sinkronisasi ringan.
- Kunci penandatanganan diputar (key rotation) dengan publikasi `/.well-known/jwks.json`.

