# PRD: AI Meal Planner

## 1. Introduction/Overview

The AI Meal Planner generates personalized weekly meal plans based on user's calorie goals, macro targets, dietary preferences, and lifestyle factors. The system uses a scoring algorithm to select optimal meals from a database of 100+ recipes, ensuring variety, nutritional balance, and practical considerations like preparation time and cost.

**Problem Solved:** Planning healthy meals that meet specific nutritional goals is time-consuming and mentally taxing. Users often default to repetitive meals or abandon their nutrition goals due to decision fatigue. An AI-powered meal planner removes this friction by automatically generating balanced, personalized meal plans.

**Android Enhancement:** This implementation includes all iOS functionality plus Android-specific features like widget integration for daily meal view, Google Calendar sync for meal reminders, and Material You theming for a native Android experience.

## 2. Goals

1. **Generate personalized meal plans** - Create 7-day plans tailored to user's goals and preferences
2. **Ensure nutritional accuracy** - Plans match user's calorie and macro targets within 5% variance
3. **Maximize variety** - Avoid repetitive meals; ensure diverse food choices across the week
4. **Consider practical factors** - Account for prep time, cooking skill, and budget constraints
5. **Support multiple diets** - Balanced, Low Carb, High Protein, Mediterranean, Vegan
6. **Enable easy logging** - One-tap to add planned meals to food diary
7. **Match iOS parity** - Same recipe database, scoring algorithm, and functionality
8. **Premium feature** - Drive subscription conversions with preview for free users

## 3. User Stories

### Free Users (Preview)
- **US-1:** As a free user, I want to see a sample meal plan so that I understand the feature's value.
- **US-2:** As a free user, I want to see an upgrade prompt with benefits listed so that I can decide to subscribe.

### Premium Users
- **US-3:** As a premium user, I want to generate a weekly meal plan based on my goals so that I don't have to plan meals manually.
- **US-4:** As a premium user, I want to select my diet type so that the plan matches my eating style.
- **US-5:** As a premium user, I want to specify foods to avoid so that I don't see meals with ingredients I dislike or can't eat.
- **US-6:** As a premium user, I want to mark favorite foods so that they appear more often in my plans.
- **US-7:** As a premium user, I want to set a maximum prep time so that I only get meals I have time to cook.
- **US-8:** As a premium user, I want to regenerate individual meals I don't like while keeping the rest of the plan.
- **US-9:** As a premium user, I want to add a planned meal to my diary with one tap.
- **US-10:** As a premium user, I want to see recipes with ingredients and instructions for each meal.
- **US-11:** As a premium user, I want my plan to refresh weekly so that I always have new meal ideas.

### All Users
- **US-12:** As a user, I want to see the total nutrition for each day so that I know I'm hitting my goals.
- **US-13:** As a user, I want to see estimated prep time and cost for each meal.

## 4. Functional Requirements

### 4.1 Meal Plan Generation
1. The system must generate 7-day meal plans (Monday through Sunday)
2. The system must include 5 meals per day: Breakfast, Morning Snack, Lunch, Afternoon Snack, Dinner
3. The system must calculate daily calorie targets from user profile
4. The system must allocate calories per meal type:
   - Breakfast: 25% of daily calories
   - Morning Snack: 10% of daily calories
   - Lunch: 30% of daily calories
   - Afternoon Snack: 10% of daily calories
   - Dinner: 25% of daily calories
5. The system must calculate macro targets based on diet type:
   - Balanced: 30% protein, 40% carbs, 30% fat
   - Low Carb: 35% protein, 20% carbs, 45% fat
   - High Protein: 40% protein, 35% carbs, 25% fat
   - Mediterranean: 25% protein, 45% carbs, 30% fat
   - Vegan: 25% protein, 50% carbs, 25% fat
6. The system must use a scoring algorithm to select optimal meals (see Section 7)
7. The system must ensure no meal repeats on consecutive days
8. The system must respect user's avoided foods list
9. The system must prioritize user's favorite foods

### 4.2 Preferences & Customization
10. The system must allow selection of diet type (Balanced, Low Carb, High Protein, Mediterranean, Vegan)
11. The system must allow users to specify foods/ingredients to avoid (allergies, dislikes)
12. The system must allow users to mark favorite foods
13. The system must allow setting maximum preparation time (15, 30, 45, 60+ minutes)
14. The system must allow budget optimization toggle (prefer lower-cost ingredients)
15. The system must allow selection of which meals to include (e.g., skip snacks)
16. The system must allow setting variety level (Low, Medium, High)
17. The system must save preferences for future plan generations

### 4.3 Recipe Database
18. The system must include 100+ recipes across all meal types
19. Each recipe must include: name, ingredients list, step-by-step instructions
20. Each recipe must include nutrition data: calories, protein, carbs, fat, fiber
21. Each recipe must include metadata: prep time, difficulty, estimated cost, cuisine type
22. The system must categorize recipes by meal type (breakfast, lunch, dinner, snack)
23. The system must tag recipes by diet compatibility (vegan, low-carb, high-protein, etc.)
24. The system must support recipe scaling for different serving sizes

### 4.4 Meal Plan Display
25. The system must display weekly view with all 7 days visible
26. The system must display daily view showing all meals for selected day
27. Each meal card must show: meal name, calories, prep time, and meal type icon
28. The system must display daily nutrition totals (calories, protein, carbs, fat)
29. The system must display weekly nutrition averages
30. The system must highlight days that are under/over calorie target (±10%)
31. The system must show progress indicator for current day

### 4.5 Meal Details
32. The system must display full recipe when meal is tapped
33. Recipe view must show: ingredients with quantities, step-by-step instructions
34. Recipe view must show: total nutrition, serving size, prep time, difficulty
35. Recipe view must allow serving size adjustment with recalculated nutrition
36. Recipe view must have "Add to Diary" button
37. Recipe view must have "Swap Meal" button to regenerate that meal only
38. Recipe view must support recipe suggestions for complementary foods

### 4.6 Diary Integration
39. The system must allow adding individual meals to food diary with one tap
40. The system must allow adding all meals for a day to diary
41. When adding to diary, system must use current date or allow date selection
42. The system must show confirmation when meals are added
43. The system must prevent duplicate additions (warn if meal already logged today)

### 4.7 Plan Management
44. The system must cache current meal plan locally (DataStore)
45. The system must track plan creation date
46. The system must prompt for plan refresh if plan is >7 days old
47. The system must allow manual plan regeneration at any time
48. The system must preserve user edits (swapped meals) in cached plan
49. The system must allow exporting meal plan (share as text/image)

### 4.8 Preview Mode (Free Users)
50. Free users must see a locked meal plan preview (blurred or limited)
51. Free users must see sample recipes for 1-2 meals
52. Preview must display prominent upgrade prompt with feature benefits
53. Free users can interact with UI but cannot add meals to diary

## 5. Non-Goals (Out of Scope)

1. **Grocery list generation** - Won't automatically create shopping lists from meal plans
2. **Inventory tracking** - Won't track what ingredients user already has
3. **Real-time API generation** - Uses pre-built recipe database, not ChatGPT generation
4. **Restaurant meals** - Only home-cooked recipes, not restaurant menu items
5. **Meal delivery integration** - Won't connect to meal kit services
6. **Social sharing** - Won't share plans with friends/family
7. **Leftover management** - Won't suggest using yesterday's dinner for today's lunch
8. **Seasonal adjustments** - Won't vary recipes based on ingredient seasonality
9. **Custom recipe import** - Won't allow users to add their own recipes to the planner database

## 6. Design Considerations

### UI Flow
```
Diary Screen (or dedicated tab)
    → Tap "Meal Plan" icon
    → [Free User] Preview mode with upgrade prompt
    → [Premium User] Meal Plan Screen
        → First time: Preferences setup wizard
        → Returning: Current week's plan
    → Weekly view (horizontal day selector)
    → Daily view (vertical meal list)
    → Tap meal → Recipe Detail Sheet
        → Add to Diary / Swap Meal / View Instructions
```

### Visual Design
- Weekly calendar strip at top (Mon-Sun, current day highlighted)
- Meal cards with food imagery, name, calories, prep time badge
- Color-coded meal types (breakfast=orange, lunch=green, dinner=purple, snack=yellow)
- Daily totals card at bottom with macro rings
- Floating action button for "Regenerate Plan"
- Recipe detail as bottom sheet (70% screen height)

### Animations
- Day selection slides meal list horizontally
- Meal cards animate in sequentially
- Swap meal has shuffle animation
- Add to diary shows success checkmark
- Plan generation shows cooking-themed loading animation

### Empty States
- No plan yet: "Generate your first meal plan" with CTA button
- Plan expired: "Your plan is outdated. Generate a fresh week?"

## 7. Technical Considerations

### Architecture
```
ui/screens/MealPlannerScreen.kt
ui/screens/MealPlanPreferencesScreen.kt
ui/screens/RecipeDetailScreen.kt
ui/viewmodels/MealPlannerViewModel.kt
ui/components/WeekDaySelector.kt
ui/components/MealCard.kt
ui/components/DailyNutritionSummary.kt
domain/usecase/GenerateMealPlanUseCase.kt
domain/usecase/ScoreMealUseCase.kt
domain/model/MealPlan.kt
domain/model/DailyPlan.kt
domain/model/PlannedMeal.kt
domain/model/Recipe.kt
domain/model/MealPlanPreferences.kt
data/local/MealPlanDataStore.kt
data/local/RecipeDatabase.kt
data/repository/MealPlanRepository.kt
```

### Scoring Algorithm

The meal selection algorithm scores each candidate meal (0-100 points):

```kotlin
fun scoreMeal(
    candidate: Recipe,
    targetMacros: MacroTargets,
    preferences: MealPlanPreferences,
    dayIndex: Int,
    usedMeals: Set<String>
): Double {
    var score = 0.0

    // 1. Macro Fit (40% weight)
    val proteinDiff = abs(candidate.protein - targetMacros.protein) / max(targetMacros.protein, 1.0)
    val carbsDiff = abs(candidate.carbs - targetMacros.carbs) / max(targetMacros.carbs, 1.0)
    val fatDiff = abs(candidate.fat - targetMacros.fat) / max(targetMacros.fat, 1.0)
    val macroScore = 1.0 - ((proteinDiff + carbsDiff + fatDiff) / 3.0)
    score += macroScore * 40

    // 2. Variety (20% weight)
    val varietyScore = if (candidate.id !in usedMeals) 1.0 else 0.3
    val dayHash = (candidate.name.hashCode() + dayIndex) % 100 / 100.0 // Deterministic randomness
    score += (varietyScore * 0.7 + dayHash * 0.3) * 20

    // 3. Preference Alignment (20% weight)
    var prefScore = 0.5 // Neutral
    if (candidate.name in preferences.favoriteFoods) prefScore = 1.0
    if (candidate.ingredients.any { it in preferences.avoidFoods }) prefScore = -1.0
    score += (prefScore + 1.0) / 2.0 * 20 // Normalize to 0-20

    // 4. Preparation Time (10% weight)
    val timeScore = if (candidate.prepTime <= preferences.maxPrepTime) 1.0 else 0.2
    score += timeScore * 10

    // 5. Cost Efficiency (10% weight)
    val costScore = if (preferences.budgetOptimized) {
        1.0 - (candidate.estimatedCost / 15.0) // $15 max reference
    } else 0.5
    score += costScore * 10

    return score.coerceIn(0.0, 100.0)
}
```

### Data Models

```kotlin
data class WeeklyMealPlan(
    val id: String = UUID.randomUUID().toString(),
    val startDate: LocalDate,
    val endDate: LocalDate,
    val dailyPlans: List<DailyMealPlan>,
    val preferences: MealPlanPreferences,
    val generatedAt: Instant,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double
)

data class DailyMealPlan(
    val date: LocalDate,
    val meals: List<PlannedMeal>,
    val totalCalories: Double,
    val totalProtein: Double,
    val totalCarbs: Double,
    val totalFat: Double
)

data class PlannedMeal(
    val id: String = UUID.randomUUID().toString(),
    val mealType: MealType, // BREAKFAST, MORNING_SNACK, LUNCH, AFTERNOON_SNACK, DINNER
    val recipe: Recipe,
    val servings: Double = 1.0,
    val isSwapped: Boolean = false
) {
    val totalCalories: Double get() = recipe.calories * servings
    val totalProtein: Double get() = recipe.protein * servings
    val totalCarbs: Double get() = recipe.carbs * servings
    val totalFat: Double get() = recipe.fat * servings
}

data class Recipe(
    val id: String,
    val name: String,
    val description: String?,
    val mealTypes: List<MealType>, // Which meal types this recipe fits
    val dietTypes: List<DietType>, // Compatible diet types
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val servingSize: Double,
    val servingUnit: String,
    val prepTime: Int, // minutes
    val cookTime: Int, // minutes
    val difficulty: Difficulty, // EASY, MEDIUM, HARD
    val estimatedCost: Double, // USD per serving
    val imageUrl: String?,
    val cuisineType: CuisineType?
)

data class RecipeIngredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

data class MealPlanPreferences(
    val dietType: DietType = DietType.BALANCED,
    val includedMeals: Set<MealType> = MealType.values().toSet(),
    val avoidFoods: List<String> = emptyList(),
    val favoriteFoods: List<String> = emptyList(),
    val maxPrepTime: Int = 60, // minutes
    val budgetOptimized: Boolean = false,
    val varietyLevel: VarietyLevel = VarietyLevel.MEDIUM,
    val cuisinePreferences: List<CuisineType> = emptyList()
)

enum class DietType {
    BALANCED,      // 30/40/30 P/C/F
    LOW_CARB,      // 35/20/45
    HIGH_PROTEIN,  // 40/35/25
    MEDITERRANEAN, // 25/45/30
    VEGAN          // 25/50/25
}

enum class MealType {
    BREAKFAST,
    MORNING_SNACK,
    LUNCH,
    AFTERNOON_SNACK,
    DINNER
}

enum class VarietyLevel {
    LOW,    // More repetition allowed
    MEDIUM, // Balanced variety
    HIGH    // Maximum variety, rarely repeat
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class CuisineType {
    AMERICAN,
    MEXICAN,
    ITALIAN,
    ASIAN,
    MEDITERRANEAN,
    INDIAN,
    OTHER
}
```

### Persistence

```kotlin
// DataStore for meal plan cache
@Serializable
data class CachedMealPlan(
    val plan: WeeklyMealPlan,
    val cachedAt: Long // epoch millis
)

// Check freshness
fun isCacheValid(cache: CachedMealPlan): Boolean {
    val ageInDays = (System.currentTimeMillis() - cache.cachedAt) / (1000 * 60 * 60 * 24)
    return ageInDays < 7
}
```

### Premium Gating
- Full generation requires premium subscription
- Free users see blurred/locked preview
- "Add to Diary" requires premium
- Preferences customization requires premium

### Performance
- Recipe database loaded at app start (bundled JSON, ~500KB)
- Plan generation runs on Dispatchers.Default (CPU-bound)
- Generation takes <1 second for 35 meals
- UI updates asynchronously as days are calculated

## 8. Success Metrics

1. **Plan Generation Rate:** >50% of premium users generate at least one plan within 14 days
2. **Plan Completion Rate:** >30% of meals in generated plans are logged to diary
3. **Retention Impact:** Premium users who use meal planner have >20% higher 30-day retention
4. **Regeneration Rate:** <20% of meals are swapped (indicates good initial selection)
5. **Preview Conversion:** >10% of free users who view preview upgrade within 7 days
6. **Plan Refresh Rate:** >60% of users generate new plan within 7 days of previous plan
7. **Feature Rating:** >4.2 stars in in-app feedback (if collected)

## 9. Open Questions

1. **Recipe Licensing:** Are the 100+ recipes original content, or do we need to source/license them? What are the legal considerations?

2. **Nutritional Accuracy:** How do we ensure recipe nutrition data is accurate? Should we use USDA API for ingredient lookup during recipe creation?

3. **Personalization Learning:** Should the algorithm learn from user behavior (e.g., which meals they actually log vs. skip) to improve future suggestions?

4. **Plan Sharing:** Should users be able to share their meal plan (as image/PDF) with partners, trainers, or nutritionists?

5. **Leftover Intelligence:** In future versions, should we suggest recipes that use similar ingredients to reduce waste?

6. **Widget Priority:** Should meal planner widget show today's meals, or be a separate implementation from the nutrition summary widget?

---

## Appendix A: Recipe Database (100+ Items - Sample)

### Breakfast Recipes (20+)

| Name | Calories | Protein | Carbs | Fat | Prep Time | Diet Types |
|------|----------|---------|-------|-----|-----------|------------|
| Scrambled Eggs (4 eggs) | 280 | 24 | 2 | 20 | 10 min | Balanced, Low Carb, High Protein |
| Egg White Omelette | 120 | 24 | 2 | 1 | 15 min | Low Carb, High Protein |
| Protein Pancakes | 300 | 28 | 30 | 8 | 20 min | Balanced, High Protein |
| Greek Yogurt Parfait | 300 | 20 | 35 | 8 | 5 min | Balanced, High Protein |
| Overnight Oats | 350 | 12 | 55 | 10 | 5 min | Balanced, Vegan |
| Avocado Toast with Egg | 320 | 14 | 25 | 20 | 10 min | Balanced, Mediterranean |
| Cottage Cheese Bowl | 220 | 28 | 10 | 8 | 5 min | Low Carb, High Protein |
| Tofu Scramble | 220 | 18 | 8 | 14 | 15 min | Vegan |
| Acai Bowl | 350 | 8 | 60 | 10 | 10 min | Vegan |
| Chia Pudding | 280 | 8 | 30 | 16 | 5 min | Vegan, Mediterranean |
| Bacon & Eggs | 350 | 22 | 1 | 28 | 15 min | Low Carb, High Protein |
| Smoked Salmon Plate | 280 | 24 | 10 | 16 | 10 min | Mediterranean, Low Carb |
| Shakshuka | 280 | 16 | 18 | 18 | 25 min | Mediterranean |

### Lunch Recipes (20+)

| Name | Calories | Protein | Carbs | Fat | Prep Time | Diet Types |
|------|----------|---------|-------|-----|-----------|------------|
| Grilled Chicken Salad | 350 | 40 | 15 | 16 | 20 min | All |
| Tuna Steak | 280 | 40 | 2 | 12 | 20 min | Low Carb, High Protein, Mediterranean |
| Chicken & Rice Bowl | 450 | 38 | 45 | 12 | 25 min | Balanced, High Protein |
| Cobb Salad | 380 | 28 | 12 | 26 | 15 min | Low Carb |
| Bunless Burger | 400 | 35 | 8 | 26 | 20 min | Low Carb, High Protein |
| Lettuce Wrap Tacos | 320 | 25 | 15 | 18 | 20 min | Low Carb |
| Greek Salad with Chicken | 380 | 32 | 18 | 22 | 15 min | Mediterranean |
| Mediterranean Bowl | 420 | 25 | 45 | 18 | 20 min | Mediterranean |
| Buddha Bowl | 380 | 15 | 55 | 14 | 25 min | Vegan |
| Black Bean Tacos | 350 | 14 | 50 | 12 | 20 min | Vegan |
| Turkey Wrap | 380 | 28 | 35 | 16 | 10 min | Balanced |
| Quinoa Power Bowl | 420 | 18 | 55 | 16 | 25 min | Vegan, Mediterranean |

### Dinner Recipes (25+)

| Name | Calories | Protein | Carbs | Fat | Prep Time | Diet Types |
|------|----------|---------|-------|-----|-----------|------------|
| Grilled Ribeye Steak | 450 | 42 | 0 | 30 | 25 min | Low Carb, High Protein |
| Baked Salmon | 350 | 35 | 5 | 22 | 25 min | All |
| Grilled Chicken Thighs | 320 | 35 | 2 | 18 | 30 min | All |
| Pork Chops | 350 | 38 | 2 | 20 | 25 min | Low Carb, High Protein |
| Baked Cod | 280 | 35 | 8 | 12 | 25 min | Mediterranean |
| Lamb Chops | 400 | 32 | 0 | 30 | 30 min | Low Carb, Mediterranean |
| Grilled Sea Bass | 280 | 32 | 5 | 14 | 25 min | Mediterranean |
| Chicken Souvlaki | 350 | 35 | 15 | 18 | 35 min | Mediterranean, High Protein |
| Chickpea Curry | 350 | 14 | 45 | 14 | 30 min | Vegan |
| Tofu Stir-Fry | 300 | 20 | 25 | 16 | 25 min | Vegan |
| Black Bean Burgers | 280 | 16 | 35 | 10 | 30 min | Vegan |
| Chicken Breast with Vegetables | 380 | 42 | 20 | 14 | 30 min | Balanced, High Protein |
| Shrimp Scampi | 380 | 30 | 35 | 14 | 25 min | Mediterranean |
| Turkey Meatballs | 350 | 35 | 18 | 16 | 35 min | Balanced, High Protein |

### Snack Recipes (20+)

| Name | Calories | Protein | Carbs | Fat | Prep Time | Diet Types |
|------|----------|---------|-------|-----|-----------|------------|
| Protein Shake | 200 | 30 | 10 | 5 | 5 min | All |
| Greek Yogurt Cup | 130 | 15 | 8 | 4 | 2 min | Balanced, High Protein |
| Hard Boiled Eggs (2) | 140 | 12 | 1 | 10 | 15 min | Low Carb, High Protein |
| Cheese & Pepperoni | 200 | 12 | 2 | 16 | 2 min | Low Carb |
| Beef Jerky | 120 | 20 | 5 | 2 | 0 min | Low Carb, High Protein |
| Trail Mix | 200 | 6 | 20 | 12 | 0 min | Balanced |
| Apple with Peanut Butter | 250 | 7 | 30 | 14 | 5 min | Balanced |
| Hummus with Vegetables | 180 | 6 | 20 | 10 | 5 min | Vegan, Mediterranean |
| Cottage Cheese with Fruit | 180 | 18 | 18 | 4 | 5 min | Balanced, High Protein |
| Almonds (1 oz) | 160 | 6 | 6 | 14 | 0 min | All |
| Edamame | 150 | 12 | 12 | 6 | 5 min | Vegan |
| Rice Cakes with Almond Butter | 180 | 5 | 22 | 10 | 5 min | Vegan |

## Appendix B: Recipe Detail Example

```json
{
  "id": "recipe_grilled_chicken_salad",
  "name": "Grilled Chicken Salad",
  "description": "A protein-packed salad with grilled chicken breast, mixed greens, and light vinaigrette.",
  "mealTypes": ["LUNCH", "DINNER"],
  "dietTypes": ["BALANCED", "LOW_CARB", "HIGH_PROTEIN", "MEDITERRANEAN"],
  "ingredients": [
    {"name": "Chicken Breast", "quantity": 150, "unit": "g", "calories": 165, "protein": 31, "carbs": 0, "fat": 4},
    {"name": "Mixed Greens", "quantity": 100, "unit": "g", "calories": 20, "protein": 2, "carbs": 4, "fat": 0},
    {"name": "Cherry Tomatoes", "quantity": 50, "unit": "g", "calories": 10, "protein": 0.5, "carbs": 2, "fat": 0},
    {"name": "Cucumber", "quantity": 50, "unit": "g", "calories": 8, "protein": 0.3, "carbs": 2, "fat": 0},
    {"name": "Olive Oil", "quantity": 15, "unit": "ml", "calories": 120, "protein": 0, "carbs": 0, "fat": 14},
    {"name": "Balsamic Vinegar", "quantity": 15, "unit": "ml", "calories": 14, "protein": 0, "carbs": 3, "fat": 0},
    {"name": "Feta Cheese", "quantity": 20, "unit": "g", "calories": 53, "protein": 3, "carbs": 1, "fat": 4}
  ],
  "instructions": [
    "Season chicken breast with salt, pepper, and herbs.",
    "Grill chicken on medium-high heat for 6-7 minutes per side until internal temperature reaches 165°F.",
    "Let chicken rest for 5 minutes, then slice into strips.",
    "Combine mixed greens, cherry tomatoes, and cucumber in a large bowl.",
    "Whisk together olive oil and balsamic vinegar for dressing.",
    "Top salad with sliced chicken and crumbled feta cheese.",
    "Drizzle dressing over salad and serve immediately."
  ],
  "calories": 390,
  "protein": 37,
  "carbs": 12,
  "fat": 22,
  "fiber": 3,
  "servingSize": 1,
  "servingUnit": "serving",
  "prepTime": 10,
  "cookTime": 15,
  "difficulty": "EASY",
  "estimatedCost": 4.50,
  "cuisineType": "MEDITERRANEAN"
}
```

## Appendix C: iOS Feature Parity Checklist

| iOS Feature | Android Implementation | Status |
|-------------|----------------------|--------|
| 7-day meal plans | Same logic | Planned |
| 5 meals per day | Same structure | Planned |
| 5 diet types | Same options | Planned |
| Scoring algorithm | Same weights | Planned |
| 100+ recipe database | Same recipes | Planned |
| Calorie allocation | Same percentages | Planned |
| Macro allocation by diet | Same formulas | Planned |
| Preferences (avoid/favorite) | Same functionality | Planned |
| Prep time filter | Same behavior | Planned |
| Budget optimization | Same logic | Planned |
| Meal swapping | Same feature | Planned |
| Recipe details with instructions | Same UI | Planned |
| 7-day cache refresh | Same timing | Planned |
| Add to diary | Same integration | Planned |
| Premium gating | Subscription check | Planned |

## Appendix D: Android-Specific Enhancements

1. **Meal Plan Widget:** Glance widget showing today's meals with quick "Log" action.

2. **Google Calendar Integration:** Option to add meal reminders to Google Calendar with recipe links.

3. **Material You Theming:** Dynamic colors based on user's wallpaper for meal type badges and progress rings.

4. **Share to Instagram Stories:** Export meal plan as visually appealing stories format.

5. **Wear OS Companion:** View today's meals and next meal reminder on smartwatch.

6. **Google Assistant Integration:** "Hey Google, what's for dinner?" reads from CalTrack meal plan.
