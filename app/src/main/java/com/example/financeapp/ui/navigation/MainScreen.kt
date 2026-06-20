package com.example.financeapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.home.HomeScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Danh sách 5 tab xuất hiện dưới Bottom Bar
    val items = listOf(
        Screen.Home,
        Screen.Transaction,
        Screen.Budget,
        Screen.Statistics,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { screen.icon?.let { Icon(it, contentDescription = screen.title) } },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    // Tránh việc nhấn lặp lại tạo ra nhiều stack màn hình
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // Hệ thống định tuyến nội bộ cho 5 màn hình thuộc Bottom Bar
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Transaction.route) { DummyScreen(title = "Màn hình Giao dịch (Transaction)") }
            composable(Screen.Budget.route) { DummyScreen(title = "Màn hình Ngân sách (Budget)") }
            composable(Screen.Statistics.route) { DummyScreen(title = "Màn hình Thống kê (Statistics)") }
            composable(Screen.Profile.route) { DummyScreen(title = "Màn hình Cá nhân (Profile)") }
        }
    }
}

// Hàm tạm thời tạo giao diện trống cho các tab chưa code chi tiết
@Composable
fun DummyScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}