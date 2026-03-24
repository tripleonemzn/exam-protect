# EXAM PROTECT — Struktur Repository

Repositori monorepo dengan pemisahan jelas antara platform, backend, dan dokumentasi.

```
exam-protect/
├─ android-app/                 # Aplikasi Android (Kotlin)
│  ├─ app/                      # Modul utama
│  │  ├─ src/
│  │  │  ├─ main/
│  │  │  │  ├─ AndroidManifest.xml
│  │  │  │  ├─ java/            # package com.examprotect.app
│  │  │  │  │  ├─ ui/           # Activity/Fragment, WebView shell
│  │  │  │  │  ├─ domain/       # use cases, entity
│  │  │  │  │  ├─ data/         # repository, network
│  │  │  │  │  ├─ di/           # Hilt modules
│  │  │  │  │  └─ util/
│  │  │  │  └─ res/
│  │  │  └─ test/
│  │  └─ build.gradle.kts
│  ├─ build-logic/              # Konvensi build (opsional)
│  └─ settings.gradle.kts
│
├─ ios-app/                     # Aplikasi iOS (Swift)
│  ├─ ExamProtect/
│  │  ├─ App/                   # AppDelegate/SceneDelegate
│  │  ├─ Modules/
│  │  │  ├─ ExamShell/          # WKWebView + Coordinator
│  │  │  ├─ Domain/             # use cases, entities
│  │  │  ├─ Data/               # repositories, networking
│  │  │  └─ Utils/
│  │  ├─ Resources/
│  │  └─ Info.plist
│  ├─ ExamProtectTests/
│  └─ Project.xcodeproj/ or .xcworkspace
│
├─ backend/                     # Backend REST (contoh: TypeScript + Fastify/Nest)
│  ├─ src/
│  │  ├─ main.ts
│  │  ├─ modules/
│  │  │  ├─ exams/
│  │  │  ├─ sessions/
│  │  │  └─ admin/
│  │  └─ infra/                 # jwks, db, config
│  ├─ package.json
│  ├─ tsconfig.json
│  └─ Dockerfile
│
├─ shared-docs/                 # Dokumen umum (salinan/ekstrak dari /docs jika perlu)
│  └─ README.md
│
└─ docs/                        # Desain & spesifikasi
   ├─ product-architecture.md
   ├─ platform-capability-matrix.md
   ├─ threat-model.md
   ├─ api-contract.md
   ├─ implementation-roadmap.md
   └─ repo-structure.md
```

## Pedoman Arsitektur
- Clean Architecture di kedua platform:
  - Domain (business rules) → independen platform.
  - Data (API/Cache) → implementasi detail.
  - Presentation (UI + state) → MVVM.
- Dependency mengalir dari luar ke dalam (Presentation → Domain → Data interface).
- Konfigurasi keamanan (key pinning, JWS public keys) diletakkan di secure storage/config build, tidak hardcode kunci privat.

## State Management
- Android: ViewModel + Kotlin Flow/StateFlow; satu sumber kebenaran per layar.
- iOS: MVVM + Coordinator; gunakan Swift Concurrency (async/await), Combine opsional.

## Praktik Keamanan
- Android: `FLAG_SECURE`, non-resizable activity, WebView hardening, DPM untuk kiosk (modul terpisah).
- iOS: `WKContentRuleList`, deteksi `isCaptured`, Info.plist disesuaikan (opsi full screen iPhone), dukungan Supervised via MDM (di luar repo aplikasi).
- Backend: JWS signing, JWKS, rate limit, audit logging.

