package com.example.financeapp.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Biến quản lý trạng thái thông báo lỗi hiển thị lên giao diện
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Đổi mật khẩu 🔑",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 1. Ô nhập Mật khẩu cũ
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Mật khẩu cũ") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 2. Ô nhập Mật khẩu mới
        OutlinedTextField(
            value = newPassword,
            onValueChange = {
                newPassword = it
                if (it.length >= 8) errorMessage = null // Tự động xóa lỗi khi người dùng gõ đủ 8 ký tự
            },
            label = { Text("Mật khẩu mới") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            isError = errorMessage != null // Đổi viền thành màu đỏ nếu có lỗi
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Ô nhập lại Mật khẩu mới
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Nhập lại mật khẩu mới") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Vùng hiển thị thông báo lỗi bằng chữ màu đỏ nếu Validation thất bại
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Hàng chứa hai nút Hủy và Lưu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Hủy")
            }

            Button(
                onClick = {
                    // --- LOGIC VALIDATION THEO ĐỒ ÁN YÊU CẦU ---
                    if (newPassword.length < 8) {
                        errorMessage = "Mật khẩu mới phải có ít nhất 8 ký tự!"
                    } else if (newPassword != confirmPassword) {
                        errorMessage = "Mật khẩu nhập lại không trùng khớp!"
                    } else {
                        errorMessage = null
                        // Tạm thời kích hoạt callback thành công (Sau này Người 1 sẽ chèn cập nhật UserEntity ở đây)
                        onSaveSuccess()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Lưu")
            }
        }
    }
}