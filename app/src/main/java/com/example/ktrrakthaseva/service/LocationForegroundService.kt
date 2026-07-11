package com.example.ktrrakthaseva.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.ktrrakthaseva.R
import com.example.ktrrakthaseva.data.repository.FirebaseRepository
import com.google.android.gms.location.*
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class LocationForegroundService : Service() {

    @Inject
    lateinit var repository: FirebaseRepository

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationCallback: LocationCallback? = null

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "location_service_channel"
    }

    override fun onCreate() {
        super.onCreate()
        
        // Check permissions before starting
        val fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        
        if (fineLocation != PackageManager.PERMISSION_GRANTED && coarseLocation != PackageManager.PERMISSION_GRANTED) {
            Log.e("LocationService", "Stopping service: Location permissions not granted")
            stopSelf()
            return
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, createNotification())
        startLocationUpdates()
        return START_STICKY
    }

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 180000L) // 3 minutes
            .setMinUpdateIntervalMillis(60000L)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    serviceScope.launch {
                        val uid = repository.getCurrentUserId()
                        if (uid != null) {
                            repository.updateUserLocation(uid, GeoPoint(loc.latitude, loc.longitude))
                            Log.d("LocationService", "Location updated: ${loc.latitude}, ${loc.longitude}")
                        }
                    }
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(
                request,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            Log.e("LocationService", "Permission missing", e)
            stopSelf()
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Raktha Seva Active")
            .setContentText("Your location is being shared to help those in need.")
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Updates",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        serviceScope.cancel()
        super.onDestroy()
    }
}
