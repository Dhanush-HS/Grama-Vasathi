package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.ui.theme.Brown200
import com.example.gramavasathi.viewmodel.BookingState
import com.example.gramavasathi.viewmodel.BookingViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSuccess: (stayName: String, dateRange: String, totalPaid: String, hostName: String) -> Unit
) {
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            onNavigateToLogin()
        }
        return
    }

    val stay by viewModel.stay.collectAsState()
    val checkInDate by viewModel.checkInDate.collectAsState()
    val checkOutDate by viewModel.checkOutDate.collectAsState()
    val totalCost by viewModel.totalCost.collectAsState()
    val numberOfNights by viewModel.numberOfNights.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()

    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(bookingState) {
        when (bookingState) {
            is BookingState.Success -> {
                val formatter = DateTimeFormatter.ofPattern("d MMM")
                val range = "${checkInDate!!.format(formatter)} → ${checkOutDate!!.format(formatter)} ${checkOutDate!!.year}"
                onNavigateToSuccess(stay!!.name, range, "₹$totalCost", stay!!.hostName)
            }
            is BookingState.Error -> snackbarHostState.showSnackbar((bookingState as BookingState.Error).message)
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Reserve your stay", fontWeight = FontWeight.Bold)
                        Text(
                            "Simple calendar booking (guest flow)",
                            fontSize = 11.sp,
                            color = Brown900.copy(alpha = 0.65f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream,
                    titleContentColor = Brown900,
                    navigationIconContentColor = Brown900
                )
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        if (stay == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Brown900)
            }
            return@Scaffold
        }

        val s = stay!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // SECTION 1: HOST INFO ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(s.imageUrl.ifBlank { "https://via.placeholder.com/80" })
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = "Stay Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brown200),
                    placeholder = ColorPainter(Color(0xFFEDE0D4)),
                    error = ColorPainter(Color(0xFFEDE0D4))
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = s.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Brown900
                        )
                        if (s.verified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "Hosted by ${s.hostName}",
                        fontSize = 14.sp,
                        color = Brown900.copy(alpha = 0.7f)
                    )
                }
                Text(
                    text = "₹${s.pricePerNight.toInt()} / night",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Brown900
                )
            }

            HorizontalDivider(color = Brown200.copy(alpha = 0.3f), thickness = 1.dp)

            val galleryUrls = remember(s.id, s.imageGalleryUrls, s.imageUrl) {
                if (s.imageGalleryUrls.isNotEmpty()) s.imageGalleryUrls
                else listOfNotNull(s.imageUrl.takeIf { it.isNotBlank() })
            }
            if (galleryUrls.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Stay gallery",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Brown900
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(galleryUrls) { url ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(width = 120.dp, height = 80.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                placeholder = ColorPainter(Color(0xFFEDE0D4)),
                                error = ColorPainter(Color(0xFFEDE0D4))
                            )
                        }
                    }
                }
                HorizontalDivider(color = Brown200.copy(alpha = 0.3f), thickness = 1.dp)
            }

            // SECTION 2: CALENDAR
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Choose dates",
                    fontWeight = FontWeight.Bold,
                    color = Brown900,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Unavailable days are marked from the host calendar. Tap check-in, then check-out.",
                    fontSize = 12.sp,
                    color = Brown900.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Month Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Previous Month", tint = Brown900)
                    }
                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Brown900
                    )
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Next Month", tint = Brown900)
                    }
                }

                // Days of week header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                        Text(
                            text = day,
                            color = Brown900.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Days Grid
                val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7
                val daysInMonth = currentMonth.lengthOfMonth()
                val totalCells = firstDayOfWeek + daysInMonth

                LazyVerticalGrid(
                    columns = GridCells.Fixed(7),
                    modifier = Modifier.height(260.dp),
                    userScrollEnabled = false
                ) {
                    items(totalCells) { index ->
                        if (index < firstDayOfWeek) {
                            Spacer(modifier = Modifier.size(40.dp))
                        } else {
                            val dayOfMonth = index - firstDayOfWeek + 1
                            val date = currentMonth.atDay(dayOfMonth)
                            val isUnavailable = s.unavailableDates.contains(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))) || date.isBefore(LocalDate.now())
                            val isCheckIn = date == checkInDate
                            val isCheckOut = date == checkOutDate
                            val isBetween = checkInDate != null && checkOutDate != null && date.isAfter(checkInDate) && date.isBefore(checkOutDate)
                            val isToday = date == LocalDate.now()

                            val bgColor = when {
                                isCheckIn || isCheckOut -> Brown900
                                isBetween -> Color(0xFFEDE0D4)
                                else -> Color.Transparent
                            }
                            val textColor = when {
                                isCheckIn || isCheckOut -> Cream
                                isUnavailable -> Color.Gray
                                isToday -> Color(0xFF4CAF50)
                                else -> Brown900
                            }

                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(if (isCheckIn || isCheckOut) CircleShape else RoundedCornerShape(4.dp))
                                    .background(bgColor)
                                    .clickable(enabled = !isUnavailable) {
                                        viewModel.onDateSelected(date)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    color = textColor,
                                    fontWeight = if (isToday || isCheckIn || isCheckOut) FontWeight.Bold else FontWeight.Normal,
                                    textDecoration = if (isUnavailable && !date.isBefore(LocalDate.now())) TextDecoration.LineThrough else null
                                )
                            }
                        }
                    }
                }
            }

            // SECTION 3: BOOKING SUMMARY CARD
            if (checkInDate != null && checkOutDate != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .border(0.5.dp, Brown900.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = Cream),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(s.name, fontWeight = FontWeight.Bold, color = Brown900, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Check-in", color = Brown900.copy(alpha = 0.7f))
                            Text(checkInDate!!.format(formatter), color = Brown900, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Check-out", color = Brown900.copy(alpha = 0.7f))
                            Text(checkOutDate!!.format(formatter), color = Brown900, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("$numberOfNights nights × ₹${s.pricePerNight.toInt()}", color = Brown900)
                            Text("₹${(numberOfNights * s.pricePerNight).toInt()}", color = Brown900)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Activities fee", color = Brown900)
                            Text("₹500", color = Brown900)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Brown200)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Brown900)
                            Text("₹$totalCost", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Brown900)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SECTION 4: CONFIRM BUTTON
            Button(
                onClick = { viewModel.confirmBooking() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                enabled = checkInDate != null && checkOutDate != null && bookingState != BookingState.Loading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Brown900,
                    disabledContainerColor = Color.Gray
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (bookingState == BookingState.Loading) {
                    CircularProgressIndicator(color = Cream, modifier = Modifier.size(24.dp))
                } else {
                    val btnText = if (checkInDate != null && checkOutDate != null) "Confirm Booking — ₹$totalCost" else "Select Dates"
                    Text(btnText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Cream)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
