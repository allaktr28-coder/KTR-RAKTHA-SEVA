package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.data.model.User
import com.example.ktrrakthaseva.data.repository.DonationHistoryRepository
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val historyRepository: DonationHistoryRepository
) : ViewModel() {

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    @OptIn(ExperimentalCoroutinesApi::class)
    val donationHistory: StateFlow<List<BloodRequest>> = _userProfile
        .filterNotNull()
        .flatMapLatest { user ->
            historyRepository.getUserDonationHistory(user.uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chartData: StateFlow<List<Float>> = donationHistory
        .map { history -> historyRepository.buildChartData(history) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bloodTypeBreakdown: StateFlow<Map<BloodType, Float>> = donationHistory
        .map { history -> historyRepository.buildBloodTypeBreakdown(history) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val uid = repository.getCurrentUserId() ?: return
        viewModelScope.launch {
            _userProfile.value = repository.getUserProfile(uid)
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            repository.saveUserProfile(updatedUser)
            _userProfile.value = updatedUser
        }
    }

    fun toggleAvailability(isAvailable: Boolean) {
        val currentUser = _userProfile.value ?: return
        val updatedUser = currentUser.copy(isAvailable = isAvailable)
        updateProfile(updatedUser)
    }
}
