package com.easyaiflows.caltrackpro.ui.scanner.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Camera viewfinder overlay with a semi-transparent background
 * and a clear cutout for the scanning area.
 */
@Composable
fun ViewfinderOverlay(
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.6f),
    borderColor: Color = Color.White,
    cornerColor: Color = Color.White,
    scanAreaWidthFraction: Float = 0.75f,
    scanAreaHeightFraction: Float = 0.25f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Calculate scan area dimensions
        val scanWidth = canvasWidth * scanAreaWidthFraction
        val scanHeight = canvasHeight * scanAreaHeightFraction
        val scanLeft = (canvasWidth - scanWidth) / 2
        val scanTop = (canvasHeight - scanHeight) / 2
        val cornerRadius = 16.dp.toPx()

        // Draw semi-transparent overlay
        val overlayPath = Path().apply {
            // Full screen rectangle
            addRect(Rect(0f, 0f, canvasWidth, canvasHeight))
            // Subtract the scan area (cutout)
            addRoundRect(
                RoundRect(
                    left = scanLeft,
                    top = scanTop,
                    right = scanLeft + scanWidth,
                    bottom = scanTop + scanHeight,
                    cornerRadius = CornerRadius(cornerRadius)
                )
            )
        }

        // Draw the overlay with cutout
        drawPath(
            path = overlayPath,
            color = overlayColor,
            blendMode = BlendMode.SrcOver
        )

        // Draw rounded border around scan area
        drawRoundRect(
            color = borderColor.copy(alpha = 0.5f),
            topLeft = Offset(scanLeft, scanTop),
            size = Size(scanWidth, scanHeight),
            cornerRadius = CornerRadius(cornerRadius),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw corner accents
        val cornerLength = 32.dp.toPx()
        val cornerStroke = 4.dp.toPx()

        drawCornerAccents(
            scanLeft = scanLeft,
            scanTop = scanTop,
            scanWidth = scanWidth,
            scanHeight = scanHeight,
            cornerLength = cornerLength,
            cornerRadius = cornerRadius,
            strokeWidth = cornerStroke,
            color = cornerColor
        )
    }
}

/**
 * Draw corner accent lines for the scan area.
 */
private fun DrawScope.drawCornerAccents(
    scanLeft: Float,
    scanTop: Float,
    scanWidth: Float,
    scanHeight: Float,
    cornerLength: Float,
    cornerRadius: Float,
    strokeWidth: Float,
    color: Color
) {
    val strokeStyle = Stroke(width = strokeWidth)

    // Top-left corner
    drawLine(
        color = color,
        start = Offset(scanLeft + cornerRadius, scanTop),
        end = Offset(scanLeft + cornerLength + cornerRadius, scanTop),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(scanLeft, scanTop + cornerRadius),
        end = Offset(scanLeft, scanTop + cornerLength + cornerRadius),
        strokeWidth = strokeWidth
    )

    // Top-right corner
    drawLine(
        color = color,
        start = Offset(scanLeft + scanWidth - cornerRadius, scanTop),
        end = Offset(scanLeft + scanWidth - cornerLength - cornerRadius, scanTop),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(scanLeft + scanWidth, scanTop + cornerRadius),
        end = Offset(scanLeft + scanWidth, scanTop + cornerLength + cornerRadius),
        strokeWidth = strokeWidth
    )

    // Bottom-left corner
    drawLine(
        color = color,
        start = Offset(scanLeft + cornerRadius, scanTop + scanHeight),
        end = Offset(scanLeft + cornerLength + cornerRadius, scanTop + scanHeight),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(scanLeft, scanTop + scanHeight - cornerRadius),
        end = Offset(scanLeft, scanTop + scanHeight - cornerLength - cornerRadius),
        strokeWidth = strokeWidth
    )

    // Bottom-right corner
    drawLine(
        color = color,
        start = Offset(scanLeft + scanWidth - cornerRadius, scanTop + scanHeight),
        end = Offset(scanLeft + scanWidth - cornerLength - cornerRadius, scanTop + scanHeight),
        strokeWidth = strokeWidth
    )
    drawLine(
        color = color,
        start = Offset(scanLeft + scanWidth, scanTop + scanHeight - cornerRadius),
        end = Offset(scanLeft + scanWidth, scanTop + scanHeight - cornerLength - cornerRadius),
        strokeWidth = strokeWidth
    )
}

/**
 * A simpler viewfinder overlay with just corner brackets.
 */
@Composable
fun SimpleBracketOverlay(
    modifier: Modifier = Modifier,
    bracketColor: Color = Color.White,
    scanAreaWidthFraction: Float = 0.75f,
    scanAreaHeightFraction: Float = 0.25f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val scanWidth = canvasWidth * scanAreaWidthFraction
        val scanHeight = canvasHeight * scanAreaHeightFraction
        val scanLeft = (canvasWidth - scanWidth) / 2
        val scanTop = (canvasHeight - scanHeight) / 2

        val bracketLength = 40.dp.toPx()
        val strokeWidth = 4.dp.toPx()

        // Draw corner brackets
        // Top-left
        drawLine(bracketColor, Offset(scanLeft, scanTop), Offset(scanLeft + bracketLength, scanTop), strokeWidth)
        drawLine(bracketColor, Offset(scanLeft, scanTop), Offset(scanLeft, scanTop + bracketLength), strokeWidth)

        // Top-right
        drawLine(bracketColor, Offset(scanLeft + scanWidth, scanTop), Offset(scanLeft + scanWidth - bracketLength, scanTop), strokeWidth)
        drawLine(bracketColor, Offset(scanLeft + scanWidth, scanTop), Offset(scanLeft + scanWidth, scanTop + bracketLength), strokeWidth)

        // Bottom-left
        drawLine(bracketColor, Offset(scanLeft, scanTop + scanHeight), Offset(scanLeft + bracketLength, scanTop + scanHeight), strokeWidth)
        drawLine(bracketColor, Offset(scanLeft, scanTop + scanHeight), Offset(scanLeft, scanTop + scanHeight - bracketLength), strokeWidth)

        // Bottom-right
        drawLine(bracketColor, Offset(scanLeft + scanWidth, scanTop + scanHeight), Offset(scanLeft + scanWidth - bracketLength, scanTop + scanHeight), strokeWidth)
        drawLine(bracketColor, Offset(scanLeft + scanWidth, scanTop + scanHeight), Offset(scanLeft + scanWidth, scanTop + scanHeight - bracketLength), strokeWidth)
    }
}
