package org.example.project.RemoteDatabase

// org.example.project.database/FirebaseDatabase.kt
import kotlinx.serialization.Serializable
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.Sample
import dev.gitlive.firebase.firestore.DocumentSnapshot
import dev.gitlive.firebase.firestore.*

object FirebaseDatabase {
    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    suspend fun addSampleWithData(sampleMap: Map<String, Any>) {
        db.collection("samples").add(sampleMap)
    }

    suspend fun getAllSamples(): List<Map<String, Any>> {
        return db.collection("samples")
            .get()
            .documents
            .map { it.data() }
    }

    // Function to listen for real-time updates
    fun listenToSamples(): Flow<List<Map<String, Any>>> {
        return db.collection("samples")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { it.data() }
            }
    }
}