package com.sprinthon.focusclock.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headset
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class GlobalNavigationItem(val route: String, val title: String, val icon: ImageVector) {
    Home(Screen.Home.route, "Home", Icons.Default.Home),
    StartFocus(Screen.StartFocus.route, "Focus", Icons.Default.PlayArrow),
    Ambient(Screen.Soundscape.route, "Ambient", Icons.Default.Headset),
    Settings(Screen.SettingsHub.route, "Settings", Icons.Default.Settings)
}
