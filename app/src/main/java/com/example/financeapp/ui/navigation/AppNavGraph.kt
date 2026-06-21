package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.MainScreen
import com.example.financeapp.ui.screens.login.LoginScreen
import com.example.financeapp.ui.screens.register.RegisterScreen
import com.example.financeapp.ui.screens.profile.ChangePasswordScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 1. Màn hình Đăng nhập
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(Screen.MainFlow.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        // 2. Màn hình Đăng ký
        composable(Screen.Register.route) {
            RegisterScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 3. Luồng chính sau khi Login thành công (Chứa các Tab Home, Transaction, Budget, Profile)
        composable(Screen.MainFlow.route) {
            // Truyền các sự kiện Logout và chuyển màn hình Đổi mật khẩu vào MainScreen
            MainScreen(
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        // Xóa sạch lịch sử để không bấm phím Back quay lại MainFlow được
                        popUpTo(Screen.MainFlow.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Màn hình Đổi mật khẩu (Giai đoạn 3)
        composable("change_password") {
            ChangePasswordScreen(
                onBackClick = {
                    navController.popBackStack() // Quay lại MainScreen
                },
                onSaveSuccess = {
                    navController.popBackStack() // Lưu thành công cũng quay lại MainScreen
                }
            )
        }
    }
}