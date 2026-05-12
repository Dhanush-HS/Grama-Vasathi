package com.example.gramavasathi.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gramavasathi.domain.model.Farmstay
import com.example.gramavasathi.domain.repository.FarmstayRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StayDetailViewModel @Inject constructor(
    private val repository: FarmstayRepository,
    private val firestore: FirebaseFirestore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val stayId: String = savedStateHandle.get<String>("stayId") ?: ""

    private val _stay = MutableStateFlow<Farmstay?>(null)
    val stay: StateFlow<Farmstay?> = _stay.asStateFlow()

    /** Host Readiness Score (0–100) from `hosts/{hostUid}` when the host has trained on the wizard. */
    private val _hostReadinessScore = MutableStateFlow<Float?>(null)
    val hostReadinessScore: StateFlow<Float?> = _hostReadinessScore.asStateFlow()

    private var hostListener: ListenerRegistration? = null

    init {
        if (stayId.isNotEmpty()) {
            viewModelScope.launch {
                repository.getFarmstayById(stayId)
                    .catch { it.printStackTrace() }
                    .collect { s ->
                        _stay.value = s
                        attachHostListener(s?.hostUid.orEmpty())
                    }
            }
        }
    }

    private fun attachHostListener(hostUid: String) {
        hostListener?.remove()
        hostListener = null
        if (hostUid.isBlank()) {
            _hostReadinessScore.value = null
            return
        }
        val ref = firestore.collection("hosts").document(hostUid)
        hostListener = ref.addSnapshotListener { snap, err ->
            if (err != null || snap == null || !snap.exists()) {
                _hostReadinessScore.value = null
                return@addSnapshotListener
            }
            val raw = snap.get("readinessScore")
            val score = when (raw) {
                is Number -> raw.toFloat()
                null -> null
                else -> null
            }
            _hostReadinessScore.value = score
        }
    }

    override fun onCleared() {
        hostListener?.remove()
        hostListener = null
        super.onCleared()
    }
}
