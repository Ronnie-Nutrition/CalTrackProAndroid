# CalTrackPro Android Development Guide

## AI Dev Tasks Integration
This project follows the structured AI development workflow from https://github.com/snarktank/ai-dev-tasks

### Workflow Files
When implementing features, use these templates:
- `/Users/ronniecraig/ai-dev-tasks/create-prd.md` - For creating Product Requirement Documents
- `/Users/ronniecraig/ai-dev-tasks/generate-tasks.md` - For breaking PRDs into tasks
- `/Users/ronniecraig/ai-dev-tasks/process-task-list.md` - For systematic implementation

### Project Documentation
- `tasks/PRD-INDEX.md` - Index of all PRDs (to be implemented)
- `GIT-COMMIT-GUIDELINES.md` - Conventional commit format requirements
- `tasks/` - Directory containing all PRDs and task lists

## Project Overview
CalTrackPro Android is a nutrition tracking Android app - the Android version of the iOS CalTrackPro app.

**IMPORTANT:** This is a completely separate codebase from the iOS version. Changes here do NOT affect the iOS app.

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Data Persistence**: Room Database
- **Minimum Android**: API 26 (Android 8.0)
- **External APIs**: Edamam Food Database API
- **Architecture**: MVVM with Jetpack Compose
- **Dependency Injection**: Hilt
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **Barcode Scanning**: ML Kit

### Package Name
`com.easyaiflows.caltrackpro`

---

## Features to Implement (Matching iOS App)

### Phase 1: Core Features

#### 1. Food Diary
- [ ] Track daily nutritional intake
- [ ] Add/edit/delete food entries
- [ ] View daily, weekly nutrition summary
- [ ] Macro tracking (calories, protein, carbs, fat)
- [ ] Micronutrient tracking

#### 2. Food Search
- [ ] Search foods via Edamam API integration
- [ ] Display nutrition information
- [ ] Add searched foods to diary
- [ ] Recent searches history
- [ ] Favorite foods

#### 3. Barcode Scanning
- [ ] Scan product barcodes using ML Kit
- [ ] Look up nutrition data from Edamam
- [ ] Multiple barcode format support (UPC, EAN, QR, Code128, Code39)
- [ ] Torch/flashlight control
- [ ] Haptic feedback on successful scan

#### 4. Recipe Management
- [ ] Create custom recipes
- [ ] Save and edit recipes
- [ ] Calculate nutrition per serving
- [ ] Add recipe servings to diary

#### 5. Nutrition Insights
- [ ] Visualize nutritional data with charts
- [ ] Daily/weekly/monthly trends
- [ ] Goal progress tracking
- [ ] Nutrient breakdown charts

#### 6. User Profile
- [ ] Set personal information (age, weight, height)
- [ ] Set dietary goals (calories, macros)
- [ ] Dietary preferences
- [ ] Activity level settings

### Phase 2: Enhanced Features

#### 7. Voice Input
- [ ] Speech-to-text food logging
- [ ] Natural language processing ("I ate a turkey sandwich")
- [ ] Multi-food detection ("chicken and rice")
- [ ] Quantity recognition ("two eggs", "large coffee")
- [ ] Recording animations and visual feedback

#### 8. Health Integration (Google Fit)
- [ ] Sync calories to Google Fit
- [ ] Sync macros (protein, carbs, fat)
- [ ] Import active calories from workouts
- [ ] Weight tracking sync
- [ ] Step count integration

#### 9. Intermittent Fasting
- [ ] Fasting timer with countdown
- [ ] Eating window tracking
- [ ] Fasting benefits timeline (8 milestones)
- [ ] Water intake tracking during fasting
- [ ] Calendar history view
- [ ] Smart notifications

**Fasting Benefits Timeline:**
- 0h: Fast Started
- 4h: Insulin Drops
- 8h: Glucose Used
- 12h: Fat Burning Begins
- 14h: Growth Hormone Rises
- 16h: Autophagy Begins
- 18h: Deep Ketosis
- 24h: Cell Regeneration

#### 10. Home Screen Widgets
- [ ] Calorie Progress Widget
- [ ] Fasting Timer Widget
- [ ] Nutrition Summary Widget
- [ ] Glance widgets for Wear OS (optional)

### Phase 3: Premium & Monetization

#### 11. Premium Subscription
- [ ] Google Play Billing integration
- [ ] Monthly subscription: $4.99/month
- [ ] Yearly subscription: $39.99/year
- [ ] Lifetime purchase: $79.99
- [ ] Premium upgrade UI
- [ ] Restore purchases functionality

#### 12. Premium Features
- [ ] AI Food Recognition (camera-based)
- [ ] Advanced Analytics dashboard
- [ ] Meal Planning capabilities
- [ ] Unlimited custom recipes
- [ ] Data export (CSV, PDF)
- [ ] Priority support
- [ ] Custom macro targets
- [ ] Smart AI-powered insights

### Phase 4: Infrastructure

#### 13. Error Handling & Offline Mode
- [ ] Network connectivity monitoring
- [ ] Offline data caching
- [ ] Error UI with retry options
- [ ] Graceful degradation when offline
- [ ] Cache management

#### 14. Security
- [ ] Secure API key storage (EncryptedSharedPreferences)
- [ ] Input validation
- [ ] HTTPS enforcement
- [ ] ProGuard/R8 obfuscation
- [ ] Root detection (optional)

#### 15. Crash Reporting
- [ ] Firebase Crashlytics integration
- [ ] Custom error logging
- [ ] Analytics events

---

## Project Structure (Planned)

```
CalTrackProAndroid/
├── app/
│   ├── src/main/
│   │   ├── java/com/easyaiflows/caltrackpro/
│   │   │   ├── data/
│   │   │   │   ├── local/           # Room database, DAOs
│   │   │   │   ├── remote/          # API services, Retrofit
│   │   │   │   └── repository/      # Data repositories
│   │   │   ├── di/                  # Hilt dependency injection
│   │   │   ├── domain/
│   │   │   │   ├── model/           # Domain models
│   │   │   │   └── usecase/         # Business logic
│   │   │   ├── ui/
│   │   │   │   ├── components/      # Reusable Compose components
│   │   │   │   ├── screens/         # Screen composables
│   │   │   │   ├── theme/           # Material theme
│   │   │   │   └── navigation/      # Navigation setup
│   │   │   ├── util/                # Utilities and extensions
│   │   │   ├── widget/              # Home screen widgets
│   │   │   └── CalTrackProApp.kt    # Application class
│   │   ├── res/
│   │   │   ├── drawable/
│   │   │   ├── values/
│   │   │   └── xml/
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
├── tasks/                           # PRD documents
├── build.gradle.kts
├── settings.gradle.kts
├── CLAUDE.md
├── GIT-COMMIT-GUIDELINES.md
└── README.md
```

---

## API Configuration

### Edamam API (Same as iOS)
The app uses the Edamam Food Database API for nutrition data.
- Base URL: `https://api.edamam.com/`
- Endpoints: Food Database API, Nutrition Analysis API

**API Keys:** Store securely using EncryptedSharedPreferences or BuildConfig fields.

---

## Android-Specific Considerations

### iOS to Android Equivalents

| iOS | Android |
|-----|---------|
| SwiftUI | Jetpack Compose |
| SwiftData | Room Database |
| HealthKit | Google Fit / Health Connect |
| StoreKit | Google Play Billing |
| AVFoundation (Camera) | CameraX + ML Kit |
| Speech Framework | Android Speech Recognition |
| WidgetKit | App Widgets + Glance |
| Keychain | EncryptedSharedPreferences |
| UserDefaults | SharedPreferences / DataStore |

### Permissions Required
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.VIBRATE" />
<uses-feature android:name="android.hardware.camera" android:required="false" />
```

---

## Development Workflow

### Before Each Session
1. Check git status and sync with GitHub
2. Review pending tasks in `tasks/` directory
3. Ensure Android Studio project builds successfully

### Adding New Features
1. Create PRD using `/Users/ronniecraig/ai-dev-tasks/create-prd.md`
2. Generate tasks using `/Users/ronniecraig/ai-dev-tasks/generate-tasks.md`
3. Implement using `/Users/ronniecraig/ai-dev-tasks/process-task-list.md`
4. Test on emulator and physical device
5. Commit with conventional commit messages

### Testing Requirements
- Test on Android emulator (API 26+)
- Test on physical Android device
- Verify all main screens function correctly
- Test food search with various queries
- Ensure data persists between app launches
- Test barcode scanning with real products

---

## Google Play Store Requirements

### Before Submission
- [ ] App icon (512x512 PNG)
- [ ] Feature graphic (1024x500)
- [ ] Screenshots (phone + tablet)
- [ ] Privacy policy URL
- [ ] App description and metadata
- [ ] Content rating questionnaire
- [ ] Target audience declaration
- [ ] Data safety form

### Google Play Developer Account
- One-time $25 registration fee
- Account: (to be set up)

---

## Git Workflow

Follow conventional commits:
```bash
feat(scope): add new feature
fix(scope): fix bug
docs(scope): update documentation
refactor(scope): code refactoring
test(scope): add tests
chore(scope): maintenance tasks
```

---

## Current Status

**Phase:** Project Setup
**Next Steps:**
1. Install Android Studio
2. Create new Android project with Jetpack Compose
3. Set up project structure
4. Implement core Food Diary feature

---

## Important Notes

- This is a SEPARATE codebase from iOS CalTrackPro
- Do NOT make changes that affect the iOS project
- Follow Android best practices and Material Design guidelines
- Prioritize feature parity with iOS app
- Use Kotlin idioms and modern Android development patterns
