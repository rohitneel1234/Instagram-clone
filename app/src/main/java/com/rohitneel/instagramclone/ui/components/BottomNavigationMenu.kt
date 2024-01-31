package com.rohitneel.instagramclone.ui.components

import androidx.compose.ui.graphics.painter.Painter
import com.rohitneel.instagramclone.navigation.DestinationScreen

data class BottomNavigationItems(
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    val bottomNavRoutes: DestinationScreen
)