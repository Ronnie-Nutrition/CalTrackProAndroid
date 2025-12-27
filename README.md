# CalTrackPro Android

A comprehensive nutrition tracking app for Android - the Android version of the iOS CalTrackPro app.

## Features

### Core Features
- **Food Diary** - Track daily nutritional intake with detailed macro tracking
- **Food Search** - Search 900,000+ foods via Edamam API
- **Barcode Scanner** - Scan product barcodes for instant nutrition data
- **Recipe Management** - Create and save custom recipes
- **Nutrition Insights** - Visualize trends with beautiful charts
- **User Profile** - Set personal goals and dietary preferences

### Enhanced Features
- **Voice Input** - Say "I ate a turkey sandwich" to log food
- **Google Fit Integration** - Sync nutrition data with Google Fit
- **Intermittent Fasting** - Timer, benefits timeline, water tracking
- **Home Screen Widgets** - Quick access to your progress

### Premium Features
- AI-powered food recognition
- Advanced analytics dashboard
- Meal planning capabilities
- Data export (CSV, PDF)
- Priority support

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Database**: Room
- **Networking**: Retrofit + OkHttp
- **DI**: Hilt
- **Barcode**: ML Kit
- **Health**: Google Fit / Health Connect

## Requirements

- Android 8.0 (API 26) or higher
- Google Play Services (for some features)

## Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle files
4. Run on emulator or device

## Project Structure

```
app/src/main/java/com/easyaiflows/caltrackpro/
├── data/           # Data layer (Room, API, Repositories)
├── di/             # Dependency injection modules
├── domain/         # Business logic and use cases
├── ui/             # Jetpack Compose UI
├── util/           # Utilities and extensions
└── widget/         # Home screen widgets
```

## Related Projects

- [CalTrackPro iOS](https://github.com/Ronnie-Nutrition/CalTrackProfixed) - iOS version

## License

Proprietary - All rights reserved

## Contact

- Developer: Ronnie Craig
- Email: extremenutrition.craig@gmail.com
