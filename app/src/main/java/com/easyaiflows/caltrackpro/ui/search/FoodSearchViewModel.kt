package com.easyaiflows.caltrackpro.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.domain.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val repository: FoodSearchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_DELAY_MS = 300L
        private const val MIN_QUERY_LENGTH = 2
    }

    // Navigation parameters
    val mealType: MealType = savedStateHandle.get<String>("mealType")
        ?.let { MealType.valueOf(it) }
        ?: MealType.SNACK

    val date: LocalDate = savedStateHandle.get<String>("date")
        ?.let { LocalDate.parse(it) }
        ?: LocalDate.now()

    private val _query = MutableStateFlow("")
    private val _uiState = MutableStateFlow(FoodSearchUiState())

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<FoodSearchUiState> = _query
        .debounce(DEBOUNCE_DELAY_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.length < MIN_QUERY_LENGTH) {
                flow {
                    emit(FoodSearchUiState(query = query))
                }
            } else {
                flow {
                    emit(_uiState.value.copy(query = query, isLoading = true, error = null))

                    val result = repository.searchFoods(query)

                    result.fold(
                        onSuccess = { foods ->
                            emit(FoodSearchUiState(
                                query = query,
                                results = foods,
                                isLoading = false,
                                hasSearched = true
                            ))
                        },
                        onFailure = { error ->
                            emit(FoodSearchUiState(
                                query = query,
                                isLoading = false,
                                error = error.message ?: "Search failed",
                                hasSearched = true
                            ))
                        }
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FoodSearchUiState()
        )

    /**
     * Update the search query.
     * Debounced search is triggered automatically.
     */
    fun updateQuery(query: String) {
        _query.value = query
        _uiState.update { it.copy(query = query) }
    }

    /**
     * Clear the search query and results.
     */
    fun clearSearch() {
        _query.value = ""
        _uiState.value = FoodSearchUiState()
    }

    /**
     * Retry the last search after an error.
     */
    fun retry() {
        val currentQuery = _query.value
        if (currentQuery.isNotBlank()) {
            // Force a re-emission by setting a slightly modified value then back
            _query.value = ""
            _query.value = currentQuery
        }
    }
}
