package com.shahzad55.ghorsam.data

import kotlinx.coroutines.flow.Flow

class MedicationRepository(private val medicationDao: MedicationDao) {
    val allMedications: Flow<List<Medication>> = medicationDao.getAllMedications()
    val allLogs: Flow<List<IntakeLog>> = medicationDao.getAllLogs()
    val allHealthNotes: Flow<List<DailyHealthNote>> = medicationDao.getAllHealthNotes()
    val caregiverContacts: Flow<List<CaregiverContact>> = medicationDao.getCaregiverContacts()

    suspend fun insertMedication(medication: Medication) = medicationDao.insertMedication(medication)
    suspend fun updateMedication(medication: Medication) = medicationDao.updateMedication(medication)
    suspend fun deleteMedication(medication: Medication) = medicationDao.deleteMedication(medication)
    suspend fun deleteMedicationById(id: Int) = medicationDao.deleteMedicationById(id)

    suspend fun insertLog(log: IntakeLog) = medicationDao.insertLog(log)
    suspend fun deleteLog(log: IntakeLog) = medicationDao.deleteLog(log)

    suspend fun insertHealthNote(note: DailyHealthNote) = medicationDao.insertHealthNote(note)
    suspend fun deleteHealthNote(note: DailyHealthNote) = medicationDao.deleteHealthNote(note)

    suspend fun insertCaregiverContact(contact: CaregiverContact) = medicationDao.insertCaregiverContact(contact)
    suspend fun deleteCaregiverContact(contact: CaregiverContact) = medicationDao.deleteCaregiverContact(contact)
}
