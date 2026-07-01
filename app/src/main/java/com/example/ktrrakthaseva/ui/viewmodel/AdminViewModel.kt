package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.repository.AdminStats
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    val adminStats: StateFlow<AdminStats> = repository.getAdminStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())
}
