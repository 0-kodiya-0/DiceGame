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

    val showWinDialog = remember { mutableStateOf(false) }
    val showDrawDialog = remember { mutableStateOf(false) }
    val showConfirmExitDialog = remember { mutableStateOf(false) }
    val showConfirmEndGameDialog = remember { mutableStateOf(false) }
    val isRolling = remember { mutableStateOf(false) }
    var one = remember { mutableStateOf(1) }

    val humanScore = remember { mutableStateOf(GameState.humanScore) }
    val computerScore = remember { mutableStateOf(GameState.computerScore) }
    val humanDice = remember { mutableStateOf(GameState.humanDice) }
    val computerDice = remember { mutableStateOf(GameState.computerDice) }
    val humanDiceSelection = remember { mutableStateOf(GameState.humanDiceSelection) }
    val rollCount = remember { mutableStateOf(GameState.currentRollCount) }
    val isTieBreaker = remember { mutableStateOf(GameState.isTieBreaker) }

    LaunchedEffect(Unit) {
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

    fun handleBackPress() {
        if (!GameState.isGameOver && (humanScore.value > 0 || computerScore.value > 0 || rollCount.value > 0)) {
            // Game in progress, show confirmation dialog
            showConfirmExitDialog.value = true
        } else {
            onBack()
        }
    }

    fun forceEndGame() {
        showConfirmEndGameDialog.value = true
    }

    fun executeForceEndGame() {
        GameState.isGameOver = true
        GameState.isGameInProgress = false
        showWinDialog.value = false
        showDrawDialog.value = false
        showConfirmEndGameDialog.value = false

        onBack()
    }

    fun abandonGame() {
        // This allows the game to be continued later
        showConfirmExitDialog.value = false
        onBack()
    }

    fun continueTieBreaker() {
        // Set up for tie-breaker rounds
        GameState.isTieBreaker = true
        isTieBreaker.value = true

        // Close the draw dialog
        showDrawDialog.value = false

        // Game is still in progress, not over
        GameState.isGameOver = false
        GameState.isGameInProgress = true
    }

    fun checkForWinner() {
        val humanReachedTarget = humanScore.value >= GameState.targetScore
        val computerReachedTarget = computerScore.value >= GameState.targetScore

        if (humanReachedTarget || computerReachedTarget) {
            if (humanReachedTarget && computerReachedTarget) {
                if (GameState.humanAttempts == GameState.computerAttempts) {
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
                        showDrawDialog.value = true
                        GameState.isTieBreaker = true
                        isTieBreaker.value = true
                    }
                } else if (GameState.humanAttempts < GameState.computerAttempts) {
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

    fun scoreRoll() {
        if (GameState.isGameOver) return

        // Calculate scores
        val humanRollScore = DiceUtils.calculateDiceSum(humanDice.value)
        val computerRollScore = DiceUtils.calculateDiceSum(computerDice.value)

        one.value+=1

        // Update total scores
        humanScore.value += humanRollScore
        computerScore.value += computerRollScore

        // Update GameState
        GameState.humanScore = humanScore.value
        GameState.computerScore = computerScore.value

        println(isTieBreaker.value)
        println(one.value)

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
    }

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

    GameDrawDialog(
        showDialog = showDrawDialog.value,
        humanScore = humanScore.value,
        computerScore = computerScore.value,
        onContinue = { continueTieBreaker() }
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
            GameStatsBar(
                humanScore = humanScore,
                computerScore = computerScore
            )

            Spacer(modifier = Modifier.height(16.dp))

            DiceSection(
                title = "Computer's Dice",
                dice = computerDice,
                isComputer = true,
                showSum = true
            )

            Spacer(modifier = Modifier.height(16.dp))

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

            RollCounter(
                rollCount = rollCount,
                isTieBreaker = isTieBreaker
            )

            GameActionButtons(
                rollCount = rollCount,
                isTieBreaker = isTieBreaker,
                isRolling = isRolling,
                onThrowDice = onThrowDice,
                onScoreDice = onScoreDice
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                LandscapeGameStats(
                    humanScore = humanScore,
                    computerScore = computerScore
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    RollCounter(
                        rollCount = rollCount,
                        isTieBreaker = isTieBreaker
                    )

                    GameActionButtons(
                        rollCount = rollCount,
                        isTieBreaker = isTieBreaker,
                        isRolling = isRolling,
                        onThrowDice = onThrowDice,
                        onScoreDice = onScoreDice
                    )

                    NavigationButtons(
                        rollCount = rollCount,
                        humanScore = humanScore,
                        computerScore = computerScore,
                        onBack = onBack,
                        onEndGame = onEndGame
                    )
                }
            }

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