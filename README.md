# Skill Development Tracker

An Android application that helps users track their skill development journey through YouTube courses, with AI-powered quizzes, study streak tracking, and adaptive learning plans.

## 🚀 Features

- **Skill Selection** — Choose from predefined skills or add custom ones
- **YouTube Course Discovery** — Search and select full courses from YouTube
- **Adaptive Learning Plans** — AI-calculated study schedules based on your availability
- **Daily Session Tracking** — Track progress with session-by-session completion
- **Learning Streaks** — Build and maintain daily study streaks with fire animations
- **Interactive Quizzes** — AI-generated quizzes after each session (Gemini API)
- **AI Chatbot Tutor** — Ask questions about your course material
- **Wellness Breaks** — Micro-exercise reminders between study sessions
- **Progress Analytics** — Circular progress, timelines, and detailed stats
- **Smart Notifications** — Customizable daily study reminders

## 🏗 Architecture

- **MVVM** + **Clean Architecture** (Domain → Data → Presentation)
- **Repository Pattern** with Use Cases
- **Offline-first** with Room DB + Firebase Firestore sync

## 🛠 Tech Stack

| Layer          | Technology                           |
|----------------|--------------------------------------|
| Language       | Kotlin                               |
| UI Framework   | Jetpack Compose (Material 3)        |
| DI             | Hilt                                 |
| Async          | Coroutines + Flow                    |
| Navigation     | Navigation Compose                   |
| Local DB       | Room                                 |
| Remote DB      | Firebase Firestore                   |
| Auth           | Firebase Authentication              |
| AI             | Google Gemini API (gemini-1.5-flash) |
| Video API      | YouTube Data API v3                  |
| Image Loading  | Coil                                 |
| Notifications  | WorkManager + NotificationManager    |
| Build System   | Gradle (Kotlin DSL)                  |

## 📋 Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17+
- Android SDK 34
- A Firebase project with Firestore + Auth enabled
- YouTube Data API v3 key
- Google Gemini API key

## ⚙️ Setup Instructions

### 1. Clone the Repository
```bash
git clone <repo-url>
cd skill-development-tracker
```

### 2. Firebase Setup
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project
3. Add an Android app with package name `com.example.skilltracker`
4. Download `google-services.json` and place in `app/` directory
5. Enable **Authentication** → Sign-in methods → Google, Email/Password
6. Enable **Cloud Firestore** → Create database in production mode

### 3. API Keys
Edit `local.properties` in the project root:
```properties
YOUTUBE_API_KEY=your_youtube_api_key_here
GEMINI_API_KEY=your_gemini_api_key_here
```

**Get YouTube API Key:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable "YouTube Data API v3"
3. Create an API key under Credentials

**Get Gemini API Key:**
1. Go to [Google AI Studio](https://aistudio.google.com/app/apikey)
2. Create a new API key

### 4. Build & Run
```bash
./gradlew assembleDebug
```
Or open in Android Studio and click ▶️ Run.

## 📁 Project Structure

```
app/src/main/java/com/example/skilltracker/
├── data/
│   ├── local/          → Room: Entities, DAOs, AppDatabase, Mappers
│   ├── remote/         → Firestore, YouTube API, Gemini API services
│   ├── repository/     → Repository implementations
│   └── model/          → API response data classes
├── domain/
│   ├── model/          → Domain models (Course, Session, Streak, etc.)
│   ├── repository/     → Repository interfaces
│   └── usecase/        → Use case classes
├── presentation/
│   ├── login/          → Login screen
│   ├── skill/          → Skill selection screen + ViewModel
│   ├── course/         → Course picker screen + ViewModel
│   ├── plan/           → Plan setup screen + ViewModel
│   ├── session/        → Daily session screen + ViewModel
│   ├── progress/       → Progress screen + ViewModel
│   ├── quiz/           → Quiz screen + ViewModel
│   ├── chatbot/        → Chatbot screen + ViewModel
│   ├── fitness/        → Fitness/wellness break screen
│   ├── settings/       → Settings screen
│   ├── navigation/     → Nav routes
│   └── theme/          → Material 3 theme
├── di/                 → Hilt dependency injection modules
├── notification/       → WorkManager reminder worker
└── util/               → Constants, DateUtils, Resource sealed class
```

## 🔑 Security Notes

- API keys are stored in `local.properties` (not committed to source control)
- Keys are injected via `BuildConfig` at compile time
- Firebase rules should restrict read/write to authenticated users
- ProGuard rules included for production builds.

## 📱 Min Requirements

- Android 8.0 (API 26) or higher
- Google Play Services for Firebase Auth

## 📄 License

This project is for educational purposes.
