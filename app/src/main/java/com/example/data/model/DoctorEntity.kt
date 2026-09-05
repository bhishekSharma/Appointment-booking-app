package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "doctors")
data class DoctorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val specialty: String,
    val hospital: String,
    val location: String,
    val experienceYears: Int,
    val consultationFee: Double,
    val rating: Double,
    val reviewCount: Int = 120,
    val availableDays: String, // e.g. "Mon, Wed, Fri" or "All Weekdays"
    val whatsappNumber: String, // e.g. "+919831498878"
    val isAvailable: Boolean = true,
    val department: String = "General",
    val bio: String = "Specialist with extensive clinical expertise."
)
