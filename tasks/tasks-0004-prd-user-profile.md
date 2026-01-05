# Tasks: User Profile & Goals

**PRD:** [0004-prd-user-profile.md](0004-prd-user-profile.md)
**Status:** Complete

---

## Relevant Files

### New Files
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/UserProfile.kt` - User profile data class with all profile fields
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/Sex.kt` - Sex enum (Male, Female)
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/ActivityLevel.kt` - Activity level enum with multipliers
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/WeightGoal.kt` - Weight goal enum with calorie adjustments
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/MacroPreset.kt` - Macro preset enum with percentages
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/UnitSystem.kt` - Unit system enum (Metric, Imperial)
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/UserProfileRepository.kt` - Repository interface for profile operations
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/UserProfileRepositoryImpl.kt` - DataStore-backed repository implementation
- `app/src/main/java/com/easyaiflows/caltrackpro/di/DataStoreModule.kt` - Hilt module for DataStore injection
- `app/src/main/java/com/easyaiflows/caltrackpro/util/NutritionCalculator.kt` - BMR/TDEE/macro calculation utilities
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/onboarding/OnboardingScreen.kt` - Multi-page onboarding wizard
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/onboarding/OnboardingViewModel.kt` - Onboarding state management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/onboarding/OnboardingUiState.kt` - Onboarding UI state class
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/onboarding/components/` - Reusable onboarding page components
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/profile/ProfileScreen.kt` - Settings/profile editing screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/profile/ProfileViewModel.kt` - Profile screen state management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/profile/ProfileUiState.kt` - Profile screen UI state

### Modified Files
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add Onboarding and Profile routes
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add new screen destinations
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryScreen.kt` - Add settings icon to TopAppBar
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryViewModel.kt` - Observe profile/goals changes
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryUiState.kt` - May need updates for goals
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/NutritionGoals.kt` - Update to support calculated goals
- `app/src/main/java/com/easyaiflows/caltrackpro/di/RepositoryModule.kt` - Bind UserProfileRepository
- `app/build.gradle.kts` - Add DataStore dependency
- `gradle/libs.versions.toml` - Add DataStore version

### Notes

- Use Preferences DataStore for simple key-value profile storage
- Follow existing repository pattern (interface + Impl with Hilt binding)
- Onboarding uses HorizontalPager from Compose Foundation
- Unit tests can be added later; focus on implementation first

---

## Tasks

- [x] 1.0 Create domain models and enums for user profile
  - [x] 1.1 Create `Sex` enum with Male and Female values
  - [x] 1.2 Create `ActivityLevel` enum with Sedentary (1.2), Active (1.55), VeryActive (1.9) multipliers
  - [x] 1.3 Create `WeightGoal` enum with Lose (-500), Maintain (0), Gain (+300) calorie adjustments
  - [x] 1.4 Create `MacroPreset` enum with Balanced (30/40/30), LowCarb (35/25/40), HighProtein (40/35/25), Custom percentages
  - [x] 1.5 Create `UnitSystem` enum with Metric and Imperial, include conversion utilities
  - [x] 1.6 Create `UserProfile` data class with all fields (age, sex, weight, height, activityLevel, weightGoal, macroPreset, unitSystem, customMacros, calorieOverride, onboardingCompleted)
  - [x] 1.7 Add validation functions to UserProfile (age 13-120, weight 20-500kg, height 100-250cm)

- [x] 2.0 Set up DataStore and UserProfileRepository
  - [x] 2.1 Add DataStore dependency to build.gradle.kts and libs.versions.toml
  - [x] 2.2 Create `DataStoreModule.kt` Hilt module that provides Preferences DataStore instance
  - [x] 2.3 Create `UserProfileRepository` interface with Flow<UserProfile>, suspend save/update methods, and onboarding status
  - [x] 2.4 Create `UserProfileRepositoryImpl` that serializes UserProfile to DataStore preferences
  - [x] 2.5 Bind repository in `RepositoryModule.kt` with @Singleton scope
  - [x] 2.6 Add default UserProfile values for first-time users

- [x] 3.0 Implement nutrition calculation utilities (BMR/TDEE/macros)
  - [x] 3.1 Create `NutritionCalculator` object with calculateBMR(sex, weight, height, age) using Mifflin-St Jeor formula
  - [x] 3.2 Add calculateTDEE(bmr, activityLevel) that applies activity multiplier
  - [x] 3.3 Add calculateTargetCalories(tdee, weightGoal) that applies goal adjustment
  - [x] 3.4 Add calculateMacroGrams(calories, proteinPct, carbsPct, fatPct) for gram conversion
  - [x] 3.5 Add getMacrosForPreset(preset, calories) that returns protein, carbs, fat grams
  - [x] 3.6 Add unit conversion helpers: lbsToKg, kgToLbs, inchesToCm, cmToInches, feetInchesToCm

- [x] 4.0 Build onboarding flow UI and logic
  - [x] 4.1 Create `OnboardingUiState` data class with currentPage, profile fields, calculatedCalories, calculatedMacros, validation errors
  - [x] 4.2 Create `OnboardingViewModel` with page navigation, field updates, calculation triggers, and save logic
  - [x] 4.3 Create Welcome page component with app introduction and "Get Started" button
  - [x] 4.4 Create Personal Info page with age input (number picker) and sex selection (segmented buttons)
  - [x] 4.5 Create Body Metrics page with weight/height inputs and unit toggle (with live conversion preview)
  - [x] 4.6 Create Activity Level page with three large selectable cards (Sedentary, Active, Very Active) with descriptions
  - [x] 4.7 Create Weight Goal page with Lose/Maintain/Gain options and calorie impact preview
  - [x] 4.8 Create Macro Preset page with Balanced/Low Carb/High Protein cards showing percentages
  - [x] 4.9 Create Review page showing calculated goals with optional calorie/macro override inputs
  - [x] 4.10 Create Completion page with summary and "Start Tracking" button
  - [x] 4.11 Build `OnboardingScreen` composable with HorizontalPager, page indicators, and Back/Next buttons
  - [x] 4.12 Add input validation with error messages on each page

- [x] 5.0 Build profile/settings screen
  - [x] 5.1 Create `ProfileUiState` data class with profile data, editing flags, and save status
  - [x] 5.2 Create `ProfileViewModel` with load, edit, and save profile logic
  - [x] 5.3 Build Profile section UI with editable personal info (age, sex) in expandable card
  - [x] 5.4 Build Body Metrics section with weight/height editors and unit toggle
  - [x] 5.5 Build Goals section with activity level, weight goal, and macro preset selectors
  - [x] 5.6 Build Calorie Goal subsection showing calculated vs custom with "Reset to calculated" button
  - [x] 5.7 Build Custom Macros subsection with percentage sliders (must sum to 100%)
  - [x] 5.8 Build Preferences section with Unit System toggle
  - [x] 5.9 Assemble `ProfileScreen` with TopAppBar (back navigation) and save confirmation

- [x] 6.0 Integrate user profile with existing Diary feature
  - [x] 6.1 Add Onboarding and Profile routes to `NavRoutes.kt`
  - [x] 6.2 Add Onboarding and Profile screen destinations to `CalTrackNavHost.kt`
  - [x] 6.3 Update CalTrackNavHost to check onboarding status and conditionally set start destination
  - [x] 6.4 Add Settings icon button to DiaryScreen TopAppBar that navigates to ProfileScreen
  - [x] 6.5 Inject `UserProfileRepository` into `DiaryViewModel`
  - [x] 6.6 Update `DiaryViewModel` to combine profile goals flow with entries flow
  - [x] 6.7 Update `DiaryUiState.goals` to use dynamic goals from repository instead of hardcoded defaults
  - [x] 6.8 Update `NutritionGoals` data class to support creation from UserProfile
  - [x] 6.9 Test end-to-end flow: onboarding → diary with personalized goals → profile editing → goals update
