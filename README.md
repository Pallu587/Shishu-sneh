# Shishu-Sneh 👶
**Your Baby's Digital Guardian Angel**

[![Build Status](https://img.shields.io/badge/Build-Success-brightgreen)](https://github.com/yourusername/Shishu-Sneh)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg)](https://kotlinlang.org)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://developer.android.com)

---

## 🚨 The Problem Statement
Rural mothers often face immense pressure to stop exclusive breastfeeding early due to myths or the need to return to work. Once they leave the hospital, they lack continuous guidance on **infant nutrition**, **vaccination milestones**, and **developmental tracking**, leading to higher risks of stunting and preventable diseases.

**Shishu-Sneh** (Infant Affection) acts as a "Digital Elder," providing expert guidance in local languages to ensure every child gets a healthy start in life.

---

## ✨ Key Features
- **💉 Automated Vaccination Scheduling**: Enter the DOB once, and get a full 12-month immunization calendar pre-populated automatically.
- **📈 Growth Analytics**: Track weight, height, and head circumference with professional trend lines using MPAndroidChart.
- **🥣 Proactive Feeding Guide**: Age-appropriate nutritional tips (0-12 months) that adapt as your baby grows.
- **✅ Developmental Milestones**: A month-by-month checklist to ensure your baby is hitting critical milestones (smiling, sitting, crawling).
- **🔔 Smart Alerts**: High-priority notifications for upcoming polio drops and vital vaccinations via WorkManager.
- **🌍 Local Language Support**: Full support for **Hindi** and **Kannada** to ensure health equity in rural areas.
- **🔐 Privacy First**: All baby records are stored locally in an encrypted database using **SQLCipher**.

---

## 🛠️ Tech Stack
- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (100%)
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room DB with [SQLCipher](https://www.zetetic.net/sqlcipher/) for encryption
- **Background Tasks**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Charts**: [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)

---

## 📂 Project Structure
```bash
Shishu-Sneh/
├── app/                # Main application module
├── docs/               # Technical documentation
│   └── architecture.md
├── screenshots/        # UI snapshots (Coming Soon)
├── gradle/             # Gradle wrapper and configurations
├── build.gradle.kts    # Project-level build script
└── README.md           # This file
```
*For a deep dive into the design, check out [docs/architecture.md](docs/architecture.md).*

---

## ⚙️ Setup & Installation
1. **Clone the Repo**:
   ```bash
   git clone https://github.com/yourusername/Shishu-Sneh.git
   ```
2. **Open in Android Studio**:
   Open the root folder and let Gradle sync.
3. **Sync & Run**:
   Ensure an Android device/emulator is connected and click **Run**.

---

## 🔮 Future Scope
- **📸 OCR Integration**: Scan physical vaccination cards to digitize records instantly.
- **🤖 AI Pediatric Chatbot**: Real-time answers to common maternal health questions.
- **🩺 Tele-Consultation**: Direct link to local primary health centers (PHCs).

---

## 📄 License
This project is developed for educational and social impact purposes.

---

## 👨‍💻 Author
**Developed with an Engineering Mindset.**
*(Project originally inspired by rural healthcare challenges)*
