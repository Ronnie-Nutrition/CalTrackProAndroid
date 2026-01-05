# PRD: Barcode Scanning with ML Kit

**Document Version:** 1.0
**Created:** 2025-01-04
**Status:** Ready for Implementation

---

## 1. Introduction/Overview

This feature enables users to scan product barcodes using their device camera to quickly look up nutrition information and add foods to their diary. Instead of manually searching for packaged foods, users can simply scan the barcode on the product packaging. The scanned barcode is used to query the Edamam Food Database API, which returns the product's nutrition data.

**Problem Solved:** Manually searching for packaged foods is time-consuming and error-prone. Users may not know the exact product name or may select the wrong item from search results. Barcode scanning provides instant, accurate identification of packaged food products.

---

## 2. Goals

1. **Quick Food Entry:** Reduce the time to add a packaged food from ~30 seconds (search + select) to ~5 seconds (scan + confirm)
2. **Accurate Identification:** Eliminate user error in selecting the wrong product from search results
3. **Seamless Integration:** Integrate barcode scanning into the existing add-food workflow
4. **Offline Resilience:** Cache scanned products for offline re-use

---

## 3. User Stories

### US-1: Basic Barcode Scan
**As a** CalTrackPro user
**I want to** scan a product barcode with my camera
**So that** I can quickly look up its nutrition information without typing

### US-2: Add Scanned Food to Diary
**As a** CalTrackPro user
**I want to** add a scanned food directly to my diary
**So that** I can track my nutrition intake efficiently

### US-3: Handle Unknown Barcodes
**As a** CalTrackPro user
**I want to** search by product name when a barcode isn't found
**So that** I can still find and add the food even if the barcode isn't in the database

### US-4: Scan in Low Light
**As a** CalTrackPro user
**I want to** use the flashlight when scanning in dark environments
**So that** I can successfully scan barcodes in any lighting condition

### US-5: Re-add Previously Scanned Foods
**As a** CalTrackPro user
**I want to** see previously scanned foods in my recent searches
**So that** I can quickly re-add foods I eat regularly without re-scanning

---

## 4. Functional Requirements

### 4.1 Scanner Setup

**FR-1:** The system must request camera permission before accessing the camera
**FR-2:** The system must display a clear permission rationale if the user initially denies camera access
**FR-3:** The system must gracefully handle permanent permission denial with a settings redirect option

### 4.2 Barcode Detection

**FR-4:** The system must use ML Kit Barcode Scanning API for barcode detection
**FR-5:** The system must support the following barcode formats:
- UPC-A (12 digits, US/Canada retail)
- UPC-E (8 digits, compressed UPC)
- EAN-13 (13 digits, international)
- EAN-8 (8 digits, small products)
- Code 128 (variable length, logistics)
- Code 39 (alphanumeric, inventory)

**FR-6:** The system must display a viewfinder overlay indicating the scan area
**FR-7:** The system must process barcodes in real-time from the camera preview
**FR-8:** The system must provide haptic feedback (vibration) upon successful barcode detection

### 4.3 Product Lookup

**FR-9:** The system must query the Edamam Food Database API using the scanned barcode (UPC parameter)
**FR-10:** The system must display a loading indicator while querying the API
**FR-11:** If the barcode is found, the system must navigate to the Food Detail screen with the product info
**FR-12:** If the barcode is NOT found, the system must:
  - Display a "Product not found" message
  - Offer to search by product name as a fallback
  - Pre-populate the search field with any text from the barcode (if applicable)

**FR-13:** The system must handle API errors gracefully with user-friendly error messages
**FR-14:** The system must cache successful barcode lookups in Room for offline access

### 4.4 Camera Controls

**FR-15:** The system must provide a flashlight/torch toggle button
**FR-16:** The flashlight toggle must be visible and easily accessible
**FR-17:** The system must remember the flashlight state during the scanning session

### 4.5 Navigation & Integration

**FR-18:** The system must be accessible from the "Add Food" options sheet (alongside "Search Foods" and "Manual Entry")
**FR-19:** The system must pass meal type and date context to the scanner, preserving user intent
**FR-20:** After successful add-to-diary, the system must navigate back to the diary screen
**FR-21:** Scanned foods must appear in the "Recent Searches" list like any other searched food

### 4.6 Offline Support

**FR-22:** The system must cache barcode-to-food mappings in Room database
**FR-23:** When offline, the system must check the local cache for previously scanned barcodes
**FR-24:** The system must display "Offline - using cached data" when serving cached results
**FR-25:** If offline and barcode not in cache, display "No internet connection. This product hasn't been scanned before."

---

## 5. Non-Goals (Out of Scope)

1. **QR Code scanning** - Not relevant for food products
2. **Image-based food recognition** - This is a separate premium AI feature
3. **Continuous/batch scanning mode** - Single scan per session is sufficient for MVP
4. **Manual barcode entry** - Users can use text search as fallback
5. **Barcode history as separate tab** - Scanned items use existing Recent Searches
6. **Front camera support** - Rear camera only for barcode scanning
7. **Zoom controls** - Basic autofocus is sufficient
8. **Audio feedback** - Haptic vibration only

---

## 6. Design Considerations

### 6.1 Scanner Screen Layout

```
┌─────────────────────────────────┐
│ ← Scan Barcode            🔦   │  ← TopAppBar with back + torch
├─────────────────────────────────┤
│                                 │
│                                 │
│      ┌─────────────────┐        │
│      │                 │        │
│      │   [Viewfinder]  │        │  ← Camera preview with overlay
│      │                 │        │
│      └─────────────────┘        │
│                                 │
│    Point camera at barcode      │  ← Instruction text
│                                 │
└─────────────────────────────────┘
```

### 6.2 UI Components

- **Viewfinder overlay:** Semi-transparent black with clear rectangular scan area
- **Scan area indicator:** Rounded rectangle with subtle animation (pulsing border)
- **Torch button:** Icon button in top app bar (Icons.Default.FlashOn / FlashOff)
- **Loading overlay:** Full-screen semi-transparent with CircularProgressIndicator
- **Error states:** Bottom sheet or dialog with error message and action buttons

### 6.3 Colors & Theme

- Use existing MaterialTheme colors
- Viewfinder overlay: `Color.Black.copy(alpha = 0.6f)`
- Scan area border: `MaterialTheme.colorScheme.primary`

---

## 7. Technical Considerations

### 7.1 Dependencies

```kotlin
// ML Kit Barcode Scanning
implementation("com.google.mlkit:barcode-scanning:17.2.0")

// CameraX for camera preview
implementation("androidx.camera:camera-core:1.3.1")
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")

// Permission handling
implementation("com.google.accompanist:accompanist-permissions:0.32.0")
```

### 7.2 Edamam API Integration

The Edamam Food Database API supports barcode lookup via the `upc` parameter:

```
GET https://api.edamam.com/api/food-database/v2/parser
?app_id={APP_ID}
&app_key={APP_KEY}
&upc={BARCODE}
```

**Note:** The existing `EdamamApiService` needs a new endpoint for barcode lookup.

### 7.3 Architecture

- **BarcodeScannerScreen.kt** - Main scanner composable with CameraX preview
- **BarcodeScannerViewModel.kt** - Handles scan state, API calls, navigation
- **BarcodeRepository.kt** - Interface for barcode lookups (extend FoodSearchRepository or create new)
- **ScannedBarcodeEntity.kt** - Room entity for caching barcode → food mappings

### 7.4 Permission Handling

Use Accompanist Permissions library for clean Compose-based permission handling:

```kotlin
val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

when {
    cameraPermissionState.status.isGranted -> { /* Show camera */ }
    cameraPermissionState.status.shouldShowRationale -> { /* Show rationale */ }
    else -> { /* Request permission */ }
}
```

---

## 8. Success Metrics

1. **Scan Success Rate:** >90% of valid barcodes should be detected within 3 seconds
2. **Lookup Success Rate:** Track % of scanned barcodes found in Edamam database
3. **User Adoption:** >30% of food entries should come from barcode scanning within 30 days of launch
4. **Error Rate:** <5% of scan attempts should result in technical errors

---

## 9. Open Questions

1. **Q:** Should we add a "Report missing barcode" feature for products not in Edamam?
   **A:** Out of scope for MVP. Consider for future enhancement.

2. **Q:** Should scanned products be marked differently in the diary (e.g., barcode icon)?
   **A:** Not necessary for MVP. All foods are treated equally in the diary.

3. **Q:** What happens if the same barcode returns multiple products from Edamam?
   **A:** Display the first/best match. This is rare for UPC barcodes.

---

## 10. Acceptance Criteria

- [ ] User can open barcode scanner from Add Food options
- [ ] Camera permission is requested and handled correctly
- [ ] Scanner detects UPC-A, UPC-E, EAN-13, EAN-8, Code128, Code39 barcodes
- [ ] Successful scan triggers haptic feedback
- [ ] Found products navigate to Food Detail screen
- [ ] Not-found barcodes offer search fallback
- [ ] Torch toggle works correctly
- [ ] Scanned foods appear in Recent Searches
- [ ] Previously scanned barcodes work offline
- [ ] All error states display user-friendly messages
