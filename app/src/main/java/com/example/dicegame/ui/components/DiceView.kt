package com.example.dicegame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dicegame.R

/**
 * Composable for displaying a single die
 * @param value The value of the die (1-6)
 * @param isSelected Whether the die is selected for keeping
 * @param isSelectable Whether the die can be selected
 * @param onClick Callback for when the die is clicked
 * @param modifier Modifier for customizing the die's appearance
 */
@Composable
fun DiceView(
    value: Int,
    isSelected: Boolean = false,
    isSelectable: Boolean = false,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Get the appropriate dice resource based on value
    val diceResource = when (value) {
        1 -> R.drawable.dice_1
        2 -> R.drawable.dice_2
        3 -> R.drawable.dice_3
        4 -> R.drawable.dice_4
        5 -> R.drawable.dice_5
        6 -> R.drawable.dice_6
        else -> R.drawable.dice_1 // Default to 1 if invalid value
    }

    // Selection highlight modifier
    val selectionModifier = if (isSelected) {
        Modifier.border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(8.dp)
        ).background(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
            shape = RoundedCornerShape(8.dp)
        )
    } else Modifier

    // Clickable modifier
    val clickableModifier = if (isSelectable) {
        Modifier.clickable { onClick() }
    } else Modifier

    Box(
        modifier = modifier
            .then(selectionModifier)
            .then(clickableModifier)
            .padding(4.dp)
            .size(60.dp)
            .clip(RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = diceResource),
            contentDescription = "Dice showing $value",
            modifier = Modifier.size(56.dp)
        )
    }
}