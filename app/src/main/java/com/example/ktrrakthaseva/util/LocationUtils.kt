package com.example.ktrrakthaseva.util

import com.google.firebase.firestore.GeoPoint
import kotlin.math.*

object LocationUtils {
    /**
     * Calculates the distance between two GeoPoints in kilometers using the Haversine formula.
     */
    fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
        val lat1 = point1.latitude
        val lon1 = point1.longitude
        val lat2 = point2.latitude
        val lon2 = point2.longitude

        val r = 6371 // Radius of the earth in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    fun formatDistance(distance: Double): String {
        return if (distance < 1) {
            "${(distance * 1000).toInt()} m"
        } else {
            "%.1f km".format(distance)
        }
    }
}
