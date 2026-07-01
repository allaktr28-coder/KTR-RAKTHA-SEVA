package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.data.model.User
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<User>>(emptyList())
    val searchResults: StateFlow<List<User>> = _searchResults

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    fun searchDonors(bloodType: BloodType?, location: GeoPoint?, radiusKm: Double) {
        viewModelScope.launch {
            _isSearching.value = true
            try {
                // If location is null, we might want to use a default or last known location
                // For this AI Smart Match, we use the new repository method
                val searchLocation = location ?: GeoPoint(17.3850, 78.4867) // Default to Hyderabad if null
                val type = bloodType ?: BloodType.O_POSITIVE // Default search type
                
                val results = repository.findMatchingDonors(type, searchLocation, radiusKm)
                _searchResults.value = results
            } catch (e: Exception) {
                _searchResults.value = emptyList()
            } finally {
                _isSearching.value = false
            }
        }
    }
}
