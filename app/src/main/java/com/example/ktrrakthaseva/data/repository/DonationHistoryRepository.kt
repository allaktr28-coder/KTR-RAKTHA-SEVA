package com.example.ktrrakthaseva.data.repository

import com.example.ktrrakthaseva.data.model.BloodRequest
import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.data.model.RequestStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DonationHistoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val requestsCollection = firestore.collection("requests")

    fun getUserDonationHistory(userId: String): Flow<List<BloodRequest>> = callbackFlow {
        val subscription = requestsCollection
            .whereEqualTo("acceptedById", userId)
            .whereEqualTo("status", RequestStatus.COMPLETED.name)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let {
                    trySend(it.toObjects(BloodRequest::class.java))
                }
            }
        awaitClose { subscription.remove() }
    }

    fun buildChartData(history: List<BloodRequest>): List<Float> {
        if (history.isEmpty()) return emptyList()
        
        val now = Calendar.getInstance()
        val months = mutableListOf<Int>()
        for (i in 0 until 6) {
            val cal = Calendar.getInstance()
            cal.add(Calendar.MONTH, -i)
            // Create a unique key for year + month
            months.add(cal.get(Calendar.MONTH) + cal.get(Calendar.YEAR) * 12)
        }
        months.reverse()

        val counts = months.associateWith { 0 }.toMutableMap()
        history.forEach { req ->
            val timestamp = req.completedAt ?: return@forEach
            val cal = Calendar.getInstance()
            cal.time = timestamp.toDate()
            val monthKey = cal.get(Calendar.MONTH) + cal.get(Calendar.YEAR) * 12
            if (counts.containsKey(monthKey)) {
                counts[monthKey] = counts[monthKey]!! + 1
            }
        }

        val max = counts.values.maxOrNull()?.toFloat() ?: 1f
        return months.map { (counts[it] ?: 0).toFloat() / (if (max > 0) max else 1f) }
    }

    fun buildBloodTypeBreakdown(history: List<BloodRequest>): Map<BloodType, Float> {
        if (history.isEmpty()) return emptyMap()
        val total = history.size.toFloat()
        return history.groupBy { it.bloodType }
            .mapNotNull { (type, list) ->
                type?.let { it to (list.size / total) }
            }.toMap()
    }
}
