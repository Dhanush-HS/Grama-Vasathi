package com.example.gramavasathi.domain.repository

import com.example.gramavasathi.domain.model.WizardProgress
import kotlinx.coroutines.flow.Flow

interface HostWizardRepository {
    fun getProgress(hostUid: String): Flow<WizardProgress>
    suspend fun updateProgress(progress: WizardProgress)
}
