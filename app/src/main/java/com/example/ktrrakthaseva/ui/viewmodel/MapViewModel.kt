package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.data.model.User
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _userLocation = MutableStateFlow(GeoPoint(17.3850, 78.4867)) // Default Hyderabad
    val userLocation: StateFlow<GeoPoint> = _userLocation

    private val _selectedBloodType = MutableStateFlow<BloodType?>(null)
    val selectedBloodType: StateFlow<BloodType?> = _selectedBloodType

    val nearbyDonors: StateFlow<List<User>> = combine(_userLocation, _selectedBloodType) { loc, type ->
        Pair(loc, type)
    }.flatMapLatest { (loc, type) ->
        repository.getNearbyDonors(type, loc, 50.0) // 50km radius
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeRequests: StateFlow<List<BloodRequest>> = _userLocation.flatMapLatest { loc ->
        repository.getNearbyRequests(loc, 50.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateLocation(lat: Double, lng: Double) {
        val newLoc = GeoPoint(lat, lng)
        _userLocation.value = newLoc
        viewModelScope.launch {
            repository.getCurrentUserId()?.let { uid ->
                repository.updateUserLocation(uid, newLoc)
            }
        }
    }

    fun filterByBloodType(type: BloodType?) {
        _selectedBloodType.value = type
    }
}
