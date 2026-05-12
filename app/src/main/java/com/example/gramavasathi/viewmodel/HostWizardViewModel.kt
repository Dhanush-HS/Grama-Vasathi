package com.example.gramavasathi.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.WizardProgress
import com.example.gramavasathi.domain.model.WizardStep
import com.example.gramavasathi.domain.repository.HostWizardRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HostWizardViewModel @Inject constructor(
    private val repository: HostWizardRepository
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val hostUid: String
        get() = auth.currentUser?.uid ?: "guest_host_placeholder"

    private val _progress = MutableStateFlow<WizardProgress?>(null)
    val progress: StateFlow<WizardProgress?> = _progress.asStateFlow()

    init {
        fetchProgress()
    }

    private fun fetchProgress() {
        viewModelScope.launch {
            repository.getProgress(hostUid)
                .catch { it.printStackTrace() }
                .collect { p ->
                    _progress.value = p.copy(hostUid = hostUid)
                }
        }
    }

    fun toggleChecklistItem(step: WizardStep, itemId: String) {
        val currentProgress = _progress.value ?: return
        val key = step.name
        val currentStepItems = currentProgress.stepProgress[key] ?: return

        val updatedItems = currentStepItems.map { item ->
            if (item.id == itemId) item.copy(completed = !item.completed) else item
        }

        val updatedStepProgress = currentProgress.stepProgress.toMutableMap()
        updatedStepProgress[key] = updatedItems

        val newProgress = currentProgress.copy(
            hostUid = hostUid,
            stepProgress = updatedStepProgress
        )

        _progress.value = newProgress

        viewModelScope.launch {
            repository.updateProgress(newProgress)
        }
    }
}
