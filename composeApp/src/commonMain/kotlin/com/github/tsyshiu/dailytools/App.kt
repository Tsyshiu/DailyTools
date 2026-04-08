package com.github.tsyshiu.dailytools

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.window.core.layout.WindowSizeClass
import com.github.tsyshiu.dailytools.ui.screen.FinancialTools
import com.github.tsyshiu.dailytools.ui.screen.HuntForDeals
import com.github.tsyshiu.dailytools.ui.theme.AppTheme

@Composable
@Preview
fun App() {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var selectedItem by remember { mutableIntStateOf(0) }
    val items = listOf("MyTools", "理财", "羊毛")
    val selectedIcons = listOf(Icons.Filled.Build, Icons.Filled.Money, Icons.Filled.CardGiftcard)
    val unselectedIcons =
        listOf(Icons.Outlined.Build, Icons.Outlined.Money, Icons.Outlined.CardGiftcard)

    // For small screens, show labels (Expanded). For medium/large, hide them (Collapsed).
    // 1. 定义判断逻辑
    val showLabels = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    // 2. 初始化 State
    val state = rememberWideNavigationRailState(
        initialValue = if (showLabels) WideNavigationRailValue.Expanded else WideNavigationRailValue.Collapsed
    )

    // 根据窗口大小切换state状态
    LaunchedEffect(showLabels) {
        if (showLabels) {
            state.expand()
        } else {
            state.collapse()
        }
    }

    AppTheme {
        Row(Modifier.fillMaxSize()) {
            WideNavigationRail(state = state, modifier = Modifier.wrapContentWidth()) {
                items.forEachIndexed { index, item ->
                    WideNavigationRailItem(
                        modifier = Modifier.wrapContentWidth(),
                        railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
                        icon = {
                            Icon(
                                if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                                contentDescription = null,
                            )
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                    )
                }
            }

            // Content for the selected item
            when (selectedItem) {
                0 -> MyTools()
                1 -> FinancialTools()
                2 -> HuntForDeals()
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
