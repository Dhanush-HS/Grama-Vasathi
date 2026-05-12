package com.example.gramavasathi.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gramavasathi.ui.theme.Brown900
import com.example.gramavasathi.ui.theme.Brown700
import com.example.gramavasathi.ui.theme.Cream
import com.example.gramavasathi.ui.theme.Green700

data class GuideSection(
    val title: String,
    val content: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CultureGuideScreen(onNavigateBack: () -> Unit) {
    val sections = listOf(
        GuideSection(
            title = "Greeting Locals",
            content = "Say 'Namaskara' with your hands folded. It shows respect and is highly appreciated by the locals.",
            icon = Icons.Default.PanTool
        ),
        GuideSection(
            title = "Dress Code",
            content = "Modest clothing is recommended. Avoid overly revealing clothes, especially when visiting temples or local village centers.",
            icon = Icons.Default.Checkroom
        ),
        GuideSection(
            title = "Meal Etiquette",
            content = "Food is often served on banana leaves and eaten with the right hand. Always wash your hands before and after meals. Wasting food is considered disrespectful.",
            icon = Icons.Default.Restaurant
        ),
        GuideSection(
            title = "Photography Rules",
            content = "Always ask for permission before taking photos of locals, their children, or their homes. Photography inside the inner sanctum of most temples is strictly prohibited.",
            icon = Icons.Default.CameraAlt
        ),
        GuideSection(
            title = "Kannada Phrases",
            content = "• Hello: Namaskara\n• Thank you: Dhanyavadagalu\n• How are you?: Hegiddira?\n• Yes: Houdhu / No: Illa\n• Water: Neeru",
            icon = Icons.Default.Translate
        ),
        GuideSection(
            title = "The Do-Not List",
            content = "• Do not smoke or drink alcohol in public village areas.\n• Do not enter temples or homes with your shoes on.\n• Do not point your feet at people or religious items.\n• Do not litter the pristine village surroundings.",
            icon = Icons.Default.Warning
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Culture Guide", fontWeight = FontWeight.Bold, color = Brown900) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cultural guide for guests",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Brown900
            )
            Text(
                text = "How to greet locals, what to wear, and village etiquette — so your rural visit stays warm and respectful.",
                style = MaterialTheme.typography.bodyMedium,
                color = Brown700
            )
            Spacer(modifier = Modifier.height(8.dp))

            sections.forEach { section ->
                ExpandableGuideCard(section)
            }
        }
    }
}

@Composable
fun ExpandableGuideCard(section: GuideSection) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Green700),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = section.icon,
                        contentDescription = section.title,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = section.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Brown900,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand",
                    tint = Brown900
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Brown900.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = section.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Brown900,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
