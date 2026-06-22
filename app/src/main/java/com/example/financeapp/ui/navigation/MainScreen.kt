package com.example.financeapp.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.financeapp.ui.components.BottomBar
import com.example.financeapp.ui.navigation.Screen
import com.example.financeapp.ui.screens.budget.BudgetScreen
import com.example.financeapp.ui.screens.home.HomeScreen
import com.example.financeapp.ui.screens.profile.ProfileScreen
import com.example.financeapp.ui.screens.statistics.StatisticsScreen
import com.example.financeapp.ui.screens.transaction.TransactionScreen
import com.example.financeapp.ui.screens.category.CategoryScreen

@Composable
fun MainScreen(
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.Transaction.route) {
                TransactionScreen(
                    onNavigateToCategories = {
                        navController.navigate(Screen.Categories.route)
                    }
                )
            }

            composable(Screen.Budget.route) {
                BudgetScreen()
            }

            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogoutClick = onLogoutClick
                )
            }

            // Route mới cho quản lý danh mục
            composable(Screen.Categories.route) {
                CategoryScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}