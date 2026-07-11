package com.example.ktrrakthaseva.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit

@HiltWorker
class CooldownResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userId = inputData.getString("userId") ?: return Result.failure()
        return try {
            firestore.collection("users").document(userId)
                .update("isAvailable", true)
                .await()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context, userId: String) {
            val data = workDataOf("userId" to userId)
            val request = OneTimeWorkRequestBuilder<CooldownResetWorker>()
                .setInitialDelay(90, TimeUnit.DAYS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "cooldown_reset_$userId",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
