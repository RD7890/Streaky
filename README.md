# 🔥 Streaky

> **Build habits that stick** — A premium streak-based habit tracker for Android

[![Build Status](https://github.com/RD7890/Streaky/actions/workflows/build.yml/badge.svg)](https://github.com/RD7890/Streaky/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/RD7890/Streaky)](https://github.com/RD7890/Streaky/releases/latest)
[![Android](https://img.shields.io/badge/Android-26%2B-green)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-blue)](https://kotlinlang.org/)

---

## 📱 Features

- **🔥 Streak Tracking** — Daily check-ins with animated counter (0→1, 4→5...)
- **🎉 Confetti Animation** — Celebration when you complete a habit
- **📊 Dashboard** — Active streaks, inactive habits, best streak stats
- **📅 Calendar View** — 28-day completion grid per habit
- **🔔 Reminders** — Daily push notifications via WorkManager
- **📦 Home Screen Widget** — Per-habit Glance widget showing streak count
- **🎨 Pure Matte Design** — Flat colors, zero gradients, premium feel

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Hilt DI |
| Database | Room + Drizzle |
| Background | WorkManager |
| Widgets | Glance API |
| Build | Gradle 8.9 + AGP 8.5.2 |
| Language | Kotlin 2.0 |

## 📦 APK Downloads

Get the latest APKs from [Releases](https://github.com/RD7890/Streaky/releases/latest):

| Device Type | APK |
|-------------|-----|
| Most Android phones (2018+) | `arm64-v8a-release` |
| Older Android phones | `armeabi-v7a-release` |
| Emulators (x86_64) | `x86_64-debug` |
| All devices | `universal-release` |

## 🚀 CI/CD

Every push to `main` or `dev`:
1. Installs Gradle 8.9 + JDK 17
2. Builds Debug + Release APKs for **arm64-v8a / armeabi-v7a / x86 / x86_64 / universal**
3. Renames APKs with version + build number
4. Creates a GitHub Release with all APK variants

## 🔧 Build Locally

```bash
git clone https://github.com/RD7890/Streaky.git
cd Streaky
./gradlew assembleDebug
```

## 📄 License

MIT © Rohan
