package com.example.financeapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = viewModel(),
    onNavigateToChangePassword: () -> Unit, // Thêm lambda để chuyển sang màn hình Đổi mật khẩu
    onLogoutClick: () -> Unit // Thêm lambda để quay lại màn hình Login
) {
    val username by profileViewModel.username.collectAsState()
    val email by profileViewModel.email.collectAsState()

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

        // Giai đoạn 3: Gọi sự kiện chuyển màn hình đổi mật khẩu
        Button(
            onClick = onNavigateToChangePassword,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đổi mật khẩu")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Giai đoạn 2: Gọi sự kiện đăng xuất quay về Login
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng xuất", color = MaterialTheme.colorScheme.error)
        }
    }
}