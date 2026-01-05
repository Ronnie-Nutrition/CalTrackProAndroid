package com.easyaiflows.caltrackpro.ui.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepository
import com.easyaiflows.caltrackpro.data.repository.UserProfileRepository
import com.easyaiflows.caltrackpro.domain.model.FoodEntry
import com.easyaiflows.caltrackpro.domain.model.NutritionGoals
import com.easyaiflows.caltrackpro.domain.model.groupByMealType
import com.easyaiflows.caltrackpro.domain.model.toDomainModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: FoodEntryRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DiaryUiState> = combine(
        _selectedDate.flatMapLatest { date ->
            val (startOfDay, endOfDay) = getDateRange(date)
            repository.getEntriesForDay(startOfDay, endOfDay)
                .map { entities -> date to entities.toDomainModels() }
        },
        userProfileRepository.userProfile
    ) { (date, entries), profile ->
        val goals = NutritionGoals.fromUserProfile(profile)
        DiaryUiState(
            selectedDate = date,
            entries = entries,
            entriesByMeal = entries.groupByMealType(),
            goals = goals,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DiaryUiState(isLoading = true)
    )

    /**
     * Select a new date to view entries for
     */
    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    /**
     * Go to the previous day
     */
    fun previousDay() {
        _selectedDate.update { it.minusDays(1) }
    }

    /**
     * Go to the next day
     */
    fun nextDay() {
        _selectedDate.update { it.plusDays(1) }
    }

    /**
     * Go to today
     */
    fun goToToday() {
        _selectedDate.value = LocalDate.now()
    }

    /**
     * Delete an entry by ID
     */
    fun deleteEntry(entryId: String) {
        viewModelScope.launch {
            repository.deleteById(entryId)
        }
    }

    /**
     * Duplicate an entry with a new ID and current timestamp
     */
    fun duplicateEntry(entry: FoodEntry) {
        viewModelScope.launch {
            val duplicatedEntry = entry.copy(
                id = UUID.randomUUID().toString(),
                timestamp = System.currentTimeMillis()
            )
            repository.insert(duplicatedEntry.toEntity())
        }
    }

    /**
     * Get the start and end timestamps for a given date
     */
    private fun getDateRange(date: LocalDate): Pair<Long, Long> {
        val zoneId = ZoneId.systemDefault()
        val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
        return startOfDay to endOfDay
    }

    /**
     * Extension to convert domain model back to entity for insertion
     */
    private fun FoodEntry.toEntity() = com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity(
        id = id,
        name = name,
        brand = brand,
        barcode = barcode,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        servingSize = servingSize,
        servingUnit = servingUnit,
        quantity = quantity,
        mealType = mealType.name,
        timestamp = timestamp,
        imageData = imageData
    )
}
