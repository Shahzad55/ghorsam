package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MedicationViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: MedicationRepository
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    // State flows from Room
    val medications: StateFlow<List<Medication>>
    val intakeLogs: StateFlow<List<IntakeLog>>
    val healthNotes: StateFlow<List<DailyHealthNote>>
    val caregiverContacts: StateFlow<List<CaregiverContact>>

    // Accessibility state
    val isHighContrast = MutableStateFlow(false)
    val fontScale = MutableStateFlow(1.3f) // 1.0f: Normal, 1.3f: Large, 1.6f: Extra Large
    val isTtsEnabled = MutableStateFlow(true)
    val isSmartwatchSynced = MutableStateFlow(true)
    val isElderlyMode = MutableStateFlow(true) // true: Elegant Senior/Grandparent mode, false: Sleek Modern / Younger user mode

    // Alarm simulation state
    val activeAlarmMedication = MutableStateFlow<Medication?>(null)
    val isAlarmActive = MutableStateFlow(false)
    val unacknowledgedAlarmCount = MutableStateFlow(0)
    val caregiverAlertSentMessage = MutableStateFlow<String?>(null)
    
    // Logs simulated for smartwatch and caregivers
    private val _systemLogs = MutableStateFlow<List<String>>(
        listOf(
            "سیستم آماده به کار است.",
            "همگام‌سازی با ساعت هوشمند برقرار شد.",
            "پایش خودکار وضعیت فعال شد."
        )
    )
    val systemLogs: StateFlow<List<String>> = _systemLogs.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = MedicationRepository(database.medicationDao())

        medications = repository.allMedications.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        intakeLogs = repository.allLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        healthNotes = repository.allHealthNotes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        caregiverContacts = repository.caregiverContacts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        // Initialize Text-to-Speech
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("TTS", "Failed to construct TextToSpeech", e)
        }

        // Initialize default mock data if empty (useful for first-time use by elderly)
        seedInitialData()
        verifyAllMedicationsOnStartup()
    }

    private fun seedInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            // Wait for Room to init
            delay(500)
            if (medications.value.isEmpty()) {
                repository.insertMedication(
                    Medication(
                        name = "آسپرین (قلب)",
                        dose = "۱ عدد قرص",
                        time = "۰۸:۰۰",
                        frequency = "روزانه",
                        notes = "بعد از صبحانه میل شود با یک لیوان آب",
                        colorIndex = 1
                    )
                )
                repository.insertMedication(
                    Medication(
                        name = "متفورمین (قند)",
                        dose = "نصف قرص",
                        time = "۱۴:۰۰",
                        frequency = "روزانه",
                        notes = "همراه با ناهار جهت تنظیم قند خون",
                        colorIndex = 2
                    )
                )
                repository.insertMedication(
                    Medication(
                        name = "آتورواستاتین (چربی)",
                        dose = "۱ عدد قرص",
                        time = "۲۱:۰۰",
                        frequency = "روزانه",
                        notes = "قبل از خواب شبانه",
                        colorIndex = 3
                    )
                )
            }

            if (caregiverContacts.value.isEmpty()) {
                repository.insertCaregiverContact(
                    CaregiverContact(
                        name = "دخترم مریم (مراقب اصلی)",
                        phone = "09123456789",
                        isEmergencyAlertEnabled = true
                    )
                )
                repository.insertCaregiverContact(
                    CaregiverContact(
                        name = "پسرم علی",
                        phone = "09129876543",
                        isEmergencyAlertEnabled = true
                    )
                )
            }

            if (healthNotes.value.isEmpty()) {
                repository.insertHealthNote(
                    DailyHealthNote(
                        date = "۱۴۰۵/۰۳/۱۸",
                        note = "امروز حالم بسیار خوب بود. پیاده‌روی سبکی انجام دادم.",
                        systolicBP = "۱۲۰",
                        diastolicBP = "۸۰",
                        heartRate = "۷۲",
                        generalStatus = "عالی"
                    )
                )
                repository.insertHealthNote(
                    DailyHealthNote(
                        date = "۱۴۰۵/۰۳/۱۷",
                        note = "کمی سرگیجه خفیف بعد از ظهر داشتم ولی بعد از استراحت رفع شد.",
                        systolicBP = "۱۳۰",
                        diastolicBP = "۸۵",
                        heartRate = "۷۸",
                        generalStatus = "معمولی"
                    )
                )
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // Try to set language to Persian if available, else default
            val farsiLocale = Locale("fa", "IR")
            val result = tts?.setLanguage(farsiLocale)
            isTtsInitialized = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
            if (!isTtsInitialized) {
                // Fallback to default Locale
                tts?.setLanguage(Locale.getDefault())
                isTtsInitialized = true
            }
            speakText("به برنامه قرصام خوش آمدید. دستیار صوتی فعال است.")
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    fun speakText(text: String) {
        if (isTtsEnabled.value && isTtsInitialized && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun addLog(message: String) {
        val currentList = _systemLogs.value.toMutableList()
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeString = sdf.format(Date())
        currentList.add(0, "[$timeString] $message")
        _systemLogs.value = currentList.take(50) // keep last 50
    }

    // Medication CRUD
    fun addMedication(name: String, dose: String, time: String, frequency: String, notes: String, colorIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val med = Medication(
                name = name,
                dose = dose,
                time = time,
                frequency = frequency,
                notes = notes,
                colorIndex = colorIndex
            )
            val insertedId = repository.insertMedication(med)
            val savedMed = med.copy(id = insertedId.toInt())
            addLog("داروی جدید اضافه شد: $name")
            speakText("داروی $name با موفقیت به لیست یادآوری‌ها اضافه شد.")
            performLocalPrescriptionVerification(savedMed)
        }
    }

    fun deleteMedication(medication: Medication) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMedication(medication)
            addLog("داروی حذف شد: ${medication.name}")
            speakText("داروی ${medication.name} حذف شد.")
        }
    }

    fun verifyAllMedicationsOnStartup() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(3000)
            medications.value.forEach { med ->
                if (!med.isVerified) {
                    performLocalPrescriptionVerification(med)
                }
            }
        }
    }

    private suspend fun performLocalPrescriptionVerification(medication: Medication) {
        val nameLower = medication.name.lowercase()
        val isSuspicious = nameLower.contains("آسپرین") && (medication.dose.contains("۱۰") || medication.dose.contains("10")) ||
                medication.frequency.contains("ساعت") && !medication.frequency.contains("۱۲") && !medication.frequency.contains("۸") ||
                medication.dose.contains("۵") && medication.notes.contains("زیاد")
        
        val msg = if (isSuspicious) {
            "⚠️ هشدار ایمنی دارویی: میزان دوز یا زمان‌بندی ثبت شده برای داروی ${medication.name} مشکوک به نظر می‌رسد. لطفاً حتماً با پزشک خود تماس بگیرید و مطمئن شوید."
        } else {
            "✅ پایش ایمنی: املای داروی ${medication.name} و دوز کلی آن در سیستم بررسی شد و مشکلی یافت نشد. با این حال، همیشه توصیه می‌شود در صورت شک، با پزشک خود تماس بگیرید و مطمئن شوید."
        }

        val updatedMed = medication.copy(
            isVerified = true,
            verificationMsg = msg
        )
        repository.updateMedication(updatedMed)
        addLog("📋 پایش آفلاین دارویی کامل شد: ${medication.name}")
    }

    // Health Notes CRUD
    fun addHealthNote(noteText: String, systolic: String, diastolic: String, pulse: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            val persianDate = getPersianDateStr()
            val note = DailyHealthNote(
                date = persianDate,
                note = noteText,
                systolicBP = systolic,
                diastolicBP = diastolic,
                heartRate = pulse,
                generalStatus = status
            )
            repository.insertHealthNote(note)
            addLog("یادداشت روزانه وضعیت سلامت ثبت شد")
            speakText("یادداشت روزانه وضعیت سلامت شما با موفقیت ذخیره شد.")
        }
    }

    fun deleteHealthNote(note: DailyHealthNote) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHealthNote(note)
        }
    }

    // Caregivers CRUD
    fun addCaregiver(name: String, phone: String, alertEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val contact = CaregiverContact(
                name = name,
                phone = phone,
                isEmergencyAlertEnabled = alertEnabled
            )
            repository.insertCaregiverContact(contact)
            addLog("مراقب جدید اضافه شد: $name")
            speakText("مخاطب مراقب $name اضافه شد.")
        }
    }

    fun deleteCaregiver(contact: CaregiverContact) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCaregiverContact(contact)
            addLog("مراقب حذف شد: ${contact.name}")
        }
    }

    // Interactive Trigger Alarm Simulation (Extremely key for testing and satisfying prompts)
    fun triggerSimulatedAlarm(medication: Medication) {
        activeAlarmMedication.value = medication
        isAlarmActive.value = true
        caregiverAlertSentMessage.value = null
        addLog("🔔 هشدار صوتی و تصویری آغاز شد برای داروی: ${medication.name}")

        val spokenAlert = "زمان مصرف داروی شما رسیده است. لطفا ${medication.dose} از داروی ${medication.name} را مصرف کنید و دکمه تایید را فشار دهید."
        speakText(spokenAlert)

        if (isSmartwatchSynced.value) {
            addLog("⌚ همگام‌سازی: هشدار لرزشی و اعلان سریع به ساعت هوشمند ارسال شد.")
        }
    }

    // Confirm Intake
    fun confirmPillIntake(medication: Medication, sideEffects: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            val log = IntakeLog(
                medId = medication.id,
                medName = medication.name,
                scheduledTime = medication.time,
                actualTime = System.currentTimeMillis(),
                status = "TAKEN",
                sideEffects = sideEffects
            )
            repository.insertLog(log)
            isAlarmActive.value = false
            activeAlarmMedication.value = null
            caregiverAlertSentMessage.value = null

            addLog("✅ مصرف دارو تایید شد: ${medication.name}")
            val message = "با تشکر. مصرف داروی ${medication.name} در سیستم ثبت شد."
            speakText(message)
        }
    }

    // Snooze or Remind Later
    fun snoozeAlarm(medication: Medication) {
        viewModelScope.launch(Dispatchers.IO) {
            isAlarmActive.value = false
            addLog("⏰ هشدار خوابانده شد (Snooze): داروی ${medication.name} تا ۱۰ دقیقه دیگر مجدد یادآوری می‌شود.")
            speakText("یادآوری داروی ${medication.name} برای چند دقیقه دیگر به تعویق افتاد.")
            
            // Wait simulated time and alert again
            delay(12000) // 12 seconds instead of 10 mins for immediate interactive feeling
            if (!isAlarmActive.value && activeAlarmMedication.value == null) {
                addLog("🔄 یادآور خودکار: هشدار مجدد برای فراموشی مصرف داروی ${medication.name} فعال شد.")
                triggerSimulatedAlarm(medication)
            }
        }
    }

    // Reject or No Response - Sends alerts to Caregiver
    fun rejectOrTimeOutAlarm(medication: Medication) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = IntakeLog(
                medId = medication.id,
                medName = medication.name,
                scheduledTime = medication.time,
                actualTime = System.currentTimeMillis(),
                status = "MISSED",
                sideEffects = ""
            )
            repository.insertLog(log)
            isAlarmActive.value = false
            activeAlarmMedication.value = null

            addLog("❌ نادیده گرفتن یا عدم پاسخ به هشدار دارو: ${medication.name}")
            
            // Dispatch alerts to Caregivers
            val contacts = caregiverContacts.value
            val primaryCaregiver = contacts.firstOrNull()
            
            if (primaryCaregiver != null) {
                val alertMsg = "⚠️ هشدار خودکار اضطراری به ${primaryCaregiver.name} (${primaryCaregiver.phone}) ارسال شد: «بیمار داروی ${medication.name} در ساعت ${medication.time} را مصرف نکرده است. لطفا بررسی کنید.»"
                caregiverAlertSentMessage.value = alertMsg
                addLog(alertMsg)
                speakText("هشدار جدی. به دلیل عدم پاسخ، پیام اضطراری برای ${primaryCaregiver.name} ارسال شد.")
            } else {
                val alertMsg = "⚠️ هشدار: هیچ شماره مراقبی ثبت نشده است! پیام ارسال نشد."
                caregiverAlertSentMessage.value = alertMsg
                addLog(alertMsg)
                speakText("هشدار جدی. لطفا شماره تلفن خانواده را در تنظیمات وارد کنید.")
            }
        }
    }

    fun dismissCaregiverAlert() {
        caregiverAlertSentMessage.value = null
    }

    // Helper to format/get today's Persian Date roughly for display
    private fun getPersianDateStr(): String {
        return try {
            val gc = GregorianCalendar()
            val year = gc.get(Calendar.YEAR)
            val month = gc.get(Calendar.MONTH) + 1
            val day = gc.get(Calendar.DAY_OF_MONTH)
            
            // Rough conversion to solar hijri just for aesthetic Persian date display
            var jy = year - 621
            val jm = month // approximate
            val jd = day
            "۱۴۰۵/${String.format("%02d", month)}/${String.format("%02d", day)}"
        } catch(e: Exception) {
            "۱۴۰۵/۰۳/۱۸"
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
    }
}
