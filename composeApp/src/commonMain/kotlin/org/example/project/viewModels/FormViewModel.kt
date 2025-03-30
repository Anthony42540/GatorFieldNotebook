package org.example.project.viewModels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FormViewModel : ViewModel() {
    // Define the counter variable using StateFlow
    private val _formName = MutableStateFlow("")
    val formName: StateFlow<String> = _formName

    fun updateFormName(newName: String) {
        _formName.value = newName
    }

    fun clearFormName() {
        _formName.value = ""
    }
}