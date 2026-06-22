package com.example.financeapp.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeapp.data.remote.ApiService
import kotlinx.coroutines.launch

// Data class hứng dữ liệu từ MySQL trả về
data class AdminUserJson(
    val user_id: Int,
    val username: String,
    val full_name: String,
    val role: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(apiService: ApiService, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var userList by remember { mutableStateOf<List<AdminUserJson>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<AdminUserJson?>(null) }

    // Hàm load danh sách người dùng từ MySQL
    val loadUsers = {
        scope.launch {
            try {
                val response = apiService.getAllUsers()
                val rawUsers = response["users"] as? List<*> ?: emptyList<Any>()

                userList = rawUsers.mapNotNull { item ->
                    val map = item as? Map<*, *> ?: return@mapNotNull null
                    AdminUserJson(
                        user_id = (map["user_id"] as? Double)?.toInt()
                            ?: (map["user_id"] as? Int)
                            ?: map["user_id"]?.toString()?.toIntOrNull() ?: 0,
                        username = map["username"]?.toString() ?: "",
                        full_name = map["full_name"]?.toString() ?: "",
                        role = map["role"]?.toString() ?: "USER"
                    )
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi nạp dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Tự động nạp danh sách khi mở màn hình
    LaunchedEffect(Unit) { loadUsers() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Bảng điều khiển Admin 👑", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.error)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedUser = null
                showDialog = true
            }, containerColor = MaterialTheme.colorScheme.errorContainer) {
                Icon(Icons.Default.Add, contentDescription = "Thêm Người Dùng")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Quản lý thành viên hệ thống (${userList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(userList) { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.full_name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Tài khoản: ${user.username}", color = Color.Gray, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(text = user.role) },
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        labelColor = if (user.role == "ADMIN") Color.Red else MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    selectedUser = user
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Sửa", tint = Color.Blue)
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        try {
                                            apiService.adminDeleteUser(user.user_id)
                                            Toast.makeText(context, "Đã xóa user thành công!", Toast.LENGTH_SHORT).show()
                                            loadUsers()
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Lỗi khi xóa user", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        var username by remember { mutableStateOf(selectedUser?.username ?: "") }
        var fullName by remember { mutableStateOf(selectedUser?.full_name ?: "") }
        var role by remember { mutableStateOf(selectedUser?.role ?: "USER") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (selectedUser == null) "Thêm người dùng mới" else "Sửa thông tin thành viên") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (selectedUser == null) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Tên tài khoản (Username)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Tên hiển thị (Full Name)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it.uppercase() },
                        label = { Text("Quyền hạn (USER hoặc ADMIN)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch {
                        try {
                            if (selectedUser == null) {
                                apiService.adminAddUser(username, fullName, role)
                                Toast.makeText(context, "Thêm user thành công!", Toast.LENGTH_SHORT).show()
                            } else {
                                apiService.adminUpdateUser(selectedUser!!.user_id, fullName, role)
                                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                            }
                            showDialog = false
                            loadUsers()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Thao tác thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Hủy") }
            }
        )
    }
}