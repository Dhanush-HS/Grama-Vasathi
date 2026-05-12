package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.gramavasathi.ui.theme.Brown700
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.ui.theme.Green700
import com.example.gramavasathi.viewmodel.StayDetailViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun StayDetailScreen(
    viewModel: StayDetailViewModel,
    onNavigateBack: () -> Unit,
    onBookStay: (String) -> Unit
) {
    val stay by viewModel.stay.collectAsState()
    val readiness by viewModel.hostReadinessScore.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stay?.name ?: "Stay", fontWeight = FontWeight.Bold, color = Brown900) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Brown900)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Cream)
            )
        },
        bottomBar = {
            if (stay != null) {
                Surface(shadowElevation = 8.dp, color = Cream) {
                    Button(
                        onClick = { onBookStay(stay!!.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Brown900),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Book this stay — ₹${stay!!.pricePerNight.toInt()} / night",
                            fontWeight = FontWeight.Bold,
                            color = Cream
                        )
                    }
                }
            }
        },
        containerColor = Cream
    ) { padding ->
        when {
            stay == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Brown900)
                }
            }
            else -> {
                val s = stay!!
                val galleryUrls = remember(s.id, s.imageUrl, s.imageGalleryUrls) {
                    buildList {
                        if (s.imageUrl.isNotBlank()) add(s.imageUrl)
                        s.imageGalleryUrls.forEach { u ->
                            if (u.isNotBlank() && !contains(u)) add(u)
                        }
                    }.ifEmpty { listOf("") }
                }
                val pagerState = rememberPagerState(pageCount = { galleryUrls.size })

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(galleryUrls.getOrNull(page))
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                                placeholder = ColorPainter(Color(0xFFEDE0D4)),
                                error = ColorPainter(Color(0xFFEDE0D4))
                            )
                        }
                        if (galleryUrls.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "${pagerState.currentPage + 1} / ${galleryUrls.size}",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    s.location,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Brown700
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        "${s.rating} · Guest rating",
                                        fontWeight = FontWeight.Medium,
                                        color = Brown900
                                    )
                                }
                            }
                            if (s.isVerified) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Green700.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = Green700,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Verified stay", color = Green700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        readiness?.let { score ->
                            HostReadinessCard(scorePercent = score)
                        }

                        Text(
                            "About this stay",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Brown900
                        )
                        Text(
                            text = s.description.ifBlank { "Authentic rural home-stay experience in Karnataka." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Brown900.copy(alpha = 0.85f),
                            lineHeight = 22.sp
                        )

                        Text(
                            "Amenities",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Brown900
                        )
                        FlowRowCompat(
                            items = s.amenities,
                            chipColor = Brown900.copy(alpha = 0.08f)
                        )

                        Text(
                            "Farm experiences",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Brown900
                        )
                        FlowRowCompat(
                            items = s.activities,
                            chipColor = Green700.copy(alpha = 0.12f),
                            textColor = Green700
                        )

                        HorizontalDivider(color = Brown900.copy(alpha = 0.12f))

                        Text(
                            "Hosted by ${s.hostName.ifBlank { "Local family" }}",
                            fontWeight = FontWeight.SemiBold,
                            color = Brown900
                        )
                        Text(
                            "Hosts listed here complete our Guest Readiness checklist — linen, safe water, hygiene, and clear expectations for city guests.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Brown700,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(88.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HostReadinessCard(scorePercent: Float) {
    val pct = scorePercent.coerceIn(0f, 100f).toInt()
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Green700.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                "Host readiness score",
                fontWeight = FontWeight.Bold,
                color = Brown900,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "From the hospitality training checklist (room setup, hygiene, safety). Higher means better prepared for guests.",
                fontSize = 12.sp,
                color = Brown700,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$pct%", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Green700)
                LinearProgressIndicator(
                    progress = pct / 100f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Green700,
                    trackColor = Green700.copy(alpha = 0.2f),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRowCompat(
    items: List<String>,
    chipColor: Color,
    textColor: Color = Brown900
) {
    if (items.isEmpty()) {
        Text("—", color = Brown700, fontSize = 13.sp)
        return
    }
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { label ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = chipColor
            ) {
                Text(
                    label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
