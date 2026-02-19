# Quick DPI Changer

Aplikasi Android untuk mengubah DPI (Density Pixel) dengan cepat via sticky notification. Memerlukan **ROOT ACCESS**.

## Fitur
- Sticky notification (tidak bisa di-swipe)
- Input DPI langsung di notification
- Apply DPI dengan satu klik
- Tombol close untuk menghentikan service

## Cara Install
1. Download APK dari [GitHub Actions](../../actions) (tab Artifacts)
2. Install di Android yang sudah root
3. Buka aplikasi dan klik "Start Sticky Notification"
4. Input DPI di notification dan apply

## Build
Build otomatis via GitHub Actions. Push ke branch `main` untuk trigger build.

## Catatan
- Android 6.0+ (API 24)
- Memerlukan akses root untuk command `wm density`
