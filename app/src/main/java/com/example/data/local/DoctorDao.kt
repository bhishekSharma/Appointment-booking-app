package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DoctorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DoctorDao {
    @Query("SELECT * FROM doctors ORDER BY rating DESC, experienceYears DESC")
    fun getAllDoctors(): Flow<List<DoctorEntity>>

    @Query("SELECT * FROM doctors WHERE id = :id LIMIT 1")
    suspend fun getDoctorById(id: Long): DoctorEntity?

    @Query("""
        SELECT * FROM doctors 
        WHERE (:query = '' OR name LIKE '%' || :query || '%' 
               OR specialty LIKE '%' || :query || '%' 
               OR hospital LIKE '%' || :query || '%' 
               OR location LIKE '%' || :query || '%')
          AND (:specialty = '' OR specialty = :specialty)
          AND (:hospital = '' OR hospital = :hospital)
        ORDER BY rating DESC
    """)
    fun searchDoctors(
        query: String,
        specialty: String = "",
        hospital: String = ""
    ): Flow<List<DoctorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctor(doctor: DoctorEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(doctors: List<DoctorEntity>)

    @Update
    suspend fun updateDoctor(doctor: DoctorEntity)

    @Delete
    suspend fun deleteDoctor(doctor: DoctorEntity)

    @Query("DELETE FROM doctors WHERE id = :id")
    suspend fun deleteDoctorById(id: Long)

    @Query("SELECT COUNT(*) FROM doctors")
    suspend fun getDoctorCount(): Int
}
