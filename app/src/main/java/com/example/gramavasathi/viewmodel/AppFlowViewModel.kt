package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.data.preferences.UserFlowPreferences
import com.example.gramavasathi.navigation.AppFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppFlowViewModel @Inject constructor(
    private val preferences: UserFlowPreferences
) : ViewModel() {

    val appFlow: StateFlow<AppFlow> = preferences.appFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppFlow.Guest)

    fun switchToGuest() {
        viewModelScope.launch { preferences.setAppFlow(AppFlow.Guest) }
    }

    fun switchToHost() {
        viewModelScope.launch { preferences.setAppFlow(AppFlow.Host) }
    }
}
