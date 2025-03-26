package com.example.dicegame.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.dicegame.data.GameState
import com.example.dicegame.ui.components.*
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
        GameState.isGameOver = true
        GameState.isGameInProgress = false
        showWinDialog.value = false
        showConfirmEndGameDialog.value = false

        onBack()
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

    // Dialogs
    GameResultDialog(
        showDialog = showWinDialog.value,
        humanScore = humanScore.value,
        computerScore = computerScore.value,
        onDismiss = {
            showWinDialog.value = false
            GameState.resetGame()
            onBack()
        }
    )

    ConfirmExitDialog(
        showDialog = showConfirmExitDialog.value,
        onConfirm = { abandonGame() },
        onDismiss = { showConfirmExitDialog.value = false }
    )

    ConfirmEndGameDialog(
        showDialog = showConfirmEndGameDialog.value,
        onConfirm = { executeForceEndGame() },
        onDismiss = { showConfirmEndGameDialog.value = false }
    )
}

/**
 * Refactored Portrait Layout for the Playground Screen
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
            GameStatsBar(
                humanScore = humanScore,
                computerScore = computerScore
            )

            // Tie Breaker Indicator
            TieBreakerIndicator(isTieBreaker = isTieBreaker)

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
            RollCounter(
                rollCount = rollCount,
                isTieBreaker = isTieBreaker
            )

            // Action Buttons
            GameActionButtons(
                rollCount = rollCount,
                isTieBreaker = isTieBreaker,
                isRolling = isRolling,
                onThrowDice = onThrowDice,
                onScoreDice = onScoreDice
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Buttons Row
            NavigationButtons(
                rollCount = rollCount,
                humanScore = humanScore,
                computerScore = computerScore,
                onBack = onBack,
                onEndGame = onEndGame
            )
        }
    }
}

/**
 * Refactored Landscape Layout for the Playground Screen
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
                    isLandscape = true,
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
                // Game Stats with Landscape layout
                LandscapeGameStats(
                    humanScore = humanScore,
                    computerScore = computerScore,
                    rollCount = rollCount,
                    isTieBreaker = isTieBreaker
                )

                // Action Buttons in Column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Throw/Reroll Button
                    GameActionButtons(
                        rollCount = rollCount,
                        isTieBreaker = isTieBreaker,
                        isRolling = isRolling,
                        onThrowDice = onThrowDice,
                        onScoreDice = onScoreDice
                    )

                    // Navigation Buttons Row
                    NavigationButtons(
                        rollCount = rollCount,
                        humanScore = humanScore,
                        computerScore = computerScore,
                        onBack = onBack,
                        onEndGame = onEndGame
                    )
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
                    isLandscape = true,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}