package com.example.dicegame.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * Singleton object to maintain game state across configuration changes
 * This approach is used instead of ViewModel (not allowed in the coursework)
 */
object GameState {
    // Target score (default 101)
    var targetScore by mutableStateOf(101)

    // Current game scores
    var humanScore by mutableStateOf(0)
    var computerScore by mutableStateOf(0)

    // Current roll scores
    var humanCurrentRollScore by mutableStateOf(0)
    var computerCurrentRollScore by mutableStateOf(0)

    // Current dice values for human player (1-6 for each die)
    var humanDice by mutableStateOf(listOf(1, 1, 1, 1, 1))

    // Current dice values for computer player (1-6 for each die)
    var computerDice by mutableStateOf(listOf(1, 1, 1, 1, 1))

    // Dice selection status for human player (true = keep, false = reroll)
    var humanDiceSelection by mutableStateOf(List(5) { false })

    // Roll counts in current turn
    var currentRollCount by mutableStateOf(0)

    // Game status
    var isGameOver by mutableStateOf(false)
    var winner by mutableStateOf("")

    // Game statistics
    var humanWins by mutableStateOf(0)
    var computerWins by mutableStateOf(0)

    // Is the game in tie-breaker mode?
    var isTieBreaker by mutableStateOf(false)

    // Number of attempts (turns) taken
    var humanAttempts by mutableStateOf(0)
    var computerAttempts by mutableStateOf(0)

    /**
     * Reset game state for a new game
     */
    fun resetGame() {
        humanScore = 0
        computerScore = 0
        resetTurn()
        isGameOver = false
        winner = ""
        isTieBreaker = false
        humanAttempts = 0
        computerAttempts = 0
    }

    /**
     * Reset state for a new turn
     */
    fun resetTurn() {
        humanCurrentRollScore = 0
        computerCurrentRollScore = 0
        humanDice = List(5) { 1 }
        computerDice = List(5) { 1 }
        humanDiceSelection = List(5) { false }
        currentRollCount = 0
    }
}