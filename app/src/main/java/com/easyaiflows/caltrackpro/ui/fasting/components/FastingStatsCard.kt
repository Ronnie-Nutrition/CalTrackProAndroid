package com.easyaiflows.caltrackpro.ui.fasting.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.domain.model.FastingStats

/**
 * Card displaying fasting statistics: streak, total fasts, and fasts this week.
 */
@Composable
fun FastingStatsCard(
    stats: FastingStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Current Streak
            StatItem(
                value = stats.currentStreak,
                label = stringResource(R.string.fasting_stats_current_streak),
                icon = R.drawable.ic_flame,
                iconTint = Color(0xFFE74C3C),
                modifier = Modifier.weight(1f)
            )

            // Longest Streak
            StatItem(
                value = stats.longestStreak,
                label = stringResource(R.string.fasting_stats_longest_streak),
                icon = R.drawable.ic_sparkles,
                iconTint = Color(0xFFF39C12),
                modifier = Modifier.weight(1f)
            )

            // Total Fasts
            StatItem(
                value = stats.totalFastsCompleted,
                label = stringResource(R.string.fasting_stats_total_fasts),
                icon = R.drawable.ic_check_circle,
                iconTint = Color(0xFF27AE60),
                modifier = Modifier.weight(1f)
            )

            // This Week
            StatItem(
                value = stats.fastsThisWeek,
                label = stringResource(R.string.fasting_stats_this_week),
                icon = R.drawable.ic_clock,
                iconTint = Color(0xFF3498DB),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(
    value: Int,
    label: String,
    icon: Int,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint = iconTint
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = value.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 14.sp
        )
    }
}
