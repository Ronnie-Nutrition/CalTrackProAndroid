# Tasks: Voice Input Food Logging

## Relevant Files

- `app/src/main/AndroidManifest.xml` - Ensure RECORD_AUDIO permission is declared
- `app/src/main/java/com/easyaiflows/caltrackpro/util/SpeechRecognizerHelper.kt` - Speech recognition wrapper
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/VoiceFoodDatabase.kt` - Hardcoded 180+ food items
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/VoiceFoodItem.kt` - Food item data class
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/ParsedFood.kt` - Parsed result with quantity/size
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/usecase/ParseFoodFromTextUseCase.kt` - Parsing algorithm
- `app/src/main/java/com/easyaiflows/caltrackpro/util/QuantityParser.kt` - Quantity and size modifier detection
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/VoiceInputScreen.kt` - Main voice input screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/VoiceInputViewModel.kt` - State management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/VoiceInputUiState.kt` - Sealed class UI states
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/components/MicrophoneButton.kt` - Animated mic button
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/components/LiquidWaveAnimation.kt` - Wave animation
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/components/TranscriptionText.kt` - Live transcription display
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/voice/components/ParsedFoodCard.kt` - Editable food result card
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add VoiceInput route
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add navigation composable

### Notes

- Android SpeechRecognizer requires no additional dependencies (built into Android)
- Speech recognition typically requires network connectivity for best accuracy
- Test on physical device - emulator speech recognition may not work properly
- The food database is intentionally hardcoded for fast offline parsing
- Run `./gradlew assembleDebug` to verify compilation after each major task

## Tasks

- [ ] 1.0 Create Voice Food Database with 180+ common foods
  - [ ] 1.1 Create `VoiceFoodItem.kt` data class with name, aliases, calories, protein, carbs, fat, defaultServing
  - [ ] 1.2 Create `VoiceFoodDatabase.kt` object with hardcoded list of 180+ food items from PRD Appendix A
  - [ ] 1.3 Add protein foods: eggs, chicken, turkey, beef, pork, fish, shrimp (19 items)
  - [ ] 1.4 Add dairy foods: milk, cheese, yogurt, butter, ice cream (8 items)
  - [ ] 1.5 Add grains: toast, bagel, pancakes, oatmeal, rice, pasta (12 items)
  - [ ] 1.6 Add fast food/meals: burger, pizza, taco, burrito, fries, soup, salad (15 items)
  - [ ] 1.7 Add fruits: apple, banana, orange, grapes, berries, mango (11 items)
  - [ ] 1.8 Add vegetables: broccoli, carrots, potato, tomato, spinach (14 items)
  - [ ] 1.9 Add beverages: coffee, tea, juice, soda, smoothie, beer, wine (15 items)
  - [ ] 1.10 Add snacks/desserts: chips, cookies, cake, chocolate, nuts (14 items)
  - [ ] 1.11 Add breakfast items: avocado toast, breakfast burrito, french toast (7 items)
  - [ ] 1.12 Add aliases for common variations (e.g., "eggs" → "scrambled eggs", "fries" → "french fries")
  - [ ] 1.13 Create `getFoodsSortedByNameLength()` function for longest-first matching

- [ ] 2.0 Implement Food Parsing Algorithm
  - [ ] 2.1 Create `ParsedFood.kt` data class with food, quantity, sizeMultiplier, confidence, computed nutrition properties
  - [ ] 2.2 Create `QuantityParser.kt` utility with numeric quantity detection ("one", "two", "1", "2", "a", "some")
  - [ ] 2.3 Add size modifier detection to QuantityParser: small (0.75x), medium (1.0x), large (1.5x), huge (2.0x)
  - [ ] 2.4 Add ounce parsing: "8 oz" (0.67x), "12 oz" (1.0x), "16 oz" (1.33x), "20 oz" (1.67x), "24 oz" (2.0x), "32 oz" (2.67x)
  - [ ] 2.5 Add portion unit detection: "bowl of" (1.5x), "cup of" (1.0x), "plate of" (2.0x), "slice of" (1.0x)
  - [ ] 2.6 Create `ParseFoodFromTextUseCase.kt` with main parsing logic
  - [ ] 2.7 Implement text normalization: lowercase, remove filler phrases ("I ate", "I had", "for breakfast")
  - [ ] 2.8 Implement longest-first matching algorithm to find foods in text
  - [ ] 2.9 Implement overlap prevention using character range tracking
  - [ ] 2.10 Implement context window search (20 chars around food) for quantity/size detection
  - [ ] 2.11 Write unit tests for ParseFoodFromTextUseCase with various input scenarios

- [ ] 3.0 Build Speech Recognition Infrastructure
  - [ ] 3.1 Verify `RECORD_AUDIO` permission in AndroidManifest.xml
  - [ ] 3.2 Create `SpeechRecognizerHelper.kt` class wrapping Android SpeechRecognizer
  - [ ] 3.3 Implement `isAvailable(): Boolean` to check if speech recognition is supported
  - [ ] 3.4 Implement `startListening(onPartialResult, onFinalResult, onError)` with Intent configuration
  - [ ] 3.5 Configure Intent with: LANGUAGE_MODEL_FREE_FORM, "en-US" locale, partial results enabled
  - [ ] 3.6 Set EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS to 2000ms for auto-stop
  - [ ] 3.7 Implement `stopListening()` and `destroy()` lifecycle methods
  - [ ] 3.8 Map SpeechRecognizer error codes to user-friendly messages
  - [ ] 3.9 Add timeout handling (15 seconds max recording duration)
  - [ ] 3.10 Create Hilt provider for SpeechRecognizerHelper with application context

- [ ] 4.0 Create Voice Input UI Components
  - [ ] 4.1 Create `VoiceInputUiState.kt` sealed class: Idle, Recording, Processing, Results, Error, DemoMode
  - [ ] 4.2 Create `MicrophoneButton.kt` composable with large circular button design
  - [ ] 4.3 Add scale animation to MicrophoneButton on press (animateFloatAsState)
  - [ ] 4.4 Create `LiquidWaveAnimation.kt` using Canvas with circular waves emanating from center
  - [ ] 4.5 Implement wave animation: 3 concentric circles expanding outward with fade
  - [ ] 4.6 Create `TranscriptionText.kt` composable showing real-time text with fade-in animation
  - [ ] 4.7 Create `RecordingDurationIndicator.kt` showing elapsed seconds
  - [ ] 4.8 Create `ParsedFoodCard.kt` composable with food name, quantity selector, calories, macros, remove button
  - [ ] 4.9 Add quantity adjustment buttons (+/-) to ParsedFoodCard
  - [ ] 4.10 Create `VoiceResultsSummary.kt` showing total calories and macros for all detected foods
  - [ ] 4.11 Add haptic feedback utility for recording start/stop

- [ ] 5.0 Implement Voice Input Screen and ViewModel
  - [ ] 5.1 Create `VoiceInputEvent.kt` sealed class for navigation events
  - [ ] 5.2 Create `VoiceInputViewModel.kt` with StateFlow for UI state, SharedFlow for events
  - [ ] 5.3 Inject SpeechRecognizerHelper and ParseFoodFromTextUseCase into ViewModel
  - [ ] 5.4 Implement `startRecording()` method updating state to Recording
  - [ ] 5.5 Implement `onPartialResult(text)` to update transcription in real-time
  - [ ] 5.6 Implement `onFinalResult(text)` to trigger parsing and show results
  - [ ] 5.7 Implement `parseTranscription(text)` using ParseFoodFromTextUseCase
  - [ ] 5.8 Implement `removeFood(index)`, `adjustQuantity(index, newQty)` for result editing
  - [ ] 5.9 Implement `addMissingFood()` to navigate to food search
  - [ ] 5.10 Implement `addToDiary(mealType)` to save all parsed foods
  - [ ] 5.11 Create `VoiceInputScreen.kt` composable with permission handling
  - [ ] 5.12 Add microphone permission request using Accompanist Permissions
  - [ ] 5.13 Build Recording state UI: wave animation + mic button + transcription text
  - [ ] 5.14 Build Results state UI: list of ParsedFoodCards + meal selector + add button
  - [ ] 5.15 Add "Add Missing Food" button that navigates to food search with transcription as query
  - [ ] 5.16 Add meal type selector dropdown (default based on time of day)
  - [ ] 5.17 Implement back navigation with confirmation if results exist

- [ ] 6.0 Implement Demo Mode for Free Users
  - [ ] 6.1 Create demo example text constant: "I ate a turkey sandwich and an apple"
  - [ ] 6.2 Create `DemoModeContent.kt` composable showing simulated voice input
  - [ ] 6.3 Implement 3-second fake recording animation sequence
  - [ ] 6.4 Parse demo text and display results after animation completes
  - [ ] 6.5 Show upgrade prompt dialog after demo results are displayed
  - [ ] 6.6 Add "Try Again" button to replay demo
  - [ ] 6.7 In ViewModel, check premium status on init and show demo mode for free users
  - [ ] 6.8 Create `VoiceDemoUpgradePrompt.kt` with premium benefits list

- [ ] 7.0 Integrate with Navigation and Food Diary
  - [ ] 7.1 Add `VoiceInput` route to `NavRoutes.kt` with mealType and date parameters
  - [ ] 7.2 Add composable entry in `CalTrackNavHost.kt` for VoiceInputScreen
  - [ ] 7.3 Add "Voice" option to DiaryScreen's add food FAB menu
  - [ ] 7.4 Implement navigation from VoiceInput to FoodSearch for adding missing foods
  - [ ] 7.5 Implement `saveFoodsToEntry()` in ViewModel using FoodEntryRepository
  - [ ] 7.6 Convert ParsedFood items to FoodEntry entities for saving
  - [ ] 7.7 Show success snackbar after adding foods
  - [ ] 7.8 Navigate back to diary on successful save
  - [ ] 7.9 Handle errors (no foods parsed, save failed) with retry option

- [ ] 8.0 Add Error Handling and Edge Cases
  - [ ] 8.1 Handle "no speech detected" error with user-friendly message and retry button
  - [ ] 8.2 Handle "no foods recognized" with suggestion: "Try saying specific foods like 'chicken and rice'"
  - [ ] 8.3 Handle network error (speech recognition may require internet)
  - [ ] 8.4 Handle microphone permission denied with button to open app settings
  - [ ] 8.5 Handle speech recognizer unavailable (some devices/regions) with graceful fallback message
  - [ ] 8.6 Handle recording timeout (15 seconds) with auto-stop and process
  - [ ] 8.7 Add analytics events: voice_started, voice_completed, voice_error, food_parsed, food_added
