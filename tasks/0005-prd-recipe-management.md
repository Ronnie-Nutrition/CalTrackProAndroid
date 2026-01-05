# PRD: Recipe Management

## 1. Introduction/Overview

CalTrackPro allows users to log individual foods, but many users eat home-cooked meals with multiple ingredients. This feature enables users to create, save, and manage custom recipes with automatic nutrition calculation per serving.

**Problem:** Users who cook at home must manually add each ingredient separately every time they eat a meal, or estimate the total nutrition. This is tedious and inaccurate.

**Solution:** Implement a Recipe Management system that allows users to create recipes with multiple ingredients, automatically calculates nutrition per serving, and enables one-tap logging of recipe servings to the food diary.

---

## 2. Goals

1. Allow users to create custom recipes with multiple ingredients
2. Search and add ingredients from Edamam API (reuse existing Food Search)
3. Automatically calculate total and per-serving nutrition
4. Support recipe photos for visual identification
5. Categorize recipes (breakfast, lunch, dinner, snack, etc.)
6. Enable serving size scaling when viewing/logging recipes
7. Add recipe servings directly to the food diary
8. Provide search, filter, and sort capabilities in the recipe library
9. Persist recipes locally using Room database

---

## 3. User Stories

1. **As a user**, I want to create a new recipe by adding ingredients from the food database, so that I can track my home-cooked meals accurately.

2. **As a user**, I want to specify the number of servings my recipe makes, so that nutrition is calculated per serving correctly.

3. **As a user**, I want to add step-by-step cooking instructions to my recipe, so that I can reference them later.

4. **As a user**, I want to categorize my recipes (breakfast, lunch, dinner, etc.), so that I can organize and find them easily.

5. **As a user**, I want to set difficulty level and cooking time for my recipes, so that I can plan my meals accordingly.

6. **As a user**, I want to add a photo to my recipe, so that I can visually identify it in my library.

7. **As a user**, I want to see the nutrition breakdown (calories, protein, carbs, fat) per serving, so that I know what I'm consuming.

8. **As a user**, I want to search my saved recipes by name, so that I can quickly find what I'm looking for.

9. **As a user**, I want to filter my recipes by category, so that I can browse relevant recipes.

10. **As a user**, I want to sort my recipes by date, name, cook time, or difficulty, so that I can organize my library.

11. **As a user**, I want to adjust the serving multiplier when viewing a recipe, so that I can see scaled nutrition for different portion sizes.

12. **As a user**, I want to add a recipe serving to my food diary with one tap, so that logging meals is quick and easy.

13. **As a user**, I want to edit or delete my saved recipes, so that I can keep my library up to date.

---

## 4. Functional Requirements

### 4.1 Recipe Data Model

The system must store the following recipe data:

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| ID | UUID | Auto-generated | Unique identifier |
| Name | String | Yes | 1-100 characters |
| Description | String | No | 0-500 characters |
| Ingredients | List<RecipeIngredient> | Yes | At least 1 ingredient |
| Instructions | List<String> | No | Ordered cooking steps |
| Servings | Integer | Yes | 1-100 servings |
| Cooking Time | Integer | Yes | 1-480 minutes |
| Difficulty | Enum | Yes | Easy, Medium, Hard |
| Category | Enum | Yes | Breakfast, Lunch, Dinner, Snack, Dessert, Main, Side, Drink |
| Image Data | ByteArray | No | JPEG compressed, max 1MB |
| Created At | Timestamp | Auto-generated | Creation date |
| Nutrition Per Serving | NutritionInfo | Calculated | Stored for quick access |

### 4.2 Recipe Ingredient Structure

Each ingredient in a recipe contains:

| Field | Type | Description |
|-------|------|-------------|
| Food Item | SimpleFoodItem | Name, brand, nutrition data, serving info |
| Quantity | Double | Amount in the recipe (in serving units) |

**SimpleFoodItem fields:**
- name: String
- brand: String? (optional)
- barcode: String? (optional)
- calories: Double (per serving size)
- protein: Double (per serving size)
- carbs: Double (per serving size)
- fat: Double (per serving size)
- servingSize: Double
- servingUnit: String

### 4.3 Nutrition Calculation

**Per-Ingredient Calculation:**
```
ingredientCalories = (foodItem.calories * quantity) / foodItem.servingSize
ingredientProtein = (foodItem.protein * quantity) / foodItem.servingSize
ingredientCarbs = (foodItem.carbs * quantity) / foodItem.servingSize
ingredientFat = (foodItem.fat * quantity) / foodItem.servingSize
```

**Total Recipe Nutrition:**
```
totalCalories = sum of all ingredient calories
totalProtein = sum of all ingredient protein
totalCarbs = sum of all ingredient carbs
totalFat = sum of all ingredient fat
```

**Per-Serving Nutrition:**
```
caloriesPerServing = totalCalories / servings
proteinPerServing = totalProtein / servings
carbsPerServing = totalCarbs / servings
fatPerServing = totalFat / servings
```

### 4.4 Recipe Library Screen

The Recipe Library must provide:

1. **Header Section**
   - Recipe book icon and "Recipe Library" title
   - Recipe count display ("X recipes saved")
   - "+" button to create new recipe

2. **Search Bar**
   - Text input for searching recipes by name or description
   - Clear button when text is present

3. **Filter Controls**
   - Category dropdown filter (All, Breakfast, Lunch, Dinner, etc.)
   - Sort dropdown (Newest, Oldest, Name A-Z, Name Z-A, Cook Time, Difficulty)

4. **Recipe List**
   - Scrollable list of RecipeCards
   - Tap to navigate to Recipe Detail

5. **Empty States**
   - No recipes: "No Recipes Yet" with "Create Recipe" button
   - No search results: "No Results" with suggestion to adjust filters

### 4.5 Recipe Card Component

Each recipe card displays:
- Recipe image (or category icon placeholder if no image)
- Recipe name (max 2 lines)
- Description (max 2 lines, if present)
- Cook time with clock icon
- Servings count with person icon
- Difficulty badge (color-coded: green=easy, orange=medium, red=hard)
- Nutrition preview: calories and protein per serving

### 4.6 Recipe Builder Screen

The Recipe Builder must provide:

1. **Header Section**
   - "Recipe Builder" title
   - Nutrition preview button (chart icon)
   - Image picker (tap to add/change photo)

2. **Basic Information Section**
   - Recipe name text field (required)
   - Description text field (optional)
   - Servings selector (stepper: 1-100, default 1)
   - Cooking time slider (5-180 minutes, 5-minute increments)
   - Difficulty picker (segmented: Easy, Medium, Hard)
   - Category picker (dropdown menu)

3. **Ingredients Section**
   - "Add Ingredient" button - opens Food Search sheet
   - List of added ingredients showing:
     - Ingredient name
     - Quantity with unit
     - Calculated calories for that quantity
     - Remove button
   - Tap quantity to edit via dialog
   - Empty state: "No ingredients added yet"

4. **Instructions Section**
   - "Add Step" button
   - Numbered instruction steps (1, 2, 3...)
   - Each step is a text field
   - Remove button for steps (when more than 1 step)

5. **Nutrition Preview Section** (visible when ingredients exist)
   - Shows per-serving nutrition: Calories, Protein, Carbs, Fat
   - Updates live as ingredients are added/modified

6. **Save Button**
   - "Save Recipe" button at bottom
   - Disabled until name and at least 1 ingredient are provided

### 4.7 Food Search Sheet (for adding ingredients)

Reuse existing Food Search functionality:
- Search bar with Edamam API integration
- Common foods list when search is empty
- Display food name, calories, and serving info
- "Add" button to select ingredient
- Returns selected food to Recipe Builder

### 4.8 Recipe Detail Screen

The Recipe Detail screen must provide:

1. **Hero Section**
   - Recipe image (full width) or category icon placeholder
   - Recipe name, description
   - Metadata row: cook time, servings, difficulty

2. **Serving Size Adjuster**
   - "-" and "+" buttons
   - Display current multiplier (0.5x to 5.0x, in 0.5 increments)
   - "Original recipe serves X" label

3. **Ingredients Section**
   - List of ingredients with scaled quantities
   - Quantities update based on serving multiplier

4. **Instructions Section**
   - Numbered step-by-step instructions

5. **Nutrition Section**
   - "Nutrition (Per Adjusted Serving)" header
   - Scaled calories, protein, carbs, fat
   - Values update with serving multiplier

6. **Action Buttons**
   - "Add to Food Diary" - adds recipe as food entry
   - "Delete Recipe" - shows confirmation dialog

### 4.9 Add Recipe to Food Diary

When user taps "Add to Food Diary":
1. Create a FoodEntry with:
   - name: recipe name
   - calories/protein/carbs/fat: scaled nutrition values
   - servingSize: 1
   - servingUnit: "serving"
   - quantity: 1
   - mealType: default to current time-based meal (or let user select)
2. Save to database
3. Show confirmation or navigate to diary

### 4.10 Delete Recipe

1. Show confirmation dialog: "Are you sure you want to delete this recipe?"
2. On confirm: delete from database, navigate back to library
3. On cancel: dismiss dialog

---

## 5. Non-Goals (Out of Scope)

1. **Recipe sharing** - No sharing recipes with other users
2. **Cloud sync** - Recipes stored locally only
3. **Recipe import** - No importing from websites or other apps
4. **Ingredient substitution suggestions** - No AI-based alternatives
5. **Meal planning integration** - Recipes are standalone, not part of meal plans
6. **Nutritional analysis beyond macros** - No micronutrient tracking for recipes
7. **Recipe scaling by ingredient** - Scale by servings only, not "double the chicken"
8. **Cost calculation** - No ingredient cost tracking
9. **Recipe versioning** - No history of recipe changes

---

## 6. Design Considerations

### UI Components

- Use Material 3 components consistent with existing app design
- Recipe cards should have rounded corners and subtle elevation
- Category filter and sort should be horizontal scrollable chips
- Image placeholders should show category-appropriate icons
- Nutrition badges should use consistent color coding (orange=calories, red=protein, blue=carbs, green=fat)

### Navigation

- Add "Recipes" tab to bottom navigation bar (book icon)
- Recipe Library is the main screen for the Recipes tab
- Recipe Builder opens as a full-screen dialog/sheet
- Recipe Detail is a navigated screen from Recipe Library
- Food Search sheet opens from Recipe Builder

### Color Coding

| Element | Color |
|---------|-------|
| Calories | Orange |
| Protein | Red |
| Carbs | Blue |
| Fat | Green |
| Easy difficulty | Green |
| Medium difficulty | Orange |
| Hard difficulty | Red |

### Accessibility

- All images must have content descriptions
- Nutrition values must be readable by screen readers
- Tap targets must be at least 48dp
- Color-coded elements must have text labels

---

## 7. Technical Considerations

### Dependencies

- **Room Database** - Already in use for FoodEntry
- **Coil** - Already in use for image loading (use for recipe photos)
- No new external dependencies required

### Architecture

- Create `Recipe` entity for Room database
- Create `RecipeDao` for database operations
- Create `RecipeRepository` interface and implementation
- Create `RecipeViewModel` for Recipe Library
- Create `RecipeBuilderViewModel` for Recipe Builder
- Create `RecipeDetailViewModel` for Recipe Detail
- Store ingredients and instructions as JSON in Room (TypeConverter)

### Database Schema

```kotlin
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val ingredients: String,  // JSON array of RecipeIngredient
    val instructions: String, // JSON array of String
    val servings: Int,
    val cookingTimeMinutes: Int,
    val difficulty: String,   // "easy", "medium", "hard"
    val category: String,     // "breakfast", "lunch", etc.
    val imageData: ByteArray?,
    val createdAt: Long,
    val caloriesPerServing: Double,
    val proteinPerServing: Double,
    val carbsPerServing: Double,
    val fatPerServing: Double
)
```

### Files to Create

**Domain Layer:**
- `domain/model/Recipe.kt` - Recipe domain model
- `domain/model/RecipeIngredient.kt` - Ingredient with quantity
- `domain/model/SimpleFoodItem.kt` - Simplified food data for recipes
- `domain/model/RecipeDifficulty.kt` - Difficulty enum
- `domain/model/RecipeCategory.kt` - Category enum

**Data Layer:**
- `data/local/entity/RecipeEntity.kt` - Room entity
- `data/local/dao/RecipeDao.kt` - Database operations
- `data/local/converter/RecipeConverters.kt` - JSON TypeConverters
- `data/repository/RecipeRepository.kt` - Repository interface
- `data/repository/RecipeRepositoryImpl.kt` - Repository implementation

**DI Layer:**
- Update `DatabaseModule.kt` - Add RecipeDao provider
- Update `RepositoryModule.kt` - Add RecipeRepository binding

**UI Layer:**
- `ui/recipe/RecipeLibraryScreen.kt` - Recipe list screen
- `ui/recipe/RecipeLibraryViewModel.kt` - Library screen logic
- `ui/recipe/RecipeBuilderScreen.kt` - Create/edit recipe
- `ui/recipe/RecipeBuilderViewModel.kt` - Builder screen logic
- `ui/recipe/RecipeDetailScreen.kt` - View recipe details
- `ui/recipe/RecipeDetailViewModel.kt` - Detail screen logic
- `ui/recipe/components/RecipeCard.kt` - Recipe card component
- `ui/recipe/components/IngredientRow.kt` - Ingredient list item
- `ui/recipe/components/NutritionBadge.kt` - Nutrition display badge
- `ui/recipe/components/FoodSearchSheet.kt` - Ingredient search modal

**Modified Files:**
- `data/local/CalTrackDatabase.kt` - Add RecipeEntity, bump version
- `ui/navigation/NavRoutes.kt` - Add RecipeLibrary, RecipeDetail routes
- `ui/navigation/CalTrackNavHost.kt` - Add recipe screen destinations
- `ui/navigation/BottomNavBar.kt` - Add Recipes tab

---

## 8. Success Metrics

1. **Recipe creation rate** - Average 2+ recipes created per active user
2. **Recipe usage rate** - 30%+ of diary entries come from saved recipes
3. **Library engagement** - Users with recipes return to library 3+ times/week
4. **Feature adoption** - 50%+ of users create at least one recipe within first week
5. **No increase in crashes** - Recipe feature maintains app stability

---

## 9. Open Questions

1. Should we allow editing existing recipes, or only delete and recreate?
   - **Recommendation:** Allow editing for better UX
2. Should Recipe Builder support reordering instructions via drag-and-drop?
   - **Recommendation:** Defer to v2, use add/remove for now
3. Should we provide sample/starter recipes for new users?
   - **Recommendation:** Defer to v2, start with empty library
4. When adding recipe to diary, should user pick the meal type or use time-based default?
   - **Recommendation:** Use time-based default with option to change
5. Should we limit the number of recipes for free users (premium feature)?
   - **Recommendation:** Defer to premium implementation phase

---

## Appendix: iOS Feature Parity Checklist

| iOS Feature | Android Implementation |
|-------------|----------------------|
| Recipe model with all fields | RecipeEntity with TypeConverters |
| Recipe photo storage | ByteArray in Room |
| Difficulty enum (easy/medium/hard) | RecipeDifficulty enum |
| Category enum (8 categories) | RecipeCategory enum |
| Nutrition per serving calculation | Same formulas |
| Recipe Library with search | RecipeLibraryScreen |
| Category filter | Dropdown/chips in library |
| Sort options (6 types) | Dropdown in library |
| Recipe cards with preview | RecipeCard composable |
| Recipe Builder with all sections | RecipeBuilderScreen |
| Food search for ingredients | FoodSearchSheet (reuse existing) |
| Ingredient quantity editing | Dialog-based editing |
| Step-by-step instructions | Dynamic list with add/remove |
| Live nutrition preview | Compose state updates |
| Recipe Detail with scaler | RecipeDetailScreen |
| Serving multiplier (0.5x-5x) | Stepper with 0.5 increments |
| Scaled ingredient display | Computed from multiplier |
| Add to Food Diary | Creates FoodEntry |
| Delete recipe with confirmation | AlertDialog |
