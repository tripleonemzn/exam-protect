EXAM-PROTECT Backend (Go + PostgreSQL)

Fitur:
- Health endpoint
- Admin auth (JWT HS256), CRUD exam policy
- Start session, heartbeat, report violation, complete
- JWS launch config: default JSON payload; HS256 JWT jika LAUNCH_JWT_SIGNED=true
- OpenAPI spec dan Docker Compose untuk dev

Persiapan:
1) cp backend/.env.example backend/.env (opsional)
2) docker compose up --build
3) Jalankan migrasi SQL 001_init.sql ke database
4) Seed admin: insert ke tabel admins dengan bcrypt hash

Catatan:
- Gunakan secret berbeda untuk production
- Disarankan beralih ke RS256 untuk launch token di produksi
- Rate limit dapat ditambahkan per-IP/gateway level

