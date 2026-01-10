package com.easyaiflows.caltrackpro.ui.fasting.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.R

/**
 * Water intake tracker with glass icons and increment/decrement buttons.
 */
@Composable
fun WaterTracker(
    waterIntake: Int,
    waterGoal: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    val waterColor = Color(0xFF3498DB)
    val emptyColor = Color.Gray.copy(alpha = 0.3f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_droplet),
                        contentDescription = "Water",
                        modifier = Modifier.size(24.dp),
                        tint = waterColor
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Water Intake",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "$waterIntake / $waterGoal glasses",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Glass icons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrement button
                IconButton(
                    onClick = onDecrement,
                    enabled = waterIntake > 0,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (waterIntake > 0)
                                MaterialTheme.colorScheme.errorContainer
                            else
                                Color.Gray.copy(alpha = 0.2f)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease water",
                        tint = if (waterIntake > 0)
                            MaterialTheme.colorScheme.onErrorContainer
                        else
                            Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Glass icons (show up to 8)
                val displayCount = minOf(waterGoal, 8)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(displayCount) { index ->
                        val isFilled = index < waterIntake
                        val color by animateColorAsState(
                            targetValue = if (isFilled) waterColor else emptyColor,
                            animationSpec = tween(300),
                            label = "glass_color"
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_droplet),
                            contentDescription = if (isFilled) "Filled glass" else "Empty glass",
                            modifier = Modifier.size(24.dp),
                            tint = color
                        )
                    }

                    // Show "+X" if goal is more than 8
                    if (waterGoal > 8) {
                        Text(
                            text = "+${waterGoal - 8}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Increment button
                IconButton(
                    onClick = onIncrement,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase water",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Progress indicator
            if (waterIntake >= waterGoal) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Daily goal reached!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF27AE60),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
