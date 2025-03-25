package com.example.dicegame.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dicegame.R
import com.example.dicegame.data.GameState
import com.example.dicegame.ui.components.DiceView
import com.example.dicegame.utils.DiceUtils
import kotlinx.coroutines.launch

@Composable
fun PlaygroundScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current

    // Game state variables
    val showWinDialog = remember { mutableStateOf(false) }
    val showConfirmExitDialog = remember { mutableStateOf(false) }
    val showConfirmEndGameDialog = remember { mutableStateOf(false) }
    val isRolling = remember { mutableStateOf(false) }

    // Read from GameState to ensure state is preserved during configuration changes
    val humanScore = remember { mutableStateOf(GameState.humanScore) }
    val computerScore = remember { mutableStateOf(GameState.computerScore) }
    val humanDice = remember { mutableStateOf(GameState.humanDice) }
    val computerDice = remember { mutableStateOf(GameState.computerDice) }
    val humanDiceSelection = remember { mutableStateOf(GameState.humanDiceSelection) }
    val rollCount = remember { mutableStateOf(GameState.currentRollCount) }
    val isTieBreaker = remember { mutableStateOf(GameState.isTieBreaker) }

    // Update states from GameState on composition
    LaunchedEffect(Unit) {
        // Mark game as in progress when screen is opened
        GameState.isGameInProgress = true

        humanScore.value = GameState.humanScore
        computerScore.value = GameState.computerScore
        humanDice.value = GameState.humanDice
        computerDice.value = GameState.computerDice
        humanDiceSelection.value = GameState.humanDiceSelection
        rollCount.value = GameState.currentRollCount
        isTieBreaker.value = GameState.isTieBreaker

        if (GameState.isGameOver) {
            showWinDialog.value = true
        }
    }

    // Function to handle back button press
    fun handleBackPress() {
        if (!GameState.isGameOver && (humanScore.value > 0 || computerScore.value > 0 || rollCount.value > 0)) {
            // Game in progress, show confirmation dialog
            showConfirmExitDialog.value = true
        } else {
            // No game in progress or game over, safe to exit
            onBack()
        }
    }

    // Function to force end the game
    fun forceEndGame() {
        showConfirmEndGameDialog.value = true
    }

    // Function to execute force end game
    fun executeForceEndGame() {
        // Determine winner based on current scores
        if (humanScore.value > computerScore.value) {
            GameState.winner = "human"
            GameState.humanWins++
        } else if (computerScore.value > humanScore.value) {
            GameState.winner = "computer"
            GameState.computerWins++
        } else {
            // In case of a tie, consider it a draw (no one gets a win)
            GameState.winner = "draw"
        }

        GameState.isGameOver = true
        GameState.isGameInProgress = false
        showWinDialog.value = true
        showConfirmEndGameDialog.value = false
    }

    // Function to exit game while preserving the current state
    fun abandonGame() {
        // Don't mark the game as canceled, just navigate back
        // This allows the game to be continued later
        showConfirmExitDialog.value = false
        onBack()
    }

    // Game logic functions - defined as separate functions to avoid reference issues

    // Function to check for winner and update game state
    fun checkForWinner() {
        val humanReachedTarget = humanScore.value >= GameState.targetScore
        val computerReachedTarget = computerScore.value >= GameState.targetScore

        if (humanReachedTarget || computerReachedTarget) {
            if (humanReachedTarget && computerReachedTarget) {
                // Both reached target in same number of attempts
                if (GameState.humanAttempts == GameState.computerAttempts) {
                    // Compare scores
                    if (humanScore.value > computerScore.value) {
                        GameState.winner = "human"
                        GameState.humanWins++
                        GameState.isGameOver = true
                        GameState.isGameInProgress = false
                        showWinDialog.value = true
                    } else if (computerScore.value > humanScore.value) {
                        GameState.winner = "computer"
                        GameState.computerWins++
                        GameState.isGameOver = true
                        GameState.isGameInProgress = false
                        showWinDialog.value = true
                    } else {
                        // Exact tie - go to tie breaker
                        GameState.isTieBreaker = true
                        isTieBreaker.value = true
                    }
                } else if (GameState.humanAttempts < GameState.computerAttempts) {
                    // Human reached target in fewer attempts
                    GameState.winner = "human"
                    GameState.humanWins++
                    GameState.isGameOver = true
                    GameState.isGameInProgress = false
                    showWinDialog.value = true
                } else {
                    // Computer reached target in fewer attempts
                    GameState.winner = "computer"
                    GameState.computerWins++
                    GameState.isGameOver = true
                    GameState.isGameInProgress = false
                    showWinDialog.value = true
                }
            } else if (humanReachedTarget) {
                GameState.winner = "human"
                GameState.humanWins++
                GameState.isGameOver = true
                GameState.isGameInProgress = false
                showWinDialog.value = true
            } else {
                GameState.winner = "computer"
                GameState.computerWins++
                GameState.isGameOver = true
                GameState.isGameInProgress = false
                showWinDialog.value = true
            }
        }
    }

    // Function to check tie breaker result
    fun checkTieBreakerResult(humanRollScore: Int, computerRollScore: Int) {
        if (humanRollScore > computerRollScore) {
            GameState.winner = "human"
            GameState.humanWins++
            GameState.isGameOver = true
            GameState.isGameInProgress = false
            showWinDialog.value = true
        } else if (computerRollScore > humanRollScore) {
            GameState.winner = "computer"
            GameState.computerWins++
            GameState.isGameOver = true
            GameState.isGameInProgress = false
            showWinDialog.value = true
        } else {
            // Still tied, continue with another tie breaker roll
            GameState.isTieBreaker = true
            isTieBreaker.value = true
        }
    }

    // Function to score the current roll
    fun scoreRoll() {
        if (GameState.isGameOver) return

        // Calculate scores
        val humanRollScore = DiceUtils.calculateDiceSum(humanDice.value)
        val computerRollScore = DiceUtils.calculateDiceSum(computerDice.value)

        // Update total scores
        humanScore.value += humanRollScore
        computerScore.value += computerRollScore

        // Update GameState
        GameState.humanScore = humanScore.value
        GameState.computerScore = computerScore.value

        // Increment attempt counters if not in tie breaker
        if (!isTieBreaker.value) {
            GameState.humanAttempts++
            GameState.computerAttempts++
        }

        // Check for winner
        checkForWinner()

        // Reset for next turn
        rollCount.value = 0
        GameState.currentRollCount = 0
        humanDiceSelection.value = List(5) { false }
        GameState.humanDiceSelection = humanDiceSelection.value

        // If in tie breaker and no winner yet, continue tie breaker
        if (isTieBreaker.value && !GameState.isGameOver) {
            checkTieBreakerResult(humanRollScore, computerRollScore)
        }
    }

    // Function to throw dice
    fun throwDice() {
        if (isRolling.value || GameState.isGameOver) return

        scope.launch {
            isRolling.value = true

            // First, ensure humanDiceSelection is synchronized
            val currentSelection = humanDiceSelection.value.toList()

            // Final roll
            if (rollCount.value == 0) {
                // First roll of the turn
                humanDice.value = DiceUtils.rollAllDice()
                computerDice.value = DiceUtils.rollAllDice()
            } else {
                // Reroll - only reroll dice that aren't selected to keep
                humanDice.value = DiceUtils.rerollSelectedDice(
                    humanDice.value,
                    currentSelection
                )

                // Computer uses smart strategy for reroll decision
                val (shouldReroll, computerKeepDice) = DiceUtils.computerSmartStrategy(
                    computerDice.value,
                    rollCount.value,
                    computerScore.value,
                    humanScore.value,
                    GameState.targetScore
                )

                if (shouldReroll) {
                    computerDice.value = DiceUtils.rerollSelectedDice(
                        computerDice.value,
                        computerKeepDice
                    )
                }
            }

            // Update GameState to persist values
            GameState.humanDice = humanDice.value
            GameState.computerDice = computerDice.value

            // Increment roll count
            rollCount.value++
            GameState.currentRollCount = rollCount.value

            // If this is the third roll, automatically score it
            if (rollCount.value >= 3 || isTieBreaker.value) {
                scoreRoll()
            }

            // Reset dice selection after roll
            val resetSelection = List(5) { false }
            humanDiceSelection.value = resetSelection
            GameState.humanDiceSelection = resetSelection

            isRolling.value = false
        }
    }

    // Toggle dice selection for reroll
    fun toggleDiceSelection(index: Int) {
        if (rollCount.value > 0 && rollCount.value < 3 && !isTieBreaker.value) {
            val newSelection = humanDiceSelection.value.toMutableList()
            newSelection[index] = !newSelection[index]
            humanDiceSelection.value = newSelection
            GameState.humanDiceSelection = newSelection
        }
    }

    // Choose layout based on orientation
    when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(
                humanDice = humanDice.value,
                computerDice = computerDice.value,
                humanDiceSelection = humanDiceSelection.value,
                humanScore = humanScore.value,
                computerScore = computerScore.value,
                rollCount = rollCount.value,
                isTieBreaker = isTieBreaker.value,
                isRolling = isRolling.value,
                onDiceSelected = { toggleDiceSelection(it) },
                onThrowDice = { throwDice() },
                onScoreDice = { scoreRoll() },
                onBack = { handleBackPress() },
                onEndGame = { forceEndGame() },
                modifier = modifier
            )
        }

        else -> {
            PortraitLayout(
                humanDice = humanDice.value,
                computerDice = computerDice.value,
                humanDiceSelection = humanDiceSelection.value,
                humanScore = humanScore.value,
                computerScore = computerScore.value,
                rollCount = rollCount.value,
                isTieBreaker = isTieBreaker.value,
                isRolling = isRolling.value,
                onDiceSelected = { toggleDiceSelection(it) },
                onThrowDice = { throwDice() },
                onScoreDice = { scoreRoll() },
                onBack = { handleBackPress() },
                onEndGame = { forceEndGame() },
                modifier = modifier
            )
        }
    }

    // Win dialog - shown in both orientations
    if (showWinDialog.value) {
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
                        text = "Final Score: ${humanScore.value}-${computerScore.value}",
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
                    onClick = {
                        showWinDialog.value = false
                        GameState.resetGame()  // Reset game state when returning to home
                        onBack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Home")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Confirm Exit Dialog
    if (showConfirmExitDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmExitDialog.value = false },
            title = { Text("Leave Game?") },
            text = {
                Text(
                    "The current game is still in progress. If you leave now, your progress will be preserved and you can continue later."
                )
            },
            confirmButton = {
                Button(
                    onClick = { abandonGame() }
                ) {
                    Text("Leave & Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmExitDialog.value = false }
                ) {
                    Text("Continue Playing")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Confirm End Game Dialog
    if (showConfirmEndGameDialog.value) {
        AlertDialog(
            onDismissRequest = { showConfirmEndGameDialog.value = false },
            title = { Text("End Game?") },
            text = {
                Text(
                    "Are you sure you want to end the current game? The player with the highest score will be declared the winner."
                )
            },
            confirmButton = {
                Button(
                    onClick = { executeForceEndGame() }
                ) {
                    Text("End Game")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmEndGameDialog.value = false }
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
 * Modern Portrait Layout for the Playground Screen
 */
@Composable
fun PortraitLayout(
    humanDice: List<Int>,
    computerDice: List<Int>,
    humanDiceSelection: List<Boolean>,
    humanScore: Int,
    computerScore: Int,
    rollCount: Int,
    isTieBreaker: Boolean,
    isRolling: Boolean,
    onDiceSelected: (Int) -> Unit,
    onThrowDice: () -> Unit,
    onScoreDice: () -> Unit,
    onBack: () -> Unit,
    onEndGame: () -> Unit,
    modifier: Modifier = Modifier
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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Game Stats Row
            Row(
                modifier = Modifier
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

            // Tie Breaker Indicator
            if (isTieBreaker) {
                Text(
                    text = "TIE BREAKER - Roll until someone wins!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Computer Dice Section
            DiceSection(
                title = "Computer's Dice",
                dice = computerDice,
                isComputer = true,
                showSum = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Human Dice Section
            DiceSection(
                title = "Your Dice${if (rollCount > 0 && rollCount < 3) " (tap to select)" else ""}",
                dice = humanDice,
                isComputer = false,
                diceSelection = humanDiceSelection,
                rollCount = rollCount,
                isTieBreaker = isTieBreaker,
                onDiceSelected = onDiceSelected
            )

            Spacer(modifier = Modifier.weight(1f))

            // Roll Counter
            Text(
                text = if (isTieBreaker) "Tie Breaker Roll" else "Roll $rollCount/3",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Back Button
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Back")
                }

                // End Game Button
                Button(
                    onClick = onEndGame,
                    enabled = !GameState.isGameOver && (rollCount > 0 || humanScore > 0 || computerScore > 0),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("End Game")
                }
            }
        }
    }
}

/**
 * Modern Landscape Layout for the Playground Screen
 */
@Composable
fun LandscapeLayout(
    humanDice: List<Int>,
    computerDice: List<Int>,
    humanDiceSelection: List<Boolean>,
    humanScore: Int,
    computerScore: Int,
    rollCount: Int,
    isTieBreaker: Boolean,
    isRolling: Boolean,
    onDiceSelected: (Int) -> Unit,
    onThrowDice: () -> Unit,
    onScoreDice: () -> Unit,
    onBack: () -> Unit,
    onEndGame: () -> Unit,
    modifier: Modifier = Modifier
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left Side - Computer Dice
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                DiceSection(
                    title = "Computer's Dice",
                    dice = computerDice,
                    isComputer = true,
                    showSum = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Center - Game Controls and Stats
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Game Stats
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
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
                        Text(
                            text = "H:${GameState.humanWins}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Text(
                            text = "Target: ${GameState.targetScore}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

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

                    // Tie Breaker or Roll Counter
                    if (isTieBreaker) {
                        Text(
                            text = "TIE BREAKER - Roll until someone wins!",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    } else {
                        Text(
                            text = "Roll $rollCount/3",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                // Action Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onThrowDice,
                        enabled = !isRolling &&
                                !GameState.isGameOver &&
                                (rollCount < 3 || isTieBreaker),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
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
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Score")
                    }

                    // Navigation Buttons Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Back Button
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Back")
                        }

                        // End Game Button
                        Button(
                            onClick = onEndGame,
                            enabled = !GameState.isGameOver && (rollCount > 0 || humanScore > 0 || computerScore > 0),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("End Game")
                        }
                    }
                }
            }

            // Right Side - Human Dice
            Box(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp)
            ) {
                DiceSection(
                    title = "Your Dice${if (rollCount > 0 && rollCount < 3) " (tap to select)" else ""}",
                    dice = humanDice,
                    isComputer = false,
                    diceSelection = humanDiceSelection,
                    rollCount = rollCount,
                    isTieBreaker = isTieBreaker,
                    onDiceSelected = onDiceSelected,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

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
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Dice Layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            dice.forEachIndexed { index, diceValue ->
                DiceView(
                    value = diceValue,
                    isSelected = if (!isComputer) diceSelection[index] else false,
                    isSelectable = !isComputer &&
                            rollCount > 0 &&
                            rollCount < 3 &&
                            !isTieBreaker,
                    onClick = { if (!isComputer) onDiceSelected?.invoke(index) }
                )
            }
        }

        // Sum Display
        if (showSum || !isComputer) {
            Text(
                text = "Sum: ${DiceUtils.calculateDiceSum(dice)}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}