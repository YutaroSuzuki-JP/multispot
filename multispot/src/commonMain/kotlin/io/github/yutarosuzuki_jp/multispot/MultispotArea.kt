package io.github.yutarosuzuki_jp.multispot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.geometry.Rect as ComposeRect
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun MultispotArea(
    state: MultispotState,
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.75f),
    arrowColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                state.areaCoordinates = coordinates
                state.updateAllRelativeCoordinates()
            }
    ) {
        content()

        if (state.isVisible) {
            val currentSpot = state.currentSpot
            val rect = currentSpot?.relativeRect

            var tooltipPosition by remember { mutableStateOf(Offset.Zero) }
            var tooltipSize by remember { mutableStateOf(IntSize.Zero) }
            var actualDirection by remember { mutableStateOf(TooltipDirection.Bottom) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .pointerInput(currentSpot) {
                        detectTapGestures { offset ->
                            if (rect != null && rect.contains(offset)) {
                                currentSpot.onTargetClicked?.invoke()
                            } else {
                                state.next()
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(color = overlayColor)
                    if (rect != null) {
                        currentSpot.shape.drawCutout(this, rect)
                    }
                }

                if (rect != null && tooltipSize != IntSize.Zero && currentSpot.tooltipStyle == TooltipStyle.Arrow) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val marginPx = currentSpot.shape.margin.toPx() + 6.dp.toPx()
                        val startX: Float
                        val startY: Float
                        val endX: Float
                        val endY: Float

                        when (actualDirection) {
                            TooltipDirection.Bottom -> {
                                startX = tooltipPosition.x + tooltipSize.width / 2f
                                startY = tooltipPosition.y - 8.dp.toPx()
                                endX = rect.center.x
                                endY = rect.bottom + marginPx
                            }
                            TooltipDirection.Top -> {
                                startX = tooltipPosition.x + tooltipSize.width / 2f
                                startY = tooltipPosition.y + tooltipSize.height + 8.dp.toPx()
                                endX = rect.center.x
                                endY = rect.top - marginPx
                            }
                            TooltipDirection.Left -> {
                                startX = tooltipPosition.x + tooltipSize.width + 8.dp.toPx()
                                startY = tooltipPosition.y + tooltipSize.height / 2f
                                endX = rect.left - marginPx
                                endY = rect.center.y
                            }
                            TooltipDirection.Right -> {
                                startX = tooltipPosition.x - 8.dp.toPx()
                                startY = tooltipPosition.y + tooltipSize.height / 2f
                                endX = rect.right + marginPx
                                endY = rect.center.y
                            }
                            else -> {
                                startX = 0f; startY = 0f; endX = 0f; endY = 0f
                            }
                        }

                        val controlX = when (actualDirection) {
                            TooltipDirection.Bottom, TooltipDirection.Top -> {
                                (startX + endX) / 2f + 50.dp.toPx() * (if (startX < endX) 1f else -1f)
                            }
                            else -> {
                                (startX + endX) / 2f
                            }
                        }
                        val controlY = when (actualDirection) {
                            TooltipDirection.Left, TooltipDirection.Right -> {
                                (startY + endY) / 2f + 50.dp.toPx() * (if (startY < endY) 1f else -1f)
                            }
                            else -> {
                                (startY + endY) / 2f
                            }
                        }

                        val path = Path().apply {
                            moveTo(startX, startY)
                            quadraticTo(controlX, controlY, endX, endY)
                        }

                        drawPath(
                            path = path,
                            color = arrowColor,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )

                        val t = 0.95f
                        val mt = 1f - t
                        val dx = 2f * mt * (controlX - startX) + 2f * t * (endX - controlX)
                        val dy = 2f * mt * (controlY - startY) + 2f * t * (endY - controlY)
                        val angle = atan2(dy, dx)

                        val arrowLength = 12.dp.toPx()
                        val arrowAngle = PI / 6

                        val x1 = endX - arrowLength * cos(angle - arrowAngle).toFloat()
                        val y1 = endY - arrowLength * sin(angle - arrowAngle).toFloat()
                        val x2 = endX - arrowLength * cos(angle + arrowAngle).toFloat()
                        val y2 = endY - arrowLength * sin(angle + arrowAngle).toFloat()

                        val arrowPath = Path().apply {
                            moveTo(endX, endY)
                            lineTo(x1, y1)
                            lineTo(x2, y2)
                            close()
                        }
                        drawPath(path = arrowPath, color = arrowColor)
                    }
                }

                if (rect != null) {
                    TooltipContainer(
                        spotRect = rect,
                        tooltipStyle = currentSpot.tooltipStyle,
                        preferredDirection = currentSpot.preferredDirection,
                        onPositionCalculated = { pos, size, direction ->
                            tooltipPosition = pos
                            tooltipSize = size
                            actualDirection = direction
                        },
                        content = currentSpot.tooltip
                    )
                }
            }
        }
    }
}

@Composable
private fun TooltipContainer(
    spotRect: ComposeRect,
    tooltipStyle: TooltipStyle,
    preferredDirection: TooltipDirection,
    onPositionCalculated: (Offset, IntSize, TooltipDirection) -> Unit,
    content: @Composable (BalloonArrowPosition) -> Unit
) {
    var arrowPosition by remember { mutableStateOf(BalloonArrowPosition.NONE) }

    val wrappedContent = @Composable {
        when (tooltipStyle) {
            TooltipStyle.Balloon -> {
                Surface(
                    shape = BalloonShape(arrowPosition),
                    color = Color(0xFF1E293B),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF475569), BalloonShape(arrowPosition))
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        content(arrowPosition)
                    }
                }
            }
            TooltipStyle.Glass -> {
                Surface(
                    shape = BalloonShape(arrowPosition),
                    color = Color.White.copy(alpha = 0.12f),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .border(1.dp, Color.White.copy(alpha = 0.22f), BalloonShape(arrowPosition))
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        content(arrowPosition)
                    }
                }
            }
            TooltipStyle.Outline -> {
                Surface(
                    shape = BalloonShape(arrowPosition),
                    color = Color.Black.copy(alpha = 0.45f),
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .border(1.5.dp, Color(0xFF8B5CF6), BalloonShape(arrowPosition))
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        content(arrowPosition)
                    }
                }
            }
            TooltipStyle.Arrow, TooltipStyle.Custom -> {
                content(arrowPosition)
            }
        }
    }

    Layout(
        content = wrappedContent,
        modifier = Modifier.fillMaxSize()
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { placeable ->
                val tooltipWidth = placeable.width
                val tooltipHeight = placeable.height

                val padding = 16.dp.roundToPx()
                val arrowSizePx = 8.dp.roundToPx()

                val gap = if (tooltipStyle == TooltipStyle.Arrow) {
                    64.dp.roundToPx()
                } else {
                    12.dp.roundToPx() + arrowSizePx
                }

                val yBelow = (spotRect.bottom + gap).roundToInt()
                val isBelowOk = yBelow + tooltipHeight + padding <= constraints.maxHeight

                val yAbove = (spotRect.top - tooltipHeight - gap).roundToInt()
                val isAboveOk = yAbove >= padding

                val xRight = (spotRect.right + gap).roundToInt()
                val isRightOk = xRight + tooltipWidth + padding <= constraints.maxWidth

                val xLeft = (spotRect.left - tooltipWidth - gap).roundToInt()
                val isLeftOk = xLeft >= padding

                val direction = when (preferredDirection) {
                    TooltipDirection.Top -> if (isAboveOk) TooltipDirection.Top else TooltipDirection.Bottom
                    TooltipDirection.Bottom -> if (isBelowOk) TooltipDirection.Bottom else TooltipDirection.Top
                    TooltipDirection.Left -> if (isLeftOk) TooltipDirection.Left else TooltipDirection.Right
                    TooltipDirection.Right -> if (isRightOk) TooltipDirection.Right else TooltipDirection.Left
                    TooltipDirection.Auto -> {
                        val spaceBelow = constraints.maxHeight - spotRect.bottom
                        val spaceAbove = spotRect.top
                        val spaceLeft = spotRect.left
                        val spaceRight = constraints.maxWidth - spotRect.right

                        val spaces = mapOf(
                            TooltipDirection.Bottom to spaceBelow,
                            TooltipDirection.Top to spaceAbove,
                            TooltipDirection.Left to spaceLeft,
                            TooltipDirection.Right to spaceRight
                        )
                        spaces.maxByOrNull { it.value }?.key ?: TooltipDirection.Bottom
                    }
                }

                var x = 0
                var y = 0
                when (direction) {
                    TooltipDirection.Bottom -> {
                        x = (spotRect.left + (spotRect.width - tooltipWidth) / 2).roundToInt()
                        y = yBelow
                    }
                    TooltipDirection.Top -> {
                        x = (spotRect.left + (spotRect.width - tooltipWidth) / 2).roundToInt()
                        y = yAbove
                    }
                    TooltipDirection.Left -> {
                        x = xLeft
                        y = (spotRect.top + (spotRect.height - tooltipHeight) / 2).roundToInt()
                    }
                    TooltipDirection.Right -> {
                        x = xRight
                        y = (spotRect.top + (spotRect.height - tooltipHeight) / 2).roundToInt()
                    }
                    TooltipDirection.Auto -> {}
                }

                x = x.safeCoerceIn(padding, constraints.maxWidth - tooltipWidth - padding)
                y = y.safeCoerceIn(padding, constraints.maxHeight - tooltipHeight - padding)

                val spotCenterX = spotRect.center.x
                val tooltipCenterX = x + tooltipWidth / 2f
                val spotCenterY = spotRect.center.y
                val tooltipCenterY = y + tooltipHeight / 2f

                val finalDirection = if (direction == TooltipDirection.Top || direction == TooltipDirection.Bottom) {
                    if (tooltipCenterY > spotCenterY) TooltipDirection.Bottom else TooltipDirection.Top
                } else {
                    if (tooltipCenterX > spotCenterX) TooltipDirection.Right else TooltipDirection.Left
                }

                arrowPosition = when (finalDirection) {
                    TooltipDirection.Bottom -> BalloonArrowPosition.TOP
                    TooltipDirection.Top -> BalloonArrowPosition.BOTTOM
                    TooltipDirection.Left -> BalloonArrowPosition.RIGHT
                    TooltipDirection.Right -> BalloonArrowPosition.LEFT
                    TooltipDirection.Auto -> BalloonArrowPosition.NONE
                }

                onPositionCalculated(
                    Offset(x.toFloat(), y.toFloat()),
                    IntSize(tooltipWidth, tooltipHeight),
                    finalDirection
                )

                placeable.place(x, y)
            }
        }
    }
}

private fun Int.safeCoerceIn(minimumValue: Int, maximumValue: Int): Int {
    if (minimumValue > maximumValue) return minimumValue
    return this.coerceIn(minimumValue, maximumValue)
}
