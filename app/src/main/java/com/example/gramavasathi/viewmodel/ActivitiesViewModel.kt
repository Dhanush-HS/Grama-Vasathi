package com.example.gramavasathi.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.Pets
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.ActivityItem
import com.example.gramavasathi.domain.repository.FarmstayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivitiesViewModel @Inject constructor(
    private val repository: FarmstayRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityItem>>(emptyList())
    val activities: StateFlow<List<ActivityItem>> = _activities.asStateFlow()

    private val activityList = listOf(
        "Cow Milking", "Field Plowing", "Local Cooking", "Birdwatching",
        "Coffee Picking", "Pottery", "Trekking", "Organic Farming"
    )

    init {
        fetchActivityCounts()
    }

    private fun fetchActivityCounts() {
        viewModelScope.launch {
            repository.getFarmstays("All").collect { farmstays ->
                val counts = activityList.associateWith { activityName ->
                    farmstays.count { it.activities.contains(activityName) }
                }

                val mappedItems = counts.map { (name, count) ->
                    ActivityItem(
                        name = name,
                        count = count,
                        icon = getIconForActivity(name)
                    )
                }
                _activities.value = mappedItems
            }
        }
    }

    private fun getIconForActivity(name: String) = when (name) {
        "Cow Milking"     -> Icons.Default.Pets
        "Field Plowing"   -> Icons.Default.Agriculture
        "Local Cooking"   -> Icons.Default.LocalDining
        "Birdwatching"    -> Icons.Default.Forest
        "Coffee Picking"  -> Icons.Default.Coffee
        "Pottery"         -> Icons.Default.Eco
        "Trekking"        -> Icons.Default.DirectionsWalk
        "Organic Farming" -> Icons.Default.Eco
        else              -> Icons.Default.Eco
    }
}