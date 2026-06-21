package com.example.financeapp.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.financeapp.ui.navigation.Screen

@Composable
fun BottomBar(
    navController: NavController
) {
    val items = listOf(
        Screen.Home,
        Screen.Transaction,
        Screen.Budget,
        Screen.Statistics,
        Screen.Profile
    )

    NavigationBar {
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route

// Khởi tạo thông tin chi tiết từng tab
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                // Sửa theo yêu cầu: Lấy trực tiếp icon đã khai báo ở Screen.kt, không dùng 'when' nữa
                icon = {
                    screen.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = screen.title
                        )
                    }
                },
                label = { Text(screen.title) }
            )
        }
    }
}