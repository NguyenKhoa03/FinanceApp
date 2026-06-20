package com.example.financeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Register : Screen("register")

    // 5 Màn hình chính của Bottom Navigation
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transaction : Screen("transaction", "Transaction", Icons.Default.List)
    object Budget : Screen("budget", "Budget", Icons.Default.ShoppingCart)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
}