package com.example.ktrrakthaseva.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.IgnoreExtraProperties

enum class BloodType { A_POSITIVE, A_NEGATIVE, B_POSITIVE, B_NEGATIVE, AB_POSITIVE, AB_NEGATIVE, O_POSITIVE, O_NEGATIVE }
enum class Urgency { LOW, MEDIUM, HIGH, EMERGENCY }
enum class RequestStatus { OPEN, IN_PROGRESS, COMPLETED, CANCELLED }
enum class BadgeType { FIRST_DONATION, LIFE_SAVER, FREQUENT_DONOR, ELITE_HERO }

object Gender {
    const val MALE = "Male"
    const val FEMALE = "Female"
    const val OTHER = "Other"
}

object DonorType {
    const val VOLUNTARY = "Voluntary"
    const val REPLACEMENT = "Replacement"
    const val DIRECTED = "Directed"
}

@IgnoreExtraProperties
data class User(
    var uid: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var bloodType: BloodType? = null,
    var location: GeoPoint? = null,
    var address: String = "",
    var state: String = "",
    var dateOfBirth: String = "",
    var gender: String = "",
    var weightKg: Double = 0.0,
    var donorType: String = DonorType.VOLUNTARY,
    var hasMedicalConditions: Boolean = false,
    var medicalNotes: String = "",
    var profileImageUrl: String = "",
    var fcmToken: String = "",
    var totalDonations: Int = 0,
    var points: Int = 0,
    var badges: List<Badge> = emptyList(),
    var isAvailable: Boolean = true,
    var isAdmin: Boolean = false,
    var lastDonationDate: Timestamp? = null
)

@IgnoreExtraProperties
data class BloodRequest(
    var requestId: String = "",
    var patientName: String = "",
    var requesterId: String = "",
    var acceptedById: String = "",
    var bloodType: BloodType? = null,
    var unitsRequired: Int = 1,
    var unitsCollected: Int = 0,
    var hospitalName: String = "",
    var hospitalLocation: GeoPoint? = null,
    var address: String = "",
    var urgency: Urgency = Urgency.MEDIUM,
    var status: RequestStatus = RequestStatus.OPEN,
    var createdAt: Timestamp = Timestamp.now(),
    var acceptedAt: Timestamp? = null,
    var completedAt: Timestamp? = null,
    var deadline: Timestamp? = null,
    var note: String = ""
)

@IgnoreExtraProperties
data class DonorResponse(
    var responseId: String = "",
    var requestId: String = "",
    var donorId: String = "",
    var status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, COMPLETED
    var timestamp: Timestamp = Timestamp.now()
)

@IgnoreExtraProperties
data class ChatMessage(
    var messageId: String = "",
    var senderId: String = "",
    var text: String = "",
    var timestamp: Timestamp = Timestamp.now(),
    var isRead: Boolean = false
)

@IgnoreExtraProperties
data class Badge(
    var id: String = "",
    var type: BadgeType = BadgeType.FIRST_DONATION,
    var name: String = "",
    var description: String = "",
    var iconUrl: String = "",
    var awardedAt: Timestamp = Timestamp.now()
)

data class LeaderboardEntry(
    var uid: String = "",
    var name: String = "",
    var totalDonations: Int = 0,
    var points: Int = 0,
    var rank: Int = 0
)

@IgnoreExtraProperties
data class BloodBank(
    var id: String = "",
    var name: String = "",
    var location: GeoPoint? = null,
    var address: String = "",
    var phone: String = "",
    var availableStocks: Map<String, Int> = emptyMap()
)
