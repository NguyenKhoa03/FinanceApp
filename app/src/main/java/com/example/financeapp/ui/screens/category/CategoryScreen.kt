package com.example.financeapp.ui.screens.category

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.FinanceApplication
import com.example.financeapp.data.local.entity.CategoryEntity
import com.example.financeapp.ui.theme.CategoryColors
import com.example.financeapp.viewmodel.CategoryViewModel
import com.example.financeapp.viewmodel.CategoryViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication
    val viewModel: CategoryViewModel = viewModel(
        factory = CategoryViewModelFactory(app.categoryRepository)
    )

    val categories by viewModel.allCategories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý danh mục") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm danh mục")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onDelete = { viewModel.deleteCategory(category) }
                )
            }
        }
    }

    if (showAddDialog) {
        AddCategoryDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, type, color ->
                viewModel.addCategory(name, type, color.toArgb())
                showAddDialog = false
            }
        )
    }
}

@Composable
fun CategoryItem(category: CategoryEntity, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(Color(category.color), CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = category.name, fontWeight = FontWeight.Bold)
                    Text(
                        text = if (category.type == "income") "Thu nhập" else "Chi tiêu",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Color) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("expense") }
    var selectedColor by remember { mutableStateOf(CategoryColors.palette[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thêm danh mục mới") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Tên danh mục") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = type == "expense", onClick = { type = "expense" })
                    Text("Chi tiêu")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = type == "income", onClick = { type = "income" })
                    Text("Thu nhập")
                }

                Text("Chọn màu sắc:", style = MaterialTheme.typography.bodyMedium)
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier.height(150.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(CategoryColors.palette) { color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, CircleShape)
                                .border(
                                    width = if (selectedColor == color) 2.dp else 0.dp,
                                    color = if (selectedColor == color) Color.Black else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onSave(name, type, selectedColor) }) {
                Text("Lưu")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}