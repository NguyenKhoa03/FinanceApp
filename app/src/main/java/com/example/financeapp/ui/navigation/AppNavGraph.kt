package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.data.remote.RetrofitClient
import com.example.financeapp.ui.screens.MainScreen
import com.example.financeapp.ui.screens.admin.AdminScreen
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
        // 1. Màn hình Đăng nhập (Đã bổ sung rẽ nhánh phân quyền)
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToDashboard = { role ->
                    if (role == "ADMIN") {
                        // 👑 Nếu là ADMIN -> Đưa thẳng sang màn hình quản lý hệ thống
                        navController.navigate("admin_screen") {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        // 👤 Nếu là USER thường -> Đi vào luồng tính năng chính (MainFlow)
                        navController.navigate(Screen.MainFlow.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
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

        // 3. Luồng chính sau khi Login thành công (Quyền USER)
        composable(Screen.MainFlow.route) {
            MainScreen(
                onNavigateToChangePassword = {
                    navController.navigate("change_password")
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MainFlow.route) { inclusive = true }
                    }
                }
            )
        }

        // 4. Màn hình Đổi mật khẩu
        composable("change_password") {
            ChangePasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // 5. 🛠️ ĐÃ THÊM: Màn hình quản trị cho ADMIN toàn quyền thêm sửa xóa
        composable("admin_screen") {
            AdminScreen(
                apiService = RetrofitClient.api
            )
        }
    }
}