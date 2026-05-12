package com.example.gramavasathi.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gramavasathi.domain.model.ChecklistItem
import com.example.gramavasathi.domain.model.WizardStep
import com.example.gramavasathi.ui.theme.Amber50
import com.example.gramavasathi.ui.theme.Amber700
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Green700
import com.example.gramavasathi.viewmodel.HostWizardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HostWizardScreen(viewModel: HostWizardViewModel) {
    val progressState by viewModel.progress.collectAsState()
    val wizardProgress = progressState ?: return
    
    val steps = WizardStep.values()
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val stepTips = listOf(
        "Clean linen is the first thing guests notice.",
        "A Western toilet boosts bookings by 40%.",
        "Bright photos get 3x more booking inquiries.",
        "Guests feel safer knowing emergency contacts.",
        "A handwritten welcome note is unforgettable."
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Guest Readiness", fontWeight = FontWeight.Bold, color = Brown900)
                        Text(
                            "Hospitality School — room, hygiene & guest care",
                            style = MaterialTheme.typography.labelSmall,
                            color = Brown900.copy(alpha = 0.65f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Amber50.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Work through each step like a checklist. Your Readiness Score shows how prepared you are before you welcome city guests.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Brown900
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            // Overall Progress Bar
            val animatedProgress by animateFloatAsState(targetValue = wizardProgress.readinessScore / 100f)
            
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Readiness Score", fontWeight = FontWeight.Bold, color = Brown900)
                    Text("${wizardProgress.readinessScore.toInt()}%", fontWeight = FontWeight.Bold, color = Green700)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = animatedProgress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Green700,
                    trackColor = Green700.copy(alpha = 0.2f)
                )
            }
            
            // Step Indicator Tabs
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = Brown900,
                edgePadding = 16.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = Green700,
                        height = 3.dp
                    )
                }
            ) {
                steps.forEachIndexed { index, step ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = step.title,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (pagerState.currentPage == index) Brown900 else Color.Gray
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Swipeable Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val currentStep = steps[page]
                val items = wizardProgress.stepProgress[currentStep.name].orEmpty()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items.forEach { item ->
                        ChecklistItemCard(
                            item = item,
                            onToggle = { viewModel.toggleChecklistItem(currentStep, item.id) }
                        )
                    }
                }
            }

            // Contextual Tip Card at bottom
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Amber50)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stepTips[pagerState.currentPage],
                        color = Brown900,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Button(
                onClick = {
                    val score = wizardProgress.readinessScore.toInt()
                    coroutineScope.launch {
                        if (score >= 100) {
                            snackbarHostState.showSnackbar("You’re fully guest-ready! We’ll review your listing for the verified badge.")
                        } else if (score >= 70) {
                            snackbarHostState.showSnackbar("Almost there — complete the remaining items to reach full readiness.")
                        } else {
                            snackbarHostState.showSnackbar("Complete more checklist items to improve your readiness score.")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Green700)
            ) {
                Text("Submit for verification", color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ChecklistItemCard(
    item: ChecklistItem,
    onToggle: () -> Unit
) {
    val borderColor = if (item.completed) Green700 else Amber700
    val iconTint = if (item.completed) Green700 else Amber700

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Border visual cue
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(borderColor)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Checkbox Icon
            Icon(
                imageVector = if (item.completed) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                contentDescription = "Status",
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Titles
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Brown900
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brown900.copy(alpha = 0.7f)
                )
            }
        }
    }
}
