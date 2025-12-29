# PRD: Food Diary Core Functionality

**Document ID:** 0001-prd-food-diary
**Feature:** Food Diary
**Platform:** Android (Google Play Store)
**Status:** Draft
**Created:** 2024-12-29

---

## 1. Introduction/Overview

The Food Diary is the core feature of CalTrackPro Android, enabling users to track their daily nutritional intake. Users can log food entries organized by meal type, view daily nutrition summaries, and monitor their progress toward calorie and macro goals.

This PRD defines the Android implementation matching the iOS CalTrackPro app's Food Diary functionality.

---

## 2. Goals

1. Allow users to log food entries with complete nutritional information
2. Organize entries by meal type (Breakfast, Lunch, Dinner, Snack)
3. Display daily nutrition totals and progress toward goals
4. Persist all data locally using Room Database
5. Provide intuitive date navigation for viewing historical entries
6. Enable entry management (edit, delete, duplicate)

---

## 3. User Stories

### Primary User Stories

**US-1:** As a user, I want to add a food entry to my diary so that I can track what I eat.

**US-2:** As a user, I want to see my food entries organized by meal (Breakfast, Lunch, Dinner, Snack) so I can understand my eating patterns.

**US-3:** As a user, I want to see my daily calorie and macro totals so I know if I'm meeting my goals.

**US-4:** As a user, I want to navigate between dates so I can view or edit past entries.

**US-5:** As a user, I want to edit a food entry so I can correct mistakes.

**US-6:** As a user, I want to delete a food entry so I can remove incorrect items.

**US-7:** As a user, I want to duplicate an entry so I can quickly log foods I eat repeatedly.

**US-8:** As a user, I want my diary data to persist between app sessions so I don't lose my tracking history.

---

## 4. Functional Requirements

### 4.1 Data Model - FoodEntry

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | UUID | Yes | Unique identifier |
| name | String | Yes | Food name |
| brand | String | No | Brand name (optional) |
| barcode | String | No | Product barcode (for scanned items) |
| calories | Double | Yes | Calories per serving |
| protein | Double | Yes | Protein in grams |
| carbs | Double | Yes | Carbohydrates in grams |
| fat | Double | Yes | Fat in grams |
| fiber | Double | No | Fiber in grams |
| sugar | Double | No | Sugar in grams |
| sodium | Double | No | Sodium in milligrams |
| servingSize | Double | Yes | Numeric serving size |
| servingUnit | String | Yes | Unit (g, oz, cup, piece, ml, etc.) |
| quantity | Double | Yes | Number of servings (default: 1.0) |
| mealType | Enum | Yes | BREAKFAST, LUNCH, DINNER, SNACK |
| timestamp | Long | Yes | Unix timestamp of entry |
| imageData | ByteArray | No | Optional food image |

**Computed Properties:**
- `totalCalories` = calories × quantity
- `totalProtein` = protein × quantity
- `totalCarbs` = carbs × quantity
- `totalFat` = fat × quantity

### 4.2 Meal Types

```kotlin
enum class MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK
}
```

### 4.3 Diary Screen (DiaryView)

**FR-1:** The diary screen SHALL display a date selector at the top allowing users to navigate between dates.

**FR-2:** The diary screen SHALL display a daily nutrition summary card showing:
- Total calories consumed vs. goal
- Protein consumed vs. goal (with progress bar)
- Carbs consumed vs. goal (with progress bar)
- Fat consumed vs. goal (with progress bar)

**FR-3:** The diary screen SHALL display food entries grouped by meal type sections (Breakfast, Lunch, Dinner, Snack).

**FR-4:** Each meal section SHALL display:
- Meal type header with icon
- Subtotal calories for that meal
- List of food entries with name, serving info, and calories

**FR-5:** Empty meal sections SHALL be hidden (only show sections with entries).

**FR-6:** The diary screen SHALL have a floating action button (+) to add new entries.

**FR-7:** Tapping the add button SHALL present options:
- Search Food (navigates to Food Search - future PRD)
- Scan Barcode (navigates to Barcode Scanner - future PRD)
- Manual Entry (navigates to Manual Entry screen)

**FR-8:** The user SHALL be able to select which meal type when adding an entry.

### 4.4 Entry Interactions

**FR-9:** Tapping a food entry SHALL navigate to a detail/edit view.

**FR-10:** Long-pressing or using a context menu on an entry SHALL show options:
- Edit
- Duplicate
- Delete

**FR-11:** Duplicating an entry SHALL create a new entry with identical properties but a new ID and current timestamp.

**FR-12:** Deleting an entry SHALL show a confirmation dialog before removal.

### 4.5 Manual Entry Screen

**FR-13:** The manual entry screen SHALL allow input of:
- Food name (required)
- Brand (optional)
- Calories (required)
- Protein (required)
- Carbs (required)
- Fat (required)
- Serving size (required)
- Serving unit (required, picker: g, oz, cup, piece, ml, tbsp, tsp)
- Quantity (required, default: 1)

**FR-14:** The save button SHALL be disabled until all required fields are valid.

**FR-15:** The meal type SHALL be passed to the manual entry screen (not selectable within the screen).

### 4.6 Edit Entry Screen

**FR-16:** The edit entry screen SHALL pre-populate all fields with the existing entry data.

**FR-17:** The edit entry screen SHALL allow modification of all editable fields.

**FR-18:** Saving edits SHALL update the existing entry (not create a new one).

### 4.7 Data Persistence

**FR-19:** All food entries SHALL be persisted to Room Database.

**FR-20:** Entries SHALL be queryable by date range.

**FR-21:** The app SHALL load entries for the selected date on diary screen display.

**FR-22:** Data operations (insert, update, delete) SHALL happen on background threads.

---

## 5. Non-Goals (Out of Scope)

1. **Food Search API Integration** - Covered in separate PRD (0002)
2. **Barcode Scanning** - Covered in separate PRD (0003)
3. **Recipe Management** - Covered in separate PRD (0004)
4. **Weekly/Monthly Summary Views** - Not in iOS v1, may add later
5. **Quick Add (calories only)** - iOS requires all macros
6. **Cloud Sync** - Phase 4 infrastructure feature
7. **User Profile/Goals Setup** - Covered in separate PRD (0006)
8. **Micronutrient Goals** - Only macros have goals; micronutrients display only

---

## 6. Design Considerations

### UI/UX Guidelines

- Follow Material Design 3 guidelines
- Use Material You dynamic color theming
- Support light and dark themes
- Use familiar Android patterns (FAB for add, swipe actions, bottom sheets)

### Screen Layout

```
┌─────────────────────────────┐
│  < Dec 29, 2024      📅    │  ← Date picker
├─────────────────────────────┤
│  Daily Summary Card         │
│  ┌─────────────────────────┐│
│  │ 1,450 / 2,000 cal      ││
│  │ ████████░░░░ 72%       ││
│  │                         ││
│  │ Protein  85/150g       ││
│  │ ██████░░░░░░░          ││
│  │ Carbs    180/250g      ││
│  │ ██████████░░░          ││
│  │ Fat      45/65g        ││
│  │ █████████░░░░          ││
│  └─────────────────────────┘│
├─────────────────────────────┤
│ 🌅 Breakfast        450 cal │
│ ├─ Oatmeal (1 cup)    300  │
│ └─ Banana (1 medium)  150  │
├─────────────────────────────┤
│ 🌞 Lunch            650 cal │
│ ├─ Chicken Salad      450  │
│ └─ Apple (1 medium)   200  │
├─────────────────────────────┤
│ 🌙 Dinner           350 cal │
│ └─ Grilled Salmon     350  │
├─────────────────────────────┤
│                        [+]  │  ← FAB
└─────────────────────────────┘
```

### Meal Type Icons

| Meal | Icon |
|------|------|
| Breakfast | 🌅 or sunrise icon |
| Lunch | 🌞 or sun icon |
| Dinner | 🌙 or moon icon |
| Snack | 🍎 or apple icon |

---

## 7. Technical Considerations

### Architecture

- **Pattern:** MVVM with Jetpack Compose
- **DI:** Hilt for dependency injection
- **Database:** Room with Flow for reactive updates
- **Navigation:** Jetpack Navigation Compose

### Project Structure

```
com.easyaiflows.caltrackpro/
├── data/
│   ├── local/
│   │   ├── CalTrackDatabase.kt
│   │   ├── FoodEntryDao.kt
│   │   └── entity/
│   │       └── FoodEntryEntity.kt
│   └── repository/
│       └── FoodEntryRepository.kt
├── domain/
│   └── model/
│       ├── FoodEntry.kt
│       └── MealType.kt
├── ui/
│   ├── diary/
│   │   ├── DiaryScreen.kt
│   │   ├── DiaryViewModel.kt
│   │   ├── components/
│   │   │   ├── DailySummaryCard.kt
│   │   │   ├── MealSection.kt
│   │   │   └── FoodEntryItem.kt
│   │   └── ManualEntryScreen.kt
│   └── components/
│       └── AddFoodOptionsSheet.kt
└── di/
    └── DatabaseModule.kt
```

### Dependencies to Add

```kotlin
// Room
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Hilt
implementation("com.google.dagger:hilt-android:2.50")
ksp("com.google.dagger:hilt-compiler:2.50")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
```

### Database Schema

```kotlin
@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double?,
    val sugar: Double?,
    val sodium: Double?,
    val servingSize: Double,
    val servingUnit: String,
    val quantity: Double,
    val mealType: String,
    val timestamp: Long,
    val imageData: ByteArray?
)
```

---

## 8. Success Metrics

1. **Data Integrity:** 100% of entries persist correctly across app restarts
2. **Performance:** Diary screen loads in < 500ms
3. **Usability:** User can add a manual entry in < 30 seconds
4. **Stability:** Zero crashes related to food diary operations

---

## 9. Open Questions

1. **Default Goals:** What default calorie/macro goals should be used before user sets up profile?
   - *Suggested:* 2,000 cal, 150g protein, 250g carbs, 65g fat

2. **Date Range:** How far back should users be able to navigate?
   - *Suggested:* No limit (show all historical data)

3. **Entry Limit:** Should there be a maximum entries per day?
   - *Suggested:* No limit

---

## 10. Future Enhancements (Not in Scope)

- Weekly/monthly summary views
- Meal templates (save frequently eaten meals)
- Copy entire day's entries
- Export diary to CSV
- Share daily summary

---

## Appendix: iOS Feature Parity Checklist

| iOS Feature | Android PRD Coverage |
|-------------|---------------------|
| Meal sections (B/L/D/S) | ✅ FR-3, FR-4 |
| Date navigation | ✅ FR-1 |
| Daily summary card | ✅ FR-2 |
| Add entry (3 options) | ✅ FR-6, FR-7 |
| Manual entry | ✅ FR-13, FR-14, FR-15 |
| Edit entry | ✅ FR-16, FR-17, FR-18 |
| Delete entry | ✅ FR-10, FR-12 |
| Duplicate entry | ✅ FR-10, FR-11 |
| Room persistence | ✅ FR-19, FR-20, FR-21 |
| Fiber/Sugar/Sodium | ✅ Data Model 4.1 |
