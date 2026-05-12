package com.example.gramavasathi.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gramavasathi.ui.theme.Brown700
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onNavigateToBooking: (String) -> Unit,
    onNavigateToStayDetail: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDistrict by viewModel.selectedDistrict.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search directory", fontWeight = FontWeight.Bold, color = Brown900) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Brown900)
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
                text = "Firebase-backed searchable catalog — filter by district and find stays that match activities (cow milking, trekking, and more).",
                style = MaterialTheme.typography.bodySmall,
                color = Brown700,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search by village, district, or activity") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.districts) { district ->
                    FilterChip(
                        selected = selectedDistrict == district,
                        onClick = { viewModel.onDistrictSelected(district) },
                        label = { Text(if (district == "All") "All Districts" else district.split(",")[0]) }, // Show only city name
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.secondary,
                            selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Results List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (searchResults.isEmpty()) {
                    item {
                        Text(
                            text = "No farmstays found matching your criteria.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    items(searchResults) { farmstay ->
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
}
