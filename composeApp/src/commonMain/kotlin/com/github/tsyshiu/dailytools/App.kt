package com.github.tsyshiu.dailytools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass

@Composable
@Preview
fun App() {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("MyTools")
    val icons = listOf(Icons.Filled.Build)

    // For small screens, show labels (Expanded). For medium/large, hide them (Collapsed).
    val showLabels = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    MaterialTheme {
        Row(Modifier.fillMaxSize()) {
            NavigationRail {
                items.forEachIndexed { index, item ->
                    NavigationRailItem(
                        icon = { Icon(icons[index], contentDescription = item) },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        alwaysShowLabel = showLabels
                    )
                }
            }

            // Content for the selected item
            when (selectedItem) {
                0 -> MyTools()
            }
        }
    }
}

@Composable
fun MyTools() {
    PlatformSpecificTools()
}

@Composable
expect fun PlatformSpecificTools()
