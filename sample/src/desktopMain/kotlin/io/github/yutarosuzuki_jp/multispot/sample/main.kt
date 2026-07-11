package io.github.yutarosuzuki_jp.multispot.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Multispot KMP Demo"
    ) {
        App()
    }
}
