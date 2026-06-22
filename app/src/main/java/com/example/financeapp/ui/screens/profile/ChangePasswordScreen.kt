package com.example.financeapp.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.viewmodel.ChangePasswordState
import com.example.financeapp.viewmodel.ChangePasswordViewModel
// 🛠️ ĐÃ THÊM: Import Factory từ package viewmodel để sửa lỗi Unresolved reference
import com.example.financeapp.viewmodel.ChangePasswordViewModelFactory

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication
    val viewModel: ChangePasswordViewModel = viewModel(
        factory = ChangePasswordViewModelFactory(app.userRepository)
    )

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is ChangePasswordState.Success -> {
                Toast.makeText(context, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                onSaveSuccess()
                viewModel.resetState()
            }
            is ChangePasswordState.Error -> {
                Toast.makeText(context, (state as ChangePasswordState.Error).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

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

        OutlinedTextField(
            value = oldPassword,
            onValueChange = { oldPassword = it },
            label = { Text("Mật khẩu cũ") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ChangePasswordState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Mật khẩu mới") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ChangePasswordState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Nhập lại mật khẩu mới") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is ChangePasswordState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state is ChangePasswordState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
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
                        viewModel.changePassword(oldPassword, newPassword, confirmPassword)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Lưu")
                }
            }
        }
    }
}