package com.example.financeapp.ui.screens.register

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
// 🛠️ ĐÃ THÊM: Import Factory từ package viewmodel để sửa lỗi Unresolved reference
import com.example.financeapp.viewmodel.AuthViewModelFactory

@Composable
fun RegisterScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(app.userRepository)
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullname by remember { mutableStateOf("") }

    val registerState by authViewModel.registerState.collectAsState()

    LaunchedEffect(registerState) {
        when (registerState) {
            is AuthState.Success -> {
                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                onBackClick() // Quay lại Login
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (registerState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
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
            text = "Register",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullname,
            onValueChange = { fullname = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is AuthState.Loading
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (registerState is AuthState.Loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            Button(
                onClick = { authViewModel.register(username, password, fullname) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Register")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = registerState !is AuthState.Loading
        ) {
            Text("Back To Login")
        }
    }
}