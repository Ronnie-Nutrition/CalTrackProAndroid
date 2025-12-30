package com.easyaiflows.caltrackpro.ui.diary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.domain.model.FoodEntry
import com.easyaiflows.caltrackpro.domain.model.MealType

@Composable
fun MealSection(
    mealType: MealType,
    entries: List<FoodEntry>,
    totalCalories: Double,
    onAddClick: () -> Unit,
    onEntryClick: (FoodEntry) -> Unit,
    onEditEntry: (FoodEntry) -> Unit,
    onDuplicateEntry: (FoodEntry) -> Unit,
    onDeleteEntry: (FoodEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Meal header
        MealHeader(
            mealType = mealType,
            totalCalories = totalCalories,
            onAddClick = onAddClick
        )

        // Food entries
        if (entries.isEmpty()) {
            EmptyMealPlaceholder(
                mealType = mealType,
                onClick = onAddClick
            )
        } else {
            entries.forEach { entry ->
                FoodEntryItem(
                    entry = entry,
                    onClick = { onEntryClick(entry) },
                    onEdit = { onEditEntry(entry) },
                    onDuplicate = { onDuplicateEntry(entry) },
                    onDelete = { onDeleteEntry(entry) }
                )
            }
        }
    }
}

@Composable
private fun MealHeader(
    mealType: MealType,
    totalCalories: Double,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mealType.displayName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (totalCalories > 0) {
                Text(
                    text = " • ${totalCalories.toInt()} cal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add food to ${mealType.displayName}",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun EmptyMealPlaceholder(
    mealType: MealType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
        Text(
            text = "Add ${mealType.displayName.lowercase()}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

/**
 * Extension property to get display name for MealType
 */
private val MealType.displayName: String
    get() = when (this) {
        MealType.BREAKFAST -> "Breakfast"
        MealType.LUNCH -> "Lunch"
        MealType.DINNER -> "Dinner"
        MealType.SNACK -> "Snacks"
    }
