package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.LeaderboardEntry
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import com.example.ktrrakthaseva.util.AnalyticsHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val analytics: AnalyticsHelper
) : ViewModel() {

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _isAccepting = MutableStateFlow(false)
    val isAccepting: StateFlow<Boolean> = _isAccepting

    val bloodRequests: StateFlow<List<BloodRequest>> = repository.getBloodRequests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<LeaderboardEntry>> = repository.getLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun postRequest(request: BloodRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isPosting.value = true
            try {
                val userId = repository.getCurrentUserId() ?: return@launch
                val requestWithUser = request.copy(requesterId = userId)
                repository.postBloodRequest(requestWithUser)
                analytics.logRequestCreated(request.bloodType?.name ?: "UNKNOWN", request.urgency.name)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isPosting.value = false
            }
        }
    }

    fun acceptBloodRequest(requestId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isAccepting.value = true
            try {
                val donorId = repository.getCurrentUserId() ?: return@launch
                repository.acceptRequest(requestId, donorId)
                analytics.logRequestAccepted(requestId)
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isAccepting.value = false
            }
        }
    }
}
