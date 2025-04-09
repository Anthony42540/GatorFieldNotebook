package org.example.project.RemoteDatabase

import com.dev.database.cache.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers


// class to handle the synchronization  between local (SQLDelight) and remote (Firestore) databases


class SampleSynchronizer(
    private val localDatabase: Database,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    suspend fun syncLocalToFirebase() {
        // Sync all sample data
        localDatabase.getAllSampleData().forEach { sampleData ->
            try {
                // Get the associated data entries for this sample
                val dataEntries = localDatabase.getDataEntry(sampleData.sampleId.toLong())

                // Create a map for Firebase
                val sampleMap = mapOf(
                    "sampleId" to sampleData.sampleId,
                    "formId" to sampleData.formId,
                    "collectorName" to sampleData.collectorName,   // NEW: include collector name
                    "dateCollectedUTC" to sampleData.dateCollectedUTC,
                    "location" to sampleData.location,
                    "dataEntries" to dataEntries.map { entry ->
                        mapOf(
                            "entryId" to entry.entryId,
                            "fieldId" to entry.fieldId,
                            "userInput" to entry.userInput
                        )
                    }
                )

                FirebaseDatabase.addSampleWithData(sampleMap)
            } catch (e: Exception) {
                println("Failed to sync sample ${sampleData.sampleId}: ${e.message}")
            }
        }
    }

    suspend fun syncFirebaseToLocal() {
        val remoteSamples = FirebaseDatabase.getAllSamples()
        remoteSamples.forEach { sampleMap ->
            try {
                // Insert sample data
                val sampleId = localDatabase.insertSampleData(
                    formId = (sampleMap["formId"] as Number).toLong(),
                    dateCollectedUtc = sampleMap["dateCollectedUTC"] as String,
                    location = sampleMap["location"] as String,
                    collectorName = sampleMap["collectorName"] as String
                )

                // Insert associated data entries
                @Suppress("UNCHECKED_CAST")
                val dataEntries = sampleMap["dataEntries"] as List<Map<String, Any>>
                dataEntries.forEach { entry ->
                    localDatabase.insertDataEntry(
                        sampleId = sampleId,
                        fieldId = (entry["fieldId"] as Number).toLong(),
                        userInput = entry["userInput"] as String
                    )
                }
            } catch (e: Exception) {
                println("Failed to sync remote sample: ${e.message}")
            }
        }
    }
}