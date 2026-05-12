package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gramavasathi.domain.model.Booking
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Brown700
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.ui.theme.Green700
import com.example.gramavasathi.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isGuestFlow: Boolean,
    onNavigateToDiscover: () -> Unit,
    onSwitchToHost: () -> Unit,
    onSwitchToGuest: () -> Unit,
    onSignOut: () -> Unit
) {
    val guest by viewModel.guestProfile.collectAsState()
    val bookings by viewModel.pastBookings.collectAsState()
    val staysCount by viewModel.staysCount.collectAsState()
    val savedCount by viewModel.savedCount.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentUser = viewModel.currentUser
    val snackbarHostState = remember { SnackbarHostState() }

    var showCancelDialog by remember { mutableStateOf(false) }
    var bookingToCancel by remember { mutableStateOf("") }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrBlank()) {
            snackbarHostState.showSnackbar(errorMessage!!)
        }
    }

    // Compute Initials
    val displayName = currentUser?.displayName ?: guest?.name ?: "Guest"
    val initials = displayName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("").take(2)
    val city = guest?.homeCity?.takeIf { it.isNotBlank() } ?: "Bengaluru, Karnataka"

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = Brown900) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream
                )
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        if (showCancelDialog) {
            AlertDialog(
                onDismissRequest = { showCancelDialog = false },
                title = {
                    Text(
                        "Cancel Booking",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C1810)
                    )
                },
                text = {
                    Text(
                        "Are you sure you want to cancel this booking? This cannot be undone.",
                        color = Color(0xFF7C5C3B)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.cancelBooking(bookingToCancel)
                            showCancelDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFA32D2D)
                        )
                    ) {
                        Text("Yes, Cancel", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showCancelDialog = false }
                    ) {
                        Text("Keep Booking", color = Color(0xFF2C1810))
                    }
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // USER INFO SECTION
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Brown900),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (initials.isNotEmpty()) initials else "?",
                            color = Cream,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Brown900
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = city,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Brown700
                    )
                }
            }

            // STATS ROW
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatBox(modifier = Modifier.weight(1f), count = staysCount.toString(), label = "Stays")
                    StatBox(modifier = Modifier.weight(1f), count = savedCount.toString(), label = "Saved")
                    StatBox(modifier = Modifier.weight(1f), count = "4.9", label = "Rating")
                }
            }

            if (isGuestFlow) {
                // PAST BOOKINGS LIST
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "My past stays",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Brown900,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (bookings.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = null,
                                modifier = Modifier.size(72.dp),
                                tint = Color(0xFFEDE0D4)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No stays yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2C1810)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your booking history will appear here",
                                fontSize = 13.sp,
                                color = Color(0xFF7C5C3B),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(
                                onClick = onNavigateToDiscover,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2C1810)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    "Discover Stays",
                                    color = Color(0xFFF5EEE6),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    items(bookings.size) { idx ->
                        val booking = bookings[idx]
                        BookingCard(
                            booking = booking,
                            onCancelClick = { bookingId ->
                                bookingToCancel = bookingId
                                showCancelDialog = true
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    OutlinedButton(
                        onClick = onSwitchToHost,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Brown900),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Brown900)
                    ) {
                        Text("Switch to Host Mode", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(0.5.dp, Brown900.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Host mode",
                                fontWeight = FontWeight.Bold,
                                color = Brown900,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Use Readiness in the tab bar to manage your listing. Switch to Guest when you want to browse and book stays.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Brown700
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = onSwitchToGuest,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Brown900)
                            ) {
                                Text("Switch to Guest Mode", color = Cream, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // SIGN OUT BUTTON
            item {
                Spacer(modifier = Modifier.height(32.dp))
                TextButton(
                    onClick = {
                        viewModel.signOut()
                        onSignOut()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sign Out",
                        color = Color(0xFFD32F2F), // Muted red
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StatBox(modifier: Modifier = Modifier, count: String, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Cream),
        border = BorderStroke(0.5.dp, Brown900.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Brown900
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Brown700
            )
        }
    }
}

@Composable
fun BookingCard(
    booking: Booking,
    onCancelClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // Top row — image + info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Stay image
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(booking.stayImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = booking.stayName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    placeholder = ColorPainter(Color(0xFFEDE0D4)),
                    error = ColorPainter(Color(0xFFEDE0D4))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Stay name
                    Text(
                        text = booking.stayName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF2C1810)
                    )
                    // Location
                    Text(
                        text = booking.stayLocation,
                        fontSize = 12.sp,
                        color = Color(0xFF7C5C3B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Dates
                    Text(
                        text = "${booking.checkIn} → ${booking.checkOut}",
                        fontSize = 11.sp,
                        color = Color(0xFF7C5C3B)
                    )
                    // Nights
                    Text(
                        text = "${booking.nights} nights",
                        fontSize = 11.sp,
                        color = Color(0xFF7C5C3B)
                    )
                }

                // Status badge
                val statusColor = when (booking.status) {
                    "confirmed" -> Color(0xFF3B6D11)
                    "pending" -> Color(0xFFBA7517)
                    "cancelled" -> Color(0xFFA32D2D)
                    else -> Color.Gray
                }
                val statusBg = when (booking.status) {
                    "confirmed" -> Color(0xFFEAF3DE)
                    "pending" -> Color(0xFFFAEEDA)
                    "cancelled" -> Color(0xFFFCEBEB)
                    else -> Color.LightGray
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = booking.status.replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Divider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = Color(0xFFEDE0D4)
            )

            // Bottom row — total + cancel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total paid",
                        fontSize = 11.sp,
                        color = Color(0xFF7C5C3B)
                    )
                    Text(
                        text = "₹${booking.totalCost}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B6D11)
                    )
                }

                // Cancel button — only show if status is confirmed or pending
                if (booking.status != "cancelled") {
                    OutlinedButton(
                        onClick = { onCancelClick(booking.id) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFA32D2D)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFA32D2D)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(
                            horizontal = 14.dp,
                            vertical = 6.dp
                        )
                    ) {
                        Text(
                            "Cancel Booking",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    Text(
                        text = "Booking cancelled",
                        fontSize = 12.sp,
                        color = Color(0xFFA32D2D)
                    )
                }
            }
        }
    }
}
