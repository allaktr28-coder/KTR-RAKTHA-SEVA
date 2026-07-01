package com.example.ktrrakthaseva

import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.util.LocationUtils
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicTests {

    @Test
    fun testDistanceCalculation() {
        // Coordinates for two points (e.g., London and Paris)
        val london = GeoPoint(51.5074, -0.1278)
        val paris = GeoPoint(48.8566, 2.3522)
        
        val distance = LocationUtils.calculateDistance(london, paris)
        
        // Expected distance is approximately 343 km
        assertEquals(343.5, distance, 1.0)
    }

    @Test
    fun testDistanceFormatting() {
        assertEquals("500 m", LocationUtils.formatDistance(0.5))
        assertEquals("10.5 km", LocationUtils.formatDistance(10.5))
    }
}
