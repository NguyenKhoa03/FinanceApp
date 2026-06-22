package com.example.financeapp.ui.screens.statistics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.financeapp.FinanceApplication
import com.example.financeapp.viewmodel.CategorySummary
import com.example.financeapp.viewmodel.MonthlySummary
import com.example.financeapp.viewmodel.StatisticsViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun StatisticsScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as FinanceApplication

    // 🛠️ ĐÃ SỬA: Thay thế hoàn toàn StatisticsViewModelFactory bằng inline viewModelFactory chuẩn Jetpack Compose
    val viewModel: StatisticsViewModel = viewModel(
        factory = viewModelFactory {
            initializer {
                StatisticsViewModel(
                    app.database.transactionDao(),
                    app.database.categoryDao()
                )
            }
        }
    )

    val categorySummaries by viewModel.categorySummaries.collectAsState()
    val yearlySummaries by viewModel.yearlySummaries.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Text(
                text = "Thống kê phân tích 📊",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        // Biểu đồ tròn theo danh mục
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Chi tiêu theo danh mục (Tháng ${selectedMonth + 1})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (categorySummaries.isEmpty()) {
                        Box(modifier = Modifier.height(200.dp).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Không có dữ liệu chi tiêu", color = Color.Gray)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SimplePieChart(
                                summaries = categorySummaries,
                                modifier = Modifier.size(150.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                categorySummaries.take(5).forEach { summary ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .background(parseHexColor(summary.category?.color), RoundedCornerShape(2.dp))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${summary.category?.category_name ?: "Khác"}: ${(summary.percentage * 100).toInt()}%",
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Biểu đồ cột so sánh Thu/Chi theo năm
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "So sánh Thu - Chi năm $selectedYear",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    SimpleBarChart(summaries = yearlySummaries)
                }
            }
        }

        // Danh sách chi tiết danh mục
        items(categorySummaries) { summary ->
            CategorySummaryItem(summary)
        }
    }
}

@Composable
fun SimplePieChart(summaries: List<CategorySummary>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        var startAngle = -90f
        summaries.forEach { summary ->
            val sweepAngle = summary.percentage * 360f
            drawArc(
                color = parseHexColor(summary.category?.color),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )
            startAngle += sweepAngle
        }
        drawCircle(color = Color.White, radius = size.minDimension / 4f)
    }
}

@Composable
fun SimpleBarChart(summaries: List<MonthlySummary>) {
    val maxVal = summaries.maxOfOrNull { maxOf(it.income.toDouble(), it.expense.toDouble()) } ?: 1.0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        summaries.forEach { summary ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.height(160.dp)) {
                    // Cột Thu (Xanh)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight((summary.income.toDouble() / maxVal).toFloat().coerceIn(0.01f, 1f))
                            .background(Color(0xFF4CAF50), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    // Cột Chi (Đỏ)
                    Box(
                        modifier = Modifier
                            .width(8.dp)
                            .fillMaxHeight((summary.expense.toDouble() / maxVal).toFloat().coerceIn(0.01f, 1f))
                            .background(Color(0xFFF44336), RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp))
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = summary.monthName, fontSize = 10.sp, color = Color.Gray)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
        LegendItem(color = Color(0xFF4CAF50), label = "Thu nhập")
        Spacer(modifier = Modifier.width(16.dp))
        LegendItem(color = Color(0xFFF44336), label = "Chi tiêu")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp)
    }
}

@Composable
fun CategorySummaryItem(summary: CategorySummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(parseHexColor(summary.category?.color), CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = summary.category?.category_name ?: "Khác", fontWeight = FontWeight.Medium)
        }
        Text(
            // 🛠️ ĐÃ SỬA: Ép kiểu tổng tiền sang Double (.toDouble()) để khớp với hàm định dạng tiền tệ hiện tại của bạn
            text = formatCurrency(summary.totalAmount.toDouble()),
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

fun parseHexColor(hexColor: String?): Color {
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

// Hàm nhận kiểu số thực Double để format tiền tệ
private fun formatCurrency(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
    return format.format(amount)
}