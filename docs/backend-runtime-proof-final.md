# Backend Runtime Proof (Final, tanpa fallback dev)

## Cleanup
- Dihapus fallback `DEV_ADMIN_PLAIN` dari:
  - docker-compose.yml (env variable di service backend)
  - internal/config/config.go (field DevAdminPlain)
  - internal/httpserver/handlers/admin.go (logika fallback)
- Rebuild container: `docker compose up -d --build backend` → OK

## Verifikasi Migrasi & Seed
- `docker compose exec -T db psql -U exam -d exam -c "\dt"` → tabel: admins, exam_policies, exam_sessions, violation_logs, audit_logs
- Regenerasi hash admin via pgcrypto (untuk memastikan bcrypt benar):
  - `create extension if not exists pgcrypto;`
  - `update admins set pass_hash = crypt('password', gen_salt('bf', 10)) where username='admin';`

## Endpoint Bukti Nyata (tanpa fallback)
- Health:
  - `curl http://localhost:8080/v1/health` → 200 `{"status":"ok"}`
- Admin Login:
  - `curl -X POST -H "Content-Type: application/json" --data-binary "@backend/test/login.json" http://localhost:8080/v1/admin/login` → 200 `{"token":"<JWT>"}`

## Status
- Backend: Hijau (Docker + Postgres + endpoint runtime; login admin memakai hash bcrypt di DB; tanpa DEV fallback).
