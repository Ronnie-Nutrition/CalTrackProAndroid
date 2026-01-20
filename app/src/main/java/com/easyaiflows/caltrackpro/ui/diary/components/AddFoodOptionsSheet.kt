package com.easyaiflows.caltrackpro.ui.diary.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.domain.model.MealType

@Composable
fun AddFoodOptionsSheet(
    mealType: MealType?,
    onSearchClick: () -> Unit,
    onScanClick: () -> Unit,
    onManualClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val headerText = if (mealType != null) {
        stringResource(R.string.add_food_to_meal, mealType.getDisplayName())
    } else {
        stringResource(R.string.add_food_title)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        // Header
        Text(
            text = headerText,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        HorizontalDivider()

        // Options
        AddFoodOption(
            icon = Icons.Default.Search,
            title = stringResource(R.string.add_food_search_title),
            description = stringResource(R.string.add_food_search_description),
            onClick = onSearchClick
        )

        AddFoodOption(
            icon = Icons.Default.CameraAlt,
            title = stringResource(R.string.add_food_scan_title),
            description = stringResource(R.string.add_food_scan_description),
            onClick = onScanClick
        )

        AddFoodOption(
            icon = Icons.Default.Edit,
            title = stringResource(R.string.add_food_manual_title),
            description = stringResource(R.string.add_food_manual_description),
            onClick = onManualClick
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun AddFoodOption(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Extension function to get display name for MealType using string resources
 */
@Composable
private fun MealType.getDisplayName(): String {
    return when (this) {
        MealType.BREAKFAST -> stringResource(R.string.meal_breakfast)
        MealType.LUNCH -> stringResource(R.string.meal_lunch)
        MealType.DINNER -> stringResource(R.string.meal_dinner)
        MealType.SNACK -> stringResource(R.string.meal_snacks)
    }
}
