package com.example.gramavasathi.domain.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ActivityItem(
    val name: String,
    val count: Int,
    val icon: ImageVector? = null
)
