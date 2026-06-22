package com.example.financeapp.ui.screens.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.data.local.entity.TransactionEntity
import com.example.financeapp.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionScreen(onNavigateToCategories: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication

    val transactionViewModel: TransactionViewModel = viewModel(
        factory = TransactionViewModelFactory(app.repository)
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(app.categoryRepository)
    )
    val accountViewModel: AccountViewModel = viewModel(
        factory = AccountViewModelFactory(app.accountRepository)
    )

    var isAddingTransaction by remember { mutableStateOf(false) }
    val transactions by transactionViewModel.allTransactions.collectAsState()
    val categories by categoryViewModel.allCategories.collectAsState()

    if (isAddingTransaction) {
        AddTransactionScreen(
            transactionViewModel = transactionViewModel,
            categoryViewModel = categoryViewModel,
            accountViewModel = accountViewModel,
            onSaveSuccess = { isAddingTransaction = false },
            onCancel = { isAddingTransaction = false },
            onManageCategories = onNavigateToCategories
        )
    } else {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { isAddingTransaction = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Lịch sử giao dịch 📝", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onNavigateToCategories) {
                        Text("Danh mục")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (transactions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Chưa có giao dịch nào", color = Color.Gray)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(transactions) { transaction ->
                            val category = categories.find { it.category_id == transaction.category_id }
                            TransactionItem(
                                transaction = transaction,
                                category = category,
                                onDelete = { transactionViewModel.deleteTransaction(transaction.transaction_id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    category: CategoryEntity?,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(transaction.transaction_date))

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(parseLocalTransactionHexColor(category?.color), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(transaction.note ?: "", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = "${category?.category_name ?: "Không xác định"} • $dateStr",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // 🛠️ ĐÃ SỬA: Hỗ trợ kiểm tra định dạng Enum của cả MySQL ("THU NHẬP", "CHI PHÍ") và Local ("income")
            val isIncome = transaction.type.lowercase() == "income" || transaction.type == "THU NHẬP"
            val amountColor = if (isIncome) Color(0xFF2E7D32) else Color.Red
            val prefix = if (isIncome) "+" else "-"
            val formattedAmount = "$prefix${String.format("%,.0f", transaction.amount.toDouble())} đ"

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formattedAmount,
                    color = amountColor,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.LightGray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

private fun parseLocalTransactionHexColor(hexColor: String?): Color {
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