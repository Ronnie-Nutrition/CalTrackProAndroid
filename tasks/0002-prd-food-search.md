# PRD: Food Search (Edamam API Integration)

**Document ID:** 0002-prd-food-search
**Status:** Draft
**Created:** 2025-12-29
**Feature:** Food Search with Edamam API Integration

---

## 1. Introduction/Overview

The Food Search feature enables users to search for foods using the Edamam Food Database API and add them to their food diary. Currently, users must manually enter all nutrition information when logging food. This feature eliminates that friction by providing access to a comprehensive database of foods with accurate nutrition data.

This is a foundational feature that will also support future functionality like barcode scanning (which requires food lookup capabilities).

---

## 2. Goals

1. **Enable food search** - Allow users to search the Edamam Food Database by food name
2. **Display nutrition information** - Show calories and macronutrients for search results
3. **Streamline food logging** - Allow users to add searched foods to their diary with customizable serving sizes
4. **Improve efficiency** - Provide recent searches and favorite foods for quick access
5. **Handle offline gracefully** - Cache recent searches for offline viewing

---

## 3. User Stories

### Primary User Stories

1. **As a user**, I want to search for foods by name so that I can find accurate nutrition information without manually entering it.

2. **As a user**, I want to see the calories and macros (protein, carbs, fat) for each search result so that I can make informed food choices.

3. **As a user**, I want to add a searched food to my diary with a specific serving size so that I can accurately track what I ate.

4. **As a user**, I want to see my recent searches so that I can quickly log foods I eat regularly.

5. **As a user**, I want to save foods as favorites so that I can access my commonly eaten foods instantly.

6. **As a user**, I want to view my recent searches even when offline so that I can still reference nutrition information.

---

## 4. Functional Requirements

### 4.1 Search Interface

1. The app must provide a search screen accessible from the diary's "Add Food" options
2. The app must display a search text field with a clear placeholder (e.g., "Search foods...")
3. The app must show a search button or trigger search on keyboard submit
4. The app must display a loading indicator while fetching results
5. The app must show "No results found" when the search returns empty

### 4.2 Search Results

6. The app must display search results in a scrollable list
7. Each result must show: food name, brand (if available), and serving size
8. Each result must display: calories, protein, carbs, and fat per serving
9. The app must support pagination/infinite scroll for large result sets
10. Results must be tappable to view food details or add to diary

### 4.3 Food Detail View

11. The app must display a detail view when a user taps a search result
12. The detail view must show the full food name and brand
13. The detail view must display all available macronutrients (calories, protein, carbs, fat, fiber, sugar, sodium)
14. The detail view must allow selection of serving size from Edamam's available measures
15. The detail view must allow quantity adjustment (e.g., 0.5, 1, 1.5, 2 servings)
16. The detail view must show a real-time calculation of nutrition based on selected serving
17. The detail view must provide an "Add to Diary" button

### 4.4 Adding to Diary

18. The app must allow users to select which meal to add the food to (Breakfast, Lunch, Dinner, Snack)
19. The app must use the currently selected date from the diary as the default date
20. The app must navigate back to the diary after successfully adding a food
21. The app must show a confirmation (toast/snackbar) when food is added
22. The app must support "quick add" - long-press or secondary action to add with default serving

### 4.5 Recent Searches

23. The app must save the last 20 searched foods locally
24. The app must display recent searches when the search field is empty
25. The app must allow users to clear individual recent items
26. The app must allow users to clear all recent searches
27. Recent searches must persist across app sessions

### 4.6 Favorite Foods

28. The app must allow users to mark/unmark foods as favorites (heart/star icon)
29. The app must display a "Favorites" tab or section on the search screen
30. Favorites must be stored locally in the Room database
31. Favorites must display with full nutrition information
32. The app must allow removing foods from favorites

### 4.7 Offline Support

33. The app must cache recent search results locally
34. The app must display cached results when offline with an offline indicator
35. The app must show an appropriate message when searching while offline
36. The app must automatically retry failed searches when connectivity returns

### 4.8 API Integration

37. The app must integrate with the Edamam Food Database API
38. API credentials must be stored securely (BuildConfig or EncryptedSharedPreferences)
39. The app must handle API rate limits gracefully
40. The app must handle API errors with user-friendly messages

---

## 5. Non-Goals (Out of Scope)

1. **Barcode scanning** - Will be implemented in a separate feature
2. **Voice search** - Part of Phase 2 Voice Input feature
3. **Custom food creation** - Users can still use manual entry for custom foods
4. **Meal suggestions/recommendations** - Not part of initial implementation
5. **Nutrition Analysis API** - Only using Food Database API for this feature
6. **Recipe search** - Separate Recipe Management feature

---

## 6. Design Considerations

### Screen Layout

**Search Screen:**
- Top: Search bar with clear button
- Tabs below search: "Recent" | "Favorites"
- Main content: Search results list OR Recent/Favorites list
- Each item shows: Food name, brand, serving, calories badge, macro summary

**Food Detail Screen:**
- Header: Food name and brand
- Serving selector: Dropdown for measure + quantity stepper
- Nutrition card: Live-updating macros based on serving
- Meal type selector: Horizontal chips (Breakfast, Lunch, Dinner, Snack)
- Footer: "Add to Diary" button (full width, prominent)

### UI Components to Create
- `SearchBar` - Reusable search input component
- `FoodSearchResultItem` - List item for search results
- `ServingSelector` - Measure dropdown + quantity controls
- `NutritionSummaryCard` - Displays macro breakdown
- `MealTypeSelector` - Horizontal chip group for meal selection

### Navigation
- Entry point: Diary screen → "Add Food" FAB → "Search Foods" option
- Flow: Search → Results → Detail → Add → Back to Diary

---

## 7. Technical Considerations

### API Setup (Edamam Food Database API)

- **Base URL:** `https://api.edamam.com/api/food-database/v2/`
- **Endpoints:**
  - `parser` - Search for foods
  - `nutrients` - Get detailed nutrition (optional)
- **Authentication:** `app_id` and `app_key` query parameters

### Data Models

```kotlin
// API Response models
data class FoodSearchResponse(
    val hints: List<FoodHint>,
    val parsed: List<ParsedFood>
)

data class FoodHint(
    val food: Food,
    val measures: List<Measure>
)

data class Food(
    val foodId: String,
    val label: String,
    val brand: String?,
    val nutrients: Nutrients,
    val image: String?
)

data class Measure(
    val uri: String,
    val label: String,
    val weight: Double
)

// Local storage
@Entity
data class FavoriteFood(...)

@Entity
data class RecentSearch(...)
```

### Dependencies to Add
- Retrofit + OkHttp (networking)
- Moshi or Gson (JSON parsing)
- Coil (image loading for food images)

### Architecture
- `FoodSearchRepository` - Handles API calls and local caching
- `FoodSearchViewModel` - Manages UI state and search logic
- Room entities for favorites and recent searches

---

## 8. Success Metrics

1. **Adoption:** 80%+ of food entries added via search vs manual entry within 2 weeks
2. **Search success:** Users find desired food within first 3 searches on average
3. **Performance:** Search results appear within 2 seconds on average connection
4. **Reliability:** API error rate < 1% under normal conditions
5. **Engagement:** Average user saves 5+ favorite foods within first week

---

## 9. Open Questions

1. ~~**API Keys:** Are Edamam API credentials already available, or do they need to be obtained?~~ **Resolved:** Using existing credentials from iOS app.
2. **Rate Limits:** What are the Edamam API rate limits for the free/paid tier being used?
3. **Food Images:** Should we display food images from Edamam (requires additional API calls)?
4. **Branded Foods:** How prominently should brand names be displayed?
5. **Unit Preferences:** Should the app support metric/imperial unit preferences for serving sizes?

---

## 10. Dependencies

- **Requires:** Food Diary feature (complete)
- **Enables:** Barcode Scanning feature (uses same food lookup)

---

## Appendix: Edamam API Reference

### Sample Parser Request
```
GET https://api.edamam.com/api/food-database/v2/parser
  ?app_id={APP_ID}
  &app_key={APP_KEY}
  &ingr=chicken breast
  &nutrition-type=logging
```

### Sample Response Structure
```json
{
  "hints": [
    {
      "food": {
        "foodId": "food_bdrxu94aj3x2djbpur8dhagfhkcn",
        "label": "Chicken Breast",
        "nutrients": {
          "ENERC_KCAL": 120,
          "PROCNT": 22.5,
          "FAT": 2.6,
          "CHOCDF": 0
        }
      },
      "measures": [
        {"label": "Serving", "weight": 140},
        {"label": "Ounce", "weight": 28.35},
        {"label": "Gram", "weight": 1}
      ]
    }
  ]
}
```
