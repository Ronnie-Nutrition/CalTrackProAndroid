# PRD: AI Photo Recognition

## 1. Introduction/Overview

AI Photo Recognition enables users to log food by taking a photo of their meal. The app analyzes the image to identify food items, estimate portions, and calculate nutritional information. This feature reduces friction in food logging by eliminating manual entry for common meals.

**Problem Solved:** Manual food logging is tedious and time-consuming. Users often skip logging meals because searching and entering each item takes too long. Photo-based recognition provides a quick, accurate alternative.

**Android Enhancement:** This implementation uses a tiered approach - free users get on-device ML Kit recognition (fast, private, no internet required), while premium users get enhanced OpenAI Vision API recognition (more accurate, multi-food detection, portion estimation).

## 2. Goals

1. **Reduce logging friction** - Allow users to log a complete meal in under 10 seconds
2. **Achieve 85%+ accuracy** - Correctly identify common foods with high confidence
3. **Support multi-food detection** - Identify 3-5+ items on a single plate (premium)
4. **Estimate portions** - Provide reasonable serving size estimates based on visual cues
5. **Work offline** - Basic recognition available without internet (ML Kit)
6. **Match iOS parity** - Equivalent functionality to iOS CalTrackPro implementation
7. **Premium conversion** - Drive upgrades by showcasing enhanced AI accuracy

## 3. User Stories

### Free Users (ML Kit)
- **US-1:** As a free user, I want to take a photo of my food so that I can quickly identify what I'm eating without manual search.
- **US-2:** As a free user, I want recognition to work offline so that I can log food without internet.
- **US-3:** As a free user, I want to see a preview of premium AI features so that I understand the upgrade value.

### Premium Users (OpenAI Vision)
- **US-4:** As a premium user, I want the AI to detect multiple foods on my plate so that I can log an entire meal at once.
- **US-5:** As a premium user, I want accurate portion estimation so that my calorie counts are reliable.
- **US-6:** As a premium user, I want confidence scores for each detected food so that I can verify accuracy.
- **US-7:** As a premium user, I want to take photos from my gallery so that I can log meals I photographed earlier.

### All Users
- **US-8:** As a user, I want to edit detected foods before adding to my diary so that I can correct any mistakes.
- **US-9:** As a user, I want the camera to have good lighting controls so that I get better recognition results.
- **US-10:** As a user, I want to select which meal type (breakfast/lunch/dinner/snack) to add foods to.

## 4. Functional Requirements

### 4.1 Camera Integration
1. The system must use CameraX for camera capture (consistent with existing barcode scanner)
2. The system must support both rear and front camera (rear preferred)
3. The system must provide a torch/flashlight toggle for low-light conditions
4. The system must display a live camera preview
5. The system must support capturing from photo gallery (premium only)
6. The system must handle camera permissions gracefully with explanation dialogs

### 4.2 Image Processing
7. The system must resize captured images to max 512px (longest dimension) before API submission
8. The system must convert images to Base64 for API transmission
9. The system must compress images to reduce bandwidth (JPEG quality 80%)
10. The system must cache processed images temporarily for retry scenarios

### 4.3 ML Kit Recognition (Free Tier)
11. The system must use ML Kit Image Labeling for on-device food detection
12. The system must filter results to food-related labels only (using 150+ food vocabulary)
13. The system must return top 3 food candidates with confidence scores
14. The system must work completely offline
15. The system must complete recognition within 2 seconds on-device
16. The system must show "Upgrade for better accuracy" prompt after results

### 4.4 OpenAI Vision Recognition (Premium Tier)
17. The system must use OpenAI Vision API (model: `gpt-4o`) for enhanced recognition
18. The system must detect multiple food items on a single plate (up to 5+)
19. The system must estimate portion sizes based on visual cues (plate size, utensils, hands)
20. The system must provide confidence scores (0.0-1.0) for each detected item
21. The system must return structured JSON with food name, calories, protein, carbs, fat, serving size
22. The system must handle markdown-wrapped JSON responses from OpenAI
23. The system must timeout API requests after 30 seconds
24. The system must fall back to ML Kit if OpenAI API fails

### 4.5 Nutrition Data Enhancement
25. The system must cross-reference detected foods with USDA FoodData Central API
26. The system must use Open Food Facts API as secondary nutrition source
27. The system must prefer USDA data for accuracy when available
28. The system must cache nutrition lookups for 24 hours
29. The system must use OpenAI-provided estimates if API lookup fails

### 4.6 Results Display
30. The system must display captured image with detected foods overlaid
31. The system must show each detected food with: name, confidence %, estimated calories, macros
32. The system must allow users to remove incorrectly detected foods
33. The system must allow users to adjust serving sizes before adding
34. The system must allow users to add missing foods manually
35. The system must show total nutrition summary for all detected items

### 4.7 Diary Integration
36. The system must allow selection of meal type (Breakfast, Lunch, Dinner, Snack)
37. The system must allow selection of date/time for the entry
38. The system must add all confirmed foods to the diary in a single action
39. The system must show success confirmation after adding to diary
40. The system must navigate back to diary after successful addition

### 4.8 Error Handling
41. The system must show user-friendly error messages for API failures
42. The system must provide retry option for failed recognition
43. The system must handle "no food detected" scenario gracefully
44. The system must handle network timeout with offline fallback option
45. The system must log errors for debugging (non-PII)

## 5. Non-Goals (Out of Scope)

1. **Real-time video recognition** - Only single image capture, not continuous video analysis
2. **Barcode detection in photos** - Use dedicated barcode scanner for packaged foods
3. **Recipe detection** - Won't identify "chicken parmesan" as a recipe, only component foods
4. **Brand recognition** - Won't identify "McDonald's Big Mac" specifically (use barcode for branded items)
5. **Nutritional label OCR** - Won't read nutrition facts from packaging photos
6. **Food freshness detection** - Won't assess if food is spoiled or expired
7. **Allergen detection** - Won't automatically flag allergens (user responsibility)
8. **Plate cleanup detection** - Won't track how much food was actually eaten vs. left over

## 6. Design Considerations

### UI Flow
```
Diary Screen
    → Tap "+" FAB
    → Select "Photo" option
    → Camera Screen (with preview, capture button, torch toggle, gallery button*)
    → Capture photo
    → Processing Screen (loading animation, "Analyzing your meal...")
    → Results Screen (image + detected foods list)
    → Edit foods (remove, adjust servings, add missing)
    → Select meal type
    → Confirm → Added to Diary

* Gallery button only visible for premium users
```

### Visual Design
- Follow existing app theme (Material 3)
- Camera UI consistent with barcode scanner screen
- Results displayed as cards with food image placeholder, name, macros
- Confidence shown as percentage badge (green >80%, yellow 50-80%, red <50%)
- Premium features highlighted with subtle "PRO" badge

### Animations
- Capture button pulse animation
- Processing spinner with food-related icon
- Results cards slide in sequentially
- Success checkmark animation on add

## 7. Technical Considerations

### Dependencies
```kotlin
// ML Kit Image Labeling
implementation("com.google.mlkit:image-labeling:17.0.8")

// CameraX (already in project for barcode scanner)
// Retrofit + OkHttp (already in project)
// Coil for image loading (already in project)
```

### API Integration

#### OpenAI Vision API
- **Endpoint:** `https://api.openai.com/v1/chat/completions`
- **Model:** `gpt-4o`
- **Authentication:** Bearer token (stored in EncryptedSharedPreferences)
- **Request format:** Multipart with base64 image
- **Cost:** ~$0.01-0.03 per image (user pays via premium subscription)

#### USDA FoodData Central API
- **Endpoint:** `https://api.nal.usda.gov/fdc/v1/foods/search`
- **Authentication:** API key (already configured for food search)
- **Rate limit:** 1000 requests/hour

#### Open Food Facts API
- **Endpoint:** `https://world.openfoodfacts.org/api/v0/product/{barcode}.json`
- **Authentication:** None required
- **Fallback source for nutrition data**

### Architecture
```
ui/screens/AIFoodRecognitionScreen.kt
ui/viewmodels/AIFoodRecognitionViewModel.kt
data/remote/OpenAIService.kt
data/remote/dto/OpenAIVisionRequest.kt
data/remote/dto/OpenAIVisionResponse.kt
data/repository/FoodRecognitionRepository.kt
domain/model/FoodRecognitionResult.kt
domain/usecase/RecognizeFoodUseCase.kt
util/ImageProcessor.kt (resize, compress, base64)
```

### Data Models
```kotlin
data class FoodRecognitionResult(
    val name: String,
    val confidence: Float,          // 0.0 - 1.0
    val calories: Int?,
    val protein: Double?,
    val carbs: Double?,
    val fat: Double?,
    val servingSize: String?,       // "approximately 100g"
    val servingQuantity: Double?,   // 1.0
    val imageUrl: String?           // Optional food image from API
)

data class RecognitionSession(
    val capturedImage: Bitmap,
    val results: List<FoodRecognitionResult>,
    val recognitionType: RecognitionType,  // ML_KIT or OPENAI
    val processingTimeMs: Long,
    val timestamp: Instant
)

enum class RecognitionType {
    ML_KIT,     // Free, on-device
    OPENAI      // Premium, cloud-based
}
```

### Premium Gating
- Check subscription status before enabling OpenAI features
- ML Kit always available as fallback
- Gallery access premium-only
- Show upgrade prompts strategically (after ML Kit results, on gallery tap)

### Security
- Store OpenAI API key in EncryptedSharedPreferences (not BuildConfig)
- Never log image data or API responses containing images
- Clear temporary image cache after processing
- Validate API responses before parsing

### Performance
- Image processing on background thread (Dispatchers.IO)
- Cancel ongoing recognition if user navigates away
- Debounce rapid capture attempts (500ms minimum between captures)
- Lazy load nutrition data (show detection first, then fetch nutrition)

## 8. Success Metrics

1. **Recognition Accuracy:** >85% of common foods correctly identified (measured via user corrections)
2. **Usage Rate:** >30% of food entries use photo recognition within 30 days of feature launch
3. **Completion Rate:** >70% of started recognition sessions result in food being added to diary
4. **Processing Time:** <3 seconds for ML Kit, <8 seconds for OpenAI (including network)
5. **Premium Conversion:** >5% of free users who try ML Kit upgrade to premium within 7 days
6. **Error Rate:** <2% of recognition attempts fail with unrecoverable errors
7. **User Satisfaction:** >4.0 rating in post-feature feedback (if collected)

## 9. Open Questions

1. **OpenAI API Key Distribution:** How will the app-provided API key be securely distributed and rotated? Consider using a backend proxy to avoid embedding keys in the app.

2. **Cost Management:** Should there be a daily/monthly limit on premium AI recognitions to prevent abuse? (iOS doesn't appear to have limits)

3. **Offline Queue:** If premium user captures photo offline, should it queue for processing when online, or fall back to ML Kit immediately?

4. **Food Vocabulary Updates:** How will the ML Kit food vocabulary (150+ terms) be updated? Should it be bundled or fetched remotely?

5. **Analytics:** What analytics events should be tracked? (recognition_started, recognition_completed, food_added, food_removed, upgrade_prompted, etc.)

6. **A/B Testing:** Should we test different upgrade prompt placements/copy to optimize conversion?

---

## Appendix A: ML Kit Food Vocabulary (Sample)

The following food-related terms should be used to filter ML Kit Image Labeling results:

```
apple, avocado, bacon, bagel, banana, beans, beef, beer, berries, biscuit,
bread, broccoli, burger, burrito, butter, cabbage, cake, candy, carrot,
cereal, cheese, chicken, chips, chocolate, coffee, cookie, corn, croissant,
cucumber, donut, egg, fish, fries, fruit, grape, ham, hamburger, honey,
hot dog, ice cream, juice, ketchup, lamb, lemon, lettuce, lime, lobster,
mango, meat, milk, muffin, mushroom, noodles, nuts, oatmeal, olive, onion,
orange, pancake, pasta, peach, peanut, pear, pepper, pickle, pie, pineapple,
pizza, pork, potato, pretzel, rice, salad, salmon, sandwich, sauce, sausage,
shrimp, smoothie, soup, spinach, steak, strawberry, sushi, taco, tea, toast,
tomato, tortilla, tuna, turkey, vegetable, waffle, watermelon, wine, yogurt
```

## Appendix B: OpenAI Vision Prompt Template

```
Analyze this food image and identify all visible food items. For each food item, provide:
1. Food name (be specific, e.g., "grilled chicken breast" not just "chicken")
2. Confidence level (0.0 to 1.0)
3. Estimated calories per visible portion
4. Estimated protein (grams)
5. Estimated carbohydrates (grams)
6. Estimated fat (grams)
7. Estimated serving size description

Use visual cues like plate size, utensils, and hand size for portion estimation.

Return ONLY a JSON array with this structure, no additional text:
[
  {
    "name": "food name",
    "confidence": 0.95,
    "calories": 250,
    "protein": 30.0,
    "carbs": 5.0,
    "fat": 12.0,
    "servingSize": "approximately 150g"
  }
]
```

## Appendix C: iOS Feature Parity Checklist

| iOS Feature | Android Implementation | Status |
|-------------|----------------------|--------|
| OpenAI Vision API (gpt-4o) | Same API | Planned |
| Apple Vision fallback | ML Kit Image Labeling | Planned |
| Image resize to 512px | ImageProcessor utility | Planned |
| Multi-food detection | OpenAI prompt | Planned |
| Portion estimation | OpenAI prompt | Planned |
| Confidence scoring | Both ML Kit + OpenAI | Planned |
| USDA API cross-reference | Same API | Planned |
| Open Food Facts fallback | Same API | Planned |
| Gallery photo selection | CameraX + MediaStore | Planned |
| Torch/flashlight | CameraX | Planned |
| Premium gating | Subscription check | Planned |

## Appendix D: Android-Specific Enhancements

1. **Offline-First ML Kit:** Unlike iOS which requires network for Apple Vision, Android ML Kit works fully offline, providing better UX for users with poor connectivity.

2. **Background Processing:** Use WorkManager for queued recognition if user closes app mid-process.

3. **Widget Integration:** Future enhancement - Add recognized foods directly from camera widget.

4. **Wear OS Support:** Future enhancement - Quick photo capture from smartwatch.

5. **Material You Theming:** Dynamic color support for camera UI based on user's wallpaper.
