# Task List: Barcode Scanning with ML Kit

**PRD:** [0003-prd-barcode-scanning.md](./0003-prd-barcode-scanning.md)
**Status:** In Progress
**Created:** 2025-01-04

---

## Relevant Files

### New Files to Create

**Dependencies & Configuration:**
- `gradle/libs.versions.toml` - Add ML Kit, CameraX, and Accompanist Permissions dependencies
- `app/build.gradle.kts` - Include new dependencies

**Data Layer - Barcode Caching:**
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/entity/ScannedBarcodeEntity.kt` - Room entity for barcode → food mappings
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/dao/ScannedBarcodeDao.kt` - DAO for barcode cache operations
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/entity/BarcodeEntityMappers.kt` - Mappers between entity and domain

**UI - Scanner Screen:**
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/scanner/BarcodeScannerScreen.kt` - Main scanner composable with CameraX
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/scanner/BarcodeScannerViewModel.kt` - ViewModel for scan state and API calls
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/scanner/BarcodeScannerUiState.kt` - UI state for scanner screen
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/scanner/components/ViewfinderOverlay.kt` - Camera viewfinder overlay composable

### Existing Files to Modify

- `app/src/main/java/com/easyaiflows/caltrackpro/data/remote/EdamamApiService.kt` - Add barcode lookup endpoint
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodSearchRepository.kt` - Add barcode methods
- `app/src/main/java/com/easyaiflows/caltrackpro/data/repository/FoodSearchRepositoryImpl.kt` - Implement barcode lookup with caching
- `app/src/main/java/com/easyaiflows/caltrackpro/data/local/CalTrackDatabase.kt` - Add ScannedBarcodeEntity and DAO
- `app/src/main/java/com/easyaiflows/caltrackpro/di/DatabaseModule.kt` - Add migration and DAO provider
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/NavRoutes.kt` - Add BarcodeScanner route
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/navigation/CalTrackNavHost.kt` - Add scanner screen destination
- `app/src/main/java/com/easyaiflows/caltrackpro/ui/diary/DiaryScreen.kt` - Wire up scan button navigation
- `app/src/main/AndroidManifest.xml` - Add CAMERA and VIBRATE permissions

### Notes

- Follow existing MVVM patterns: ViewModel + StateFlow + Repository
- Use Accompanist Permissions for Compose-friendly permission handling
- Reuse existing `FoodDetailScreen` for displaying scan results
- Scanned foods should integrate with existing Recent Searches flow
- Database version will increment from 3 to 4

---

## Tasks

- [x] 1.0 Set up dependencies and permissions for camera and ML Kit
  - [x] 1.1 Add ML Kit Barcode Scanning dependency to `libs.versions.toml` (`com.google.mlkit:barcode-scanning:17.2.0`)
  - [x] 1.2 Add CameraX dependencies to `libs.versions.toml` (camera-core, camera-camera2, camera-lifecycle, camera-view)
  - [x] 1.3 Add Accompanist Permissions dependency to `libs.versions.toml` (`com.google.accompanist:accompanist-permissions`)
  - [x] 1.4 Update `app/build.gradle.kts` to include all new dependencies
  - [x] 1.5 Add CAMERA permission to `AndroidManifest.xml`
  - [x] 1.6 Add VIBRATE permission to `AndroidManifest.xml` for haptic feedback
  - [x] 1.7 Add `<uses-feature android:name="android.hardware.camera" android:required="false"/>` to manifest

- [x] 2.0 Add barcode lookup endpoint to Edamam API service
  - [x] 2.1 Add `searchByBarcode(upc: String)` method to `EdamamApiService.kt` interface
  - [x] 2.2 Configure the endpoint with `@Query("upc")` parameter for barcode lookup
  - [x] 2.3 Add `lookupByBarcode(barcode: String)` method to `FoodSearchRepository` interface
  - [x] 2.4 Implement `lookupByBarcode()` in `FoodSearchRepositoryImpl` calling the API
  - [x] 2.5 Handle empty/null response when barcode is not found in database

- [x] 3.0 Create barcode caching infrastructure in Room database
  - [x] 3.1 Create `ScannedBarcodeEntity.kt` with barcode, foodId, and all nutrition fields
  - [x] 3.2 Create `ScannedBarcodeDao.kt` with insert, getByBarcode, and delete operations
  - [x] 3.3 Add barcode mappers to `SearchEntityMappers.kt` (SearchedFood ↔ ScannedBarcodeEntity)
  - [x] 3.4 Update `CalTrackDatabase.kt` to include ScannedBarcodeEntity (increment to version 4)
  - [x] 3.5 Add MIGRATION_3_4 to `DatabaseModule.kt` creating scanned_barcodes table
  - [x] 3.6 Add `provideScannedBarcodeDao()` to `DatabaseModule.kt`
  - [x] 3.7 Inject `ScannedBarcodeDao` into `FoodSearchRepositoryImpl`
  - [x] 3.8 Add `cacheBarcodeResult()` and `getCachedBarcode()` methods to repository

- [ ] 4.0 Implement barcode scanner screen with CameraX and ML Kit
  - [ ] 4.1 Create `BarcodeScannerUiState.kt` with scanning, loading, success, error, permission states
  - [ ] 4.2 Create `BarcodeScannerViewModel.kt` with Hilt injection and StateFlow
  - [ ] 4.3 Implement ML Kit BarcodeScanner setup with supported formats (UPC_A, UPC_E, EAN_13, EAN_8, CODE_128, CODE_39)
  - [ ] 4.4 Create `ViewfinderOverlay.kt` composable with semi-transparent overlay and scan area cutout
  - [ ] 4.5 Create `BarcodeScannerScreen.kt` with CameraX PreviewView integration
  - [ ] 4.6 Implement camera permission handling using Accompanist Permissions
  - [ ] 4.7 Add permission denied state with rationale and settings redirect button
  - [ ] 4.8 Implement barcode detection callback from ML Kit ImageAnalysis
  - [ ] 4.9 Add haptic feedback (Vibrator) on successful barcode detection
  - [ ] 4.10 Add flashlight/torch toggle button in TopAppBar
  - [ ] 4.11 Display loading overlay while looking up barcode in API
  - [ ] 4.12 Handle "Product not found" with option to search by text

- [ ] 5.0 Integrate scanner with navigation and add-food flow
  - [ ] 5.1 Add `BarcodeScanner` route to `NavRoutes.kt` with mealType and date parameters
  - [ ] 5.2 Add `BarcodeScannerScreen` composable to `CalTrackNavHost.kt`
  - [ ] 5.3 Update `DiaryScreen.kt` to navigate to scanner when "Scan Barcode" is clicked
  - [ ] 5.4 Navigate to `FoodDetailScreen` when barcode lookup succeeds
  - [ ] 5.5 Navigate to `FoodSearchScreen` with pre-filled query on "Search instead" action
  - [ ] 5.6 Add scanned food to Recent Searches using existing `addToRecentSearches()` method

- [ ] 6.0 Add offline support and error handling for barcode scanning
  - [ ] 6.1 Check `NetworkMonitor.isConnected` before API call
  - [ ] 6.2 If offline, check local cache for previously scanned barcode
  - [ ] 6.3 Display "Offline - using cached data" banner when serving cached result
  - [ ] 6.4 Display "No internet connection. This product hasn't been scanned before." when offline and not cached
  - [ ] 6.5 Cache successful barcode lookups in Room via `cacheBarcodeResult()`
  - [ ] 6.6 Handle API errors with user-friendly messages (timeout, server error, rate limit)
  - [ ] 6.7 Add retry button for failed lookups
  - [ ] 6.8 Handle camera initialization errors gracefully
