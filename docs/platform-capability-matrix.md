# EXAM PROTECT — Platform Capability Matrix

Legenda:
- Penuh = sepenuhnya memungkinkan secara resmi
- Sebagian/best-effort = ada celah/keterbatasan; tidak selalu andal; mungkin perlu interaksi pengguna
- Tidak mungkin/tidak andal = tidak didukung OS atau memerlukan kebijakan MDM/entitlement khusus yang tidak tersedia di mode tsb.

| Fitur/Restriksi | Android BYOD | Android Managed (Device Owner/Kiosk) | iOS BYOD | iOS Supervised/Managed |
|---|---|---|---|---|
| Blok tombol Home/Recent | Tidak mungkin | Penuh (LockTask) | Tidak mungkin | Penuh (Single App Mode/ASAM) |
| Blok status bar/notifikasi | Sebagian (tidak bisa blok notifikasi OS) | Penuh (DPM + kebijakan OEM/MDM) | Tidak mungkin | Penuh (MDM) |
| Cegah screenshot/recording | Penuh (`FLAG_SECURE`) | Penuh (DPM `setScreenCaptureDisabled`) | Sebagian (deteksi `isCaptured`, tidak cegah screenshot) | Penuh (MDM kebijakan) |
| Deteksi screenshot | Penuh (event tidak langsung, vendor bergantung) | Penuh | Sebagian (`userDidTakeScreenshotNotification`, best-effort) | Penuh |
| Blok split screen/multiwindow | Sebagian (`resizableActivity=false`, tidak 100%) | Penuh (kiosk) | Tidak mungkin | Penuh (Single App Mode) |
| Blok navigasi eksternal | Penuh (WebViewClient) | Penuh | Penuh (WKNavDelegate + RuleList) | Penuh |
| Whitelist domain | Penuh | Penuh | Penuh (WKContentRuleList) | Penuh |
| Nonaktifkan copy/paste | Sebagian (JS/CSS; OS-level clipboard masih ada) | Penuh (kiosk + kebijakan) | Sebagian (JS/CSS) | Penuh (MDM kebijakan terbatas) |
| Nonaktifkan download | Penuh (intersep download/DownloadManager) | Penuh | Sebagian (WK UIDelegate; bergantung konten) | Penuh |
| Nonaktifkan upload | Sebagian (intersep file chooser) | Penuh | Sebagian | Penuh |
| Deteksi overlay/tapjacking | Penuh (TYPE_APPLICATION_OVERLAY deteksi + izin) | Penuh | Tidak mungkin andal | Penuh (tergantung kebijakan MDM) |
| Deteksi app switch/background | Penuh | Penuh | Penuh | Penuh |
| Kiosk/Single App Mode | Tidak mungkin | Penuh (Device Owner) | Tidak mungkin | Penuh (Supervised) |
| Root/jailbreak deteksi | Sebagian (heuristik) | Sebagian | Sebagian | Sebagian |
| Debugging/tampering deteksi | Penuh (isDebuggerConnected, signature, integrity) | Penuh | Penuh (ptrace/Checks) | Penuh |
| Integrity/Attestation | Sebagian (Play Integrity) | Penuh (Enterprise) | Sebagian (App Attest/DeviceCheck) | Penuh (MDM kebijakan) |
| Force VPN/Proxy | Sebagian (tidak paksa BYOD) | Penuh (Always-On VPN via DPM) | Tidak mungkin BYOD | Penuh (MDM) |

Catatan:
1) iOS BYOD tidak dapat memblokir notifikasi OS, Home/Recent, atau memaksa Single App Mode.  
2) Android BYOD tidak bisa memaksa LockTask; namun `FLAG_SECURE` efektif mencegah screen capture dalam app.  
3) Whitelist domain di iOS efektif dan kencang dengan `WKContentRuleList`, namun konten yang melakukan redirect ke app lain perlu dicegat via `WKNavigationDelegate`.  
4) Split screen di Android dapat dikurangi, tetapi tidak selalu efektif pada semua OEM/versi.  
5) Root/jailbreak detection bersifat heuristik; gunakan untuk meningkatkan biaya serangan, bukan jaminan absolut.  

