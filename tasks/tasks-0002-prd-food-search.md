# Task List: Food Search (Edamam API Integration)

**PRD:** [0002-prd-food-search.md](./0002-prd-food-search.md)
**Status:** Complete
**Created:** 2025-12-29

---

## Relevant Files

### New Files to Create

**Networking & API:**
- `app/src/main/java/com/easyaiflows/caltrackpro/di/NetworkModule.kt` - Hilt module providing Retrofit, OkHttp, Moshi
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/EdamamApiService.kt` - Retrofit interface for Edamam API
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/dto/FoodSearchResponseDto.kt` - API response DTOs
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/dto/FoodDto.kt` - Food data transfer object
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/dto/MeasureDto.kt` - Serving measure DTO
- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/dto/NutrientsDto.kt` - Nutrients DTO

**Domain Models:**
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/SearchedFood.kt` - Domain model for search results
- `app/src/main/java/com/easyaiflows/caltrackpro/domain/model/ServingMeasure.kt` - Serving measure domain model

**Repository:**
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodSearchRepository.kt` - Repository interface
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodSearchRepositoryImpl.kt` - Repository implementation

**Local Storage (Recent/Favorites):**
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/entity/RecentSearchEntity.kt` - Recent search Room entity
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/entity/FavoriteFoodEntity.kt` - Favorite food Room entity
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/dao/RecentSearchDao.kt` - Recent search DAO
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/dao/FavoriteFoodDao.kt` - Favorite food DAO

**UI - Search Screen:**
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/FoodSearchScreen.kt` - Main search screen composable
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/FoodSearchViewModel.kt` - Search screen ViewModel
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/FoodSearchUiState.kt` - UI state for search screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/components/SearchBar.kt` - Reusable search input
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/components/FoodSearchResultItem.kt` - Search result list item

**UI - Food Detail Screen:**
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/FoodDetailScreen.kt` - Food detail screen composable
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/FoodDetailViewModel.kt` - Detail screen ViewModel
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/FoodDetailUiState.kt` - UI state for detail screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/components/ServingSelector.kt` - Measure dropdown + quantity controls
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/components/NutritionSummaryCard.kt` - Macro breakdown card
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/search/components/MealTypeSelector.kt` - Meal type chip group

**Utilities:**
- `app/src/main/java/com/easyaiflows/caltrackpro/util/NetworkMonitor.kt` - Connectivity state observer

### Existing Files to Modify

- `gradle/libs.versions.toml` - Add Retrofit, OkHttp, Moshi dependencies
- `app/build.gradle.kts` - Add networking dependencies
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/CalTrackDatabase.kt` - Add new entities and DAOs
- `app/src/main/java/com/easyaiflows/caltrackpro/di/RepositoryModule.kt` - Bind FoodSearchRepository
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add FoodSearch and FoodDetail routes
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add new screen destinations
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/components/AddFoodOptionsSheet.kt` - Add "Search Foods" option

### Notes

- Follow existing patterns: Repository interface + Impl, StateFlow in ViewModels, Hilt injection
- Use `libs.versions.toml` for dependency version management (existing pattern)
- API keys should be stored in `local.properties` and accessed via BuildConfig

---

## Tasks

- [x] 1.0 Set up networking infrastructure (Retrofit, OkHttp, Hilt modules)
  - [x] 1.1 Add Retrofit, OkHttp, and Moshi dependencies to `libs.versions.toml`
  - [x] 1.2 Update `app/build.gradle.kts` to include networking dependencies
  - [x] 1.3 Create `NetworkModule.kt` Hilt module providing OkHttpClient, Moshi, and Retrofit
  - [x] 1.4 Configure API key storage in `local.properties` with BuildConfig access
  - [x] 1.5 Set up Retrofit with Edamam base URL (`https://api.edamam.com/api/food-database/v2/`)

- [x] 2.0 Create Edamam API service and data models (DTOs, domain models, repository)
  - [x] 2.1 Create `NutrientsDto.kt` with Edamam nutrient field mappings (ENERC_KCAL, PROCNT, FAT, CHOCDF, FIBTG, SUGAR, NA)
  - [x] 2.2 Create `MeasureDto.kt` for serving measure data (uri, label, weight)
  - [x] 2.3 Create `FoodDto.kt` for food item data (foodId, label, brand, nutrients, image)
  - [x] 2.4 Create `FoodSearchResponseDto.kt` with hints list and parsed list
  - [x] 2.5 Create `EdamamApiService.kt` Retrofit interface with `parser` endpoint
  - [x] 2.6 Create `SearchedFood.kt` domain model with computed nutrition values
  - [x] 2.7 Create `ServingMeasure.kt` domain model for serving options
  - [x] 2.8 Add DTO-to-domain mapping extension functions
  - [x] 2.9 Create `FoodSearchRepository.kt` interface with search method signature
  - [x] 2.10 Create `FoodSearchRepositoryImpl.kt` implementing API search
  - [x] 2.11 Add `FoodSearchRepository` binding to `RepositoryModule.kt`

- [x] 3.0 Implement Food Search screen with search bar and results list
  - [x] 3.1 Create `FoodSearchUiState.kt` with query, results, loading, and error states
  - [x] 3.2 Create `FoodSearchViewModel.kt` with Hilt injection and StateFlow
  - [x] 3.3 Implement debounced search using `debounce()` and `flatMapLatest()` operators
  - [x] 3.4 Create `SearchBar.kt` composable with text field and clear button
  - [x] 3.5 Create `FoodSearchResultItem.kt` displaying name, brand, serving, and macros
  - [x] 3.6 Create `FoodSearchScreen.kt` with Scaffold, search bar, and LazyColumn results
  - [x] 3.7 Add `FoodSearch` route to `NavRoutes.kt` with mealType and date parameters
  - [x] 3.8 Add `FoodSearchScreen` composable to `CalTrackNavHost.kt`
  - [x] 3.9 Update `AddFoodOptionsSheet.kt` to include "Search Foods" navigation option

- [x] 4.0 Implement Food Detail screen with serving selection and add-to-diary flow
  - [x] 4.1 Create `FoodDetailUiState.kt` with food, selected measure, quantity, and calculated nutrition
  - [x] 4.2 Create `FoodDetailViewModel.kt` with serving calculation logic
  - [x] 4.3 Create `ServingSelector.kt` composable with measure dropdown and quantity stepper (+/- buttons)
  - [x] 4.4 Create `NutritionSummaryCard.kt` displaying calories, protein, carbs, fat, fiber, sugar, sodium
  - [x] 4.5 Create `MealTypeSelector.kt` composable with horizontal chip group
  - [x] 4.6 Create `FoodDetailScreen.kt` with full layout and "Add to Diary" button
  - [x] 4.7 Add `FoodDetail` route to `NavRoutes.kt` with foodId parameter
  - [x] 4.8 Add `FoodDetailScreen` composable to `CalTrackNavHost.kt`
  - [x] 4.9 Implement add-to-diary logic: create FoodEntryEntity and save via FoodEntryRepository
  - [x] 4.10 Add success snackbar and navigate back to diary after adding

- [x] 5.0 Implement Recent Searches and Favorites with local persistence
  - [x] 5.1 Create `RecentSearchEntity.kt` Room entity with food data and timestamp
  - [x] 5.2 Create `FavoriteFoodEntity.kt` Room entity with full food data
  - [x] 5.3 Create `RecentSearchDao.kt` with insert, getAll, delete, and deleteOldest operations
  - [x] 5.4 Create `FavoriteFoodDao.kt` with insert, getAll, delete, and exists check
  - [x] 5.5 Update `CalTrackDatabase.kt` to include new entities and DAOs (increment version)
  - [x] 5.6 Add recent search and favorites methods to `FoodSearchRepository` interface
  - [x] 5.7 Implement recent/favorites persistence in `FoodSearchRepositoryImpl.kt`
  - [x] 5.8 Add "Recent" and "Favorites" tabs to `FoodSearchScreen.kt`
  - [x] 5.9 Implement favorite toggle (heart icon) in `FoodSearchResultItem.kt` and `FoodDetailScreen.kt`
  - [x] 5.10 Implement automatic cleanup to limit recent searches to 20 items

- [x] 6.0 Add offline support, error handling, and connectivity monitoring
  - [x] 6.1 Create `NetworkMonitor.kt` utility using ConnectivityManager for network state
  - [x] 6.2 Add network state observation to `FoodSearchViewModel`
  - [x] 6.3 Cache recent search API results in Room for offline access
  - [x] 6.4 Display offline indicator banner when no network connectivity
  - [x] 6.5 Show cached results when offline with "Offline - showing cached results" message
  - [x] 6.6 Implement user-friendly error messages for API failures
  - [x] 6.7 Handle API rate limits with "Too many requests, please try again later" message
  - [x] 6.8 Add retry button for failed searches

