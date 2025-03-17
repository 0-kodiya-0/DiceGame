package com.example.dicegame.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dicegame.ui.AppScreen
import com.example.dicegame.ui.components.AboutDialog

@Composable
fun HomeScreen(onNavigate: (AppScreen) -> Unit, modifier: Modifier = Modifier) {
    // State to control About dialog visibility
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎲 Dice Game 🎲",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { onNavigate(AppScreen.Playground) },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text("🎲 New Game", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNavigate(AppScreen.Settings) },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("⚙️ Settings", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAboutDialog = true },
            modifier = Modifier.fillMaxWidth(0.7f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("ℹ️ About", style = MaterialTheme.typography.titleMedium)
        }
    }

    // About dialog
    if (showAboutDialog) {
        AboutDialog(
            studentId = "w1234567", // Replace with your student ID
            studentName = "Your Name", // Replace with your name
            onDismiss = { showAboutDialog = false }
        )
    }
}