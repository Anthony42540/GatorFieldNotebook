package org.example.project.RemoteDatabase

// org.example.project.database/FirebaseDatabase.kt

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.initialize
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object FirebaseDatabase {
    private val db: FirebaseFirestore by lazy { Firebase.firestore }

    // Function to add sample data
    suspend fun addSample(
        name: String,
        collectionName: String,
        latitude: Double,
        longitude: Double,
        altitude: Double,
        date: String,
        time: String
    ) {
        val sample = mapOf(
            "name" to name,
            "collectionName" to collectionName,
            "latitude" to latitude,
            "longitude" to longitude,
            "altitude" to altitude,
            "date" to date,
            "time" to time
        )

        db.collection("samples").add(sample)
    }
}
