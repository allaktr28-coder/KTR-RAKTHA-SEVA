package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.Badge
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.User
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    private val _donationHistory = MutableStateFlow<List<BloodRequest>>(emptyList())
    val donationHistory: StateFlow<List<BloodRequest>> = _donationHistory

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val uid = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _userProfile.value = repository.getUserProfile(uid)
            // Load history logic here
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            repository.saveUserProfile(updatedUser)
            _userProfile.value = updatedUser
        }
    }
}
