# EXAM PROTECT — Threat Model

Lingkup: Aplikasi native Android (Kotlin) dan iOS (Swift) yang berfungsi sebagai secure exam browser untuk membuka ujian web. Model ancaman dibagi per skenario BYOD vs managed/supervised.

## Aset yang Dilindungi
- Integritas sesi ujian (tidak ada akses ke aplikasi lain sesuai kebijakan).
- Kerahasiaan konten ujian (mencegah rekam layar/screenshot sebisa mungkin).
- Konsistensi jawaban dan identitas peserta.
- Ketersediaan sesi (jaringan stabil, bebas gangguan injeksi).

## Ancaman dan Mitigasi

1) Perpindahan aplikasi (app switching)
- Vektor: pengguna menekan Home/Recent/Switcher.
- Android BYOD: Tidak bisa dicegah. Mitigasi: deteksi onPause/onStop dan akhiri sesi atau tambahkan strike; tampilkan peringatan.  
- Android Managed (Device Owner): LockTask Mode mencegah efektif.  
- iOS BYOD: Tidak bisa dicegah; deteksi scene resign active → strike/end.  
- iOS Supervised: Single App Mode/ASAM mencegah efektif.

2) Notifikasi (gangguan/overlay)
- Android BYOD: tidak bisa memblokir notifikasi; mitigasi: sarankan Do Not Disturb; strike jika app kehilangan fokus.  
- Android Managed: DPM/MDM dapat meminimalisir status bar/notification drawer.  
- iOS BYOD: tidak bisa memblokir; sarankan Focus Mode; strike saat resign active.  
- iOS Supervised: MDM dapat menekan notifikasi/Single App Mode.

3) Screenshot / Screen Recording
- Android BYOD: `FLAG_SECURE` mencegah capture di app.  
- Android Managed: DPM menonaktifkan capture sistem.  
- iOS BYOD: tidak bisa blok screenshot; deteksi `UIScreen.isCaptured` dan `userDidTakeScreenshotNotification` → blur/strike.  
- iOS Supervised: MDM dapat memblokir capture untuk app/perangkat.

4) Split screen / Multitasking
- Android BYOD: set `resizableActivity=false` untuk mengurangi; tidak 100%. Deteksi ukuran jendela abnormal → strike.  
- Android Managed: LockTask menutup celah.  
- iOS BYOD: tidak dapat mencegah pada iPad; deteksi perubahan ukuran scene → strike/info.  
- iOS Supervised: Single App Mode mencegah.

5) Tapjacking / Overlay
- Android: deteksi overlay dengan `Settings.canDrawOverlays` dan `TYPE_APPLICATION_OVERLAY` indikator; tolak input pada view sensitif; log/strike.  
- iOS: tidak ada deteksi overlay pihak ketiga andal; residual risk.

6) Browser eksternal
- Android: WebViewClient blok `Intents`/URL di luar whitelist; `shouldOverrideUrlLoading`.  
- iOS: `WKNavigationDelegate` + `WKContentRuleList` mencegah keluar dari domain whitelist.

7) Root / Jailbreak
- Android: heuristik (file su, properti sistem, Play Integrity verdict). Jika terdeteksi → tolak atau jalankan read-only mode.  
- iOS: heuristik (file sistem, symlink, sandbox escape tanda), App Attest (fase 2). Tindakan: tolak mulai atau beri flag risiko.

8) Debugging / Tampering
- Android: deteksi debugger (`Debug.isDebuggerConnected`), signature check, classloader tampering, SafetyNet/Play Integrity; obfuscation.  
- iOS: ptrace anti-debug, entitlements check, jailbreak heuristik; Integrity signals. Langkah: nonaktifkan fitur ujian saat debugging terdeteksi.

9) Cheating dengan device kedua
- Tidak bisa dicegah OS-level. Mitigasi: kebijakan pengawasan eksternal, pengawasan manual, atau proctoring kamera (fase 2). Komunikasikan residual risk jelas.

10) Serangan jaringan / Session
- Vektor: MITM, cookie hijacking, downgrade TLS.  
- Mitigasi: TLS 1.2+, HSTS pada LMS, certificate pinning untuk backend EXAM PROTECT, tanda tangan JWS pada config, token sesi dengan masa berlaku pendek, nonce per heartbeat, rate-limit dan replay protection.

## Residual Risk per Mode
- Android BYOD: App switching, notifikasi, split screen terbatas.  
- Android Managed: Risiko rendah; bergantung pada kepatuhan OEM/MDM dan konfigurasi.  
- iOS BYOD: Notifikasi, screenshot, split view iPad tidak bisa diblokir.  
- iOS Supervised: Relatif paling ketat di iOS; masih tidak cegah device kedua.

## Assumptions
- Ujian web dapat berjalan di dalam WebView/WKWebView tanpa kebutuhan API khusus yang tidak kompatibel.  
- Backend dan LMS target menggunakan HTTPS modern.  
- Untuk mode managed, institusi memiliki MDM yang mendukung Single App Mode (iOS) dan Device Owner (Android).

