package com.example.dicegame.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.dicegame.ui.screens.*

// Global variable to persist screen state across configuration changes
object NavigationState {
    var currentScreen = AppScreen.Home
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    // Use the persisted state from NavigationState object
    var currentScreen by remember { mutableStateOf(NavigationState.currentScreen) }

    // Update NavigationState whenever current screen changes
    DisposableEffect(currentScreen) {
        NavigationState.currentScreen = currentScreen
        onDispose { }
    }

    when (currentScreen) {
        AppScreen.Home -> HomeScreen(
            onNavigate = { screen ->
                currentScreen = screen
            },
            modifier = modifier
        )

        AppScreen.Playground -> PlaygroundScreen(
            onBack = { currentScreen = AppScreen.Home },
            modifier = modifier
        )

        AppScreen.Settings -> SettingsScreen(
            onBack = { currentScreen = AppScreen.Home },
            modifier = modifier
        )
    }
}