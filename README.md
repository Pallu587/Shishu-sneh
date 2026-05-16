# Shishu-Sneh 👶
**Your Baby's Digital Guardian Angel**

[![Build Status](https://img.shields.io/badge/Build-Success-brightgreen)](https://github.com/Pallu587/Shishu-sneh)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20+%20Clean-orange)](docs/architecture.md)

---

## 📽️ Visual Showcase
| **Main Interface** | **Profile Management** | **Health Records** |
|:---:|:---:|:---:|
| <img src="screenshots/min%20page.png" width="200"> | <img src="screenshots/profile%20page.png" width="200"> | <img src="screenshots/Health%20Record.png" width="200"> |
| **Sneh AI Chat** | **Reminders & Features** | **Project PRD** |
| <img src="screenshots/Sneh%20AI.png" width="200"> | <img src="screenshots/Medical%20remainders%20and%20other%20features.png" width="200"> | [📄 View PRD PDF](docs/Shishu_Sneh_PRD.pdf) |

---

## 📖 Overview
**Shishu-Sneh** is an AI-powered healthcare ecosystem designed to reduce infant mortality and stunting in rural areas. By acting as a "Digital Elder," the platform provides personalized guidance, automated health tracking, and real-time AI assistance to mothers who lack consistent access to pediatric experts.

---

## 🚨 Problem Statement
Rural mothers often lack continuous guidance on **infant nutrition**, **vaccination milestones**, and **developmental tracking** after leaving the hospital. Language barriers and the absence of digital records lead to missed polio drops and preventable nutritional deficiencies. Shishu-Sneh bridges this gap with localized, encrypted, and AI-assisted care.

---

## ✨ Key Features
- **💉 Smart Vaccination Scheduler**: Automatically generates a 12-month immunization calendar based on the baby's DOB.
- **📈 Growth Analytics**: Real-time tracking of weight, height, and head circumference with trend visualization.
- **🥣 Proactive Feeding Guide**: Age-appropriate nutritional tips (0-12 months) that adapt dynamically.
- **✅ Developmental Milestones**: A month-by-month checklist for critical physical and cognitive milestones.
- **🔔 Intelligent Alerts**: High-priority push notifications for upcoming health events via WorkManager.
- **🌍 Vernacular Support**: Full accessibility in **Hindi** and **Kannada** to ensure health equity.

---

## 🛠️ Technologies Used
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Modern Declarative UI)
- **Architecture**: MVVM + Clean Architecture (Domain Driven Design)
- **Database**: Room DB with **SQLCipher** (Military-grade AES-256 Encryption)
- **AI Engine**: Google Gemini Pro API
- **Backend**: Firebase (Auth, Firestore, Messaging, Analytics)
- **Background Tasks**: WorkManager
- **Visualization**: MPAndroidChart
- **DI**: Hilt

---

## 🏗️ Architecture
The project follows **Clean Architecture** to ensure the code is decoupled, testable, and scalable.
- **Presentation Layer**: ViewModels & Compose UI.
- **Domain Layer**: Functional UseCases & Repository Interfaces.
- **Data Layer**: Room, Firebase, and SQLCipher implementations.
*Detailed technical breakdown available in [docs/architecture.md](docs/architecture.md).*

---

## 📦 APK Download
You can test the application immediately by downloading the latest release:
**[🚀 Download Shishu-Sneh-v1.apk](apk/Shishu-Sneh-v1-debug.apk)**

---

## ⚙️ Installation Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/Pallu587/Shishu-sneh.git
   ```
2. **Setup API Keys**: Add your `GEMINI_API_KEY` in `local.properties`.
3. **Build & Run**: Open in Android Studio and deploy to a device with API level 24+.

---

## 🔥 Firebase Integration
Shishu-Sneh leverages the full Firebase suite for a robust backend:
- **Authentication**: Secure multi-factor entry for mother's profiles.
- **Firestore**: Real-time synchronization of baby health records across devices.
- **Cloud Messaging**: Low-latency reminders for vaccination drops.
- **Crashlytics**: Real-time stability monitoring to ensure zero-downtime for users.

---

## 🤖 AI Features (Sneh AI)
Powered by **Google Gemini Pro**, Sneh AI acts as a 24/7 pediatric assistant:
- **Natural Language Querying**: Mothers can ask health questions in their local language.
- **Context-Aware Insights**: The AI analyzes the baby's age and health records to provide personalized nutritional advice.

---

## 🔮 Future Enhancements
- **📸 OCR Integration**: Instant digitization of physical health cards via camera.
- **🩺 Tele-Consultation**: Direct integration with local Primary Health Centers (PHCs).
- **📊 Community Health Heatmaps**: Aggregated, anonymized data for health officials to track regional stunting trends.

---

## 🎓 Internship Details
- **Organization**: MindMatrix
- **Project Scope**: Social Impact Healthcare Ecosystem
- **Evaluation Status**: Final Submission Ready 🚀

---

## 👩‍💻 Developed By
**Pallavi Premanand Asnotikar**
*Engineering with a Social Conscience.*

[![GitHub](https://img.shields.io/badge/GitHub-Profile-black?logo=github)](https://github.com/Pallu587)

