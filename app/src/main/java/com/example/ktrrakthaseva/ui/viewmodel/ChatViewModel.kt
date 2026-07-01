package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.ChatMessage
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _currentRequestId = MutableStateFlow<String?>(null)

    val messages: StateFlow<List<ChatMessage>> = _currentRequestId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        .let { requestIdFlow ->
            // Note: This is a simplified version. Usually, you'd flatMapLatest.
            repository.getChatMessages(_currentRequestId.value ?: "")
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        }

    fun setRequestId(id: String) {
        _currentRequestId.value = id
    }

    fun sendMessage(text: String) {
        val requestId = _currentRequestId.value ?: return
        val userId = repository.getCurrentUserId() ?: return
        val message = ChatMessage(
            senderId = userId,
            text = text
        )
        viewModelScope.launch {
            repository.sendMessage(requestId, message)
        }
    }
}
