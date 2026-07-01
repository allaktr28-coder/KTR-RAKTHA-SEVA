package com.example.ktrrakthaseva.util

import com.example.ktrrakthaseva.data.model.BloodType

object BloodCompatibility {
    fun getCompatibleBloodTypes(target: BloodType): List<BloodType> {
        return when (target) {
            BloodType.A_POSITIVE -> listOf(BloodType.A_POSITIVE, BloodType.A_NEGATIVE, BloodType.O_POSITIVE, BloodType.O_NEGATIVE)
            BloodType.A_NEGATIVE -> listOf(BloodType.A_NEGATIVE, BloodType.O_NEGATIVE)
            BloodType.B_POSITIVE -> listOf(BloodType.B_POSITIVE, BloodType.B_NEGATIVE, BloodType.O_POSITIVE, BloodType.O_NEGATIVE)
            BloodType.B_NEGATIVE -> listOf(BloodType.B_NEGATIVE, BloodType.O_NEGATIVE)
            BloodType.AB_POSITIVE -> BloodType.entries.toList()
            BloodType.AB_NEGATIVE -> listOf(BloodType.AB_NEGATIVE, BloodType.A_NEGATIVE, BloodType.B_NEGATIVE, BloodType.O_NEGATIVE)
            BloodType.O_POSITIVE -> listOf(BloodType.O_POSITIVE, BloodType.O_NEGATIVE)
            BloodType.O_NEGATIVE -> listOf(BloodType.O_NEGATIVE)
        }
    }
}
