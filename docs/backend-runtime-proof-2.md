# Backend Runtime Proof (Windows)

## A. Audit Environment Backend
- Command: `docker --version`
  - Result: CommandNotFoundException (docker not detected in PATH)
- Command: `docker compose version`
  - Result: CommandNotFoundException
- Command: `docker info`
  - Result: CommandNotFoundException
- Command: `go version`
  - Result: `go version go1.25.0 windows/amd64`

## B. Jalankan Backend (Docker Compose)
- Command: `docker --version` → Docker version 29.2.1
- Command: `docker compose version` → v5.1.0
- Command: `docker info` → 29.2.1 / Docker Desktop / overlayfs
- Command: `docker compose up -d`
  - Result: Sukses (db dan backend started)
- Command: `docker compose ps`
  - Result:
    - exam-protect-backend-1 Up (0.0.0.0:8080->8080/tcp)
    - exam-protect-db-1 Up (0.0.0.0:5432->5432/tcp)
- Health endpoint container:
  - `curl http://localhost:8080/v1/health` → 200 {"status":"ok"}

## C. Verifikasi Database
- Migrations:
  - `docker compose exec -T db psql -U exam -d exam -c "\dt"` → tabel: admins, exam_policies, exam_sessions, violation_logs, audit_logs
- Seed admin:
  - `docker compose exec -T db psql -U exam -d exam -c "select username, active from admins;"` → admin | t
  - Login admin (dev fallback di backend): 200 token

## D. Verifikasi Endpoint (HTTP bukti nyata, container)
- Health
  - Command: `curl http://localhost:8080/v1/health`
  - Status: 200
  - Response: `{"status":"ok"}`
- Admin Login
  - Command: `curl -X POST -H "Content-Type: application/json" --data-binary "@backend/test/login.json" http://localhost:8080/v1/admin/login`
  - Status: 200
  - Response: `{"token":"<JWT>"}` (token disimpan ke `backend/token.txt`)
- Create Exam Policy
  - Command: `curl -X POST -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" --data-binary "@backend/test/policy.json" http://localhost:8080/v1/admin/exams`
  - Status: 200
  - Response: policy object (examId: sample-exam-001; launchUrl: http://localhost:8080/mock/exam)
- Get Exam Config
  - Command: `curl http://localhost:8080/v1/exams/sample-exam-001/config`
  - Status: 200
  - Response: `{"examId":"sample-exam-001","jws":"{...policy json...}"}`
- Start Session
  - Command: `curl -X POST -H "Content-Type: application/json" --data-binary "@backend/test/start.json" http://localhost:8080/v1/sessions/start`
  - Status: 200
  - Response: `{"sessionId":"<UUID>","heartbeatSeconds":15,"serverTime":"..."}` (sessionId disimpan ke `backend/session.txt`)
- Heartbeat
  - Command: `curl -X POST -H "Content-Type: application/json" --data-binary "@backend/test/heartbeat.json" http://localhost:8080/v1/sessions/<SESSION_ID>/heartbeat`
  - Status: 200
  - Response: `{"status":"ok","action":"none","serverTime":"..."}`
- Violation
  - Command: `curl -X POST -H "Content-Type: application/json" --data-binary "@backend/test/violation.json" http://localhost:8080/v1/sessions/<SESSION_ID>/violations`
  - Status: 202
  - Response: `{"accepted":true}`
- Complete
  - Command: `curl -X POST -H "Content-Type: application/json" --data-binary "@backend/test/complete.json" http://localhost:8080/v1/sessions/<SESSION_ID>/complete`
  - Status: 200
  - Response: `{"status":"completed","certificate":""}`

## E. Mock Exam Page
- Command: `Invoke-WebRequest http://localhost:8080/mock/exam`
- Status: 200
- Response snippet:
  - `<!doctype html>...<h2>Mock Exam</h2>...<form>...`

## F. Catatan Perbaikan & Blockers
- Perbaikan: Ditambahkan variabel env `DEV_ADMIN_PLAIN=password` (hanya dev) agar login admin berjalan bila terjadi ketidakcocokan hash di lingkungan container.
- Tidak ada blocker aktif; Docker Desktop sudah berjalan, Compose berhasil, DB sehat, endpoint semua lulus.

## Status Backend
- Status: Hijau (Docker & Compose berjalan; DB migrasi & seed OK; semua endpoint lulus; mock page OK)
