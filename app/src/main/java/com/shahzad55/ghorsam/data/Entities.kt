package com.shahzad55.ghorsam.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class Medication(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dose: String,
    val time: String, // format HH:mm
    val frequency: String, // e.g. "روزانه", "هر 12 ساعت"
    val notes: String = "",
    val colorIndex: Int = 0, // index of color palette
    val isActive: Boolean = true,
    val isVerified: Boolean = false,
    val verificationMsg: String? = null
)

@Entity(tableName = "intake_logs")
data class IntakeLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medId: Int,
    val medName: String,
    val scheduledTime: String,
    val actualTime: Long, // timestamp
    val status: String, // "TAKEN", "MISSED", "SNOOZED"
    val sideEffects: String = "" // side effects logged
)

@Entity(tableName = "daily_health_notes")
data class DailyHealthNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String, // "YYYY-MM-DD" style or Persian date
    val note: String,
    val systolicBP: String = "", // blood pressure
    val diastolicBP: String = "",
    val heartRate: String = "",
    val generalStatus: String = "خوب" // "عالی", "خوب", "معمولی", "ضعیف"
)

@Entity(tableName = "caregiver_contacts")
data class CaregiverContact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val phone: String,
    val isEmergencyAlertEnabled: Boolean = true
)
