package com.example.financeapp.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.model.Transaction
import com.example.financeapp.viewmodel.TransactionViewModel

@Composable
fun TransactionScreen(
    transactionViewModel: TransactionViewModel = viewModel()
) {
    var isAddingTransaction by remember { mutableStateOf(false) }

    if (isAddingTransaction) {
        // Bước 4: Mở màn hình thêm mới
        AddTransactionScreen(
            viewModel = transactionViewModel,
            onSaveSuccess = { isAddingTransaction = false }
        )
    } else {
        val transactions by transactionViewModel.filteredTransactions.collectAsState()
        val currentFilter by transactionViewModel.currentFilter.collectAsState()

        Scaffold(
            // Bước 4: Nút + hình tròn FloatingActionButton
            floatingActionButton = {
                FloatingActionButton(onClick = { isAddingTransaction = true }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(text = "Lịch sử giao dịch 📝", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                // Thanh Bộ Lọc Filter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(onClick = { transactionViewModel.setFilter("All") }) { Text("Tất cả") }
                    Button(onClick = { transactionViewModel.setFilter("Income") }) { Text("Thu") }
                    Button(onClick = { transactionViewModel.setFilter("Expense") }) { Text("Chi") }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onDelete = {
                                // Bước 5: Gọi hàm xóa từ ViewModel
                                transactionViewModel.deleteTransaction(transaction.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: Transaction,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = transaction.title, style = MaterialTheme.typography.titleMedium)
                Text(text = transaction.category, style = MaterialTheme.typography.bodyMedium)
                Text(text = transaction.date, style = MaterialTheme.typography.bodySmall)
            }

            val amountColor = if (transaction.type == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            val prefix = if (transaction.type == "Income") "+" else ""

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$prefix${transaction.amount} đ", color = amountColor, style = MaterialTheme.typography.titleMedium)

                // Bước 5: Nút Xóa (Delete Icon) tích hợp trên từng hàng
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}