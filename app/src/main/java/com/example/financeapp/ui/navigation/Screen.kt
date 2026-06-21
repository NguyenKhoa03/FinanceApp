package com.example.financeapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String = "",
    val icon: ImageVector? = null
) {
    object Login : Screen("login")
    object Register : Screen("register")

    // Quản lý luồng chính sau đăng nhập
    object MainFlow : Screen("main_flow")

    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transaction : Screen("transaction", "Transaction", Icons.Default.List)
    object Budget : Screen("budget", "Budget", Icons.Default.ShoppingCart)
    object Statistics : Screen("statistics", "Statistics", Icons.Default.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Default.AccountCircle)
}