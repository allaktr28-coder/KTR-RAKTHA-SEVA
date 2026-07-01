package com.example.ktrrakthaseva.util

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun logEvent(name: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(name, params)
    }

    fun logRequestCreated(bloodType: String, urgency: String) {
        val bundle = Bundle().apply {
            putString("blood_type", bloodType)
            putString("urgency", urgency)
        }
        logEvent("blood_request_created", bundle)
    }

    fun logRequestAccepted(requestId: String) {
        val bundle = Bundle().apply {
            putString("request_id", requestId)
        }
        logEvent("blood_request_accepted", bundle)
    }

    fun logDonationCompleted(requestId: String) {
        val bundle = Bundle().apply {
            putString("request_id", requestId)
        }
        logEvent("donation_completed", bundle)
    }

    fun logNotificationEngagement(type: String) {
        val bundle = Bundle().apply {
            putString("notification_type", type)
        }
        logEvent("notification_engaged", bundle)
    }
}
