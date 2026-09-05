package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long = 1,
    val patientName: String,
    val recordType: String, // Prescription, Lab Report, Scan/X-Ray, Discharge Summary, Vaccination
    val title: String,
    val doctorName: String = "",
    val hospitalName: String = "",
    val date: String,
    val fileSize: String = "1.8 MB",
    val isEncrypted: Boolean = true,
    val cloudSyncStatus: String = "SYNCED", // SYNCED, SYNCING, LOCAL
    val notes: String = "",
    val fileName: String = "report.pdf"
)
