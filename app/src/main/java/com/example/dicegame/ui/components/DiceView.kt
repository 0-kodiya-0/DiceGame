package com.example.dicegame.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val darkTheme: Boolean = isSystemInDarkTheme()

    val diceResource = when (value) {
        1 -> if (darkTheme) R.drawable.dice_1_light else R.drawable.dice_1_dark
        2 -> if (darkTheme) R.drawable.dice_2_light else R.drawable.dice_2_dark
        3 -> if (darkTheme) R.drawable.dice_3_light else R.drawable.dice_3_dark
        4 -> if (darkTheme) R.drawable.dice_4_light else R.drawable.dice_4_dark
        5 -> if (darkTheme) R.drawable.dice_5_light else R.drawable.dice_5_dark
        6 -> if (darkTheme) R.drawable.dice_6_light else R.drawable.dice_6_dark
        else -> null // Default to 1 if invalid value
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
        if (diceResource !== null)
        Image(
            painter = painterResource(id = diceResource),
            contentDescription = "Dice showing $value",
            modifier = Modifier.size(56.dp)
        )
    }
}