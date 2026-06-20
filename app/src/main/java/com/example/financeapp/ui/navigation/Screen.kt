package com.example.financeapp.ui.navigation

sealed class Screen(val route: String) {

    object Login : Screen("login")

    object Register : Screen("register")

    object Home : Screen("home")

    object Transaction : Screen("transaction")

    object Budget : Screen("budget")

    object SavingGoal : Screen("saving_goal")

    object Statistics : Screen("statistics")

    object Profile : Screen("profile")
}