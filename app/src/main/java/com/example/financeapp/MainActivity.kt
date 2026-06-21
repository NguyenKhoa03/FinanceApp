package com.example.financeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.financeapp.ui.navigation.AppNavGraph
import com.example.financeapp.ui.theme.FinanceAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Bật tính năng tràn viền hệ thống
        setContent {
            FinanceAppTheme {
                // 🛠️ GỢI Ý THÊM: Sử dụng Surface để làm nền chuẩn cho toàn bộ các màn hình con,
                // tránh hiện tượng nháy nền trắng/đen khi chuyển màn hình (Transition).
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavGraph()
                }
            }
        }
    }
}