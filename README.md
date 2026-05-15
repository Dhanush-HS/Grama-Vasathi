# 🌾 Grama Vasathi
### *Matti-Vasane — Scent of the Soil*

<p align="center">
  <b>Rural Home-stay Accelerator for Karnataka</b>
</p>

---

# 📖 About

**Grama Vasathi** is an Android application designed to promote authentic rural tourism in Karnataka by helping travelers discover and book village homestays.

The platform also includes a dedicated **Host Readiness Experience** that enables property owners to prepare and manage their homestays efficiently.

---

# 🌍 Project Goals

- 🌱 Rural income generation
- 🏡 Agri-tourism promotion
- 🤝 Cultural exchange
- ♻️ Sustainable tourism
- 🎓 Rural hospitality skill development

---

# ✨ Features

## 🔐 Authentication & Onboarding

- Splash screen with session-based routing
- Guest / Host role selection
- Firebase Email & Password Authentication
- Persistent login sessions
- Protected navigation using Auth Gate
- Auto-created guest profiles in Firestore
- Demo account support

---

## 🏡 Guest Experience

### 🔍 Discover Rural Stays

- Browse farm stays from Firestore
- Search by:
  - Stay name
  - District
  - Activities
- Filter stays using activity tabs

### 📍 Stay Details

- Stay image gallery
- Amenities overview
- Rural experience information

### 📅 Booking System

- Date selection
- Price preview
- Booking confirmation summary

### 👤 Guest Profile

- View past bookings
- Cancel bookings
- Switch to Host mode
- Sign out

### 🌾 Rural Culture Guide

- Traditional etiquette
- Local customs
- Kannada phrases
- Rural travel guidance

---

## 🎓 Host Experience

### Host Readiness Wizard

A multi-step onboarding workflow for rural property owners.

### 📋 Checklist Categories

- Basic setup
- Facilities
- Media uploads
- Safety & Food
- Pricing configuration

### ⚡ Features

- Firestore-based progress persistence
- Host-focused dashboard
- Switch back to Guest mode

---

# ☁️ Backend & Data

## Firestore Collections

```text
stays
bookings
guests
host_wizard_progress
```

---

## 🌱 Auto Seed Data

If the `stays` collection is empty, the app automatically seeds:

- 4 Karnataka rural homestays
- Stable stay IDs

### Example Stay ID

```text
stay_patil_dharwad
```

---

# 🎨 UI & Platform Features

- Material 3 UI
- Cream & brown rural theme
- Adaptive launcher icon
- Android 12+ SplashScreen API
- Navigation Compose with typed routes
- Coil image loading
- Hilt dependency injection
- Kotlin Coroutines & Flow
- DataStore Preferences

---

# 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM |
| Dependency Injection | Hilt |
| Async | Kotlin Coroutines, Flow |
| Backend | Firebase Authentication, Cloud Firestore |
| Image Loading | Coil |
| Local Storage | DataStore Preferences |

---

# 📁 Project Structure

```text
app/src/main/java/com/example/gramavasathi/

├── MainActivity.kt
├── GramaVasathiApp.kt
├── data/
├── domain/
├── di/
├── navigation/
├── ui/
│   ├── screens/
│   └── theme/
├── viewmodel/
└── demo/
```

---

# 📋 Requirements

- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 34
- Minimum SDK 26

## Firebase Services

Enable:

- Firebase Authentication
- Cloud Firestore

---

# 🚀 Getting Started

## 1️⃣ Clone Repository

```bash
git clone <your-repository-url>
```

## 2️⃣ Firebase Setup

### Create Firebase Project

Add Android app:

```text
com.example.gramavasathi
```

### Download Configuration File

Download:

```text
google-services.json
```

Place it inside:

```text
app/
```

### Enable Firebase Services

- Email/Password Authentication
- Cloud Firestore

---

## 3️⃣ Build the Application

```bash
./gradlew :app:assembleDebug
```

---

# 🧪 Demo Accounts

| Role | Email | Password |
|---|---|---|
| Guest | guest.demo@gramavasathi.app | GuestDemo#2026 |
| Host | host.patil@gramavasathi.app | HostDemo#2026 |

---

# 🔒 Security Notes

Before making the repository public:

- Restrict Firebase API keys
- Configure secure Firestore rules
- Review Firebase Authentication settings

## ❌ Do NOT Commit

```text
local.properties
keystore.properties
*.jks
*.keystore
```

---

# 🌍 Vision

Grama Vasathi aims to digitally empower Karnataka’s rural tourism ecosystem by connecting travelers with authentic village experiences while creating sustainable opportunities for local communities.


---

