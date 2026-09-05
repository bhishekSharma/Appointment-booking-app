package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val userName: String = "",
    val userMobile: String = "",
    val userEmail: String = "",
    val patientName: String,
    val patientAge: Int = 30,
    val patientGender: String = "Male",
    val doctorId: Long,
    val doctorName: String,
    val doctorSpecialty: String,
    val hospital: String,
    val location: String,
    val preferredDate: String, // e.g. "2026-09-02" or "Tomorrow"
    val reason: String = "General Checkup",
    val status: String = "PENDING", // PENDING, CONFIRMED, CANCELLED, COMPLETED
    val confirmedDate: String = "",
    val confirmedTime: String = "", // e.g. "10:30 AM"
    val tokenNumber: String = "", // e.g. "TK-108"
    val estimatedWaitingTime: String = "", // e.g. "15-20 mins"
    val consultationType: String = "In-Person Consultation", // In-Person Consultation, Video Consultation
    val paymentStatus: String = "Pay at Hospital",
    val consultationFee: Double = 500.0,
    val requestedAt: Long = System.currentTimeMillis(),
    val confirmedAt: Long? = null,
    val whatsappSentAt: Long? = null,
    val adminNotes: String = ""
)
