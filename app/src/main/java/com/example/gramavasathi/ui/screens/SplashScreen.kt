package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Park
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.gramavasathi.viewmodel.SplashViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        onNavigate(viewModel.resolveNextRoute())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C1810)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Park,
                contentDescription = null,
                tint = Color(0xFFF5EEE6),
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Grama Vasathi",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF5EEE6)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Rural Home-stay Accelerator",
                fontSize = 14.sp,
                color = Color(0xFFEDE0D4)
            )
            Spacer(modifier = Modifier.height(48.dp))
            CircularProgressIndicator(
                color = Color(0xFF7C5C3B),
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.dp
            )
        }
    }
}
