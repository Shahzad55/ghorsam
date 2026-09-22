package com.shahzad55.ghorsam.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicationDao {
    // Medications
    @Query("SELECT * FROM medications ORDER BY time ASC")
    fun getAllMedications(): Flow<List<Medication>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: Medication): Long

    @Update
    suspend fun updateMedication(medication: Medication)

    @Delete
    suspend fun deleteMedication(medication: Medication)

    @Query("DELETE FROM medications WHERE id = :id")
    suspend fun deleteMedicationById(id: Int)

    // Intake Logs
    @Query("SELECT * FROM intake_logs ORDER BY actualTime DESC")
    fun getAllLogs(): Flow<List<IntakeLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: IntakeLog): Long

    @Delete
    suspend fun deleteLog(log: IntakeLog)

    // Daily Health Notes
    @Query("SELECT * FROM daily_health_notes ORDER BY date DESC")
    fun getAllHealthNotes(): Flow<List<DailyHealthNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthNote(note: DailyHealthNote): Long

    @Delete
    suspend fun deleteHealthNote(note: DailyHealthNote)

    // Caregiver Contacts
    @Query("SELECT * FROM caregiver_contacts ORDER BY id ASC")
    fun getCaregiverContacts(): Flow<List<CaregiverContact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCaregiverContact(contact: CaregiverContact): Long

    @Delete
    suspend fun deleteCaregiverContact(contact: CaregiverContact)
}
