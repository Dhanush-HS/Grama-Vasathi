package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.Farmstay
import com.example.gramavasathi.domain.repository.FarmstayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: FarmstayRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDistrict = MutableStateFlow("All")
    val selectedDistrict: StateFlow<String> = _selectedDistrict.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Farmstay>>(emptyList())
    val searchResults: StateFlow<List<Farmstay>> = _searchResults.asStateFlow()

    val districts = listOf("All", "Ooty, Tamil Nadu", "Coorg, Karnataka", "Wayanad, Kerala") // Simulating districts via the location string

    init {
        performSearch()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        performSearch()
    }

    fun onDistrictSelected(district: String) {
        _selectedDistrict.value = district
        performSearch()
    }

    private fun performSearch() {
        viewModelScope.launch {
            repository.searchFarmstays(_searchQuery.value, _selectedDistrict.value).collect { results ->
                _searchResults.value = results
            }
        }
    }
}
