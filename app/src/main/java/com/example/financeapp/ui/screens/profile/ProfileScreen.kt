package com.example.financeapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val username by profileViewModel.username.collectAsState()
    val email by profileViewModel.email.collectAsState()

    // 🛠️ BỔ SUNG: Khai báo Scope và Context để thao tác với Room DB khi đăng xuất
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current.applicationContext as FinanceApplication

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Tài khoản 👤",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Tên người dùng",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = username,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Email",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đổi mật khẩu")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🛠️ SỬA ĐỔI: Thực hiện logic Đăng xuất thật xóa sạch DB ngầm trước khi về Login
        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        // 1. Xóa toàn bộ dữ liệu giao dịch cũ lưu trên máy để bảo mật tài khoản
                        context.database.transactionDao().deleteAll()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        // 2. Kích hoạt lambda chuyển hướng về LoginScreen (đã dọn sạch Stack bên NavGraph)
                        onLogoutClick()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng xuất", color = MaterialTheme.colorScheme.error)
        }
    }
}