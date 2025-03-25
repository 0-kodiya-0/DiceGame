package com.example.dicegame.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dicegame.data.GameState

@Composable
fun SettingsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    // Load initial target score from GameState
    var targetScore by remember { mutableStateOf(GameState.targetScore.toString()) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current

    // Save settings function
    val saveSettings = {
        val parsedScore = targetScore.toIntOrNull()
        when {
            parsedScore == null -> {
                errorMessage = "Please enter a valid number"
                showErrorDialog = true
            }
            parsedScore < 20 -> {
                errorMessage = "Target score must be at least 20"
                showErrorDialog = true
            }
            parsedScore > 999 -> {
                errorMessage = "Target score must be less than 1000"
                showErrorDialog = true
            }
            else -> {
                // Valid input, save to GameState
                GameState.targetScore = parsedScore
                onBack()
            }
        }
    }

    // Choose layout based on orientation
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            SettingsLandscapeLayout(
                targetScore = targetScore,
                onTargetScoreChange = {
                    // Only allow numeric input
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        targetScore = it
                    }
                },
                onSave = saveSettings,
                onCancel = onBack,
                modifier = modifier
            )
        }
        else -> {
            SettingsPortraitLayout(
                targetScore = targetScore,
                onTargetScoreChange = {
                    // Only allow numeric input
                    if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                        targetScore = it
                    }
                },
                onSave = saveSettings,
                onCancel = onBack,
                modifier = modifier
            )
        }
    }

    // Error dialog - shown in both orientations
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Invalid Input") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false }
                ) {
                    Text("OK")
                }
            }
        )
    }
}

/**
 * Portrait layout for the settings screen
 */
@Composable
fun SettingsPortraitLayout(
    targetScore: String,
    onTargetScoreChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚙️ Game Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Target score setting
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Target Score",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Set the target score needed to win the game",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = targetScore,
                    onValueChange = onTargetScoreChange,
                    label = { Text("Target Score") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )

                Text(
                    text = "Default: 101",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // About the game
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Rules",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "• Both players throw 5 dice each turn\n" +
                            "• Each player may take up to 2 optional rerolls per turn\n" +
                            "• You can select dice to keep before rerolling\n" +
                            "• After the third roll (or sooner), score is calculated\n" +
                            "• First player to reach the target score wins\n" +
                            "• In case of a tie, players roll until the tie is broken",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Save and Cancel buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Cancel")
            }

            Button(
                onClick = onSave
            ) {
                Text("Save Settings")
            }
        }
    }
}

/**
 * Landscape layout for the settings screen
 */
@Composable
fun SettingsLandscapeLayout(
    targetScore: String,
    onTargetScoreChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side - Target score setting
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "⚙️ Game Settings",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Target Score",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "Set the target score needed to win the game",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = targetScore,
                    onValueChange = onTargetScoreChange,
                    label = { Text("Target Score") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Default: 101",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons in landscape mode
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = onCancel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onSave
                    ) {
                        Text("Save Settings")
                    }
                }
            }
        }

        // Right side - Game rules
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(start = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Game Rules",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "• Both players throw 5 dice each turn\n\n" +
                            "• Each player may take up to 2 optional rerolls per turn\n\n" +
                            "• You can select dice to keep before rerolling\n\n" +
                            "• After the third roll (or sooner), score is calculated\n\n" +
                            "• First player to reach the target score wins\n\n" +
                            "• In case of a tie, players roll until the tie is broken",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Remember to tap on dice to select which ones to keep during rerolls!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}