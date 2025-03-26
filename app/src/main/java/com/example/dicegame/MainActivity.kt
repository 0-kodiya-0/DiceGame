package com.example.dicegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.dicegame.data.GameState
import com.example.dicegame.ui.AppNavigation
import com.example.dicegame.ui.theme.DiceGameTheme

/**
 * Main activity for the Dice Game application.
 *
 * This activity serves as the entry point for the app and sets up the Compose UI.
 * It initializes the game state and handles configuration changes by preserving
 * the game state through the GameState singleton object.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the game state if it's a fresh start
        if (savedInstanceState == null) {
            GameState.resetGame()
        }

        enableEdgeToEdge()
        setContent {
            DiceGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        AppNavigation(
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}