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

@Composable
fun MainScreen(
    // 🛠️ BỔ SUNG: Nhận 2 sự kiện điều hướng từ AppNavGraph truyền xuống
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
            // Màn hình Tổng quan (Đã kết nối ViewModel)
            composable(Screen.Home.route) {
                HomeScreen()
            }

            // Màn hình Giao dịch thực tế
            composable(Screen.Transaction.route) {
                TransactionScreen()
            }

            // Màn hình Ngân sách thực tế
            composable(Screen.Budget.route) {
                BudgetScreen()
            }

            // Màn hình Thống kê thực tế
            composable(Screen.Statistics.route) {
                StatisticsScreen()
            }

            // Màn hình Cá nhân thực tế
            composable(Screen.Profile.route) {
                // 🛠️ SỬA CHỖ NÀY: Truyền tiếp 2 sự kiện vào ProfileScreen để thực hiện bấm nút
                ProfileScreen(
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    }
}