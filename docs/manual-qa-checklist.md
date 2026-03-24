## Checklist QA Manual

- Backend
  - [ ] Health 200
  - [ ] Admin login JWT
  - [ ] Create/Get policy
  - [ ] Start/Heartbeat/Violation/Complete
  - [ ] Rate limit dasar aktif

- Android
  - [ ] Build assembleDebug
  - [ ] Unit tests lulus
  - [ ] FLAG_SECURE aktif
  - [ ] Allowlist domain bekerja
  - [ ] Skema eksternal diblokir
  - [ ] Focus-loss → violation
  - [ ] Teacher PIN flow sesuai policy

- iOS
  - [ ] WKWebView load policy/launch URL
  - [ ] Allowlist domain
  - [ ] Nonaktif selection (best-effort)
  - [ ] Deteksi screenshot/recording
  - [ ] Guided Access UX tampil

- Keamanan/Realism
  - [ ] Tidak klaim full lockdown BYOD iOS/Android
  - [ ] Kebijakan dijelaskan ke pengguna

