# EXAM-PROTECT — Security Review (Red-Team Mindset)

## Ringkasan Temuan Teratas
1) Hardcoded Teacher PIN bypass (Android, iOS) — critical  
   Android menerima PIN “000000” untuk keluar. Ini backdoor yang mudah dieksploitasi.  
2) WKWebView allowlist hanya via navigation delegate — high  
   Tanpa content rules, beberapa resource third‑party bisa lolos (misal subresource) dan skema khusus.  
3) WebView Android tidak set explicit mixed content mode & safe browsing awalnya — high  
   Berisiko load HTTP resource pada halaman HTTPS pada perangkat tertentu, dan kehilangan proteksi Safe Browsing eksplisit.  
4) Skema URL eksternal belum diblokir komprehensif — high  
   “about:”, “data:”, “blob:”, “content:” belum seluruhnya difilter (Android/iOS).  
5) Copy/Paste suppression best‑effort saja — medium  
   Hanya CSS/long‑press block; tidak menjamin di semua konten.  
6) Screenshot/recording tidak dapat dicegah di iOS BYOD — medium  
   Deteksi sudah ada; perlu perkuat UX dan penalti.  
7) Background/focus loss logging ada, namun threshold handling perlu jelas — medium  
   Pastikan konsistensi penegakan saat threshold tercapai.  
8) Backend policy validation minim — medium  
   Launch URL, allowedDomains belum divalidasi kuat; risiko konfigurasi berbahaya.  
9) Launch token default dikirim sebagai JSON mentah — medium  
   Perlu HS256/RS256 untuk produksi; default dev boleh, tetapi harus explicit.  
10) Rate limiting masih global — low  
    Perluat granular per endpoint (mis. login/admin, violations).

## Detail Analisis
- Bypass domain allowlist:  
  Android dan iOS awalnya tidak memblok “about/data/blob/content/chrome”. Berpotensi eksploitasi navigasi dari konten berbahaya.  
- External intent / URL scheme:  
  Tambahan skema telah diblok; sebelumnya sekadar tel/sms/mailto/market.  
- WebView/WKWebView insecure defaults:  
  Android: explicit `MIXED_CONTENT_NEVER_ALLOW` dan `setSafeBrowsingEnabled(true)` kini ditambahkan.  
  iOS: user script di `atDocumentStart` menggantikan evaluasi di `onPageFinished`.  
- Bypass copy/paste:  
  Masih best‑effort (CSS, non‑selectable). Platform BYOD tidak menjamin.  
- Screenshot/capture:  
  iOS: deteksi `isCaptured` + screenshot notification; tidak bisa mencegah BYOD — dikomunikasikan.  
  Android: FLAG_SECURE sudah diterapkan.  
- Background/focus loss:  
  Android melog “FOCUS_LOSS” dan background. iOS melog resign active/background. Threshold enforcement memaksa selesai.  
- Teacher PIN:  
  Backdoor “000000” di Android dihapus. Sekarang exit tanpa backend/PIN hanya di policy yang tidak mewajibkan PIN.  
- Tampering/debugger/root/jailbreak checks:  
  Heuristik publik ada (Android/iOS). Tidak absolut, cukup meningkatkan biaya serangan.  
- Token/session:  
  Backend mendukung JWT HS256 untuk launch token; default dev kirim JSON mentah.  
- Local storage/security:  
  Tidak menyimpan jawaban; hanya log pelanggaran minimal.  
- Heartbeat/violations:  
  Infrastruktur endpoint tersedia; retry/backoff di client perlu ditambah di fase berikut.  
- Policy trust boundaries:  
  Validasi backend diperkuat (launchUrl https/http, allowedDomains wajib).

## Klaim Kemampuan yang Diperbaiki
- iOS BYOD tidak diklaim “full lockdown”. Dokumentasi sudah menekankan residual risk.  
- Android BYOD tidak diklaim bisa memblok Home/Recent.

