package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.Farmstay
import com.example.gramavasathi.domain.repository.FarmstayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FarmstayRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow("All")
    val selectedFilter: StateFlow<String> = 
        _selectedFilter.asStateFlow()

    private val _farmstays = MutableStateFlow<List<Farmstay>>(emptyList())
    val farmstays: StateFlow<List<Farmstay>> = 
        _farmstays.asStateFlow()

    init {
        fetchFarmstays()
    }

    fun setFilter(filter: String) {
        _selectedFilter.value = filter
        fetchFarmstays()
    }

    private fun fetchFarmstays() {
        viewModelScope.launch {
            repository.getFarmstays(_selectedFilter.value)
                .collect { stays ->
                    _farmstays.value = stays
                }
        }
    }
}
