package com.example.ktrrakthaseva.data.repository

import com.example.ktrrakthaseva.data.model.*
import com.example.ktrrakthaseva.util.BloodCompatibility
import com.example.ktrrakthaseva.util.LocationUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class AdminStats(
    val activeRequests: Int = 0,
    val totalDonors: Int = 0,
    val availableDonors: Int = 0,
    val avgResponseTimeMin: Double = 0.0,
    val demandTrends: Map<String, Int> = emptyMap(),
    val successRate: Double = 0.0
)

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")
    private val requestsCollection = firestore.collection("requests")
    private val bloodBanksCollection = firestore.collection("blood_banks")

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun getUserProfile(uid: String): User? {
        // Do not catch exceptions here; let the ViewModel handle it to distinguish 
        // between "document does not exist" and "network error".
        val snapshot = usersCollection.document(uid).get().await()
        return if (snapshot.exists()) snapshot.toObject(User::class.java) else null
    }

    suspend fun saveUserProfile(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    fun getBloodRequests(): Flow<List<BloodRequest>> = callbackFlow {
        val subscription = requestsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                snapshot?.let { trySend(it.toObjects(BloodRequest::class.java)) }
            }
        awaitClose { subscription.remove() }
    }

    fun getAdminStats(): Flow<AdminStats> {
        val requestsFlow = callbackFlow<List<BloodRequest>> {
            val sub = requestsCollection.addSnapshotListener { s, _ -> s?.let { trySend(it.toObjects(BloodRequest::class.java)) } }
            awaitClose { sub.remove() }
        }
        val usersFlow = callbackFlow<List<User>> {
            val sub = usersCollection.addSnapshotListener { s, _ -> s?.let { trySend(it.toObjects(User::class.java)) } }
            awaitClose { sub.remove() }
        }

        return combine(requestsFlow, usersFlow) { requests, users ->
            val activeEmergencies = requests.count { it.status == RequestStatus.OPEN || it.status == RequestStatus.IN_PROGRESS }
            val totalDonors = users.count { !it.isAdmin }
            val availableDonors = users.count { it.isAvailable && !it.isAdmin }
            
            val completedReqs = requests.filter { it.status == RequestStatus.COMPLETED }
            val avgResponseTimeMinutes = if (completedReqs.isNotEmpty()) {
                completedReqs.mapNotNull { 
                    if (it.acceptedAt != null && it.createdAt != null) {
                        (it.acceptedAt!!.seconds - it.createdAt!!.seconds) / 60
                    } else null
                }.average()
            } else 0.0

            AdminStats(
                activeRequests = activeEmergencies,
                totalDonors = totalDonors,
                availableDonors = availableDonors,
                avgResponseTimeMin = avgResponseTimeMinutes,
                demandTrends = requests.groupBy { it.bloodType }.mapValues { it.value.size }.mapKeys { it.key?.name ?: "Unknown" },
                successRate = if (requests.isNotEmpty()) (completedReqs.size.toDouble() / requests.size) * 100 else 0.0
            )
        }
    }

    suspend fun acceptRequest(requestId: String, donorId: String) {
        val batch = firestore.batch()
        val requestRef = requestsCollection.document(requestId)
        batch.update(requestRef, mapOf(
            "status" to RequestStatus.IN_PROGRESS,
            "acceptedById" to donorId,
            "acceptedAt" to Timestamp.now()
        ))
        val responseRef = requestRef.collection("responses").document(donorId)
        batch.set(responseRef, DonorResponse(donorId, requestId, donorId, "ACCEPTED", Timestamp.now()))
        batch.commit().await()
    }

    fun getLeaderboard(): Flow<List<LeaderboardEntry>> = callbackFlow {
        val subscription = usersCollection
            .orderBy("points", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, _ ->
                snapshot?.let {
                    val entries = it.documents.mapIndexed { index, doc ->
                        val user = doc.toObject(User::class.java)
                        LeaderboardEntry(user?.uid ?: "", user?.name ?: "", user?.totalDonations ?: 0, user?.points ?: 0, index + 1)
                    }
                    trySend(entries)
                }
            }
        awaitClose { subscription.remove() }
    }
}
