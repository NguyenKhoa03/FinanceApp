package com.example.financeapp.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.viewmodel.AuthState
import com.example.financeapp.viewmodel.AuthViewModel
import com.example.financeapp.viewmodel.AuthViewModelFactory

@Composable
fun LoginScreen(
    onNavigateToDashboard: (role: String) -> Unit, // Thay bằng lambda nhận tham số String (role)
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(app.userRepository)
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by authViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is AuthState.Success -> {
                // Đăng nhập thành công -> Bắn quyền hạn (USER/ADMIN) ra ngoài cho Graph điều hướng xử lý
                onNavigateToDashboard(state.role)
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Finance App",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (loginState is AuthState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = { authViewModel.login(username, password) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onRegisterClick,
            enabled = loginState !is AuthState.Loading
        ) {
            Text("Create Account")
        }
    }
}