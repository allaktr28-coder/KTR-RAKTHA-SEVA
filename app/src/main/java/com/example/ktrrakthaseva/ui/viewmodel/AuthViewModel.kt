package com.example.ktrrakthaseva.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ktrrakthaseva.data.model.*
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestoreException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _isSessionReady = MutableStateFlow(false)
    val isSessionReady: StateFlow<Boolean> = _isSessionReady

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            try {
                val uid = repository.getCurrentUserId()
                if (uid != null) {
                    val profile = repository.getUserProfile(uid)
                    if (profile != null) {
                        _currentUser.value = profile
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Unauthenticated
                    }
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Session check failed", e)
                _authState.value = AuthState.Unauthenticated
            } finally {
                _isSessionReady.value = true
            }
        }
    }

    fun login(email: String, psw: String) {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || psw.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(trimmedEmail, psw).await()
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val profile = repository.getUserProfile(uid)
                    if (profile != null) {
                        _currentUser.value = profile
                        _authState.value = AuthState.Authenticated
                    } else {
                        _authState.value = AuthState.Error("Profile not found in database. Please register.")
                        auth.signOut()
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                _authState.value = AuthState.Error(mapFirebaseError(e))
            }
        }
    }

    fun register(
        name: String, email: String, phone: String, psw: String, confirmPsw: String,
        dob: String, gender: String, weight: String, city: String, state: String,
        bloodType: BloodType?, donorType: String, hasMedical: Boolean, medicalNotes: String,
        agreeTerms: Boolean, agreePrivacy: Boolean
    ) {
        val trimmedEmail = email.trim()
        if (psw.length < 8) {
            _authState.value = AuthState.Error("Password must be at least 8 characters")
            return
        }
        if (psw != confirmPsw) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }
        val weightVal = weight.toDoubleOrNull() ?: 0.0
        if (weightVal < 45.0) {
            _authState.value = AuthState.Error("Minimum weight for donation is 45kg")
            return
        }
        if (bloodType == null) {
            _authState.value = AuthState.Error("Please select a blood type")
            return
        }
        if (!agreeTerms || !agreePrivacy) {
            _authState.value = AuthState.Error("Consent is required to continue")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(trimmedEmail, psw).await()
                val uid = result.user?.uid ?: throw Exception("Auth account creation failed")
                
                val newUser = User(
                    uid = uid, name = name, email = trimmedEmail, phone = phone,
                    bloodType = bloodType, address = city, state = state,
                    dateOfBirth = dob, gender = gender, weightKg = weightVal,
                    donorType = donorType, hasMedicalConditions = hasMedical,
                    medicalNotes = medicalNotes
                )
                
                repository.saveUserProfile(newUser)
                _currentUser.value = newUser
                _authState.value = AuthState.Authenticated
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                _authState.value = AuthState.Error(mapFirebaseError(e))
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
        _authState.value = AuthState.Unauthenticated
    }

    private fun mapFirebaseError(e: Exception): String {
        return when (e) {
            is FirebaseAuthException -> {
                when (e.errorCode) {
                    "auth/wrong-password", "ERROR_WRONG_PASSWORD" -> "Invalid password."
                    "auth/user-not-found", "ERROR_USER_NOT_FOUND" -> "No account found with this email."
                    "auth/email-already-in-use", "ERROR_EMAIL_ALREADY_IN_USE" -> "Email is already registered."
                    "auth/network-request-failed" -> "Network error. Check your connection."
                    "auth/too-many-requests" -> "Too many attempts. Please wait."
                    "auth/invalid-email" -> "Invalid email format."
                    else -> e.localizedMessage ?: "Authentication error"
                }
            }
            is FirebaseFirestoreException -> {
                when (e.code) {
                    FirebaseFirestoreException.Code.PERMISSION_DENIED -> 
                        "Database access denied. Please ensure Firestore is enabled in the Firebase Console."
                    FirebaseFirestoreException.Code.UNAVAILABLE -> 
                        "Database is currently unavailable. Check your connection."
                    else -> "Database error: ${e.code}"
                }
            }
            else -> e.localizedMessage ?: "An unexpected error occurred"
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object PasswordResetSent : AuthState()
    data class Error(val message: String) : AuthState()
}
