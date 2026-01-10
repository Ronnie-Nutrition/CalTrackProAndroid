# Tasks: AI Meal Planner

## Relevant Files

- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/Recipe.kt` - Recipe data class with nutrition, ingredients, instructions
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/MealPlan.kt` - WeeklyMealPlan, DailyMealPlan data classes
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/PlannedMeal.kt` - Individual planned meal with recipe reference
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/MealPlanPreferences.kt` - User preferences for plan generation
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/DietType.kt` - Enum for diet types (Balanced, Low Carb, etc.)
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/PlannerMealType.kt` - Enum for 5 meal types
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/usecase/GenerateMealPlanUseCase.kt` - Plan generation algorithm
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/usecase/ScoreMealUseCase.kt` - Meal scoring algorithm
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/RecipeDatabase.kt` - Hardcoded 100+ recipes
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/MealPlanDataStore.kt` - DataStore for plan persistence
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/MealPlanRepository.kt` - Repository interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/MealPlanRepositoryImpl.kt` - Repository implementation
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/MealPlannerScreen.kt` - Main meal planner screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/MealPlannerViewModel.kt` - State management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/MealPlannerUiState.kt` - UI state sealed class
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/MealPlanPreferencesScreen.kt` - Preferences wizard
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/RecipeDetailSheet.kt` - Bottom sheet for recipe details
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/components/WeekDaySelector.kt` - Day picker
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/components/MealCard.kt` - Meal display card
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/screens/mealplanner/components/DailyNutritionSummary.kt` - Daily totals
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add MealPlanner routes
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add navigation entries

### Notes

- Recipe database is hardcoded JSON/Kotlin objects for fast offline access
- Plan generation runs on Dispatchers.Default (CPU-bound algorithm)
- Use DataStore with Kotlin serialization for plan persistence
- Calorie allocation: Breakfast 25%, Snacks 10% each, Lunch 30%, Dinner 25%
- Run `./gradlew assembleDebug` to verify compilation after each major task

## Tasks

- [ ] 1.0 Create Recipe and Meal Plan Domain Models
  - [ ] 1.1 Create `DietType.kt` enum: BALANCED, LOW_CARB, HIGH_PROTEIN, MEDITERRANEAN, VEGAN
  - [ ] 1.2 Create `PlannerMealType.kt` enum: BREAKFAST, MORNING_SNACK, LUNCH, AFTERNOON_SNACK, DINNER
  - [ ] 1.3 Create `Difficulty.kt` enum: EASY, MEDIUM, HARD
  - [ ] 1.4 Create `CuisineType.kt` enum: AMERICAN, MEXICAN, ITALIAN, ASIAN, MEDITERRANEAN, INDIAN, OTHER
  - [ ] 1.5 Create `RecipeIngredient.kt` data class with name, quantity, unit, calories, protein, carbs, fat
  - [ ] 1.6 Create `Recipe.kt` data class with all fields from PRD (id, name, mealTypes, dietTypes, ingredients, instructions, nutrition, prepTime, cost, etc.)
  - [ ] 1.7 Create `PlannedMeal.kt` data class with mealType, recipe, servings, isSwapped, computed nutrition properties
  - [ ] 1.8 Create `DailyMealPlan.kt` data class with date, meals list, computed daily totals
  - [ ] 1.9 Create `WeeklyMealPlan.kt` data class with id, startDate, endDate, dailyPlans, preferences, generatedAt
  - [ ] 1.10 Create `MealPlanPreferences.kt` data class with dietType, includedMeals, avoidFoods, favoriteFoods, maxPrepTime, budgetOptimized, varietyLevel

- [ ] 2.0 Build Recipe Database with 100+ Recipes
  - [ ] 2.1 Create `RecipeDatabase.kt` object to hold all recipes
  - [ ] 2.2 Add 13+ breakfast recipes (scrambled eggs, protein pancakes, overnight oats, avocado toast, etc.)
  - [ ] 2.3 Add 12+ lunch recipes (grilled chicken salad, buddha bowl, turkey wrap, etc.)
  - [ ] 2.4 Add 15+ dinner recipes (baked salmon, grilled steak, chicken thighs, tofu stir-fry, etc.)
  - [ ] 2.5 Add 12+ snack recipes (protein shake, greek yogurt, hard boiled eggs, trail mix, etc.)
  - [ ] 2.6 Tag each recipe with compatible diet types (vegan, low-carb, high-protein, etc.)
  - [ ] 2.7 Add detailed ingredients list with quantities for each recipe
  - [ ] 2.8 Add step-by-step instructions (5-10 steps) for each recipe
  - [ ] 2.9 Add prep time, cook time, difficulty, and estimated cost per serving
  - [ ] 2.10 Create helper functions: `getRecipesByMealType()`, `getRecipesByDietType()`, `getRecipeById()`

- [ ] 3.0 Implement Meal Scoring and Selection Algorithm
  - [ ] 3.1 Create `MacroTargets.kt` data class with protein, carbs, fat gram targets
  - [ ] 3.2 Create `ScoreMealUseCase.kt` with scoring function from PRD Section 7
  - [ ] 3.3 Implement macro fit scoring (40% weight): calculate protein/carbs/fat difference from targets
  - [ ] 3.4 Implement variety scoring (20% weight): penalize recently used meals, add deterministic randomness
  - [ ] 3.5 Implement preference alignment scoring (20% weight): boost favorites, reject avoided foods
  - [ ] 3.6 Implement prep time scoring (10% weight): full score if within max, penalize if over
  - [ ] 3.7 Implement cost efficiency scoring (10% weight): lower cost = higher score when budget mode enabled
  - [ ] 3.8 Create `GenerateMealPlanUseCase.kt` orchestrating full plan generation
  - [ ] 3.9 Implement calorie allocation: Breakfast 25%, Morning Snack 10%, Lunch 30%, Afternoon Snack 10%, Dinner 25%
  - [ ] 3.10 Implement macro targets calculation based on diet type percentages
  - [ ] 3.11 Implement meal selection loop: for each day, for each meal type, score all candidates, select highest
  - [ ] 3.12 Ensure no meal repeats on consecutive days
  - [ ] 3.13 Write unit tests for scoring algorithm with various preference combinations

- [ ] 4.0 Create Meal Plan Repository and Persistence
  - [ ] 4.1 Create `MealPlanRepository.kt` interface with generate, get, save, clear methods
  - [ ] 4.2 Create `MealPlanRepositoryImpl.kt` implementation
  - [ ] 4.3 Create `MealPlanDataStore.kt` using DataStore preferences for plan persistence
  - [ ] 4.4 Implement `@Serializable` annotations on plan models for Kotlin serialization
  - [ ] 4.5 Implement `saveMealPlan(plan: WeeklyMealPlan)` to DataStore
  - [ ] 4.6 Implement `getCachedMealPlan(): WeeklyMealPlan?` from DataStore
  - [ ] 4.7 Implement `isPlanExpired(): Boolean` checking if plan is >7 days old
  - [ ] 4.8 Implement `savePreferences()` and `getPreferences()` for user settings
  - [ ] 4.9 Implement `swapMeal(dayIndex, mealType, newRecipe)` to update single meal in cached plan
  - [ ] 4.10 Add `@Binds` entry in RepositoryModule for MealPlanRepository

- [ ] 5.0 Create Meal Planner UI Components
  - [ ] 5.1 Create `MealPlannerUiState.kt` sealed class: Loading, Empty, Preview, PlanReady, Preferences, Error
  - [ ] 5.2 Create `WeekDaySelector.kt` horizontal scrollable day strip (Mon-Sun)
  - [ ] 5.3 Highlight current day and selected day in WeekDaySelector
  - [ ] 5.4 Create `MealCard.kt` component with meal name, calories, prep time, meal type icon
  - [ ] 5.5 Add color-coded badges for meal types (breakfast=orange, lunch=green, dinner=purple, snack=yellow)
  - [ ] 5.6 Create `DailyNutritionSummary.kt` card with calorie + macro progress rings
  - [ ] 5.7 Highlight days over/under calorie target (±10%) in WeekDaySelector
  - [ ] 5.8 Create `RecipeDetailSheet.kt` bottom sheet with ingredients, instructions, nutrition
  - [ ] 5.9 Add serving size adjuster to RecipeDetailSheet with recalculated nutrition
  - [ ] 5.10 Add "Add to Diary" and "Swap Meal" buttons to RecipeDetailSheet
  - [ ] 5.11 Create `GeneratingPlanAnimation.kt` cooking-themed loading animation

- [ ] 6.0 Implement Meal Planner Screen and ViewModel
  - [ ] 6.1 Create `MealPlannerEvent.kt` sealed class for navigation events
  - [ ] 6.2 Create `MealPlannerViewModel.kt` with StateFlow for UI state
  - [ ] 6.3 Inject GenerateMealPlanUseCase, MealPlanRepository, UserProfileRepository
  - [ ] 6.4 Implement `loadPlan()` - check cache, show plan or prompt generation
  - [ ] 6.5 Implement `generateNewPlan()` - run algorithm on Dispatchers.Default, save to cache
  - [ ] 6.6 Implement `selectDay(dayIndex)` - update selected day in state
  - [ ] 6.7 Implement `showRecipeDetail(meal)` - show bottom sheet
  - [ ] 6.8 Implement `swapMeal(dayIndex, mealType)` - regenerate single meal, update cache
  - [ ] 6.9 Implement `addMealToDiary(meal, date)` - save to FoodEntryRepository
  - [ ] 6.10 Implement `addAllDayMealsToDiary(dayIndex)` - batch save all meals for day
  - [ ] 6.11 Create `MealPlannerScreen.kt` composable with weekly view + daily meal list
  - [ ] 6.12 Add FAB for "Regenerate Plan" action
  - [ ] 6.13 Add empty state: "Generate your first meal plan" with CTA
  - [ ] 6.14 Add expired state: "Your plan is outdated. Generate fresh?" prompt
  - [ ] 6.15 Handle recipe detail bottom sheet display with animation

- [ ] 7.0 Create Meal Plan Preferences Screen
  - [ ] 7.1 Create `MealPlanPreferencesScreen.kt` composable for first-time setup
  - [ ] 7.2 Add diet type selector (radio buttons for 5 options)
  - [ ] 7.3 Add meal inclusion toggles (checkboxes for 5 meal types)
  - [ ] 7.4 Add max prep time selector (15, 30, 45, 60+ minutes)
  - [ ] 7.5 Add budget optimization toggle switch
  - [ ] 7.6 Add variety level selector (Low, Medium, High)
  - [ ] 7.7 Add avoided foods text input (comma-separated or chips)
  - [ ] 7.8 Add favorite foods text input (comma-separated or chips)
  - [ ] 7.9 Add "Generate Plan" CTA button
  - [ ] 7.10 Save preferences to DataStore on generate
  - [ ] 7.11 Add "Edit Preferences" entry point from main planner screen

- [ ] 8.0 Integrate with Navigation and Premium Gating
  - [ ] 8.1 Add `MealPlanner` route to NavRoutes.kt
  - [ ] 8.2 Add `MealPlanPreferences` route to NavRoutes.kt
  - [ ] 8.3 Add composable entries in CalTrackNavHost.kt for both screens
  - [ ] 8.4 Add "Meal Plan" icon/button to DiaryScreen top bar or bottom nav
  - [ ] 8.5 Check premium status in ViewModel on load
  - [ ] 8.6 For free users: show blurred/limited preview with upgrade prompt
  - [ ] 8.7 Disable "Add to Diary" for free users with premium upsell
  - [ ] 8.8 Create `MealPlanPreviewMode.kt` composable showing locked preview
  - [ ] 8.9 Allow free users to view 1-2 sample recipes without adding
  - [ ] 8.10 Track analytics: plan_generated, meal_swapped, meal_added_to_diary, upgrade_prompt_shown

- [ ] 9.0 Add Diary Integration and Duplicate Prevention
  - [ ] 9.1 Implement `convertPlannedMealToFoodEntry()` mapper function
  - [ ] 9.2 In ViewModel, use FoodEntryRepository to save meals to diary
  - [ ] 9.3 Check if meal already logged for selected date before adding
  - [ ] 9.4 Show warning dialog if duplicate detected: "Already logged - add anyway?"
  - [ ] 9.5 Show success snackbar after meal(s) added to diary
  - [ ] 9.6 Allow date selection when adding meals (default to today)
  - [ ] 9.7 Navigate to diary after batch add with date set to added day
