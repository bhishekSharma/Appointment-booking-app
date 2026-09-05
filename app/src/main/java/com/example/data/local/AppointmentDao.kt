package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY requestedAt DESC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY requestedAt DESC")
    fun getAppointmentsByUser(userId: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE status = :status ORDER BY requestedAt DESC")
    fun getAppointmentsByStatus(status: String): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    suspend fun getAppointmentById(id: Long): AppointmentEntity?

    @Query("""
        SELECT * FROM appointments 
        WHERE (:query = '' OR patientName LIKE '%' || :query || '%' 
               OR doctorName LIKE '%' || :query || '%' 
               OR hospital LIKE '%' || :query || '%'
               OR tokenNumber LIKE '%' || :query || '%')
          AND (:status = '' OR status = :status)
        ORDER BY requestedAt DESC
    """)
    fun searchAppointments(query: String, status: String = ""): Flow<List<AppointmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointments(appointments: List<AppointmentEntity>)

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)

    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun getTotalAppointmentCount(): Int

    @Query("SELECT COUNT(*) FROM appointments WHERE status = :status")
    suspend fun getAppointmentCountByStatus(status: String): Int

    @Query("SELECT SUM(consultationFee) FROM appointments WHERE status = 'CONFIRMED' OR status = 'COMPLETED'")
    suspend fun getTotalConfirmedRevenue(): Double?
}
