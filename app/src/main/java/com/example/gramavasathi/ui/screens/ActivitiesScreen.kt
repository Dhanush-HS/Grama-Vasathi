package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Brown700
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.ui.theme.Green700
import com.example.gramavasathi.viewmodel.ActivitiesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    viewModel: ActivitiesViewModel,
    onNavigateToDiscover: (String) -> Unit,
    onNavigateToCultureGuide: () -> Unit
) {
    val activities by viewModel.activities.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Experiences", fontWeight = FontWeight.Bold, color = Brown900) },
                actions = {
                    IconButton(onClick = onNavigateToCultureGuide) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Culture Guide",
                            tint = Green700
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Cream
                )
            )
        },
        containerColor = Cream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                text = "Farm experience",
                fontWeight = FontWeight.Bold,
                color = Brown900,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            Text(
                text = "Each card is an activity guests can book as part of rural life — cow milking, field plowing, local cooking, and more. Tap one to filter the Discover list to stays that offer it.",
                fontSize = 12.sp,
                color = Brown700,
                lineHeight = 17.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 12.dp)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            items(activities) { activity ->
                val isNatureActivity = activity.name in listOf("Birdwatching", "Trekking", "Lake Visit")
                val iconColor = if (isNatureActivity) Green700 else Brown700

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clickable { onNavigateToDiscover(activity.name) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Brown900.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (activity.icon != null) {
                            Icon(
                                imageVector = activity.icon,
                                contentDescription = activity.name,
                                tint = iconColor,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = activity.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Brown900,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${activity.count} stays offer this",
                            style = MaterialTheme.typography.bodySmall,
                            color = Brown700
                        )
                    }
                }
            }
            }
        }
    }
}
