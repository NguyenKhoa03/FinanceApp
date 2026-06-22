package com.example.financeapp.ui.screens.home

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.data.local.entity.AccountEntity
import com.example.financeapp.viewmodel.AccountViewModel
import com.example.financeapp.viewmodel.AccountViewModelFactory
import java.text.NumberFormat
import java.util.*

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication
    val accountViewModel: AccountViewModel = viewModel(
        factory = AccountViewModelFactory(app.accountRepository)
    )

    // Lấy thông tin User hiện tại từ UserRepository để lấy user_id thực tế (Tránh lỗi khóa ngoại)
    val userRepository = app.userRepository
    val currentUser by userRepository.currentUser.collectAsState()
    val currentUserId = currentUser?.user_id ?: 1 // Nếu chưa đăng nhập thì mặc định là 1

    // Lấy danh sách gốc từ ViewModel của bạn
    val allAccountsFromDb by accountViewModel.allAccounts.collectAsState()

    // TỰ LỌC TRÊN UI: Chỉ hiển thị tài khoản thuộc về User đang đăng nhập để không bị lệch ID
    val accounts = remember(allAccountsFromDb, currentUserId) {
        allAccountsFromDb.filter { it.user_id == currentUserId }
    }

    // Tính toán tổng số dư động dựa trên danh sách tài khoản đã lọc
    val totalBalance = remember(accounts) { accounts.sumOf { it.balance }.toDouble() }

    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tài chính của tôi 💰",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tổng số dư hiện có", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = formatCurrency(totalBalance),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Danh sách ngân hàng/ví",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm tài khoản")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(accounts) { account ->
                AccountItem(
                    account = account,
                    onDeleteClick = {
                        // Gọi trực tiếp hàm xóa vừa thêm vào ViewModel, không lo báo đỏ nữa
                        accountViewModel.deleteAccount(account)
                        Toast.makeText(context, "Đã xóa ${account.account_name}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }

    if (showDialog) {
        AccountDialog(
            onDismiss = { showDialog = false },
            onSave = { name, balance, type ->
                val newAccount = AccountEntity(
                    account_id = 0,
                    account_name = name,
                    balance = balance,
                    account_type = type,
                    user_id = currentUserId // Đồng bộ chuẩn ID người dùng đang đăng nhập
                )
                accountViewModel.insertAccount(newAccount)
                showDialog = false
                Toast.makeText(context, "Đã thêm tài khoản mới", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun AccountItem(account: AccountEntity, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = account.account_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = "${account.account_type} • ${formatCurrency(account.balance.toDouble())}",
                    color = if (account.balance >= 0) Color(0xFF2E7D32) else Color.Red,
                    fontSize = 14.sp
                )
                Text(
                    text = "ID CSDL: ${account.account_id} • User: ${account.user_id}",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Xóa ngân hàng",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AccountDialog(
    onDismiss: () -> Unit,
    onSave: (String, Long, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var balanceStr by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Ngân hàng") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm ngân hàng/ví") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên ngân hàng/ví") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = balanceStr,
                    onValueChange = { balanceStr = it },
                    label = { Text("Số dư ban đầu") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Loại tài khoản (Ví dụ: Ngân hàng, Ví, Tiền mặt)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val balance = balanceStr.toLongOrNull() ?: 0L
                if (name.isNotBlank() && type.isNotBlank()) {
                    onSave(name, balance, type)
                }
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}