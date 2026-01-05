package com.easyaiflows.caltrackpro.ui.scanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BarcodeScannerViewModel @Inject constructor(
    private val repository: FoodSearchRepository,
    private val networkMonitor: NetworkMonitor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Navigation parameters
    val mealType: MealType = savedStateHandle.get<String>("mealType")
        ?.let { MealType.valueOf(it) }
        ?: MealType.SNACK

    val date: LocalDate = savedStateHandle.get<String>("date")
        ?.let { LocalDate.parse(it) }
        ?: LocalDate.now()

    private val _uiState = MutableStateFlow<BarcodeScannerUiState>(BarcodeScannerUiState.RequestingPermission)
    val uiState: StateFlow<BarcodeScannerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BarcodeScannerEvent>()
    val events: SharedFlow<BarcodeScannerEvent> = _events.asSharedFlow()

    // Track if we're currently processing a barcode to prevent duplicate scans
    private var isProcessingBarcode = false

    // Current torch state (tracked separately for state transitions)
    private var currentTorchEnabled = false

    /**
     * Called when camera permission is granted.
     */
    fun onPermissionGranted() {
        _uiState.value = BarcodeScannerUiState.Scanning(isTorchEnabled = currentTorchEnabled)
    }

    /**
     * Called when camera permission is denied.
     */
    fun onPermissionDenied(isPermanentlyDenied: Boolean) {
        _uiState.value = BarcodeScannerUiState.PermissionDenied(isPermanentlyDenied)
    }

    /**
     * Called when a barcode is detected by ML Kit.
     */
    fun onBarcodeDetected(barcode: String) {
        // Prevent duplicate processing
        if (isProcessingBarcode) return
        if (barcode.isBlank()) return

        isProcessingBarcode = true
        _uiState.value = BarcodeScannerUiState.Loading(
            barcode = barcode,
            isTorchEnabled = currentTorchEnabled
        )

        viewModelScope.launch {
            lookupBarcode(barcode)
        }
    }

    /**
     * Look up the barcode in the API or cache.
     */
    private suspend fun lookupBarcode(barcode: String) {
        // Check if online
        if (!networkMonitor.isConnected) {
            // Try cache first when offline
            val cachedFood = repository.getCachedBarcode(barcode)
            if (cachedFood != null) {
                _uiState.value = BarcodeScannerUiState.Success(
                    food = cachedFood,
                    barcode = barcode,
                    isFromCache = true
                )
                // Add to recent searches
                repository.addToRecentSearches(cachedFood)
            } else {
                _uiState.value = BarcodeScannerUiState.Error(
                    message = "No internet connection. This product hasn't been scanned before.",
                    barcode = barcode,
                    isOffline = true
                )
            }
            isProcessingBarcode = false
            return
        }

        // Online - try API first
        val result = repository.lookupByBarcode(barcode)

        result.fold(
            onSuccess = { food ->
                if (food != null) {
                    // Cache the result for offline use
                    repository.cacheBarcodeResult(barcode, food)
                    // Add to recent searches
                    repository.addToRecentSearches(food)

                    _uiState.value = BarcodeScannerUiState.Success(
                        food = food,
                        barcode = barcode,
                        isFromCache = false
                    )
                } else {
                    _uiState.value = BarcodeScannerUiState.NotFound(barcode)
                }
            },
            onFailure = { error ->
                // Try cache on API failure
                val cachedFood = repository.getCachedBarcode(barcode)
                if (cachedFood != null) {
                    _uiState.value = BarcodeScannerUiState.Success(
                        food = cachedFood,
                        barcode = barcode,
                        isFromCache = true
                    )
                    repository.addToRecentSearches(cachedFood)
                } else {
                    _uiState.value = BarcodeScannerUiState.Error(
                        message = parseErrorMessage(error),
                        barcode = barcode,
                        isOffline = false
                    )
                }
            }
        )

        isProcessingBarcode = false
    }

    /**
     * Toggle the flashlight/torch.
     */
    fun toggleTorch() {
        currentTorchEnabled = !currentTorchEnabled
        val currentState = _uiState.value

        when (currentState) {
            is BarcodeScannerUiState.Scanning -> {
                _uiState.value = currentState.copy(isTorchEnabled = currentTorchEnabled)
            }
            is BarcodeScannerUiState.Loading -> {
                _uiState.value = currentState.copy(isTorchEnabled = currentTorchEnabled)
            }
            else -> {
                // Torch toggle not applicable in other states
            }
        }
    }

    /**
     * Get the current torch state (for CameraX control).
     */
    fun isTorchEnabled(): Boolean = currentTorchEnabled

    /**
     * Retry the last barcode lookup.
     */
    fun retry() {
        val currentState = _uiState.value
        val barcode = when (currentState) {
            is BarcodeScannerUiState.Error -> currentState.barcode
            is BarcodeScannerUiState.NotFound -> currentState.barcode
            else -> null
        }

        if (barcode != null) {
            isProcessingBarcode = false
            onBarcodeDetected(barcode)
        } else {
            // Just go back to scanning
            resumeScanning()
        }
    }

    /**
     * Resume scanning after viewing a result.
     */
    fun resumeScanning() {
        isProcessingBarcode = false
        _uiState.value = BarcodeScannerUiState.Scanning(isTorchEnabled = currentTorchEnabled)
    }

    /**
     * Navigate to food detail screen.
     */
    fun navigateToFoodDetail() {
        val currentState = _uiState.value
        if (currentState is BarcodeScannerUiState.Success) {
            viewModelScope.launch {
                _events.emit(
                    BarcodeScannerEvent.NavigateToFoodDetail(
                        foodId = currentState.food.foodId,
                        barcode = currentState.barcode
                    )
                )
            }
        }
    }

    /**
     * Navigate to search screen with the barcode as query.
     */
    fun navigateToSearch() {
        val currentState = _uiState.value
        val barcode = when (currentState) {
            is BarcodeScannerUiState.NotFound -> currentState.barcode
            is BarcodeScannerUiState.Error -> currentState.barcode
            else -> null
        }

        if (barcode != null) {
            viewModelScope.launch {
                _events.emit(BarcodeScannerEvent.NavigateToSearch(barcode))
            }
        }
    }

    /**
     * Navigate back.
     */
    fun navigateBack() {
        viewModelScope.launch {
            _events.emit(BarcodeScannerEvent.NavigateBack)
        }
    }

    /**
     * Called when camera initialization fails.
     */
    fun onCameraError(message: String) {
        _uiState.value = BarcodeScannerUiState.CameraError(message)
    }

    /**
     * Parse error into user-friendly message.
     */
    private fun parseErrorMessage(error: Throwable): String {
        return when {
            error.message?.contains("429") == true ||
            error.message?.contains("Too Many Requests", ignoreCase = true) == true ->
                "Too many requests. Please try again later."

            error.message?.contains("401") == true ||
            error.message?.contains("Unauthorized", ignoreCase = true) == true ->
                "API authentication error. Please check configuration."

            error.message?.contains("timeout", ignoreCase = true) == true ||
            error.message?.contains("timed out", ignoreCase = true) == true ->
                "Request timed out. Please check your connection."

            error.message?.contains("Unable to resolve host", ignoreCase = true) == true ||
            error.message?.contains("No address associated", ignoreCase = true) == true ->
                "No internet connection. Please check your network."

            error.message?.contains("500") == true ||
            error.message?.contains("Internal Server Error", ignoreCase = true) == true ->
                "Server error. Please try again later."

            else -> error.message ?: "Lookup failed. Please try again."
        }
    }
}
