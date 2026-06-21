package com.example.financeapp.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financeapp.model.Transaction
import com.example.financeapp.viewmodel.TransactionViewModel

@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel,
    onSaveSuccess: () -> Unit
) {

    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

    var type by remember {
        mutableStateOf("Expense")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Thêm giao dịch",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tên giao dịch") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Số tiền") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Danh mục") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row {

            Button(
                onClick = {
                    type = "Income"
                }
            ) {
                Text("Thu")
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    type = "Expense"
                }
            ) {
                Text("Chi")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                val transaction = Transaction(
                    id = System.currentTimeMillis().toInt(),
                    title = title,
                    amount = amount.toDouble(),
                    type = type,
                    category = category,
                    date = "21/06/2026"
                )

                viewModel.addTransaction(transaction)

                onSaveSuccess()
            }
        ) {
            Text("Lưu")
        }
    }
    Button(
        onClick = {
            val parsedAmount = amount.toDoubleOrNull() ?: 0.0

            // --- LOGIC VALIDATION (KIỂM TRA DỮ LIỆU ĐẦU VÀO) ---
            if (title.isBlank()) {
                // Tên trống -> Không cho lưu
                return@Button
            }
            if (amount.isBlank() || parsedAmount <= 0) {
                // Số tiền trống hoặc nhỏ hơn/bằng 0 -> Không cho lưu
                return@Button
            }

            // Nếu vượt qua Validation thành công, tiến hành lưu:
            val finalAmount = if (type == "Expense") -kotlin.math.abs(parsedAmount) else parsedAmount

            val transaction = Transaction(
                id = System.currentTimeMillis().toInt(),
                title = title,
                amount = finalAmount,
                category = category,
                type = type,
                date = "21/06/2026"
            )
            viewModel.addTransaction(transaction)
            onSaveSuccess()
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Lưu")
    }
}