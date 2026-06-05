# 📱 FactChecker AI — Android App

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple?logo=kotlin)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-Material_3-blue?logo=android)](https://developer.android.com/jetpack/compose)
[![Hilt](https://img.shields.io/badge/DI-Hilt-orange)](https://dagger.dev/hilt/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM_Clean-green)](https://developer.android.com/topic/architecture)
[![MinSDK](https://img.shields.io/badge/MinSDK-24_(Android_7)-lightgrey)](https://developer.android.com/about/versions/nougat)

> A full-stack AI-powered Android app that fact-checks any claim in real time using a custom FastAPI backend, Groq LLM, and multi-source web evidence.

---

## 📸 Screens

| Home | Result | History | Settings |
|---|---|---|---|
| Enter claim + recent checks | Animated verdict card + evidence | Searchable + filterable history | API URL, theme, max sources |

---

## ✨ Features

- 🔍 **Real-time fact checking** — calls live AI backend deployed on Render
- 🎨 **Animated verdict card** — color-coded TRUE / FALSE / INCONCLUSIVE with spring animation
- 📊 **Confidence meter** — animated progress bar with label (Very High → Very Low)
- 🧠 **AI reasoning** — expandable LLM explanation for every verdict
- 📋 **Evidence cards** — each source with relevance %, stance, and credibility badge
- 📖 **Full history** — all past checks saved locally in Room DB
- 🔎 **Search + filter** — search by text, filter by verdict or bookmarks
- 👆 **Swipe to delete** — swipe left on history items to remove
- 🔖 **Bookmarks** — save important fact-checks for later
- 📤 **Share result** — share verdict as formatted text to any app
- 🌙 **Dark mode** — full Material 3 dark/light theme support
- ⚙️ **Configurable** — set your own API URL and max sources in settings
- 📴 **Offline history** — past checks available without internet (Room DB)

---

## 🏗️ Architecture — Clean MVVM

```
┌──────────────────────────────────────────────┐
│                  UI LAYER                    │
│  Jetpack Compose · Material 3 · Navigation   │
│                                              │
│  HomeScreen   ResultScreen   HistoryScreen   │
│  HomeVM       ResultVM       HistoryVM       │
└─────────────────────┬────────────────────────┘
                      │ StateFlow / collectAsStateWithLifecycle
┌─────────────────────▼────────────────────────┐
│               DOMAIN LAYER                   │
│  ClaimResult · Verdict · Evidence            │
│  CheckStats · VerifyState                    │
└─────────────────────┬────────────────────────┘
                      │
┌─────────────────────▼────────────────────────┐
│                DATA LAYER                    │
│                                              │
│  FactCheckerRepository (single source)       │
│         ┌───────────┴───────────┐            │
│  Remote (Retrofit)       Local (Room DB)     │
│  FactCheckerApiService   VerificationDao     │
│  Render backend          fact_checker_db     │
│                                              │
│  UserPreferencesDataStore (DataStore)        │
└──────────────────────────────────────────────┘
                      │
┌─────────────────────▼────────────────────────┐
│              DI (Hilt)                       │
│  DatabaseModule · NetworkModule              │
└──────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Category | Library / Tool |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt 2.52 |
| Navigation | Navigation Compose |
| Network | Retrofit 2.11 + OkHttp 4.12 |
| Local DB | Room 2.6.1 |
| Preferences | DataStore 1.1.1 |
| Animations | Lottie 6.6 + Compose animations |
| Async | Coroutines + StateFlow |
| Serialization | Gson |
| Splash Screen | AndroidX SplashScreen |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 36 |

---

## 📂 Project Structure

```
app/src/main/java/eu/tutorials/fact_checker_app/
│
├── FactCheckerApp.kt              # Hilt Application class
├── MainActivity.kt                # NavHost + theme setup
│
├── domain/
│   └── model/
│       └── Models.kt              # Verdict · ClaimResult · Evidence · VerifyState · CheckStats
│
├── data/
│   ├── api/
│   │   ├── ApiDtos.kt             # Retrofit request/response models
│   │   └── FactCheckerApiService.kt  # Retrofit interface
│   ├── local/
│   │   ├── FactCheckerDatabase.kt # Room database
│   │   ├── UserPreferencesDataStore.kt  # API URL · theme · max sources
│   │   ├── dao/
│   │   │   └── VerificationDao.kt # All DB queries
│   │   └── entity/
│   │       └── VerificationEntity.kt # Room table + type converters
│   └── repository/
│       ├── FactCheckerRepository.kt  # Single source of truth
│       └── Mappers.kt             # DTO ↔ Domain ↔ Entity converters
│
├── ui/
│   ├── Screen.kt                  # Navigation routes
│   ├── theme/
│   │   └── Theme.kt               # Material 3 colors + VerdictColors
│   ├── home/
│   │   ├── HomeScreen.kt          # Input · stats · recent checks
│   │   └── HomeViewModel.kt
│   ├── result/
│   │   ├── ResultScreen.kt        # Verdict card · confidence · evidence list
│   │   └── ResultViewModel.kt
│   ├── history/
│   │   ├── HistoryScreen.kt       # Search · filter chips · swipe-to-delete
│   │   └── HistoryViewModel.kt
│   └── settings/
│       ├── SettingsScreen.kt      # API URL · dark mode · max sources · about
│       └── SettingsViewModel.kt
│
├── di/
│   └── AppModules.kt              # Hilt: Room + Retrofit + OkHttp
└── util/
    └── Extensions.kt              # Verdict colors · confidence labels · timestamps
```

---

## 🚀 Setup & Run

### Prerequisites
- Android Studio Hedgehog or newer
- Android device / emulator (API 24+)
- Backend deployed (or running locally)

### Steps

```bash
# 1. Clone
git clone https://github.com/YOUR_USERNAME/agentic-fact-checker-android.git

# 2. Open in Android Studio
# File → Open → select the project folder

# 3. Wait for Gradle sync

# 4. Set your API URL
# Either in Settings screen after running,
# or change DEFAULT_API_URL in UserPreferencesDataStore.kt:
const val DEFAULT_API_URL = "https://your-backend.onrender.com/"

# 5. Run on device or emulator
# Press ▶ or Shift+F10
```

---

## 🔌 Backend Connection

This app connects to the [Agentic Fact Checker Backend](https://github.com/YOUR_USERNAME/agentic-fact-checker).

| Setting | Default |
|---|---|
| API Base URL | `https://agentic-fact-checker.onrender.com/` |
| Timeout (read) | 60 seconds (AI responses take time) |
| Timeout (connect) | 30 seconds |

You can change the API URL at any time from the **Settings screen** inside the app — no rebuild needed.

---

## 🎨 UI Design Decisions

### Verdict color system
| Verdict | Background | Text |
|---|---|---|
| TRUE | `#E8F5E9` (light green) | `#1B5E20` (dark green) |
| FALSE | `#FFEBEE` (light red) | `#B71C1C` (dark red) |
| INCONCLUSIVE | `#FFF3E0` (light amber) | `#E65100` (dark orange) |
| UNVERIFIABLE | `#F5F5F5` (light grey) | `#424242` (dark grey) |

### Animations
- Verdict card: **spring bounce** on enter (`dampingRatio = MediumBouncy`)
- Confidence meter: **1200ms ease-out** animated progress bar
- Loading steps: **fade + slide** animated text transitions
- History filter: **FilterChip** selection animation built-in

---

## 🧪 Testing the App

Once running, try these 4 test queries:

| Claim | Expected Verdict |
|---|---|
| "Humans only use 10% of their brain" | FALSE |
| "Water boils at 100 degrees Celsius at sea level" | TRUE |
| "The Great Wall of China is visible from space" | FALSE |
| "Napoleon Bonaparte was very short" | FALSE |

---

## 📈 What This Demonstrates to Recruiters

| Android Skill | Shown By |
|---|---|
| **Jetpack Compose** | All 4 screens fully in Compose |
| **Material 3** | Dynamic colors, cards, chips, dialogs |
| **MVVM + Clean Arch** | Separate domain/data/ui layers |
| **Hilt DI** | NetworkModule + DatabaseModule |
| **Retrofit** | Full API integration with suspend functions |
| **Room DB** | Entity, DAO, TypeConverters, reactive Flow |
| **DataStore** | Persistent user preferences |
| **Navigation Compose** | Type-safe routes with arguments |
| **Coroutines + Flow** | StateFlow, collectAsStateWithLifecycle |
| **Animations** | Spring, tween, AnimatedVisibility |
| **SwipeToDismiss** | Swipe-to-delete on history items |
| **AI Integration** | Real LLM-powered backend |

---

## 🤝 Related

🐍 **Backend API** → [agentic-fact-checker](https://github.com/YOUR_USERNAME/agentic-fact-checker)
Python FastAPI + Groq LLM + DuckDuckGo + Wikipedia + Neo4j

---

## 📄 License

MIT — free to use, modify and distribute.

APK of the APP is in the Released one.

