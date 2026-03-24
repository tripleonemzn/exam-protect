# Bukti Runtime Backend (Lokal)

## Informasi Lingkungan
- OS: Windows
- Docker: tidak tersedia (perintah `docker --version` tidak ditemukan)
- Go: tersedia (hasil `go version` dicantumkan di bawah)

## Perintah yang Dijalankan (dan hasil)

### go version
   go version go1.25.0 windows/amd64

### go mod tidy (backend)
   sukses (mengunduh dependency)

### go test ./... (backend)
   ok exam-protect/backend/internal/security (1.090s)
   paket lainnya [no test files]

### go build -o server.exe ./cmd/server
   sukses, artefak: backend/server.exe

## Catatan
- Menjalankan server HTTP penuh membutuhkan PostgreSQL berjalan. Karena Docker tidak tersedia di lingkungan ini, verifikasi endpoint runtime dilakukan melalui skrip dev-backend/test-backend pada mesin dengan Docker (lihat docs/run-local.md). 
- Mock exam page tersedia pada route /mock/exam saat server berjalan.

