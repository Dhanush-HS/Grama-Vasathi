package com.example.gramavasathi.data.repository

import com.example.gramavasathi.domain.model.ChecklistItem
import com.example.gramavasathi.domain.model.WizardProgress
import com.example.gramavasathi.domain.model.WizardStep
import com.example.gramavasathi.domain.repository.HostWizardRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HostWizardRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HostWizardRepository {

    private val defaultStepProgress: Map<String, List<ChecklistItem>> = mapOf(
        WizardStep.BASIC_SETUP.name to listOf(
            ChecklistItem("bs1", "Clean bedsheets & towels", "Fresh linen for every guest"),
            ChecklistItem("bs2", "Safe drinking water", "Filter or sealed bottles provided")
        ),
        WizardStep.FACILITIES.name to listOf(
            ChecklistItem("f1", "Western toilet available", "Clean and functional"),
            ChecklistItem("f2", "Mosquito nets / repellent", "Essential for rural stays")
        ),
        WizardStep.MEDIA.name to listOf(
            ChecklistItem("m1", "Room photo gallery", "Upload minimum 3 photos"),
            ChecklistItem("m2", "Activity schedule listed", "What guests can do on your farm")
        ),
        WizardStep.SAFETY_FOOD.name to listOf(
            ChecklistItem("sf1", "Emergency contact displayed", "Nearest hospital and police"),
            ChecklistItem("sf2", "Food menu shared", "Meals included or optional")
        ),
        WizardStep.PRICING.name to listOf(
            ChecklistItem("p1", "Pricing set on platform", "Base price and activity add-ons"),
            ChecklistItem("p2", "Welcome note prepared", "Kannada and English welcome message")
        )
    )

    private fun defaultProgress(hostUid: String) = WizardProgress(
        hostUid = hostUid,
        stepProgress = defaultStepProgress
    )

    /**
     * Merge Firestore data into the canonical defaults by checklist [ChecklistItem.id].
     * Replacing whole lists was brittle if deserialization dropped booleans; merging by id keeps UI stable.
     */
    /**
     * Firestore's Kotlin mapping mishandles some boolean property names; read completion from raw maps.
     * Supports both `completed` (current) and legacy `isCompleted` keys.
     */
    private fun patchChecklistBooleans(doc: DocumentSnapshot, parsed: WizardProgress?): WizardProgress? {
        if (parsed == null || !doc.exists()) return parsed
        val rawSteps = doc.get("stepProgress") as? Map<*, *> ?: return parsed
        val patched = parsed.stepProgress.mapValues { (stepKey, items) ->
            val rawList = rawSteps[stepKey] as? List<*> ?: return@mapValues items
            val rawMaps = rawList.mapNotNull { it as? Map<*, *> }
            items.map { item ->
                val rawItem = rawMaps.find { (it["id"] as? String) == item.id } ?: return@map item
                val done = when {
                    rawItem["completed"] == true -> true
                    rawItem["isCompleted"] == true -> true
                    rawItem["completed"] == false -> false
                    rawItem["isCompleted"] == false -> false
                    else -> item.completed
                }
                item.copy(completed = done)
            }
        }
        return parsed.copy(stepProgress = patched)
    }

    private fun mergeProgress(hostUid: String, remote: WizardProgress?): WizardProgress {
        val defaults = defaultProgress(hostUid).stepProgress
        if (remote == null || remote.stepProgress.isEmpty()) {
            return WizardProgress(hostUid = hostUid, stepProgress = defaults)
        }
        val merged = defaults.mapValues { (stepKey, defaultItems) ->
            val remoteItems = remote.stepProgress[stepKey].orEmpty()
            defaultItems.map { def ->
                remoteItems.find { it.id == def.id }?.let { rem ->
                    def.copy(
                        completed = rem.completed,
                        title = rem.title.ifBlank { def.title },
                        subtitle = rem.subtitle.ifBlank { def.subtitle }
                    )
                } ?: def
            }
        }
        return WizardProgress(hostUid = hostUid, stepProgress = merged)
    }

    override fun getProgress(hostUid: String): Flow<WizardProgress> = callbackFlow {
        val docRef = firestore.collection("hosts").document(hostUid)
            .collection("wizard").document("progress")

        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val parsed = snapshot?.toObject(WizardProgress::class.java)
            val remote = snapshot?.let { patchChecklistBooleans(it, parsed) } ?: parsed
            trySend(mergeProgress(hostUid, remote))
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun updateProgress(progress: WizardProgress) {
        val hostRef = firestore.collection("hosts").document(progress.hostUid)

        hostRef.set(
            mapOf("readinessScore" to progress.readinessScore),
            SetOptions.merge()
        ).await()

        hostRef.collection("wizard").document("progress").set(progress).await()
    }
}
