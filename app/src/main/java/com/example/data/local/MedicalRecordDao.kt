package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MedicalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records ORDER BY id DESC")
    fun getAllRecords(): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records WHERE userId = :userId ORDER BY id DESC")
    fun getRecordsByUser(userId: Long): Flow<List<MedicalRecordEntity>>

    @Query("SELECT * FROM medical_records WHERE userId = :userId AND (:category = '' OR recordType = :category) ORDER BY id DESC")
    fun getRecordsByUserAndCategory(userId: Long, category: String): Flow<List<MedicalRecordEntity>>

    @Query("""
        SELECT * FROM medical_records 
        WHERE (:query = '' OR title LIKE '%' || :query || '%' 
               OR patientName LIKE '%' || :query || '%' 
               OR doctorName LIKE '%' || :query || '%'
               OR hospitalName LIKE '%' || :query || '%')
        ORDER BY id DESC
    """)
    fun searchRecords(query: String): Flow<List<MedicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: MedicalRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<MedicalRecordEntity>)

    @Update
    suspend fun updateRecord(record: MedicalRecordEntity)

    @Delete
    suspend fun deleteRecord(record: MedicalRecordEntity)

    @Query("SELECT COUNT(*) FROM medical_records")
    suspend fun getRecordCount(): Int
}
