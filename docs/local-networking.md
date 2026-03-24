# Local Networking (Dev)

- Desktop Windows
  - Base URL: `http://localhost:8080`
  - Sample policy: `launchUrl: http://localhost:8080/mock/exam`, `allowedDomains: ["localhost"]`
- Android Emulator (AVD)
  - Base URL: `http://10.0.2.2:8080`
  - Sample policy: `launchUrl: http://10.0.2.2:8080/mock/exam`, `allowedDomains: ["10.0.2.2"]`
- iOS Simulator
  - Base URL: `http://localhost:8080`
  - Sample policy: `launchUrl: http://localhost:8080/mock/exam`, `allowedDomains: ["localhost"]`

Catatan:
- Jika backend berjalan via Docker Compose, pastikan port `8080` dipublish.
- Untuk Android perangkat fisik dalam jaringan yang sama, gunakan IP host Windows (mis. `http://192.168.x.x:8080`) dan sesuaikan `allowedDomains`.
