package org.example.project.RemoteDatabase



import kotlinx.serialization.Serializable

@Serializable
data class FirebaseSample(
    val sampleId: Long,
    val formId: Long,
    val dateCollectedUTC: String,
    val location: String,
    val dataEntries: List<FirebaseDataEntry>
)

@Serializable
data class FirebaseDataEntry(
    val entryId: Long,
    val fieldId: Long,
    val userInput: String
)
