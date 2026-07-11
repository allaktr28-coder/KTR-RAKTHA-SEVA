package com.example.ktrrakthaseva.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.LeaderboardEntry
import com.example.ktrrakthaseva.data.model.RequestStatus
import com.example.ktrrakthaseva.data.repository.BadgeRepository
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import com.example.ktrrakthaseva.service.CooldownResetWorker
import com.example.ktrrakthaseva.util.AnalyticsHelper
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class HomeEvent {
    data class BadgesEarned(val count: Int) : HomeEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val badgeRepository: BadgeRepository,
    private val analytics: AnalyticsHelper,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting

    private val _isAccepting = MutableStateFlow(false)
    val isAccepting: StateFlow<Boolean> = _isAccepting

    private val _events = MutableSharedFlow<HomeEvent>()
    val events = _events.asSharedFlow()

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

    fun completeDonation(requestId: String, donorId: String) {
        viewModelScope.launch {
            try {
                val batch = firestore.batch()
                val requestRef = firestore.collection("requests").document(requestId)
                val userRef = firestore.collection("users").document(donorId)

                batch.update(requestRef, mapOf(
                    "status" to RequestStatus.COMPLETED,
                    "completedAt" to Timestamp.now()
                ))

                batch.update(userRef, mapOf(
                    "totalDonations" to FieldValue.increment(1),
                    "points" to FieldValue.increment(100),
                    "isAvailable" to false, // Cooldown
                    "lastDonationDate" to Timestamp.now()
                ))

                batch.commit().await()

                // Schedule cooldown reset (90 days)
                CooldownResetWorker.schedule(context, donorId)

                // Check for new badges
                val updatedUser = repository.getUserProfile(donorId)
                if (updatedUser != null) {
                    val newBadges = badgeRepository.evaluateAndAwardBadges(updatedUser)
                    if (newBadges.isNotEmpty()) {
                        _events.emit(HomeEvent.BadgesEarned(newBadges.size))
                    }
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
