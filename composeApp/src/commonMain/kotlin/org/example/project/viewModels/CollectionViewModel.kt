package org.example.project.viewModels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel

class CollectionViewModel : ViewModel() {
    // Define the counter variable using StateFlow
    private val _collectionID = MutableStateFlow(0)
    val collectionID: StateFlow<Int> = _collectionID

    fun updateCollectionID(newID: Int) {
        _collectionID.value = newID
    }

    fun clearCollectionID() {
        _collectionID.value = 0
    }
}