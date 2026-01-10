package com.easyaiflows.caltrackpro.ui.fasting.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyaiflows.caltrackpro.domain.model.FASTING_MILESTONES
import kotlin.math.cos
import kotlin.math.sin

/**
 * Circular progress ring for fasting timer with gradient fill and milestone markers.
 */
@Composable
fun FastingProgressRing(
    progress: Float,
    elapsedHours: Int,
    elapsedMinutes: Int,
    elapsedSeconds: Int,
    remainingHours: Int,
    remainingMinutes: Int,
    targetHours: Int,
    isGoalReached: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    strokeWidth: Dp = 16.dp
) {
    // Animate progress changes smoothly
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 500),
        label = "progress"
    )

    // Colors for gradient
    val gradientColors = listOf(
        Color(0xFF3498DB), // Blue
        Color(0xFF8E44AD), // Purple
        Color(0xFF9B59B6)  // Light purple
    )

    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    val glowColor = Color(0xFF8E44AD).copy(alpha = 0.5f)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val radius = (canvasSize - strokeWidth.toPx()) / 2
            val center = Offset(canvasSize / 2, canvasSize / 2)
            val topLeft = Offset(
                (canvasSize - radius * 2) / 2,
                (canvasSize - radius * 2) / 2
            )
            val arcSize = Size(radius * 2, radius * 2)

            // Draw background ring
            drawArc(
                color = backgroundColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Draw progress arc with gradient
            val sweepAngle = animatedProgress * 360f
            if (sweepAngle > 0) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = gradientColors,
                        center = center
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
                )

                // Draw glow effect at current progress point
                val progressAngle = Math.toRadians((-90 + sweepAngle).toDouble())
                val glowX = center.x + radius * cos(progressAngle).toFloat()
                val glowY = center.y + radius * sin(progressAngle).toFloat()

                // Outer glow
                drawCircle(
                    color = glowColor,
                    radius = strokeWidth.toPx() * 1.5f,
                    center = Offset(glowX, glowY)
                )

                // Inner bright point
                drawCircle(
                    color = Color.White,
                    radius = strokeWidth.toPx() * 0.5f,
                    center = Offset(glowX, glowY)
                )
            }

            // Draw milestone marker dots
            val milestoneHours = FASTING_MILESTONES.filter { it.hours > 0 && it.hours <= targetHours }.map { it.hours }
            milestoneHours.forEach { hour ->
                val milestoneProgress = hour.toFloat() / targetHours
                val milestoneAngle = Math.toRadians((-90 + milestoneProgress * 360).toDouble())
                val markerX = center.x + radius * cos(milestoneAngle).toFloat()
                val markerY = center.y + radius * sin(milestoneAngle).toFloat()

                val isPassed = animatedProgress >= milestoneProgress
                val markerColor = if (isPassed) Color(0xFF27AE60) else Color.Gray.copy(alpha = 0.5f)

                drawCircle(
                    color = markerColor,
                    radius = strokeWidth.toPx() * 0.4f,
                    center = Offset(markerX, markerY)
                )
            }
        }

        // Center content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elapsed time (large)
            Text(
                text = String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Label
            Text(
                text = if (isGoalReached) "Goal Reached!" else "Elapsed",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isGoalReached) Color(0xFF27AE60) else MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Remaining time (smaller)
            if (!isGoalReached) {
                Text(
                    text = "${remainingHours}h ${remainingMinutes}m remaining",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                )
            }
        }
    }
}
