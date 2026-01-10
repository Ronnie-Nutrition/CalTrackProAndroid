# Tasks: AI Photo Recognition

## Relevant Files

- `app/build.gradle.kts` - Add ML Kit Image Labeling dependency
- `app/src/main/java/com/easyaiflows/caltrackpro/di/NetworkModule.kt` - Add OpenAI Retrofit service
- `app/src/main/java/com/easyaiflows/caltrackpro/di/RepositoryModule.kt` - Bind FoodRecognitionRepository
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/OpenAIService.kt` - OpenAI Vision API interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/dto/OpenAIVisionRequest.kt` - Request DTOs
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/dto/OpenAIVisionResponse.kt` - Response DTOs
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodRecognitionRepository.kt` - Repository interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodRecognitionRepositoryImpl.kt` - Repository implementation
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/FoodRecognitionResult.kt` - Domain model for recognition results
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/RecognitionType.kt` - Enum for ML_KIT vs OPENAI
- `app/src/main/java/com/easyaiflows/caltrackpro/util/ImageProcessor.kt` - Image resize, compress, Base64 utilities
- `app/src/main/java/com/easyaiflows/caltrackpro/util/FoodVocabulary.kt` - ML Kit food label filter vocabulary
- `app/src/main/java/com/easyaiflows/caltrackpro/util/SecureStorage.kt` - EncryptedSharedPreferences wrapper
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/AIFoodRecognitionScreen.kt` - Main camera/capture screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/AIFoodRecognitionViewModel.kt` - ViewModel with state management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/AIFoodRecognitionUiState.kt` - Sealed class UI states
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/RecognitionResultsScreen.kt` - Results display and editing
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/components/CaptureButton.kt` - Animated capture button
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/components/RecognizedFoodCard.kt` - Food result card with editing
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/recognition/components/ProcessingOverlay.kt` - Loading animation overlay
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add AIFoodRecognition route
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add navigation composable

### Notes

- Follow existing patterns from `BarcodeScannerScreen.kt` for CameraX integration
- Use `hiltViewModel()` for ViewModel injection in Compose
- Test ML Kit recognition on physical device (emulator has limitations)
- OpenAI API key should be stored in EncryptedSharedPreferences, not BuildConfig
- Run `./gradlew assembleDebug` to verify compilation after each major task

## Tasks

- [ ] 1.0 Set up ML Kit Image Labeling infrastructure and food vocabulary filter
  - [ ] 1.1 Add ML Kit Image Labeling dependency to `app/build.gradle.kts`: `implementation("com.google.mlkit:image-labeling:17.0.8")`
  - [ ] 1.2 Create `FoodVocabulary.kt` in `/util/` with 150+ food-related terms as a Set for filtering
  - [ ] 1.3 Create `MLKitFoodRecognizer.kt` class that wraps `ImageLabeling.getClient()` with default options
  - [ ] 1.4 Implement `recognizeFood(bitmap: Bitmap): List<FoodRecognitionResult>` method that filters labels against food vocabulary
  - [ ] 1.5 Add confidence threshold (0.5) and return top 3 food matches sorted by confidence
  - [ ] 1.6 Test ML Kit recognition with sample food images on physical device

- [ ] 2.0 Create OpenAI Vision API service with secure key storage
  - [ ] 2.1 Create `SecureStorage.kt` utility class using `EncryptedSharedPreferences` for API key storage
  - [ ] 2.2 Add methods `saveOpenAIKey(key: String)` and `getOpenAIKey(): String?` to SecureStorage
  - [ ] 2.3 Create `OpenAIService.kt` Retrofit interface with `@POST("v1/chat/completions")` endpoint
  - [ ] 2.4 Create `OpenAIVisionRequest.kt` DTO with nested message structure for vision API
  - [ ] 2.5 Create `OpenAIVisionResponse.kt` DTO to parse JSON response with choices array
  - [ ] 2.6 Add OpenAI Retrofit instance to `NetworkModule.kt` with Bearer token interceptor
  - [ ] 2.7 Create `OpenAIVisionPrompt.kt` constant with the food analysis prompt from PRD Appendix B
  - [ ] 2.8 Implement JSON parsing for OpenAI response (handle markdown-wrapped JSON)

- [ ] 3.0 Build image processing utilities (resize, compress, Base64 encoding)
  - [ ] 3.1 Create `ImageProcessor.kt` utility object in `/util/`
  - [ ] 3.2 Implement `resizeToMaxDimension(bitmap: Bitmap, maxSize: Int = 512): Bitmap` function
  - [ ] 3.3 Implement `compressToJpeg(bitmap: Bitmap, quality: Int = 80): ByteArray` function
  - [ ] 3.4 Implement `toBase64(byteArray: ByteArray): String` function using `Base64.encodeToString()`
  - [ ] 3.5 Implement `processForAPI(bitmap: Bitmap): String` combining resize, compress, and Base64
  - [ ] 3.6 Add `rotateBitmapIfRequired(bitmap: Bitmap, uri: Uri): Bitmap` for EXIF orientation handling
  - [ ] 3.7 Write unit tests for ImageProcessor functions

- [ ] 4.0 Implement FoodRecognitionRepository with dual-tier recognition logic
  - [ ] 4.1 Create `FoodRecognitionResult.kt` domain model with name, confidence, calories, protein, carbs, fat, servingSize
  - [ ] 4.2 Create `RecognitionType.kt` enum with ML_KIT and OPENAI values
  - [ ] 4.3 Create `FoodRecognitionRepository.kt` interface with `recognizeFood(bitmap, isPremium): Result<List<FoodRecognitionResult>>`
  - [ ] 4.4 Create `FoodRecognitionRepositoryImpl.kt` implementing the interface
  - [ ] 4.5 Implement ML Kit recognition path: call MLKitFoodRecognizer, then lookup nutrition via existing FoodSearchRepository
  - [ ] 4.6 Implement OpenAI recognition path: process image, call API, parse response
  - [ ] 4.7 Add fallback logic: if OpenAI fails and isPremium, fall back to ML Kit
  - [ ] 4.8 Add `@Binds` entry in `RepositoryModule.kt` for FoodRecognitionRepository
  - [ ] 4.9 Implement nutrition enhancement by cross-referencing with USDA API (use existing EdamamApiService)

- [ ] 5.0 Create AI Food Recognition UI (Camera screen, Results screen, ViewModel)
  - [ ] 5.1 Create `AIFoodRecognitionUiState.kt` sealed class with states: Idle, CameraPreview, Capturing, Processing, Results, Error
  - [ ] 5.2 Create `AIFoodRecognitionEvent.kt` sealed class for navigation events
  - [ ] 5.3 Create `AIFoodRecognitionViewModel.kt` with StateFlow for UI state and SharedFlow for events
  - [ ] 5.4 Implement `capturePhoto()`, `processImage()`, `removeFood()`, `adjustServing()`, `addToDiary()` methods in ViewModel
  - [ ] 5.5 Create `AIFoodRecognitionScreen.kt` with CameraX preview (reuse pattern from BarcodeScannerScreen)
  - [ ] 5.6 Add camera permission handling using Accompanist Permissions library
  - [ ] 5.7 Create `CaptureButton.kt` component with pulse animation on press
  - [ ] 5.8 Add torch toggle button (reuse from barcode scanner)
  - [ ] 5.9 Add gallery button (visible only for premium users) using `ActivityResultContracts.GetContent()`
  - [ ] 5.10 Create `ProcessingOverlay.kt` component with loading animation and "Analyzing your meal..." text
  - [ ] 5.11 Create `RecognitionResultsScreen.kt` (or section) showing captured image and detected foods list
  - [ ] 5.12 Create `RecognizedFoodCard.kt` component with food name, confidence badge, calories, macros, remove button, serving adjuster
  - [ ] 5.13 Add meal type selector (Breakfast, Lunch, Dinner, Snack) dropdown
  - [ ] 5.14 Add "Add to Diary" button that calls ViewModel to save all foods
  - [ ] 5.15 Add "Add Missing Food" button that navigates to food search

- [ ] 6.0 Integrate with Food Diary and Navigation
  - [ ] 6.1 Add `AIFoodRecognition` route to `NavRoutes.kt` with mealType and date parameters
  - [ ] 6.2 Add composable entry in `CalTrackNavHost.kt` for AIFoodRecognitionScreen
  - [ ] 6.3 Add "Photo" option to DiaryScreen's add food FAB menu (alongside Search, Barcode, Manual)
  - [ ] 6.4 Implement navigation from AIFoodRecognition results to FoodDetail for individual food editing
  - [ ] 6.5 Implement `addFoodsToEntry()` in ViewModel using existing FoodEntryRepository
  - [ ] 6.6 Show success snackbar and navigate back to Diary after adding foods
  - [ ] 6.7 Handle back navigation properly (confirm discard if results exist)

- [ ] 7.0 Add premium gating and upgrade prompts
  - [ ] 7.1 Create `PremiumManager.kt` utility (or use existing subscription check) to verify premium status
  - [ ] 7.2 In ViewModel, check premium status to decide ML Kit vs OpenAI path
  - [ ] 7.3 Show "Upgrade for better accuracy" banner after ML Kit results for free users
  - [ ] 7.4 Gate gallery access behind premium check with upgrade prompt on tap
  - [ ] 7.5 Add "PRO" badge to premium-only UI elements (gallery button, OpenAI indicator)
  - [ ] 7.6 Create `UpgradePromptDialog.kt` reusable component for premium upsell
  - [ ] 7.7 Track analytics events: recognition_started, recognition_completed, upgrade_prompt_shown, upgrade_clicked
