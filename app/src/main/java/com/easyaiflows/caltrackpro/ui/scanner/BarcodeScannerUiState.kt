package com.easyaiflows.caltrackpro.ui.scanner

import com.easyaiflows.caltrackpro.domain.model.SearchedFood

/**
 * Sealed class representing the different states of the barcode scanner.
 */
sealed class BarcodeScannerUiState {

    /**
     * Waiting for camera permission.
     */
    data object RequestingPermission : BarcodeScannerUiState()

    /**
     * Camera permission was denied.
     * @param isPermanentlyDenied True if user selected "Don't ask again"
     */
    data class PermissionDenied(
        val isPermanentlyDenied: Boolean = false
    ) : BarcodeScannerUiState()

    /**
     * Camera is active and scanning for barcodes.
     * @param isTorchEnabled True if flashlight is on
     */
    data class Scanning(
        val isTorchEnabled: Boolean = false
    ) : BarcodeScannerUiState()

    /**
     * Barcode detected, looking up in API.
     * @param barcode The detected barcode value
     * @param isTorchEnabled True if flashlight is on
     */
    data class Loading(
        val barcode: String,
        val isTorchEnabled: Boolean = false
    ) : BarcodeScannerUiState()

    /**
     * Food found successfully.
     * @param food The found food item
     * @param barcode The scanned barcode
     * @param isFromCache True if result was loaded from offline cache
     */
    data class Success(
        val food: SearchedFood,
        val barcode: String,
        val isFromCache: Boolean = false
    ) : BarcodeScannerUiState()

    /**
     * Product not found in database.
     * @param barcode The scanned barcode that wasn't found
     */
    data class NotFound(
        val barcode: String
    ) : BarcodeScannerUiState()

    /**
     * Error occurred during scanning or lookup.
     * @param message User-friendly error message
     * @param barcode The barcode that was being looked up (if any)
     * @param isOffline True if the error is due to no network connection
     */
    data class Error(
        val message: String,
        val barcode: String? = null,
        val isOffline: Boolean = false
    ) : BarcodeScannerUiState()

    /**
     * Camera initialization failed.
     * @param message Error message
     */
    data class CameraError(
        val message: String
    ) : BarcodeScannerUiState()
}

/**
 * Events that can be triggered from the scanner screen.
 */
sealed class BarcodeScannerEvent {
    data object NavigateBack : BarcodeScannerEvent()
    data class NavigateToFoodDetail(val foodId: String, val barcode: String) : BarcodeScannerEvent()
    data class NavigateToSearch(val query: String) : BarcodeScannerEvent()
}
