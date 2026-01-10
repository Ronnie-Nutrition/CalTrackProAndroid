package com.easyaiflows.caltrackpro.ui.fasting.components

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.domain.model.FastingSession
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Card displaying a fasting session with date, duration, schedule, and completion status.
 */
@Composable
fun FastingSessionCard(
    session: FastingSession,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")

    val startLocalDateTime = session.startTime.atZone(ZoneId.systemDefault()).toLocalDateTime()
    val endLocalDateTime = session.endTime?.atZone(ZoneId.systemDefault())?.toLocalDateTime()

    val completedColor = Color(0xFF27AE60)
    val partialColor = Color(0xFFF39C12)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Icon(
                painter = painterResource(
                    id = if (session.completed) R.drawable.ic_check_circle else R.drawable.ic_clock
                ),
                contentDescription = if (session.completed) "Completed" else "Partial",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        (if (session.completed) completedColor else partialColor).copy(alpha = 0.1f)
                    )
                    .padding(8.dp),
                tint = if (session.completed) completedColor else partialColor
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Session details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = startLocalDateTime.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${startLocalDateTime.format(timeFormatter)} - ${endLocalDateTime?.format(timeFormatter) ?: "In progress"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Schedule chip
                    Text(
                        text = session.schedule.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )

                    // Status chip
                    Text(
                        text = if (session.completed) "Completed" else "Partial",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (session.completed) completedColor else partialColor,
                        modifier = Modifier
                            .background(
                                (if (session.completed) completedColor else partialColor).copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Duration
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val hours = session.actualDuration.toHours()
                val minutes = session.actualDuration.toMinutes() % 60

                Text(
                    text = "${hours}h ${minutes}m",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (session.completed) completedColor else partialColor
                )

                Text(
                    text = "of ${session.targetDuration.toHours()}h goal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
