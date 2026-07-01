package com.example.ktrrakthaseva

import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.data.model.User
import com.example.ktrrakthaseva.util.BloodCompatibility
import com.example.ktrrakthaseva.util.LocationUtils
import com.google.firebase.firestore.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatchingLogicTest {

    @Test
    fun testMatchingLogic() {
        val targetBloodType = BloodType.B_POSITIVE
        val myLocation = GeoPoint(0.0, 0.0)
        val radiusKm = 10.0

        val donors = listOf(
            User(uid = "1", name = "Donor 1", bloodType = BloodType.B_POSITIVE, location = GeoPoint(0.01, 0.01), isAvailable = true), // Near & Compatible
            User(uid = "2", name = "Donor 2", bloodType = BloodType.O_NEGATIVE, location = GeoPoint(0.02, 0.02), isAvailable = true), // Near & Compatible
            User(uid = "3", name = "Donor 3", bloodType = BloodType.A_POSITIVE, location = GeoPoint(0.01, 0.01), isAvailable = true), // Near & Not Compatible
            User(uid = "4", name = "Donor 4", bloodType = BloodType.B_NEGATIVE, location = GeoPoint(1.0, 1.0), isAvailable = true),    // Far & Compatible
            User(uid = "5", name = "Donor 5", bloodType = BloodType.B_POSITIVE, location = GeoPoint(0.005, 0.005), isAvailable = true) // Nearest & Compatible
        )

        val compatibleTypes = BloodCompatibility.getCompatibleBloodTypes(targetBloodType)
        
        val matchedDonors = donors.filter { donor ->
            val donorLoc = donor.location ?: return@filter false
            val isCompatible = compatibleTypes.contains(donor.bloodType)
            val distance = LocationUtils.calculateDistance(myLocation, donorLoc)
            isCompatible && distance <= radiusKm
        }.sortedBy { donor ->
            LocationUtils.calculateDistance(myLocation, donor.location!!)
        }

        assertEquals(3, matchedDonors.size)
        assertEquals("Donor 5", matchedDonors[0].name) // Nearest
        assertEquals("Donor 1", matchedDonors[1].name)
        assertEquals("Donor 2", matchedDonors[2].name) // Furthest within radius
    }
}
