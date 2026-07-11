package io.github.yutarosuzuki_jp.multispot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates

@Stable
class MultispotState {
    var isVisible by mutableStateOf(false)
        private set

    var currentStep by mutableStateOf(0)
        private set

    internal val spots = mutableStateMapOf<Int, SpotInfo>()

    internal var areaCoordinates: LayoutCoordinates? by mutableStateOf(null)

    val currentSpot: SpotInfo?
        get() = if (isVisible) spots[currentStep] else null

    fun start(initialStep: Int? = null) {
        val sortedSteps = spots.keys.sorted()
        if (sortedSteps.isEmpty()) return
        currentStep = initialStep ?: sortedSteps.first()
        isVisible = true
    }

    fun next() {
        val sortedSteps = spots.keys.sorted()
        val index = sortedSteps.indexOf(currentStep)
        if (index != -1 && index + 1 < sortedSteps.size) {
            currentStep = sortedSteps[index + 1]
        } else {
            finish()
        }
    }

    fun previous() {
        val sortedSteps = spots.keys.sorted()
        val index = sortedSteps.indexOf(currentStep)
        if (index > 0) {
            currentStep = sortedSteps[index - 1]
        }
    }

    fun jumpTo(step: Int) {
        if (spots.containsKey(step)) {
            currentStep = step
        }
    }

    fun finish() {
        isVisible = false
    }

    internal fun registerSpot(
        step: Int,
        key: String,
        shape: SpotShape,
        tooltipStyle: TooltipStyle,
        preferredDirection: TooltipDirection,
        tooltip: @Composable (BalloonArrowPosition) -> Unit,
        onTargetClicked: (() -> Unit)?
    ) {
        val existing = spots[step]
        spots[step] = SpotInfo(
            step = step,
            key = key,
            shape = shape,
            tooltipStyle = tooltipStyle,
            preferredDirection = preferredDirection,
            coordinates = existing?.coordinates,
            relativeRect = existing?.relativeRect,
            tooltip = tooltip,
            onTargetClicked = onTargetClicked
        )
    }

    internal fun unregisterSpot(step: Int) {
        spots.remove(step)
    }

    internal fun updateSpotCoordinates(step: Int, coordinates: LayoutCoordinates) {
        val spot = spots[step] ?: return
        val rect = areaCoordinates?.let { area ->
            if (coordinates.isAttached && area.isAttached) {
                val localOffset = area.localPositionOf(coordinates, Offset.Zero)
                Rect(localOffset, Size(coordinates.size.width.toFloat(), coordinates.size.height.toFloat()))
            } else {
                null
            }
        }
        spots[step] = spot.copy(
            coordinates = coordinates,
            relativeRect = rect
        )
    }

    internal fun updateAllRelativeCoordinates() {
        spots.forEach { (step, spot) ->
            spot.coordinates?.let { coords ->
                if (coords.isAttached) {
                    updateSpotCoordinates(step, coords)
                }
            }
        }
    }
}

data class SpotInfo(
    val step: Int,
    val key: String,
    val shape: SpotShape,
    val tooltipStyle: TooltipStyle,
    val preferredDirection: TooltipDirection,
    val coordinates: LayoutCoordinates?,
    val relativeRect: Rect?,
    val tooltip: @Composable (BalloonArrowPosition) -> Unit,
    val onTargetClicked: (() -> Unit)? = null
)

@Composable
fun rememberMultispotState(): MultispotState {
    return remember { MultispotState() }
}
