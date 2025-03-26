package com.example.dicegame.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dicegame.R
import com.example.dicegame.data.GameState
import com.example.dicegame.ui.AppScreen
import com.example.dicegame.ui.components.AboutDialog

@Composable
fun HomeScreen(onNavigate: (AppScreen) -> Unit, modifier: Modifier = Modifier) {
    // State to control About dialog visibility
    var showAboutDialog by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val darkTheme: Boolean = isSystemInDarkTheme()

    // Track if there's an ongoing game that can be continued
    val canContinueGame = remember { mutableStateOf(false) }

    // Check if there's a game that can be continued
    LaunchedEffect(Unit) {
        if (GameState.isGameOver) {
            // If game is over, no continuation is possible
            val humanWins = GameState.humanWins
            val computerWins = GameState.computerWins
            GameState.resetGame()
            GameState.humanWins = humanWins
            GameState.computerWins = computerWins
            GameState.isGameInProgress = false
            canContinueGame.value = false
        } else if (GameState.humanScore > 0 || GameState.computerScore > 0 || GameState.currentRollCount > 0) {
            // There's saved game progress (scores or dice rolls) that can be continued
            canContinueGame.value = true
        } else {
            // No saved game to continue
            canContinueGame.value = false
        }
    }

    fun startNewGame() {
        GameState.resetGame()
        onNavigate(AppScreen.Playground)
    }

    fun continueGame() {
        onNavigate(AppScreen.Playground)
    }

    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            HomeLandscapeLayout(
                onNavigateToGame = {
                    if (canContinueGame.value) continueGame() else startNewGame()
                },
                onNavigateToSettings = { onNavigate(AppScreen.Settings) },
                onShowAbout = { showAboutDialog = true },
                canContinueGame = canContinueGame.value,
                modifier = modifier,
                darkTheme
            )
        }
        else -> {
            HomePortraitLayout(
                onNavigateToGame = {
                    if (canContinueGame.value) continueGame() else startNewGame()
                },
                onNavigateToSettings = { onNavigate(AppScreen.Settings) },
                onShowAbout = { showAboutDialog = true },
                canContinueGame = canContinueGame.value,
                modifier = modifier,
                darkTheme
            )
        }
    }

    if (showAboutDialog) {
        AboutDialog(
            studentId = "20222284 / w2052292",
            studentName = "Sanithu Jayakody",
            onDismiss = { showAboutDialog = false }
        )
    }
}

/**
 * Portrait layout for the home screen
 */
@Composable
fun HomePortraitLayout(
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onShowAbout: () -> Unit,
    canContinueGame: Boolean,
    modifier: Modifier = Modifier,
    darkTheme: Boolean,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = if (darkTheme) R.drawable.logo_light else R.drawable.logo_dark),
                contentDescription = "Dice Game Logo",
                modifier = Modifier
                    .height(150.dp)  // Adjust size as needed
                    .padding(bottom = 32.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Dice Game",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onNavigateToGame,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    // Change button text based on game state
                    text = if (canContinueGame) "Continue Game" else "New Game",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Button(
                onClick = onShowAbout,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

/**
 * Landscape layout for the home screen
 */
@Composable
fun HomeLandscapeLayout(
    onNavigateToGame: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onShowAbout: () -> Unit,
    canContinueGame: Boolean,
    modifier: Modifier = Modifier,
    darkTheme: Boolean
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side - Title
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = if (darkTheme) R.drawable.logo_light else R.drawable.logo_dark),
                    contentDescription = "Dice Game Logo",
                    modifier = Modifier
                        .height(150.dp)  // Adjust size as needed
                        .padding(bottom = 32.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "Dice Game",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Text(
                    text = "A game of chance and strategy",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Right side - Buttons
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onNavigateToGame,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        // Change button text based on game state
                        text = if (canContinueGame) "Continue Game" else "New Game",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onNavigateToSettings,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Button(
                    onClick = onShowAbout,
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}