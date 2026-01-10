# PRD: Voice Input Food Logging

## 1. Introduction/Overview

Voice Input enables users to log food by speaking naturally, such as "I ate a turkey sandwich and an apple." The app uses speech-to-text recognition to transcribe the user's voice, then parses the text to identify foods, quantities, and portion sizes. Detected foods are matched against a built-in nutrition database and added to the food diary.

**Problem Solved:** Typing food names and searching through databases is slow and cumbersome, especially when logging multiple items. Voice input provides a hands-free, natural way to log meals quickly - ideal for busy users, those cooking, or users with accessibility needs.

**Android Enhancement:** This implementation uses Android's native Speech Recognition API with an enhanced parsing algorithm that handles natural language variations, size modifiers, and quantity expressions. Premium users get full voice logging; free users see a demo preview.

## 2. Goals

1. **Enable natural language logging** - Users speak conversationally ("I had two eggs and toast")
2. **Achieve 90%+ parse accuracy** - Correctly identify foods from natural speech for common items
3. **Support multi-food detection** - Parse multiple foods from a single utterance
4. **Handle quantities and sizes** - Recognize "large coffee", "2 eggs", "8 oz steak"
5. **Provide instant feedback** - Show real-time transcription as user speaks
6. **Match iOS parity** - Equivalent food database and parsing logic to iOS CalTrackPro
7. **Work as premium feature** - Drive subscription conversions with demo mode for free users

## 3. User Stories

### Free Users (Demo Mode)
- **US-1:** As a free user, I want to see a demo of voice input so that I understand what I'm missing.
- **US-2:** As a free user, I want to see an upgrade prompt after the demo so that I can subscribe.

### Premium Users
- **US-3:** As a premium user, I want to speak my meal naturally so that I can log food hands-free.
- **US-4:** As a premium user, I want to say multiple foods at once so that I can log an entire meal quickly.
- **US-5:** As a premium user, I want to specify quantities ("two eggs") so that portions are accurate.
- **US-6:** As a premium user, I want to use size modifiers ("large coffee") so that calories reflect actual portions.
- **US-7:** As a premium user, I want to see real-time transcription so that I know I'm being heard correctly.
- **US-8:** As a premium user, I want to edit detected foods before adding so that I can correct mistakes.

### All Users
- **US-9:** As a user, I want clear visual feedback during recording so that I know the app is listening.
- **US-10:** As a user, I want the app to handle background noise gracefully so that recognition still works.

## 4. Functional Requirements

### 4.1 Speech Recognition
1. The system must use Android SpeechRecognizer API for voice-to-text conversion
2. The system must support English (US) locale as primary language
3. The system must display real-time partial transcription results as user speaks
4. The system must auto-stop recording after 2 seconds of silence
5. The system must have a maximum recording duration of 15 seconds
6. The system must handle microphone permission requests gracefully
7. The system must show appropriate error if speech recognition is unavailable

### 4.2 Recording UI
8. The system must display a prominent microphone button to start recording
9. The system must show animated visual feedback while recording (liquid wave animation)
10. The system must display the transcribed text in real-time above/below the mic button
11. The system must allow manual stop via tap on recording button
12. The system must show recording duration indicator
13. The system must provide haptic feedback on recording start/stop

### 4.3 Food Parsing
14. The system must parse transcribed text to identify food items
15. The system must use longest-first matching to prioritize specific foods ("turkey sandwich" before "sandwich")
16. The system must prevent overlap/double-counting of detected foods
17. The system must recognize numeric quantities ("1", "2", "three", "a", "an", "some", "few")
18. The system must recognize size modifiers with multipliers:
    - "small" = 0.75x
    - "medium" = 1.0x
    - "large" / "big" = 1.5x
    - "huge" / "extra large" = 2.0x
19. The system must recognize portion units ("bowl", "cup", "glass", "plate", "serving", "slice", "scoop")
20. The system must recognize ounce-based quantities:
    - "8 oz" = 0.67x
    - "12 oz" = 1.0x
    - "16 oz" = 1.33x
    - "20 oz" = 1.67x
    - "24 oz" = 2.0x
    - "32 oz" = 2.67x
21. The system must strip filler phrases ("I ate", "I had", "just ate", "for breakfast/lunch/dinner")
22. The system must handle conjunctions ("and", "with", "plus") to separate foods

### 4.4 Food Database
23. The system must include a built-in database of 180+ common foods with nutrition data
24. The system must store calories, protein, carbs, and fat for each food item
25. The system must support food aliases (e.g., "eggs" matches "scrambled eggs")
26. The system must match foods case-insensitively
27. The system must prioritize exact matches over partial matches

### 4.5 Results Display
28. The system must display all detected foods in a list format
29. The system must show for each food: name, quantity, calculated calories, macros
30. The system must allow users to remove incorrectly detected foods
31. The system must allow users to adjust quantities for each food
32. The system must allow users to add missing foods manually (via search)
33. The system must show total nutrition summary for all detected foods
34. The system must indicate confidence level if a match is uncertain

### 4.6 Diary Integration
35. The system must allow selection of meal type (Breakfast, Lunch, Dinner, Snack)
36. The system must default meal type based on current time of day
37. The system must add all confirmed foods to diary in a single action
38. The system must show success confirmation after adding
39. The system must navigate back to diary after successful addition

### 4.7 Demo Mode (Free Users)
40. The system must show a simulated voice input experience for free users
41. The system must use pre-recorded example: "I ate a turkey sandwich and an apple"
42. The system must animate a 3-second fake recording sequence
43. The system must display parsed results from the example
44. The system must show upgrade prompt after demo completes
45. The system must allow demo to be replayed

### 4.8 Error Handling
46. The system must handle "no speech detected" with user-friendly message
47. The system must handle "no foods recognized" with suggestion to try again
48. The system must handle network errors (speech recognition may require network)
49. The system must handle microphone permission denied with settings redirect
50. The system must handle speech recognizer unavailability (some devices/regions)

## 5. Non-Goals (Out of Scope)

1. **Multi-language support** - English only for initial release
2. **Custom food training** - Won't learn user's personal food names/nicknames
3. **Continuous listening** - Won't stay active waiting for "Hey CalTrack" trigger
4. **Offline speech recognition** - Requires network (Android limitation for accuracy)
5. **Voice commands** - Won't support "delete last entry" or "show my calories"
6. **Conversational AI** - Won't engage in back-and-forth clarification dialogs
7. **Restaurant menu recognition** - Won't understand "I had the #5 combo from McDonald's"
8. **Ingredient breakdown** - Won't parse "chicken salad" into chicken + lettuce + dressing

## 6. Design Considerations

### UI Flow
```
Diary Screen
    → Tap "+" FAB
    → Select "Voice" option
    → Voice Input Screen
        → [Free User] Show demo mode with example
        → [Premium User] Show mic button, tap to record
    → Recording state (wave animation, live transcription)
    → Auto-stop or manual stop
    → Processing ("Finding foods...")
    → Results Screen (detected foods list)
    → Edit foods (remove, adjust quantities, add missing)
    → Select meal type
    → Confirm → Added to Diary
```

### Visual Design
- Large, prominent microphone button (center of screen)
- Liquid wave animation around mic during recording (matches iOS)
- Real-time transcription text above mic button
- Clean white/light background for readability
- Results displayed as editable cards
- Premium badge on feature if showing to free user

### Animations
- Mic button scale animation on press
- Liquid wave animation during recording (circular waves emanating from center)
- Pulse effect on silence detection
- Text fade-in for transcription
- Card slide-in for results
- Success checkmark animation

### Accessibility
- Voice input itself is an accessibility feature
- Ensure all UI elements have content descriptions
- Support TalkBack for result editing
- High contrast mode support

## 7. Technical Considerations

### Dependencies
```kotlin
// Android Speech Recognition (built-in, no additional dependency)
// Uses android.speech.SpeechRecognizer

// For wave animation
implementation("com.github.nicepath:wave-loading-animation:1.0.0")
// Or custom Canvas animation
```

### Architecture
```
ui/screens/VoiceInputScreen.kt
ui/viewmodels/VoiceInputViewModel.kt
ui/components/LiquidWaveAnimation.kt
ui/components/MicrophoneButton.kt
domain/usecase/ParseFoodFromTextUseCase.kt
domain/model/ParsedFood.kt
domain/model/VoiceFoodDatabase.kt
data/local/VoiceFoodDatabase.kt (hardcoded foods)
util/SpeechRecognizerHelper.kt
```

### Speech Recognition Implementation
```kotlin
class SpeechRecognizerHelper(context: Context) {
    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

    fun startListening(
        onPartialResult: (String) -> Unit,
        onFinalResult: (String) -> Unit,
        onError: (Int) -> Unit
    ) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                     RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.startListening(intent)
    }
}
```

### Food Parsing Algorithm
```kotlin
fun parseFood(text: String): List<ParsedFood> {
    // 1. Normalize text (lowercase, remove filler phrases)
    val normalized = text.lowercase()
        .replace(Regex("i (ate|had|just ate|just had)"), "")
        .replace(Regex("for (breakfast|lunch|dinner|snack)"), "")
        .trim()

    // 2. Sort food database by name length (longest first)
    val sortedFoods = foodDatabase.sortedByDescending { it.name.length }

    // 3. Track used character ranges to prevent overlap
    val usedRanges = mutableListOf<IntRange>()
    val results = mutableListOf<ParsedFood>()

    // 4. Find matches
    for (food in sortedFoods) {
        val index = normalized.indexOf(food.name)
        if (index >= 0) {
            val range = index until (index + food.name.length)
            if (usedRanges.none { it.overlaps(range) }) {
                usedRanges.add(range)

                // 5. Look for quantity in surrounding context
                val quantity = findQuantity(normalized, index)
                val sizeMultiplier = findSizeModifier(normalized, index)

                results.add(ParsedFood(
                    food = food,
                    quantity = quantity,
                    sizeMultiplier = sizeMultiplier
                ))
            }
        }
    }

    return results
}
```

### Data Models
```kotlin
data class VoiceFoodItem(
    val name: String,
    val aliases: List<String> = emptyList(),
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val defaultServing: String = "1 serving"
)

data class ParsedFood(
    val food: VoiceFoodItem,
    val quantity: Int = 1,
    val sizeMultiplier: Double = 1.0,
    val confidence: Float = 1.0f
) {
    val totalCalories: Int get() = (food.calories * quantity * sizeMultiplier).toInt()
    val totalProtein: Double get() = food.protein * quantity * sizeMultiplier
    val totalCarbs: Double get() = food.carbs * quantity * sizeMultiplier
    val totalFat: Double get() = food.fat * quantity * sizeMultiplier
}

enum class RecordingState {
    IDLE,
    RECORDING,
    PROCESSING,
    RESULTS,
    ERROR
}
```

### Premium Gating
- Check subscription status on screen entry
- Free users see demo mode only
- Premium users get full functionality
- Track demo completions for analytics

### Permissions
```xml
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

### Performance
- Speech recognition runs on system service (no performance impact)
- Food parsing is O(n*m) where n=foods, m=text length - fast for 180 items
- Pre-sort food database on app start
- Cache parsing results during session

## 8. Success Metrics

1. **Parse Accuracy:** >90% of common foods correctly identified from clear speech
2. **Usage Rate:** >20% of premium users try voice input within 7 days
3. **Completion Rate:** >60% of started voice sessions result in food added to diary
4. **Recognition Speed:** <2 seconds from end of speech to parsed results displayed
5. **Demo Conversion:** >8% of free users who see demo upgrade to premium within 7 days
6. **Error Rate:** <5% of voice sessions fail with unrecoverable errors
7. **Repeat Usage:** >40% of users who try voice input use it again within 7 days

## 9. Open Questions

1. **Offline Speech Recognition:** Android's offline speech recognition is less accurate. Should we require network, or offer degraded offline mode?

2. **Food Database Updates:** Should the 180+ food database be updatable via remote config, or bundled with app updates only?

3. **Regional Food Variations:** Should we add regional foods (e.g., "biscuits and gravy" for US South)? How do we manage regional databases?

4. **Quantity Confirmation:** If parsing is uncertain about quantity, should we ask user to confirm, or just default to 1?

5. **Integration with Search:** If voice parsing fails, should we auto-redirect to food search with the transcribed text as query?

6. **Analytics Events:** What events to track? (voice_started, voice_completed, voice_error, food_parsed, food_added, demo_shown, demo_completed)

---

## Appendix A: Voice Food Database (180+ Items)

### Proteins
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| scrambled eggs (2) | 140 | 12 | 1.2 | 10 |
| fried egg | 90 | 6 | 0.4 | 7 |
| hard boiled egg | 78 | 6 | 0.6 | 5 |
| chicken breast | 165 | 31 | 0 | 4 |
| grilled chicken | 165 | 31 | 0 | 4 |
| chicken thigh | 210 | 26 | 0 | 11 |
| chicken wings (4) | 320 | 27 | 0 | 22 |
| turkey breast | 135 | 30 | 0 | 1 |
| turkey sandwich | 300 | 24 | 30 | 12 |
| ham | 145 | 21 | 1.5 | 6 |
| bacon (3 strips) | 120 | 9 | 0 | 9 |
| sausage (2 links) | 180 | 8 | 1 | 16 |
| steak | 270 | 26 | 0 | 18 |
| ground beef | 250 | 26 | 0 | 15 |
| pork chop | 200 | 26 | 0 | 10 |
| salmon | 208 | 20 | 0 | 13 |
| tuna | 130 | 29 | 0 | 1 |
| shrimp | 100 | 24 | 0 | 1 |
| fish fillet | 150 | 25 | 0 | 5 |

### Dairy
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| milk | 150 | 8 | 12 | 8 |
| cheese slice | 110 | 7 | 0.5 | 9 |
| cottage cheese | 110 | 14 | 4 | 5 |
| greek yogurt | 130 | 17 | 6 | 4 |
| yogurt | 150 | 6 | 26 | 2 |
| butter (1 tbsp) | 100 | 0 | 0 | 11 |
| cream cheese | 100 | 2 | 2 | 10 |
| ice cream | 270 | 5 | 31 | 14 |

### Grains & Bread
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| toast | 80 | 3 | 15 | 1 |
| bread slice | 80 | 3 | 15 | 1 |
| bagel | 270 | 10 | 53 | 2 |
| croissant | 230 | 5 | 26 | 12 |
| muffin | 340 | 5 | 50 | 14 |
| pancakes (2) | 260 | 6 | 40 | 8 |
| waffle | 220 | 6 | 25 | 11 |
| oatmeal | 150 | 5 | 27 | 3 |
| cereal | 150 | 3 | 33 | 1 |
| rice | 200 | 4 | 45 | 0 |
| pasta | 220 | 8 | 43 | 1 |
| noodles | 220 | 8 | 40 | 3 |

### Fast Food & Meals
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| burger | 500 | 25 | 40 | 28 |
| cheeseburger | 550 | 28 | 42 | 32 |
| hot dog | 290 | 11 | 24 | 17 |
| pizza slice | 285 | 12 | 36 | 10 |
| taco | 210 | 9 | 21 | 10 |
| burrito | 430 | 18 | 50 | 18 |
| sandwich | 350 | 15 | 40 | 14 |
| grilled cheese | 400 | 14 | 30 | 26 |
| quesadilla | 470 | 20 | 40 | 26 |
| fries | 365 | 4 | 48 | 17 |
| chicken nuggets (6) | 280 | 14 | 18 | 18 |
| mac and cheese | 350 | 12 | 40 | 16 |
| soup | 150 | 8 | 18 | 5 |
| salad | 100 | 3 | 10 | 6 |
| chicken salad | 350 | 25 | 10 | 24 |

### Fruits
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| apple | 95 | 0.5 | 25 | 0 |
| banana | 105 | 1 | 27 | 0 |
| orange | 62 | 1 | 15 | 0 |
| grapes | 100 | 1 | 27 | 0 |
| strawberries | 50 | 1 | 12 | 0 |
| blueberries | 85 | 1 | 21 | 0 |
| watermelon | 85 | 2 | 21 | 0 |
| mango | 100 | 1 | 25 | 1 |
| pineapple | 80 | 1 | 22 | 0 |
| peach | 60 | 1 | 15 | 0 |
| pear | 100 | 1 | 27 | 0 |

### Vegetables
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| broccoli | 55 | 4 | 11 | 0 |
| carrots | 50 | 1 | 12 | 0 |
| spinach | 25 | 3 | 4 | 0 |
| corn | 130 | 5 | 27 | 2 |
| green beans | 35 | 2 | 8 | 0 |
| potato | 160 | 4 | 37 | 0 |
| baked potato | 160 | 4 | 37 | 0 |
| sweet potato | 115 | 2 | 27 | 0 |
| tomato | 25 | 1 | 5 | 0 |
| cucumber | 15 | 1 | 4 | 0 |
| lettuce | 10 | 1 | 2 | 0 |
| mushrooms | 20 | 3 | 3 | 0 |
| onion | 45 | 1 | 11 | 0 |
| peppers | 30 | 1 | 7 | 0 |

### Beverages
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| coffee | 5 | 0 | 0 | 0 |
| coffee with milk | 50 | 1 | 6 | 2 |
| large coffee with milk | 100 | 1 | 20 | 2 |
| latte | 190 | 10 | 18 | 7 |
| cappuccino | 120 | 6 | 10 | 6 |
| tea | 2 | 0 | 0 | 0 |
| orange juice | 110 | 2 | 26 | 0 |
| apple juice | 120 | 0 | 29 | 0 |
| soda | 140 | 0 | 39 | 0 |
| diet soda | 0 | 0 | 0 | 0 |
| smoothie | 250 | 5 | 50 | 3 |
| protein shake | 200 | 25 | 10 | 5 |
| beer | 150 | 2 | 13 | 0 |
| wine | 125 | 0 | 4 | 0 |
| water | 0 | 0 | 0 | 0 |

### Snacks & Desserts
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| chips | 150 | 2 | 15 | 10 |
| popcorn | 100 | 3 | 19 | 1 |
| pretzels | 110 | 3 | 23 | 1 |
| crackers | 70 | 1 | 11 | 2 |
| cookies (2) | 160 | 2 | 22 | 8 |
| brownie | 230 | 3 | 30 | 12 |
| cake slice | 350 | 4 | 50 | 15 |
| donut | 250 | 4 | 30 | 13 |
| chocolate | 210 | 3 | 24 | 13 |
| candy bar | 280 | 4 | 35 | 14 |
| granola bar | 140 | 3 | 24 | 5 |
| trail mix | 175 | 5 | 15 | 11 |
| nuts | 170 | 5 | 6 | 15 |
| peanut butter | 190 | 7 | 7 | 16 |

### Breakfast Items
| Food | Calories | Protein | Carbs | Fat |
|------|----------|---------|-------|-----|
| avocado toast | 280 | 6 | 25 | 18 |
| breakfast burrito | 380 | 15 | 35 | 20 |
| french toast (2) | 300 | 10 | 36 | 14 |
| eggs benedict | 400 | 18 | 24 | 26 |
| breakfast sandwich | 350 | 16 | 30 | 18 |
| hash browns | 220 | 2 | 25 | 13 |
| fruit salad | 80 | 1 | 20 | 0 |

## Appendix B: Quantity and Size Modifier Reference

### Numeric Quantities
- "one", "1", "a", "an" → 1
- "two", "2", "a couple", "couple" → 2
- "three", "3", "few", "a few" → 3
- "four", "4" → 4
- "five", "5" → 5
- "six", "6", "half dozen" → 6
- "some" → 2 (default)

### Size Modifiers
- "small", "little", "mini" → 0.75x
- "medium", "regular", "normal" → 1.0x
- "large", "big" → 1.5x
- "huge", "extra large", "xl", "jumbo" → 2.0x

### Ounce Conversions (for beverages)
- "8 oz", "8 ounce" → 0.67x (small)
- "12 oz", "12 ounce" → 1.0x (medium/regular)
- "16 oz", "16 ounce" → 1.33x (large)
- "20 oz", "20 ounce" → 1.67x (venti)
- "24 oz", "24 ounce" → 2.0x (trenta)
- "32 oz", "32 ounce" → 2.67x (large fast food)

### Portion Units
- "bowl of" → 1.5x
- "cup of" → 1.0x
- "glass of" → 1.0x
- "plate of" → 2.0x
- "serving of" → 1.0x
- "slice of" → 1.0x (for pizza, cake, bread)
- "scoop of" → 1.0x (for ice cream)
- "handful of" → 0.5x (for nuts, snacks)

## Appendix C: iOS Feature Parity Checklist

| iOS Feature | Android Implementation | Status |
|-------------|----------------------|--------|
| AVFoundation + Speech | SpeechRecognizer API | Planned |
| 180+ food database | Same database | Planned |
| Quantity detection | Same parsing logic | Planned |
| Size modifiers | Same multipliers | Planned |
| Ounce parsing | Same conversions | Planned |
| Liquid wave animation | Custom Canvas/Library | Planned |
| Real-time transcription | Partial results | Planned |
| Demo mode | Same example text | Planned |
| 15-second timeout | Same timeout | Planned |
| 2-second silence stop | Same behavior | Planned |
| Premium gating | Subscription check | Planned |

## Appendix D: Android-Specific Enhancements

1. **Material You Integration:** Microphone button and animations adapt to user's dynamic color theme.

2. **Wear OS Support:** Future enhancement - Voice input directly from smartwatch with results synced to phone.

3. **Assistant Integration:** Future enhancement - "Hey Google, log my lunch in CalTrack" via App Actions.

4. **Accessibility:** Full TalkBack support, ensuring voice input is accessible to visually impaired users.

5. **Background Processing:** If user switches apps during processing, continue in background and show notification when ready.
