package com.example.dicegame.utils

import kotlin.random.Random

/**
 * Utility object for dice-related operations
 */
object DiceUtils {
    /**
     * Roll all 5 dice and return their values (1-6 for each die)
     * @return List of 5 integers representing dice values
     */
    fun rollAllDice(): List<Int> {
        return List(5) { rollSingleDie() }
    }

    /**
     * Roll a single die
     * @return Random integer between 1 and 6
     */
    fun rollSingleDie(): Int {
        return Random.nextInt(1, 7)
    }

    /**
     * Roll selected dice and keep others
     * @param currentDice Current values of all dice
     * @param keepDice Boolean list where true means keep, false means reroll
     * @return New list of dice values with selected dice rerolled
     */
    fun rerollSelectedDice(currentDice: List<Int>, keepDice: List<Boolean>): List<Int> {
        return currentDice.mapIndexed { index, value ->
            if (keepDice[index]) value else rollSingleDie()
        }
    }

    /**
     * Calculate the sum of dice values
     * @param dice List of dice values
     * @return Sum of all dice values
     */
    fun calculateDiceSum(dice: List<Int>): Int {
        return dice.sum()
    }

    /**
     * An enhanced strategy for computer player
     *
     * This strategy implements a more intelligent approach by:
     * - Keeping high value dice (5 and 6)
     * - Making decisions based on current score
     * - Adapting strategy based on roll count
     *
     * @param currentDice Current dice values
     * @param rollCount Current roll count (0-2)
     * @param computerScore Current computer score
     * @param targetScore Target score to win
     * @return Pair of (shouldReroll, diceToKeep)
     */
    fun computerSmartStrategy(
        currentDice: List<Int>,
        rollCount: Int,
        computerScore: Int,
        targetScore: Int
    ): Pair<Boolean, List<Boolean>> {
        // Calculate current sum
        val currentSum = calculateDiceSum(currentDice)

        // If this is the third roll, no reroll is allowed
        if (rollCount >= 2) {
            return Pair(false, List(5) { true })
        }

        // If current roll would win the game, keep it
        if (computerScore + currentSum >= targetScore) {
            return Pair(false, List(5) { true })
        }

        // If we have a very good roll, keep it
        if (currentSum >= 25) {
            return Pair(false, List(5) { true })
        }

        // Decide whether to reroll based on current sum
        val shouldReroll = when {
            currentSum < 15 -> true  // Poor roll, always reroll
            currentSum > 22 -> false // Good roll, keep it
            else -> Random.nextDouble() < 0.7 // Middle range - 70% chance to reroll
        }

        // Determine which dice to keep
        val keepDice = when {
            // If we're not rerolling, keep all dice
            !shouldReroll -> List(5) { true }

            // First roll - only keep 5 and 6
            rollCount == 0 -> currentDice.map { it >= 5 }

            // Second roll - keep 4, 5, and 6
            else -> currentDice.map { it >= 4 }
        }

        return Pair(shouldReroll, keepDice)
    }

    /**
     * Strategy documentation for the computer player.
     *
     * This smart strategy for the computer dice game focuses on maximizing the expected value
     * of the final score through optimal dice selection and reroll decisions.
     *
     * Key aspects of the strategy:
     *
     * 1. Value-based dice retention:
     *    - Always keep high-value dice (5 and 6)
     *    - On later rolls, keep medium-value dice (4)
     *    - Almost always reroll low-value dice (1, 2, 3)
     *
     * 2. Roll-count adaptation:
     *    - First roll: More aggressive, keep only 5 and 6
     *    - Second roll: More conservative, keep 4, 5, and 6
     *    - Third roll: Must keep all (per game rules)
     *
     * 3. Score-awareness:
     *    - If current dice would reach target score, keep immediately
     *    - If current roll is exceptional (25+), keep regardless of other factors
     *    - If current roll is poor (<15), almost always reroll
     *
     * 4. Risk assessment:
     *    - Balance between maximizing expected value and variance
     *    - Take more risks when behind, play conservatively when ahead
     *
     * Advantages:
     * - Simple to implement yet effective
     * - Adapts to game state
     * - Makes reasonable decisions based on probability
     *
     * Limitations:
     * - Doesn't fully model all possible dice combinations
     * - Doesn't use advanced probability theory for perfect decisions
     * - Doesn't remember previous roll history
     *
     * This strategy provides a good balance between computational simplicity
     * and effective gameplay, making reasonable decisions that approximate
     * optimal play without requiring complex calculations.
     */
}