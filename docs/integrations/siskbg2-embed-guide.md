# Panduan Embed Mode untuk siskbg2 (dan aplikasi umum)

## Deteksi ExamProtect
- Periksa header `X-ExamProtect: 1` atau User-Agent mengandung `ExamProtectClient`.

## Node/Express
```javascript
app.use((req,res,next)=>{req.isEP=req.headers['x-examprotect']==='1'||(req.headers['user-agent']||'').includes('ExamProtectClient');next();});
app.get('/home', (req,res)=> req.isEP ? res.render('home_mobile') : res.render('home_normal'));
```

## PHP/Laravel
```php
$isEP = request()->header('X-ExamProtect')==='1' || str_contains(request()->header('User-Agent',''), 'ExamProtectClient');
return view($isEP ? 'home_mobile' : 'home_normal');
```

## Nginx
```nginx
map $http_x_examprotect $is_ep { default 0; 1 1; }
map $http_user_agent $ua_ep { default 0; "~ExamProtectClient" 1; }
server {
  location /home {
    if ($is_ep = 1) { rewrite ^ /home-mobile last; }
    if ($ua_ep = 1) { rewrite ^ /home-mobile last; }
    try_files $uri @home;
  }
}
```

## Rekomendasi Tampilan Mobile Variant
- Tanpa navbar/footer global
- Single column, tombol besar, tipografi mobile
- Domain tetap sama agar lolos allowlist

## Konfigurasi di exam-protect-server
- Isi `mobileLaunchUrl` ke route embed mode (mis. `/home-mobile`)
- Isi `desktopLaunchUrl` ke route normal (mis. `/home`)
- Set `autoVariant=true`
- Tambah domain ke `allowedDomains`
