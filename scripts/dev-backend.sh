#!/usr/bin/env bash
set -euo pipefail
echo "==> Memulai Docker Compose untuk Postgres + Backend"
docker compose up -d
echo "==> Cek health endpoint"
sleep 3 || true
curl -fsS http://localhost:8080/v1/health || echo "Backend belum siap, tunggu beberapa detik..."
echo "Selesai. Backend di http://localhost:8080"
