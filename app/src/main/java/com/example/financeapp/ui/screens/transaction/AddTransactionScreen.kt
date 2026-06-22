package com.example.financeapp.ui.screens.transaction

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financeapp.data.local.entity.AccountEntity
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.data.local.entity.TransactionEntity
import com.example.financeapp.viewmodel.AccountViewModel
import com.example.financeapp.viewmodel.CategoryViewModel
import com.example.financeapp.viewmodel.TransactionViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    transactionViewModel: TransactionViewModel,
    categoryViewModel: CategoryViewModel,
    accountViewModel: AccountViewModel,
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit,
    onManageCategories: () -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }

    // Gốc rễ ENUM: "CHI PHÍ" hoặc "THU NHẬP"
    var type by remember { mutableStateOf("CHI PHÍ") }

    val categories by categoryViewModel.allCategories.collectAsState()
    val accounts by accountViewModel.allAccounts.collectAsState()

    val filteredCategories = categories.filter {
        it.type.uppercase() == type ||
                (type == "CHI PHÍ" && it.type.lowercase() == "expense") ||
                (type == "THU NHẬP" && it.type.lowercase() == "income")
    }

    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedAccount by remember { mutableStateOf<AccountEntity?>(null) }

    var categoryExpanded by remember { mutableStateOf(false) }
    var accountExpanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    var selectedDate by remember { mutableLongStateOf(calendar.timeInMillis) }
    val sdfDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // 🛠️ ĐÃ THÊM: Định dạng ngày chuẩn YYYY-MM-DD để khớp với cột DATE của MySQL
    val sdfSql = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(type) {
        selectedCategory = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Thêm giao dịch mới 💸",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = type == "CHI PHÍ",
                onClick = { type = "CHI PHÍ" },
                label = { Text("Chi tiêu") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = type == "THU NHẬP",
                onClick = { type = "THU NHẬP" },
                label = { Text("Thu nhập") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = sdfDisplay.format(Date(selectedDate)),
            onValueChange = {},
            label = { Text("Ngày tháng năm") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = {
                    val currentCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val newCalendar = Calendar.getInstance()
                            newCalendar.set(year, month, dayOfMonth)
                            selectedDate = newCalendar.timeInMillis
                        },
                        currentCal.get(Calendar.YEAR),
                        currentCal.get(Calendar.MONTH),
                        currentCal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Chọn ngày")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Ghi chú / Tên giao dịch") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Ví dụ: Ăn trưa, Lương tháng 10...") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amountStr,
            onValueChange = { amountStr = it },
            label = { Text("Số tiền (đ)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("0") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = accountExpanded,
            onExpandedChange = { accountExpanded = !accountExpanded }
        ) {
            OutlinedTextField(
                value = selectedAccount?.account_name ?: "Chọn tài khoản/ngân hàng",
                onValueChange = {},
                readOnly = true,
                label = { Text("Tài khoản nguồn/nhận") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = accountExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = accountExpanded,
                onDismissRequest = { accountExpanded = false }
            ) {
                accounts.forEach { account ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(account.account_name)
                                Text(
                                    "Số dư: ${String.format("%,.0f", account.balance.toDouble())} đ",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
                        },
                        onClick = {
                            selectedAccount = account
                            accountExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory?.category_name ?: "Chọn danh mục",
                onValueChange = {},
                readOnly = true,
                label = { Text("Danh mục") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                leadingIcon = {
                    selectedCategory?.let {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(parseLocalHexColor(it.color), CircleShape)
                        )
                    }
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                filteredCategories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(parseLocalHexColor(category.color), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(category.category_name)
                            }
                        },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Hủy")
            }

            Button(
                onClick = {
                    val amountDouble = amountStr.toDoubleOrNull() ?: 0.0
                    if (title.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập tên giao dịch", Toast.LENGTH_SHORT).show()
                    } else if (amountDouble <= 0) {
                        Toast.makeText(context, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                    } else if (selectedAccount == null) {
                        Toast.makeText(context, "Vui lòng chọn tài khoản ngân hàng", Toast.LENGTH_SHORT).show()
                    } else if (selectedCategory == null) {
                        Toast.makeText(context, "Vui lòng chọn danh mục", Toast.LENGTH_SHORT).show()
                    } else {
                        // 🛠️ ĐÃ SỬA: Ép chuỗi định dạng ngày thành "yyyy-MM-dd" để lồng vào thực thể tạm thời hoặc chuyển tiếp
                        val sqlDateStr = sdfSql.format(Date(selectedDate))

                        // Tạo thực thể giao dịch chuẩn
                        val newTransaction = TransactionEntity(
                            transaction_id = 0,
                            user_id = selectedAccount!!.user_id,
                            account_id = selectedAccount!!.account_id,
                            category_id = selectedCategory!!.category_id,
                            amount = amountDouble.toLong(),
                            type = type,
                            transaction_date = selectedDate, // Vẫn lưu SQLite dạng Long
                            note = title
                        )

                        // Gọi ViewModel lưu dữ liệu (ViewModel sẽ gọi Repo bóc tách sqlDateStr truyền lên API PHP)
                        transactionViewModel.addTransaction(newTransaction)
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

private fun parseLocalHexColor(hexColor: String?): Color {
    if (hexColor.isNullOrEmpty()) return Color.Gray
    return try {
        if (hexColor.startsWith("#")) {
            Color(android.graphics.Color.parseColor(hexColor))
        } else {
            Color(hexColor.toIntOrNull() ?: android.graphics.Color.GRAY)
        }
    } catch (e: Exception) {
        Color.Gray
    }
}