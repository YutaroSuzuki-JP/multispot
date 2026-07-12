package io.github.yutarosuzuki_jp.multispot

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MultispotAnimationSpec(
    val enabled: Boolean = true,
    val durationMillis: Int = 1200,
    val maxOffset: Dp = 6.dp
)

data class MultispotAnimationConfig(
    val arrowAnimation: MultispotAnimationSpec = MultispotAnimationSpec(durationMillis = 1500),
    val balloonAnimation: MultispotAnimationSpec = MultispotAnimationSpec(durationMillis = 2000, maxOffset = 6.dp)
) {
    companion object {
        val Default = MultispotAnimationConfig()
        val Disabled = MultispotAnimationConfig(
            arrowAnimation = MultispotAnimationSpec(enabled = false),
            balloonAnimation = MultispotAnimationSpec(enabled = false)
        )
    }
}
