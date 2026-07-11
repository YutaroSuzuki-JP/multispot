package io.github.yutarosuzuki_jp.multispot

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect as ComposeRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

enum class TooltipStyle {
    Arrow,
    Balloon,
    Glass,
    Outline,
    Custom
}

enum class TooltipDirection {
    Auto, Top, Bottom, Left, Right
}

enum class BalloonArrowPosition {
    TOP, BOTTOM, LEFT, RIGHT, NONE
}

class BalloonShape(
    val arrowPosition: BalloonArrowPosition,
    val arrowSize: Dp = 8.dp,
    val cornerRadius: Dp = 8.dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val arrowPx = with(density) { arrowSize.toPx() }
        val radiusPx = with(density) { cornerRadius.toPx() }

        val left = if (arrowPosition == BalloonArrowPosition.LEFT) arrowPx else 0f
        val top = if (arrowPosition == BalloonArrowPosition.TOP) arrowPx else 0f
        val right = if (arrowPosition == BalloonArrowPosition.RIGHT) size.width - arrowPx else size.width
        val bottom = if (arrowPosition == BalloonArrowPosition.BOTTOM) size.height - arrowPx else size.height

        path.moveTo(left + radiusPx, top)

        if (arrowPosition == BalloonArrowPosition.TOP) {
            val arrowCenterX = (left + right) / 2f
            path.lineTo(arrowCenterX - arrowPx, top)
            path.lineTo(arrowCenterX, 0f)
            path.lineTo(arrowCenterX + arrowPx, top)
        }
        path.lineTo(right - radiusPx, top)
        path.quadraticTo(right, top, right, top + radiusPx)

        if (arrowPosition == BalloonArrowPosition.RIGHT) {
            val arrowCenterY = (top + bottom) / 2f
            path.lineTo(right, arrowCenterY - arrowPx)
            path.lineTo(size.width, arrowCenterY)
            path.lineTo(right, arrowCenterY + arrowPx)
        }
        path.lineTo(right, bottom - radiusPx)
        path.quadraticTo(right, bottom, right - radiusPx, bottom)

        if (arrowPosition == BalloonArrowPosition.BOTTOM) {
            val arrowCenterX = (left + right) / 2f
            path.lineTo(arrowCenterX + arrowPx, bottom)
            path.lineTo(arrowCenterX, size.height)
            path.lineTo(arrowCenterX - arrowPx, bottom)
        }
        path.lineTo(left + radiusPx, bottom)
        path.quadraticTo(left, bottom, left, bottom - radiusPx)

        if (arrowPosition == BalloonArrowPosition.LEFT) {
            val arrowCenterY = (top + bottom) / 2f
            path.lineTo(left, arrowCenterY + arrowPx)
            path.lineTo(0f, arrowCenterY)
            path.lineTo(left, arrowCenterY - arrowPx)
        }
        path.lineTo(left, top + radiusPx)
        path.quadraticTo(left, top, left + radiusPx, top)

        path.close()
        return Outline.Generic(path)
    }
}

interface SpotShape {
    val margin: Dp

    fun DrawScope.getExpandedRect(rect: ComposeRect): ComposeRect {
        val px = margin.toPx()
        return ComposeRect(
            left = rect.left - px,
            top = rect.top - px,
            right = rect.right + px,
            bottom = rect.bottom + px
        )
    }

    fun drawCutout(drawScope: DrawScope, rect: ComposeRect)

    data class Circle(
        val radius: Dp? = null,
        override val margin: Dp = 4.dp
    ) : SpotShape {
        override fun drawCutout(drawScope: DrawScope, rect: ComposeRect) {
            val expanded = drawScope.getExpandedRect(rect)
            val r = if (radius != null) {
                with(drawScope) { radius.toPx() }
            } else {
                maxOf(expanded.width, expanded.height) / 2f
            }
            drawScope.drawCircle(
                color = Color.Transparent,
                radius = r,
                center = rect.center,
                blendMode = BlendMode.Clear
            )
        }
    }

    data class Rect(
        override val margin: Dp = 4.dp
    ) : SpotShape {
        override fun drawCutout(drawScope: DrawScope, rect: ComposeRect) {
            val expanded = drawScope.getExpandedRect(rect)
            drawScope.drawRect(
                color = Color.Transparent,
                topLeft = expanded.topLeft,
                size = expanded.size,
                blendMode = BlendMode.Clear
            )
        }
    }

    data class RoundedRect(
        val cornerRadius: Dp = 8.dp,
        override val margin: Dp = 4.dp
    ) : SpotShape {
        override fun drawCutout(drawScope: DrawScope, rect: ComposeRect) {
            val expanded = drawScope.getExpandedRect(rect)
            val pxRadius = with(drawScope) { cornerRadius.toPx() }
            drawScope.drawRoundRect(
                color = Color.Transparent,
                topLeft = expanded.topLeft,
                size = expanded.size,
                cornerRadius = CornerRadius(pxRadius, pxRadius),
                blendMode = BlendMode.Clear
            )
        }
    }
}
