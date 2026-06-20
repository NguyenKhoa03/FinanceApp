package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.financeapp.ui.screens.home.HomeScreen
import com.example.financeapp.ui.screens.login.LoginScreen
import com.example.financeapp.ui.screens.register.RegisterScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // Đổi điểm bắt đầu thành Login
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.Home.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.Transaction.route) {

        }

        composable(Screen.Budget.route) {

        }

        composable(Screen.SavingGoal.route) {

        }

        composable(Screen.Statistics.route) {

        }

        composable(Screen.Profile.route) {

        }
    }
}