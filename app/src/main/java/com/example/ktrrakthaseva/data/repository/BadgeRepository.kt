package com.example.ktrrakthaseva.data.repository

import com.example.ktrrakthaseva.data.model.Badge
import com.example.ktrrakthaseva.data.model.BadgeType
import com.example.ktrrakthaseva.data.model.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    suspend fun evaluateAndAwardBadges(user: User): List<Badge> {
        val earnedBadges = user.badges.map { it.type }.toSet()
        val newBadges = mutableListOf<Badge>()

        val thresholds = listOf(
            Triple(1, BadgeType.FIRST_DONATION, "First Donation"),
            Triple(5, BadgeType.LIFE_SAVER, "Life Saver"),
            Triple(10, BadgeType.FREQUENT_DONOR, "Frequent Donor"),
            Triple(25, BadgeType.ELITE_HERO, "Elite Hero")
        )

        for ((threshold, type, name) in thresholds) {
            if (user.totalDonations >= threshold && type !in earnedBadges) {
                newBadges.add(
                    Badge(
                        id = type.name,
                        type = type,
                        name = name,
                        description = "Awarded for $threshold donations",
                        awardedAt = Timestamp.now()
                    )
                )
            }
        }

        if (newBadges.isNotEmpty()) {
            val updatedBadges = user.badges + newBadges
            usersCollection.document(user.uid).update("badges", updatedBadges).await()
        }

        return newBadges
    }
}
