# Tasks: Recipe Management

**PRD:** [0005-prd-recipe-management.md](0005-prd-recipe-management.md)
**Status:** Not Started

---

## Relevant Files

### New Files
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/Recipe.kt` - Recipe domain model
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/RecipeIngredient.kt` - Ingredient with quantity wrapper
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/SimpleFoodItem.kt` - Simplified food data for recipes
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/RecipeDifficulty.kt` - Difficulty enum (Easy, Medium, Hard)
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/RecipeCategory.kt` - Category enum (8 categories)
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/entity/RecipeEntity.kt` - Room entity for recipes
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/dao/RecipeDao.kt` - DAO for recipe CRUD operations
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/converter/RecipeConverters.kt` - JSON TypeConverters for ingredients/instructions
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/RecipeRepository.kt` - Repository interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/RecipeRepositoryImpl.kt` - Repository implementation
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeLibraryScreen.kt` - Main recipe list screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeLibraryViewModel.kt` - Library screen state management
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeLibraryUiState.kt` - Library UI state
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeBuilderScreen.kt` - Create/edit recipe screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeBuilderViewModel.kt` - Builder screen logic
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeBuilderUiState.kt` - Builder UI state
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeDetailScreen.kt` - Recipe detail view
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeDetailViewModel.kt` - Detail screen logic
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/RecipeDetailUiState.kt` - Detail UI state
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/components/RecipeCard.kt` - Recipe card component
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/components/IngredientRow.kt` - Ingredient list item
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/components/NutritionBadge.kt` - Nutrition display badge
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/recipe/components/IngredientSearchSheet.kt` - Modal for adding ingredients

### Modified Files
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/CalTrackDatabase.kt` - Add RecipeEntity, bump version to 4
- `app/src/main/java/com/easyaiflows/caltrackpro/di/DatabaseModule.kt` - Add RecipeDao provider
- `app/src/main/java/com/easyaiflows/caltrackpro/di/RepositoryModule.kt` - Bind RecipeRepository
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add Recipe routes
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add recipe screen destinations
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/BottomNavBar.kt` - Add Recipes tab

### Notes

- Recipe ingredients and instructions are stored as JSON strings using Gson TypeConverters
- Follow existing patterns from FoodSearchScreen for the ingredient search sheet
- Use existing FoodSearchRepository for Edamam API ingredient searches
- Image picker will use Android's built-in photo picker (ActivityResultContracts.PickVisualMedia)
- Run `./gradlew assembleDebug` to verify builds after each major task

---

## Tasks

- [x] 1.0 Create domain models and enums for recipes
  - [x] 1.1 Create `RecipeDifficulty` enum with Easy, Medium, Hard values and display names
  - [x] 1.2 Create `RecipeCategory` enum with Breakfast, Lunch, Dinner, Snack, Dessert, Main, Side, Drink values plus icon resource mappings
  - [x] 1.3 Create `SimpleFoodItem` data class with name, brand, barcode, calories, protein, carbs, fat, servingSize, servingUnit fields
  - [x] 1.4 Create `RecipeIngredient` data class with SimpleFoodItem and quantity, plus computed nutrition properties (calories, protein, carbs, fat based on quantity/servingSize)
  - [x] 1.5 Create `Recipe` data class with id, name, description, ingredients list, instructions list, servings, cookingTimeMinutes, difficulty, category, imageData, createdAt, and nutritionPerServing
  - [x] 1.6 Add computed properties to Recipe: totalCalories, totalProtein, totalCarbs, totalFat (summing ingredients)
  - [x] 1.7 Add computed properties to Recipe: caloriesPerServing, proteinPerServing, carbsPerServing, fatPerServing (total / servings)

- [x] 2.0 Set up Room database entities and DAO for recipes
  - [x] 2.1 Create `RecipeEntity` with @Entity annotation, all fields matching domain model, using String for JSON fields (ingredients, instructions)
  - [x] 2.2 Create `RecipeConverters` TypeConverter class with Moshi serialization for List<RecipeIngredient> and List<String>
  - [x] 2.3 ByteArray handled natively by Room - no additional converter needed
  - [x] 2.4 Create `RecipeDao` interface with @Insert, @Update, @Delete operations
  - [x] 2.5 Add RecipeDao query: `getAllRecipes(): Flow<List<RecipeEntity>>` for observing recipe list
  - [x] 2.6 Add RecipeDao query: `getRecipeById(id: String): Flow<RecipeEntity?>` for detail screen
  - [x] 2.7 Add RecipeDao query: `searchRecipes(query: String): Flow<List<RecipeEntity>>` for search functionality
  - [x] 2.8 Update `CalTrackDatabase` to include RecipeEntity in entities array, add RecipeConverters to TypeConverters, bump version to 5
  - [x] 2.9 Add MIGRATION_4_5 to create recipes table
  - [x] 2.10 Update `DatabaseModule.kt` to provide RecipeDao from database instance

- [x] 3.0 Create RecipeRepository with CRUD operations
  - [x] 3.1 Create `RecipeRepository` interface with getAllRecipes(), getRecipeById(), searchRecipes(), insertRecipe(), updateRecipe(), deleteRecipe() methods
  - [x] 3.2 Create `RecipeRepositoryImpl` class with @Inject constructor taking RecipeDao
  - [x] 3.3 Implement entity-to-domain mapping functions (RecipeEntity.toDomain(), Recipe.toEntity())
  - [x] 3.4 Implement getAllRecipes() returning Flow<List<Recipe>> with mapping
  - [x] 3.5 Implement getRecipeById() returning Flow<Recipe?> with mapping
  - [x] 3.6 Implement searchRecipes() for text search by name/description
  - [x] 3.7 Implement insertRecipe() and updateRecipe() suspend functions
  - [x] 3.8 Implement deleteRecipe() suspend function
  - [x] 3.9 Update `RepositoryModule.kt` to bind RecipeRepository to RecipeRepositoryImpl with @Singleton scope

- [x] 4.0 Build Recipe Library screen with search, filter, and sort
  - [x] 4.1 Create `RecipeLibraryUiState` data class with recipes list, searchQuery, selectedCategory, sortOrder, isLoading, and filteredRecipes computed property
  - [x] 4.2 Create `RecipeSortOrder` enum with Newest, Oldest, NameAZ, NameZA, CookTime, Difficulty options
  - [x] 4.3 Create `RecipeLibraryViewModel` with @HiltViewModel, inject RecipeRepository
  - [x] 4.4 Implement recipe loading, filtering by category, sorting, and search logic in ViewModel
  - [x] 4.5 Create `RecipeCard` composable showing image/placeholder, name, description, cook time, servings, difficulty badge, nutrition preview
  - [x] 4.6 Create difficulty color helper: green for Easy, orange for Medium, red for Hard
  - [x] 4.7 Create category icon helper mapping each RecipeCategory to appropriate Material icon
  - [x] 4.8 Build RecipeLibraryScreen header section with title, recipe count, and "+" FAB button
  - [x] 4.9 Build search bar component with text input and clear button
  - [x] 4.10 Build filter controls row with category dropdown and sort dropdown
  - [x] 4.11 Build scrollable LazyColumn of RecipeCards with click navigation to detail
  - [x] 4.12 Implement empty states: "No Recipes Yet" with create button, and "No Results" for empty search

- [ ] 5.0 Build Recipe Builder screen with ingredient management
  - [ ] 5.1 Create `RecipeBuilderUiState` data class with all form fields, ingredients list, instructions list, selectedImage, validation errors, and isSaving flag
  - [ ] 5.2 Create `RecipeBuilderViewModel` with form field update methods, ingredient add/remove/update, instruction add/remove, and save logic
  - [ ] 5.3 Implement nutrition calculation in ViewModel: compute total and per-serving nutrition from ingredients list
  - [ ] 5.4 Build header section with title, nutrition preview button, and image picker area
  - [ ] 5.5 Implement image picker using ActivityResultContracts.PickVisualMedia() with JPEG compression to ByteArray
  - [ ] 5.6 Build basic info section: recipe name TextField, description TextField
  - [ ] 5.7 Build servings selector with - and + buttons (1-100 range)
  - [ ] 5.8 Build cooking time slider (5-180 minutes, 5-minute increments) with time display
  - [ ] 5.9 Build difficulty picker using SegmentedButtons (Easy, Medium, Hard)
  - [ ] 5.10 Build category picker using ExposedDropdownMenu
  - [ ] 5.11 Build ingredients section with "Add Ingredient" button and list of IngredientRows
  - [ ] 5.12 Create `IngredientRow` composable showing name, quantity, unit, calories, and remove button
  - [ ] 5.13 Implement quantity edit dialog that opens on tap with TextField and Save/Cancel buttons
  - [ ] 5.14 Create `IngredientSearchSheet` ModalBottomSheet that reuses FoodSearchRepository for Edamam API search
  - [ ] 5.15 Implement converting SearchResult to SimpleFoodItem when adding ingredient
  - [ ] 5.16 Build instructions section with "Add Step" button and numbered TextFields for each step
  - [ ] 5.17 Implement instruction step add/remove with minimum 1 step and remove button when >1
  - [ ] 5.18 Build nutrition preview section (visible when ingredients exist) showing per-serving calories, protein, carbs, fat with NutritionBadge components
  - [ ] 5.19 Create `NutritionBadge` composable with icon, value, unit, and color-coded background
  - [ ] 5.20 Build "Save Recipe" button at bottom, disabled until name and ≥1 ingredient provided
  - [ ] 5.21 Implement save logic: validate, calculate final nutrition, call repository insert, navigate back on success
  - [ ] 5.22 Add edit mode support: accept optional recipeId parameter, load existing recipe, use update instead of insert

- [ ] 6.0 Build Recipe Detail screen with serving scaler and diary integration
  - [ ] 6.1 Create `RecipeDetailUiState` data class with recipe, servingMultiplier (default 1.0), scaledNutrition, isDeleting flag
  - [ ] 6.2 Create `RecipeDetailViewModel` with @HiltViewModel, inject RecipeRepository and FoodEntryRepository
  - [ ] 6.3 Implement recipe loading by ID with error handling for not found
  - [ ] 6.4 Implement serving multiplier logic (0.5 to 5.0 in 0.5 increments) with scaled nutrition calculation
  - [ ] 6.5 Build hero section with full-width recipe image or category placeholder with icon
  - [ ] 6.6 Build recipe info card with name, description, and metadata row (cook time, servings, difficulty)
  - [ ] 6.7 Build serving size adjuster with - button, multiplier display (e.g., "1.5×"), + button, and "Original serves X" label
  - [ ] 6.8 Build ingredients section showing list with scaled quantities based on multiplier
  - [ ] 6.9 Build instructions section with numbered step-by-step list
  - [ ] 6.10 Build nutrition section with "Per Adjusted Serving" header and scaled calories, protein, carbs, fat values
  - [ ] 6.11 Build "Add to Food Diary" button that creates FoodEntry from scaled recipe
  - [ ] 6.12 Implement add to diary: create FoodEntry with recipe name, scaled nutrition, servingUnit="serving", mealType based on current time
  - [ ] 6.13 Build "Delete Recipe" button with red styling
  - [ ] 6.14 Implement delete with confirmation AlertDialog, call repository delete, navigate back on confirm
  - [ ] 6.15 Add "Edit Recipe" button that navigates to RecipeBuilderScreen with recipeId parameter

- [ ] 7.0 Integrate recipes into app navigation (bottom nav tab + routes)
  - [ ] 7.1 Add NavRoutes: `RecipeLibrary`, `RecipeDetail(recipeId)`, `RecipeBuilder(recipeId?)` to NavRoutes.kt
  - [ ] 7.2 Add recipe screen composables to CalTrackNavHost with proper arguments and navigation callbacks
  - [ ] 7.3 Add "Recipes" tab to BottomNavBar with book icon (Icons.Default.MenuBook or similar)
  - [ ] 7.4 Update BottomNavBar selection logic to include Recipes route
  - [ ] 7.5 Implement navigation: Library → Detail (with recipeId), Library → Builder (create), Detail → Builder (edit with recipeId)
  - [ ] 7.6 Implement back navigation from Detail and Builder screens
  - [ ] 7.7 Test full navigation flow: Diary tab → Recipes tab → Create Recipe → Save → View Detail → Add to Diary → Diary tab shows entry
  - [ ] 7.8 Verify app builds successfully with `./gradlew assembleDebug`
