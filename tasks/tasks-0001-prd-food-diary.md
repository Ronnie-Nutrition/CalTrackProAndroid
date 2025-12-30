# Task List: Food Diary Core Functionality

**PRD Reference:** `0001-prd-food-diary.md`
**Status:** Complete
**Created:** 2024-12-29

---

## Relevant Files

### Infrastructure & Configuration
- `app/build.gradle.kts` - Add Room, Hilt, Navigation, ViewModel dependencies
- `build.gradle.kts` (project-level) - Add Hilt and KSP plugins
- `gradle/libs.versions.toml` - Add version catalog entries for new dependencies
- `app/src/main/java/com/easyaiflows/caltrackpro/CalTrackProApp.kt` - Hilt Application class
- `app/src/main/AndroidManifest.xml` - Register Application class

### Data Layer
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/entity/FoodEntryEntity.kt` - Room entity for food entries
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/dao/FoodEntryDao.kt` - Data access object with queries
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/CalTrackDatabase.kt` - Room database definition
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/converter/Converters.kt` - Type converters for Room
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodEntryRepository.kt` - Repository interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodEntryRepositoryImpl.kt` - Repository implementation

### Domain Layer
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FoodEntry.kt` - Domain model for food entries
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/MealType.kt` - MealType enum
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/NutritionGoals.kt` - Default nutrition goals

### Dependency Injection
- `app/src/main/java/com/easyaiflows/caltrackpro/di/DatabaseModule.kt` - Hilt module for database
- `app/src/main/java/com/easyaiflows/caltrackpro/di/RepositoryModule.kt` - Hilt module for repositories

### UI - Diary Screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryScreen.kt` - Main diary screen composable
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryViewModel.kt` - ViewModel for diary screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryUiState.kt` - UI state data class
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/components/DailySummaryCard.kt` - Nutrition summary card
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/components/MealSection.kt` - Meal section with entries
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/components/FoodEntryItem.kt` - Individual food entry row
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/components/DateSelector.kt` - Date navigation component

### UI - Entry Screens
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/entry/ManualEntryScreen.kt` - Manual food entry screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/entry/ManualEntryViewModel.kt` - ViewModel for manual entry
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/entry/EditEntryScreen.kt` - Edit existing entry screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/components/AddFoodOptionsSheet.kt` - Bottom sheet for add options

### Navigation
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Navigation route definitions
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Navigation host setup
- `app/src/main/java/com/easyaiflows/caltrackpro/MainActivity.kt` - Updated with navigation

### Tests
- `app/src/test/java/com/easyaiflows/caltrackpro/data/repository/FoodEntryRepositoryTest.kt` - Repository unit tests
- `app/src/test/java/com/easyaiflows/caltrackpro/ui/diary/DiaryViewModelTest.kt` - ViewModel unit tests
- `app/src/androidTest/java/com/easyaiflows/caltrackpro/data/local/FoodEntryDaoTest.kt` - DAO instrumented tests

### Notes

- Unit tests should be placed alongside the code files they test
- Use `./gradlew test` to run unit tests
- Use `./gradlew connectedAndroidTest` to run instrumented tests
- Follow MVVM pattern with unidirectional data flow
- Use Kotlin Flow for reactive data streams from Room

---

## Tasks

- [x] 1.0 Set up project dependencies and infrastructure (Room, Hilt, Navigation)
  - [x] 1.1 Update `gradle/libs.versions.toml` with Room, Hilt, Navigation, and KSP versions
  - [x] 1.2 Update project-level `build.gradle.kts` to add Hilt and KSP plugins
  - [x] 1.3 Update `app/build.gradle.kts` to apply plugins and add all dependencies
  - [x] 1.4 Create `CalTrackProApp.kt` Application class with `@HiltAndroidApp` annotation
  - [x] 1.5 Update `AndroidManifest.xml` to register the Application class
  - [x] 1.6 Add `@AndroidEntryPoint` to `MainActivity.kt`
  - [x] 1.7 Sync Gradle and verify project builds successfully

- [x] 2.0 Create data layer (entities, DAOs, database, repository)
  - [x] 2.1 Create `MealType.kt` enum in domain/model (BREAKFAST, LUNCH, DINNER, SNACK)
  - [x] 2.2 Create `FoodEntryEntity.kt` Room entity with all fields from PRD
  - [x] 2.3 Create `Converters.kt` for Room type converters (ByteArray, etc.)
  - [x] 2.4 Create `FoodEntryDao.kt` with insert, update, delete, and query methods
  - [x] 2.5 Create `CalTrackDatabase.kt` Room database with FoodEntryEntity
  - [x] 2.6 Create `FoodEntryRepository.kt` interface defining repository contract
  - [x] 2.7 Create `FoodEntryRepositoryImpl.kt` implementing the repository with DAO
  - [x] 2.8 Create `DatabaseModule.kt` Hilt module providing Database and DAO
  - [x] 2.9 Create `RepositoryModule.kt` Hilt module binding repository implementation

- [x] 3.0 Create domain layer (models and mappers)
  - [x] 3.1 Create `FoodEntry.kt` domain model with computed properties (totalCalories, etc.)
  - [x] 3.2 Create `NutritionGoals.kt` data class with default values (2000 cal, 150g protein, etc.)
  - [x] 3.3 Add mapper extension functions to convert between Entity and Domain models
  - [x] 3.4 Add helper extension for grouping entries by MealType

- [x] 4.0 Build Diary Screen UI with daily summary and meal sections
  - [x] 4.1 Create `DiaryUiState.kt` data class for screen state (date, entries, goals, loading, error)
  - [x] 4.2 Create `DiaryViewModel.kt` with date selection, entry loading, and state management
  - [x] 4.3 Create `DateSelector.kt` component with date picker dialog
  - [x] 4.4 Create `DailySummaryCard.kt` component showing calories and macro progress bars
  - [x] 4.5 Create `FoodEntryItem.kt` component displaying entry name, serving, and calories
  - [x] 4.6 Create `MealSection.kt` component with header, calorie subtotal, and entry list
  - [x] 4.7 Create `DiaryScreen.kt` composable assembling all components with LazyColumn
  - [x] 4.8 Add FAB to DiaryScreen that triggers AddFoodOptionsSheet
  - [x] 4.9 Create `AddFoodOptionsSheet.kt` bottom sheet with Search/Scan/Manual options

- [x] 5.0 Implement Manual Entry / Edit Entry screens
  - [x] 5.1 Create `ManualEntryViewModel.kt` with form state and validation logic
  - [x] 5.2 Create `ManualEntryScreen.kt` with input fields for all required nutrition data
  - [x] 5.3 Add serving unit picker (g, oz, cup, piece, ml, tbsp, tsp)
  - [x] 5.4 Implement form validation - disable Save button until all required fields valid
  - [x] 5.5 Create `EditEntryScreen.kt` reusing ManualEntry components with pre-populated data
  - [x] 5.6 Handle save action in ViewModel - insert new or update existing entry

- [x] 6.0 Set up navigation and integrate all screens
  - [x] 6.1 Create `NavRoutes.kt` sealed class defining all routes (Diary, ManualEntry, EditEntry)
  - [x] 6.2 Create `CalTrackNavHost.kt` with NavHost and composable destinations
  - [x] 6.3 Update `MainActivity.kt` to use CalTrackNavHost as content
  - [x] 6.4 Wire up navigation from DiaryScreen FAB → ManualEntryScreen with mealType argument
  - [x] 6.5 Wire up navigation from FoodEntryItem tap → EditEntryScreen with entryId argument
  - [x] 6.6 Handle back navigation and save/cancel flows

- [x] 7.0 Implement entry actions (duplicate, delete with confirmation)
  - [x] 7.1 Add long-press context menu to FoodEntryItem (Edit, Duplicate, Delete options)
  - [x] 7.2 Implement duplicate action in DiaryViewModel - copy entry with new ID and timestamp
  - [x] 7.3 Create delete confirmation dialog composable
  - [x] 7.4 Implement delete action in DiaryViewModel with confirmation flow
  - [x] 7.5 Add visual feedback (snackbar) for successful duplicate/delete actions
  - [x] 7.6 Test all entry actions work correctly and data persists
