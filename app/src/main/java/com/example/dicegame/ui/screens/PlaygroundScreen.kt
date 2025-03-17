package com.example.dicegame.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dicegame.data.GameState
import com.example.dicegame.ui.components.DiceView
import com.example.dicegame.utils.DiceUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlaygroundScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()

    // Game state variables
    val showWinDialog = remember { mutableStateOf(false) }
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
                        showWinDialog.value = true
                    } else if (computerScore.value > humanScore.value) {
                        GameState.winner = "computer"
                        GameState.computerWins++
                        GameState.isGameOver = true
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
                    showWinDialog.value = true
                } else {
                    // Computer reached target in fewer attempts
                    GameState.winner = "computer"
                    GameState.computerWins++
                    GameState.isGameOver = true
                    showWinDialog.value = true
                }
            } else if (humanReachedTarget) {
                GameState.winner = "human"
                GameState.humanWins++
                GameState.isGameOver = true
                showWinDialog.value = true
            } else {
                GameState.winner = "computer"
                GameState.computerWins++
                GameState.isGameOver = true
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
            showWinDialog.value = true
        } else if (computerRollScore > humanRollScore) {
            GameState.winner = "computer"
            GameState.computerWins++
            GameState.isGameOver = true
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

            // Animate rolling dice
            repeat(5) {
                humanDice.value = DiceUtils.rollAllDice()
                computerDice.value = DiceUtils.rollAllDice()
                delay(100)
            }

            // Final roll
            if (rollCount.value == 0) {
                // First roll of the turn
                humanDice.value = DiceUtils.rollAllDice()
                computerDice.value = DiceUtils.rollAllDice()
            } else {
                // Reroll - only reroll dice that aren't selected to keep
                humanDice.value = DiceUtils.rerollSelectedDice(
                    humanDice.value,
                    humanDiceSelection.value
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
            humanDiceSelection.value = List(5) { false }
            GameState.humanDiceSelection = humanDiceSelection.value

            isRolling.value = false
        }
    }

    // Toggle dice selection for reroll
    fun toggleDiceSelection(index: Int) {
        if (rollCount.value > 0 && rollCount.value < 3 && !isTieBreaker.value) {
            val newSelection = humanDiceSelection.value.toMutableList()
            newSelection[index] = !newSelection[index]
            humanDiceSelection.value = newSelection
            GameState.humanDiceSelection = humanDiceSelection.value
        }
    }

    // UI Implementation
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Score and Win counters at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Win counters
            Text(
                text = "H:${GameState.humanWins}/C:${GameState.computerWins}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Target score
            Text(
                text = "Target: ${GameState.targetScore}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Current scores
            Text(
                text = "Score: ${humanScore.value}-${computerScore.value}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Game status indicator (if in tie breaker)
        if (isTieBreaker.value) {
            Text(
                text = "TIE BREAKER - Roll until someone wins!",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Computer's dice section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Computer's Dice",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    computerDice.value.forEach { diceValue ->
                        DiceView(
                            value = diceValue,
                            isSelected = false,
                            isSelectable = false
                        )
                    }
                }

                Text(
                    text = "Sum: ${DiceUtils.calculateDiceSum(computerDice.value)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Human's dice section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Dice${if (rollCount.value > 0 && rollCount.value < 3) " (tap to select)" else ""}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    humanDice.value.forEachIndexed { index, diceValue ->
                        DiceView(
                            value = diceValue,
                            isSelected = humanDiceSelection.value[index],
                            isSelectable = rollCount.value > 0 && rollCount.value < 3 && !isTieBreaker.value,
                            onClick = { toggleDiceSelection(index) }
                        )
                    }
                }

                Text(
                    text = "Sum: ${DiceUtils.calculateDiceSum(humanDice.value)}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Roll counter
        Text(
            text = if (isTieBreaker.value) "Tie Breaker Roll" else "Roll ${rollCount.value}/3",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Control buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { throwDice() },
                enabled = !isRolling.value &&
                        !GameState.isGameOver &&
                        (rollCount.value < 3 || isTieBreaker.value)
            ) {
                Text(if (rollCount.value == 0) "Throw Dice" else "Reroll")
            }

            Button(
                onClick = { scoreRoll() },
                enabled = !isRolling.value &&
                        !GameState.isGameOver &&
                        rollCount.value > 0 &&
                        rollCount.value < 3 &&
                        !isTieBreaker.value
            ) {
                Text("Score")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onBack,
            modifier = Modifier.padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("⬅ Back to Home")
        }
    }

    // Win dialog
    if (showWinDialog.value) {
        AlertDialog(
            onDismissRequest = { /* Dialog is not dismissible by clicking outside */ },
            title = {
                Text(
                    text = if (GameState.winner == "human") "You Win! 🎉" else "You Lose 😢",
                    color = if (GameState.winner == "human") Color.Green else Color.Red
                )
            },
            text = {
                Text(
                    text = "Final Score: ${humanScore.value}-${computerScore.value}\n" +
                            "Attempts: ${GameState.humanAttempts}",
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWinDialog.value = false
                        onBack()
                    }
                ) {
                    Text("Back to Home")
                }
            }
        )
    }
}