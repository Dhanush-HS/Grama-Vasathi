package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.gramavasathi.domain.model.Farmstay
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Brown700
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.ui.theme.Green700
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.gramavasathi.navigation.DiscoverFilterHolder
import com.example.gramavasathi.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToStayDetail: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {}
) {
    val farmstays by viewModel.farmstays.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                DiscoverFilterHolder.consumePendingFilter()?.let { filter ->
                    viewModel.setFilter(filter)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filters = listOf(
        "All", "Birdwatching", "Cow Milking",
        "Local Cooking", "Field Plowing", "Pottery", "Trekking"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Grama Vasathi",
                            fontWeight = FontWeight.Bold,
                            color = Cream,
                            fontSize = 24.sp
                        )
                        Text(
                            "Rural Home-stay Accelerator",
                            color = Cream.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Brown900
                ),
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search stays",
                            tint = Cream
                        )
                    }
                }
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { viewModel.setFilter(filter) },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Brown900,
                            selectedLabelColor = Cream,
                            containerColor = Color.White,
                            labelColor = Brown900
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (selectedFilter == filter)
                                Color.Transparent
                            else
                                Brown900.copy(alpha = 0.5f),
                            enabled = true,
                            selected = selectedFilter == filter
                        )
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        border = BorderStroke(1.dp, Brown900.copy(alpha = 0.12f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Verified catalog — Matti-Vasane",
                                fontWeight = FontWeight.Bold,
                                color = Brown900,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Browse authentic farm-stays with hygiene-ready hosts. Filter by farm experience, then book with the simple calendar.",
                                fontSize = 12.sp,
                                color = Brown700,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
                items(farmstays) { farmstay ->
                    FarmstayCard(
                        farmstay = farmstay,
                        onBookClick = { onNavigateToBooking(farmstay.id) },
                        onOpenDetail = { onNavigateToStayDetail(farmstay.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FarmstayCard(
    farmstay: Farmstay,
    onBookClick: () -> Unit,
    onOpenDetail: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenDetail() }
            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(farmstay.imageUrl)
                        .crossfade(true)
                        .diskCachePolicy(CachePolicy.ENABLED)
                        .memoryCachePolicy(CachePolicy.ENABLED)
                        .build(),
                    contentDescription = farmstay.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    placeholder = ColorPainter(Color(0xFFEDE0D4)),
                    error = ColorPainter(Color(0xFFEDE0D4))
                )
                
                // Star Rating Badge (Top-Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.9f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFB300),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = farmstay.rating.toString(),
                            color = Brown900,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Verified Badge (Top-Right)
                if (farmstay.isVerified) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Green700.copy(alpha = 0.9f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Verified",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Verified",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            val galleryUrls = remember(farmstay.id, farmstay.imageGalleryUrls, farmstay.imageUrl) {
                if (farmstay.imageGalleryUrls.isNotEmpty()) farmstay.imageGalleryUrls
                else listOfNotNull(farmstay.imageUrl.takeIf { it.isNotBlank() })
            }
            if (galleryUrls.size > 1) {
                StayGalleryStrip(urls = galleryUrls)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = farmstay.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Brown900
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = farmstay.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Brown700
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Activity Pill Badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    farmstay.activities.take(3).forEach { activity ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Green700.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = activity,
                                color = Green700,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
            }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₹${farmstay.pricePerNight.toInt()} / night",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Brown900
                    )
                    Button(
                        onClick = onBookClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Brown900
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                    ) {
                        Text(
                            "Book Now",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Cream
                        )
                    }
                }
        }
    }
}

@Composable
private fun StayGalleryStrip(urls: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = "Stay gallery",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Brown700,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(urls.size) { i ->
                val url = urls[i]
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
                        .size(width = 96.dp, height = 64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    placeholder = ColorPainter(Color(0xFFEDE0D4)),
                    error = ColorPainter(Color(0xFFEDE0D4))
                )
            }
        }
    }
}