# Dev Credentials

- Admin (dev):
  - username: `admin`
  - password: `password`
  - note: seeded di MemStore (local run) dan di Postgres (Docker Compose) melalui `backend/db/migrations/002_seed.sql`

- JWT Secrets (dev):
  - ADMIN_JWT_SECRET: `dev-admin-secret`
  - LAUNCH_JWT_SECRET: `dev-launch-secret`
  - LAUNCH_JWT_SIGNED: `false` (ubah ke `true` untuk JWS via HS256)

Catatan:
- Ganti semua secret untuk staging/production.
- Disarankan beralih ke RS256 untuk launch token di production.
