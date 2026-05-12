package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WelcomeAuthScreen(
    onGuestLogin: () -> Unit,
    onHostLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C1810))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Park,
            contentDescription = null,
            tint = Color(0xFFF5EEE6),
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Grama Vasathi",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFF5EEE6)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose how you want to sign in",
            fontSize = 15.sp,
            color = Color(0xFFF5EEE6).copy(alpha = 0.85f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onGuestLogin),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = Color(0xFF2C1810),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Guest",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2C1810)
                    )
                    Text(
                        text = "Browse Discover, book stays, manage trips",
                        fontSize = 13.sp,
                        color = Color(0xFF7C5C3B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onHostLogin),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFEDE0D4),
            tonalElevation = 4.dp,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF2C1810),
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Host",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2C1810)
                    )
                    Text(
                        text = "Open Host readiness & listing checklist",
                        fontSize = 13.sp,
                        color = Color(0xFF7C5C3B)
                    )
                }
            }
        }
    }
}
