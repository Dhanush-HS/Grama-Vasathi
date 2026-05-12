package com.example.gramavasathi.domain.model

import com.google.firebase.firestore.IgnoreExtraProperties

enum class WizardStep(val title: String) {
    BASIC_SETUP("Basic Setup"),
    FACILITIES("Facilities"),
    MEDIA("Media"),
    SAFETY_FOOD("Safety & Food"),
    PRICING("Pricing")
}

/**
 * Use [completed] (not `isCompleted`): Firestore's Kotlin mapper often fails to round-trip
 * boolean properties whose JVM getter starts with `is`, which breaks checklist toggles.
 */
data class ChecklistItem(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val extraInfo: String? = null,
    val completed: Boolean = false
)

/**
 * Firestore-friendly progress: map keys are [WizardStep.name] strings (not enum keys).
 */
@IgnoreExtraProperties
data class WizardProgress(
    val hostUid: String = "",
    val stepProgress: Map<String, List<ChecklistItem>> = emptyMap()
) {
    val readinessScore: Float
        get() {
            var completed = 0
            var total = 0
            stepProgress.values.forEach { items ->
                items.forEach { item ->
                    total++
                    if (item.completed) completed++
                }
            }
            return if (total == 0) 0f else (completed.toFloat() / total.toFloat()) * 100f
        }
}
