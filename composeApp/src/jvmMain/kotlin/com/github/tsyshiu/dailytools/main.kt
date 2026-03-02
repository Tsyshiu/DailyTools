package com.github.tsyshiu.dailytools

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "DailyTools",
    ) {
        App()
    }
}