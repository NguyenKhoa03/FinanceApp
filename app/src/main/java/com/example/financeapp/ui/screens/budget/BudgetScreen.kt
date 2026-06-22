package com.example.financeapp.ui.screens.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.data.local.entity.CategoryEntity
// 🛠️ ĐÃ FIX: Chỉ định cụ thể tường minh các class ViewModel và Factory cần dùng
import com.example.financeapp.viewmodel.BudgetViewModel
import com.example.financeapp.viewmodel.BudgetViewModelFactory
import com.example.financeapp.viewmodel.CategoryViewModel
import com.example.financeapp.viewmodel.CategoryViewModelFactory
import com.example.financeapp.viewmodel.BudgetWithProgress
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication

    val budgetViewModel: BudgetViewModel = viewModel(
        factory = BudgetViewModelFactory(app.budgetRepository, app.categoryRepository)
    )
    val categoryViewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(app.categoryRepository)
    )

    val budgetsWithProgress by budgetViewModel.budgetsWithProgress.collectAsState()
    val monthYear by budgetViewModel.currentMonthYear.collectAsState()
    val categories by categoryViewModel.allCategories.collectAsState()

    // Lọc theo kiểu chữ hoa tiếng Việt "CHI PHÍ" để khớp logic đồng bộ MySQL
    val expenseCategories = categories.filter { it.type == "CHI PHÍ" || it.type == "expense" }

    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm hạn mức")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Quản lý ngân sách $monthYear 🎯",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Hạn mức chi tiêu hàng tháng theo danh mục",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (budgetsWithProgress.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chưa có hạn mức nào được thiết lập", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(budgetsWithProgress) { item ->
                        BudgetItem(
                            item = item,
                            onDelete = { budgetViewModel.deleteBudget(item.budget) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddBudgetDialog(
            categories = expenseCategories,
            onDismiss = { showAddDialog = false },
            onSave = { categoryId, amount ->
                budgetViewModel.addOrUpdateBudget(categoryId, amount.toDouble())
                showAddDialog = false
            }
        )
    }
}

@Composable
fun BudgetItem(
    item: BudgetWithProgress,
    onDelete: () -> Unit
) {
    val limitAmount = item.budget.limit_amount
    val spentAmount = item.spentAmount

    val progress = if (limitAmount > 0)
        (spentAmount.toDouble() / limitAmount.toDouble()).toFloat()
    else 0f

    val isOverBudget = spentAmount > limitAmount
    val isNearLimit = progress > 0.8f && !isOverBudget

    val progressColor = when {
        isOverBudget -> Color.Red
        isNearLimit -> Color(0xFFFFA000) // Orange
        else -> Color(0xFF4CAF50) // Green
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val boxColor = try {
                        if (!item.category?.color.isNullOrEmpty()) Color(android.graphics.Color.parseColor(item.category!!.color)) else Color.Gray
                    } catch (e: Exception) {
                        Color.Gray
                    }

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(boxColor, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.category?.category_name ?: "Danh mục",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Đã chi: ${formatCurrency(spentAmount)}",
                    fontSize = 13.sp,
                    color = if (isOverBudget) Color.Red else Color.Unspecified
                )
                Text(
                    text = "Hạn mức: ${formatCurrency(limitAmount)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress.coerceAtMost(1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.2f),
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isOverBudget) {
                val overAmount = spentAmount - limitAmount
                Text(
                    text = "⚠️ Vượt hạn mức: ${formatCurrency(overAmount)}",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                val remaining = limitAmount - spentAmount
                Text(
                    text = "Còn lại: ${formatCurrency(remaining)}",
                    color = if (isNearLimit) Color(0xFFFFA000) else Color(0xFF4CAF50),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetDialog(
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (Int, Long) -> Unit
) {
    var amountStr by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thiết lập hạn mức tháng") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory?.category_name ?: "Chọn danh mục chi tiêu",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Danh mục") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.category_name) },
                                onClick = {
                                    selectedCategory = category
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Hạn mức tối đa (đ)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val amount = amountStr.toLongOrNull() ?: 0L
                selectedCategory?.let { onSave(it.category_id, amount) }
            }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

private fun formatCurrency(amount: Long): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}