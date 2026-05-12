package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Cream

@Composable
fun BookingSuccessScreen(
    stayName: String,
    dateRange: String,
    totalPaid: String,
    hostName: String,
    onViewBookings: () -> Unit,
    onDiscoverMore: () -> Unit
) {
    Scaffold(
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(72.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Booking Confirmed!",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = Brown900
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Brown900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dateRange,
                        fontSize = 16.sp,
                        color = Brown900.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = totalPaid,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Hosted by $hostName",
                        fontSize = 14.sp,
                        color = Brown900.copy(alpha = 0.6f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onViewBookings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Brown900),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("View My Bookings", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Cream)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onDiscoverMore,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Discover More Stays", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Brown900)
            }
        }
    }
}
