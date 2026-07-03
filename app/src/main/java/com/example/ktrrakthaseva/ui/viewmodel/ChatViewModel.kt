package com.example.ktrrakthaseva.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.ChatMessage
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _currentRequestId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val messages: StateFlow<List<ChatMessage>> = _currentRequestId
        .flatMapLatest { requestId: String? ->
            if (requestId != null) {
                repository.getChatMessages(requestId)
            } else {
                flowOf(emptyList<ChatMessage>())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val currentUserId: String? get() = repository.getCurrentUserId()

    fun setRequestId(id: String) {
        _currentRequestId.value = id
    }

    fun sendMessage(text: String) {
        val requestId = _currentRequestId.value ?: return
        val userId = currentUserId ?: return
        val message = ChatMessage(
            senderId = userId,
            text = text
        )
        viewModelScope.launch {
            repository.sendMessage(requestId, message)
        }
    }
}
