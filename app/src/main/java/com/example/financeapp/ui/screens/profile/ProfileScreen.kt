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
// 🛠️ ĐÃ THÊM: Import Factory từ package viewmodel để sửa lỗi Unresolved reference
import com.example.financeapp.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    onNavigateToChangePassword: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext as FinanceApplication
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context.userRepository)
    )

    val displayName by profileViewModel.displayName.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
                    text = displayName,
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

        OutlinedButton(
            onClick = {
                coroutineScope.launch {
                    try {
                        context.userRepository.logout()
                        context.database.transactionDao().deleteAll()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
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