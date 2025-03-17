# Dice Game - Android Application Documentation

## Project Overview

This project is an Android dice game application developed using Kotlin and Jetpack Compose. The game follows a set of specific rules where two players (human and computer) compete to reach a target score (default 101) by rolling five dice. Players can choose to reroll dice up to two times per turn, with the option to keep selected dice between rolls.

## Folder Structure and File Organization

The project follows a standard Android application structure with clear separation of concerns:

### Main Package: `com.example.dicegame`

- **MainActivity.kt**: Entry point of the application that initializes the game state and sets up the Compose UI.

### Data Package: `com.example.dicegame.data`

- **GameState.kt**: Singleton object that stores and manages the game state across configuration changes. This approach is used instead of ViewModel as per the requirements.

### UI Package: `com.example.dicegame.ui`

- **AppNavigation.kt**: Handles navigation between different screens and maintains navigation state.
- **AppScreen.kt**: Enum class defining the available screens in the application.

#### UI Screens Subpackage: `com.example.dicegame.ui.screens`

- **HomeScreen.kt**: The main menu screen with options to start a new game, access settings, and view the about information.
- **PlaygroundScreen.kt**: The main game screen where dice rolling, scoring, and game logic occur.
- **SettingsScreen.kt**: Allows the user to configure game settings, such as the target score.

#### UI Components Subpackage: `com.example.dicegame.ui.components`

- **DiceView.kt**: Reusable component for displaying and interacting with dice.
- **AboutDialog.kt**: Dialog component for displaying author information and the plagiarism declaration.

#### UI Theme Subpackage: `com.example.dicegame.ui.theme`

- Contains theme-related files like **Color.kt**, **Theme.kt**, and **Type.kt** for styling the application.

### Utils Package: `com.example.dicegame.utils`

- **DiceUtils.kt**: Utility functions for dice-related operations, random dice generation, and computer AI strategy.

### Resources: `res/drawable`

- **dice_1.xml** through **dice_6.xml**: SVG vector drawables for displaying dice faces.

## Game Logic Implementation

### Game Rules

1. Both players (human and computer) throw 5 dice at the same time.
2. The score of each throw is the sum of the numbers on the dice faces.
3. After a roll, each player may take up to two optional rerolls, keeping any dice they choose.
4. Players can score at any time, ending their current turn.
5. The first player to reach the target score (default 101) wins.
6. If both players reach the target in the same number of attempts, the player with the higher score wins.
7. If there's an exact tie, players enter a tie-breaker mode where they roll until one player wins.

### State Management

The game uses `GameState` singleton object to persist state across configuration changes. Key state elements include:

- Current scores for both players
- Dice values
- Selection status of dice
- Roll count in the current turn
- Game status (in progress, game over, tie-breaker)
- Win statistics

### Game Flow

1. **Initial Setup**: The game starts from the HomeScreen, where the player can choose to start a new game or adjust settings.

2. **Turn Structure**:
    - Each turn begins with rolling all five dice for both players.
    - After each roll, the human player can select dice to keep for the next reroll.
    - The computer uses a strategic algorithm to decide which dice to keep.
    - After up to three rolls (initial roll + 2 rerolls), the score is calculated and added to the total.

3. **Winning Logic**:
    - The `checkForWinner()` function in PlaygroundScreen checks if either player has reached the target score.
    - It handles various winning scenarios including the tie-breaker logic.
    - When a player wins, a dialog is displayed showing the final score and the winner.

4. **Tie-Breaker Handling**:
    - In case of a tie, the game enters a special tie-breaker mode.
    - In this mode, players take single rolls (no rerolls) until one player scores higher.
    - The `checkTieBreakerResult()` function handles this special case.

### Computer AI Strategy

The computer's decision-making is implemented in `DiceUtils.kt` through the `computerSmartStrategy()` function:

1. The AI evaluates the current dice values and game state.
2. It makes an intelligent decision about whether to reroll and which dice to keep based on:
    - Current dice values
    - Current roll count
    - Current score difference between players
    - How close each player is to the target score

This provides a challenging opponent while still maintaining fair gameplay.

## Navigation and State Persistence

The application handles navigation and state persistence without using ViewModel (as per requirements):

1. **Navigation**: Managed through `AppNavigation.kt` which maintains the current screen in a `NavigationState` object.

2. **State Persistence**: All game state is stored in the `GameState` singleton, which persists across configuration changes.

3. **Screen Rotation**: The app correctly maintains its state during configuration changes like screen rotation without using `onSaveInstanceState()`.

## Resources and Assets

The dice visuals are implemented as vector drawables in XML format, located in the drawable resources directory. These SVG-based vectors provide crisp dice faces at any screen resolution.

## Conclusion

This application demonstrates effective use of Kotlin and Jetpack Compose for creating a fully functional dice game with proper state management, UI implementation, and game logic. The project architecture separates concerns effectively, making the code maintainable and extensible.