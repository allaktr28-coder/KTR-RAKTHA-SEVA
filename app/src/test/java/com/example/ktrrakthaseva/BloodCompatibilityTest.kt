package com.example.ktrrakthaseva

import com.example.ktrrakthaseva.data.model.BloodType
import com.example.ktrrakthaseva.util.BloodCompatibility
import org.junit.Assert.assertTrue
import org.junit.Test

class BloodCompatibilityTest {

    @Test
    fun testABPositiveCanReceiveFromAll() {
        val compatibleTypes = BloodCompatibility.getCompatibleBloodTypes(BloodType.AB_POSITIVE)
        assertEquals(BloodType.entries.size, compatibleTypes.size)
    }

    @Test
    fun testONegativeCanOnlyReceiveFromONegative() {
        val compatibleTypes = BloodCompatibility.getCompatibleBloodTypes(BloodType.O_NEGATIVE)
        assertEquals(1, compatibleTypes.size)
        assertTrue(compatibleTypes.contains(BloodType.O_NEGATIVE))
    }

    @Test
    fun testBPositiveCompatibility() {
        val compatibleTypes = BloodCompatibility.getCompatibleBloodTypes(BloodType.B_POSITIVE)
        val expected = listOf(BloodType.B_POSITIVE, BloodType.B_NEGATIVE, BloodType.O_POSITIVE, BloodType.O_NEGATIVE)
        assertEquals(expected.size, compatibleTypes.size)
        assertTrue(compatibleTypes.containsAll(expected))
    }

    private fun assertEquals(expected: Int, actual: Int) {
        org.junit.Assert.assertEquals(expected, actual)
    }
}
