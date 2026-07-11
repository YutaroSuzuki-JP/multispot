package io.github.yutarosuzuki_jp.multispot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onGloballyPositioned

fun Modifier.multispot(
    state: MultispotState,
    step: Int,
    key: String,
    shape: SpotShape = SpotShape.RoundedRect(),
    tooltipStyle: TooltipStyle = TooltipStyle.Arrow,
    preferredDirection: TooltipDirection = TooltipDirection.Auto,
    onTargetClicked: (() -> Unit)? = null,
    tooltip: @Composable (BalloonArrowPosition) -> Unit
): Modifier = this.composed {
    DisposableEffect(state, step, key, shape, tooltipStyle, preferredDirection, tooltip, onTargetClicked) {
        state.registerSpot(
            step = step,
            key = key,
            shape = shape,
            tooltipStyle = tooltipStyle,
            preferredDirection = preferredDirection,
            tooltip = tooltip,
            onTargetClicked = onTargetClicked
        )
        onDispose {
            state.unregisterSpot(step)
        }
    }

    onGloballyPositioned { coordinates ->
        state.updateSpotCoordinates(step, coordinates)
    }
}
