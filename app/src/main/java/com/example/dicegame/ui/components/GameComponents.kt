package com.example.dicegame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dicegame.R
import com.example.dicegame.data.GameState
import com.example.dicegame.utils.DiceUtils

/**
 * Reusable Dice Section Component
 */
@Composable
fun DiceSection(
    title: String,
    dice: List<Int>,
    isComputer: Boolean,
    showSum: Boolean = false,
    diceSelection: List<Boolean> = emptyList(),
    rollCount: Int = 0,
    isTieBreaker: Boolean = false,
    onDiceSelected: ((Int) -> Unit)? = null,
    isLandscape: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(bottom = 8.dp),
            textAlign = TextAlign.Center
        )

        // Choose layout based on orientation
        if (isLandscape) {
            // Landscape Dice Layout - Grouped in two rows
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Group dice into rows of 3 and 2 for better layout
                val firstRow = dice.take(3)
                val secondRow = dice.drop(3).take(2)

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    firstRow.forEachIndexed { index, diceValue ->
                        DiceView(
                            value = diceValue,
                            isSelected = if (!isComputer) diceSelection.getOrElse(index) { false } else false,
                            isSelectable = !isComputer &&
                                    rollCount > 0 &&
                                    rollCount < 3 &&
                                    !isTieBreaker,
                            onClick = { if (!isComputer) onDiceSelected?.invoke(index) },
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    secondRow.forEachIndexed { rowIndex, diceValue ->
                        val index = rowIndex + 3 // Adjust index for the actual dice list
                        DiceView(
                            value = diceValue,
                            isSelected = if (!isComputer) diceSelection.getOrElse(index) { false } else false,
                            isSelectable = !isComputer &&
                                    rollCount > 0 &&
                                    rollCount < 3 &&
                                    !isTieBreaker,
                            onClick = { if (!isComputer) onDiceSelected?.invoke(index) },
                        )
                    }
                }
            }
        } else {
            // Portrait Dice Layout - All dice in one row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dice.forEachIndexed { index, diceValue ->
                    DiceView(
                        value = diceValue,
                        isSelected = if (!isComputer) diceSelection.getOrElse(index) { false } else false,
                        isSelectable = !isComputer &&
                                rollCount > 0 &&
                                rollCount < 3 &&
                                !isTieBreaker,
                        onClick = { if (!isComputer) onDiceSelected?.invoke(index) }
                    )
                }
            }
        }

        if (showSum || !isComputer) {
            Text(
                text = "Sum: ${DiceUtils.calculateDiceSum(dice)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Game Stats Bar Component
 */
@Composable
fun GameStatsBar(
    humanScore: Int,
    computerScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Wins Counter
        Text(
            text = "H:${GameState.humanWins}/C:${GameState.computerWins}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Target Score
        Text(
            text = "Target: ${GameState.targetScore}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        // Current Scores
        Text(
            text = "Score: $humanScore-$computerScore",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

/**
 * Landscape Game Stats Component
 */
@Composable
fun LandscapeGameStats(
    humanScore: Int,
    computerScore: Int,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        // Wins and Target
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Human wins Counter
            Text(
                text = "H:${GameState.humanWins}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Target Score
            Text(
                text = "Target: ${GameState.targetScore}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            // Computer wins Counter
            Text(
                text = "C:${GameState.computerWins}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Current Scores
        Text(
            text = "Score: $humanScore-$computerScore",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

/**
 * Roll Counter Component
 */
@Composable
fun RollCounter(
    rollCount: Int,
    isTieBreaker: Boolean,
    modifier: Modifier = Modifier
) {
    Text(
        text = if (isTieBreaker) "Tie Breaker Roll" else "Roll $rollCount/3",
        style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        ),
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/**
 * Game Action Buttons Component
 */
@Composable
fun GameActionButtons(
    rollCount: Int,
    isTieBreaker: Boolean,
    isRolling: Boolean,
    onThrowDice: () -> Unit,
    onScoreDice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = onThrowDice,
            enabled = !isRolling &&
                    !GameState.isGameOver &&
                    (rollCount < 3 || isTieBreaker),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(if (rollCount == 0) "Throw Dice" else "Reroll")
        }

        Button(
            onClick = onScoreDice,
            enabled = !isRolling &&
                    !GameState.isGameOver &&
                    rollCount > 0 &&
                    rollCount < 3 &&
                    !isTieBreaker,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text("Score")
        }
    }
}

/**
 * Navigation Buttons Component that provides back and end game functionality
 */
@Composable
fun NavigationButtons(
    rollCount: Int,
    humanScore: Int,
    computerScore: Int,
    onBack: () -> Unit,
    onEndGame: () -> Unit,
    backButtonText: String = "Back",
    endGameButtonText: String = "End Game",
    isEndGameEnabled: Boolean = !GameState.isGameOver && (rollCount > 0 || humanScore > 0 || computerScore > 0),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back Button
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(backButtonText)
        }

        // End Game Button
        Button(
            onClick = onEndGame,
            enabled = isEndGameEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(endGameButtonText)
        }
    }
}

/**
 * Game Result Dialog Component
 */
@Composable
fun GameResultDialog(
    showDialog: Boolean,
    humanScore: Int,
    computerScore: Int,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Dialog is not dismissible by clicking outside */ },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (GameState.winner != "draw") {
                        Image(
                            painter = painterResource(
                                id = if (GameState.winner == "human")
                                    R.drawable.win
                                else R.drawable.lose
                            ),
                            contentDescription = if (GameState.winner == "human") "Win" else "Lose",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 16.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Text(
                        text = when (GameState.winner) {
                            "human" -> "You Win!"
                            "computer" -> "You Lose"
                            else -> "Game Draw"
                        },
                        color = when (GameState.winner) {
                            "human" -> MaterialTheme.colorScheme.primary
                            "computer" -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.secondary
                        },
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Final Score: ${humanScore}-${computerScore}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    Text(
                        text = "Attempts: ${GameState.humanAttempts}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Game Draw Dialog Component
 * Shows when there's a tie in the game and prompts players to continue with a tie-breaker
 */
@Composable
fun GameDrawDialog(
    showDialog: Boolean,
    humanScore: Int,
    computerScore: Int,
    onContinue: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { /* Dialog is not dismissible by clicking outside */ },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "It's a Tie!",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Current Score: $humanScore-$computerScore",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        text = "Both players reached the target score in the same number of attempts with the same score.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Continue with tie-breaker rounds until there's a winner!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue to Tie-Breaker")
                }
            },
            dismissButton = null,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Confirm Exit Dialog Component
 */
@Composable
fun ConfirmExitDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Leave Game?") },
            text = {
                Text(
                    "The current game is still in progress. If you leave now, your progress will be preserved and you can continue later."
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm
                ) {
                    Text("Leave & Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Continue Playing")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

/**
 * Confirm End Game Dialog Component
 *
 * This dialog confirms if the user wants to end the current game.
 * It handles both the UI presentation and the user interaction callbacks.
 */
@Composable
fun ConfirmEndGameDialog(
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("End Game?") },
            text = {
                Text(
                    text = "Are you sure you want to end the current game? Your progress will be lost.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("End Game")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss
                ) {
                    Text("Continue Playing")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp),
            modifier = modifier
        )
    }
}