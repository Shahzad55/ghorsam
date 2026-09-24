package com.shahzad55.ghorsam.ui

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.shahzad55.ghorsam.data.Medication
import com.shahzad55.ghorsam.data.IntakeLog
import com.shahzad55.ghorsam.data.DailyHealthNote
import com.shahzad55.ghorsam.data.CaregiverContact
import com.shahzad55.ghorsam.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MedicationViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ViewModel State variables
    val medications by viewModel.medications.collectAsState()
    val intakeLogs by viewModel.intakeLogs.collectAsState()
    val healthNotes by viewModel.healthNotes.collectAsState()
    val caregiverContacts by viewModel.caregiverContacts.collectAsState()

    val isHighContrast by viewModel.isHighContrast.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val isTtsEnabled by viewModel.isTtsEnabled.collectAsState()
    val isSmartwatchSynced by viewModel.isSmartwatchSynced.collectAsState()
    val isElderlyMode by viewModel.isElderlyMode.collectAsState()

    val systemLogs by viewModel.systemLogs.collectAsState()
    val activeAlarmMedication by viewModel.activeAlarmMedication.collectAsState()
    val isAlarmActive by viewModel.isAlarmActive.collectAsState()
    val caregiverAlertSentMessage by viewModel.caregiverAlertSentMessage.collectAsState()

    // Navigation and sub-screen state
    var selectedTab by remember { mutableStateOf(0) }

    // Dialog sheets state
    var showAddMedicationDialog by remember { mutableStateOf(false) }
    var showAddNoteDialog by remember { mutableStateOf(false) }
    var showAddCaregiverDialog by remember { mutableStateOf(false) }
    var showSideEffectLogDialog by remember { mutableStateOf<Medication?>(null) }

    // Sound effect simulation
    val colorsList = listOf(PillRed, PillBlue, PillGreen, PillYellow, PillOrange, PillPurple)

    // Function to calculate responsive text sizes easily
    fun getFontSize(base: Float): Float {
        return base * fontScale
    }

    MyApplicationTheme(highContrastEnabled = isHighContrast) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = "آیکون دارو",
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                if (isElderlyMode) "قرصام (ویژه سالمند 👵👴)" else "قرصام (پایش سلامت جوانان 📱)",
                                fontSize = getFontSize(18f).sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    },
                    actions = {
                        // Toggle between Youth (Modern) / Elderly (Accessibility) modes
                        IconButton(
                            onClick = {
                                val nextMode = !isElderlyMode
                                viewModel.isElderlyMode.value = nextMode
                                if (nextMode) {
                                    viewModel.fontScale.value = 1.3f
                                    viewModel.speakText("حالت کاربری به سالمند تغییر یافت")
                                } else {
                                    viewModel.fontScale.value = 1.0f
                                    viewModel.speakText("حالت کاربری به جوان و مدرن تغییر یافت")
                                }
                            },
                            modifier = Modifier.testTag("toggle_user_mode")
                        ) {
                            Icon(
                                imageVector = if (isElderlyMode) Icons.Default.Face else Icons.Default.Accessibility,
                                contentDescription = "تغییر حالت جوان / سالمند",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // Quick accessibility toggle bar (large font scaling & High contrast mode)
                        IconButton(
                            onClick = {
                                viewModel.isHighContrast.value = !isHighContrast
                                val stateStr = if (isHighContrast) "غیرفعال" else "فعال"
                                viewModel.speakText("تم با کنتراست بالا $stateStr شد")
                            },
                            modifier = Modifier.testTag("toggle_contrast")
                        ) {
                            Icon(
                                imageVector = if (isHighContrast) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "تغییر کنتراست صفحه",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        IconButton(
                            onClick = {
                                val nextScale = when (fontScale) {
                                    1.0f -> 1.3f
                                    1.3f -> 1.6f
                                    else -> 1.0f
                                }
                                viewModel.fontScale.value = nextScale
                                val scaleStr = when (nextScale) {
                                    1.0f -> "معمولی"
                                    1.3f -> "بزرگ"
                                    else -> "خیلی بزرگ"
                                }
                                viewModel.speakText("اندازه نوشته‌ها به حالت $scaleStr تغییر یافت")
                            },
                            modifier = Modifier.testTag("toggle_font_size")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "تغییر اندازه متن",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = if (isHighContrast) Color.Black else MaterialTheme.colorScheme.surface,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val tabs = listOf(
                        Triple("یادآورها", Icons.Default.Alarm, 0),
                        Triple("نمودار و تاریخچه", Icons.Default.BarChart, 1),
                        Triple("دفترچه سلامت", Icons.Default.Favorite, 2),
                        Triple("مراقبان و ساعت", Icons.Default.Watch, 3)
                    )
                    
                    tabs.forEach { (title, icon, index) ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                viewModel.speakText(title)
                            },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title,
                                    modifier = Modifier.size(28.dp)
                                )
                            },
                            label = {
                                Text(
                                    title,
                                    fontSize = getFontSize(11f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = if (isHighContrast) Color.Yellow.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Main Content Switching
                when (selectedTab) {
                    0 -> RemindersTab(
                        viewModel = viewModel,
                        medications = medications,
                        onAddMedicationClick = { showAddMedicationDialog = true },
                        onLogSideEffect = { med -> showSideEffectLogDialog = med },
                        getFontSize = ::getFontSize,
                        isHighContrast = isHighContrast,
                        isElderlyMode = isElderlyMode,
                        colorsList = colorsList
                    )
                    1 -> HistoryAndChartTab(
                        viewModel = viewModel,
                        logs = intakeLogs,
                        medications = medications,
                        getFontSize = ::getFontSize,
                        isHighContrast = isHighContrast
                    )
                    2 -> HealthJournalTab(
                        viewModel = viewModel,
                        notes = healthNotes,
                        onAddNoteClick = { showAddNoteDialog = true },
                        getFontSize = ::getFontSize,
                        isHighContrast = isHighContrast
                    )
                    3 -> CaregiversAndWatchTab(
                        viewModel = viewModel,
                        contacts = caregiverContacts,
                        systemLogs = systemLogs,
                        isWatchSynced = isSmartwatchSynced,
                        onAddCaregiverClick = { showAddCaregiverDialog = true },
                        getFontSize = ::getFontSize,
                        isHighContrast = isHighContrast
                    )
                }

                // Caregiver Emergency SMS Dispatch Notification Banner
                caregiverAlertSentMessage?.let { msg ->
                    AlertDialog(
                        onDismissRequest = { viewModel.dismissCaregiverAlert() },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Warning, contentDescription = "هشدار", tint = PillRed)
                                Text("اتصال اضطراری با خانواده", fontSize = getFontSize(18f).sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        text = {
                            Text(msg, fontSize = getFontSize(15f).sp, color = MaterialTheme.colorScheme.onSurface)
                        },
                        confirmButton = {
                            Button(
                                onClick = { viewModel.dismissCaregiverAlert() },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("متوجه شدم 👌", fontSize = getFontSize(14f).sp)
                            }
                        }
                    )
                }

                // Sound and Visual Full-screen Overriding Alarm due
                if (isAlarmActive && activeAlarmMedication != null) {
                    val activeMed = activeAlarmMedication!!

                    Dialog(
                        onDismissRequest = { /* Force response, cannot dismiss easily by touching outside */ },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(if (isHighContrast) Color.Black else Color(0xFFFFF9C4)), // Flashing yellow backdrop
                            color = if (isHighContrast) Color.Black else Color(0xFFFFF9C4)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // Pulsing Visual Icons
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "زنگ هشدار فعال است",
                                    tint = PillRed,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .padding(bottom = 16.dp)
                                )

                                Text(
                                    "🔔 وقت خوردن قرص رسیده است!",
                                    fontSize = getFontSize(28f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PillRed,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                // Huge Pill Representative Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    elevation = CardDefaults.cardElevation(8.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        // Pill colored circle wrapper
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .background(
                                                    colorsList.getOrElse(activeMed.colorIndex) { PillRed },
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.MedicalServices,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(16.dp))

                                        Text(
                                            "نام دارو: ${activeMed.name}",
                                            fontSize = getFontSize(25f).sp,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.Center
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Text(
                                            "میزان مصرف: ${activeMed.dose}",
                                            fontSize = getFontSize(21f).sp,
                                            color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center
                                        )
                                        
                                        if (activeMed.notes.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Text(
                                                "توضیحات: ${activeMed.notes}",
                                                fontSize = getFontSize(17f).sp,
                                                color = Color.Gray,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(30.dp))

                                // Massive buttons for elderly grandparents to press with ease
                                Button(
                                    onClick = { viewModel.confirmPillIntake(activeMed) },
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(72.dp)
                                        .testTag("alarm_confirm_button"),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PillGreen)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(32.dp), tint = Color.White)
                                        Text("خوردم! ✅ (تایید نهایی)", fontSize = getFontSize(22f).sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(0.9f),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Snooze (Remind later in a few min/secs)
                                    OutlinedButton(
                                        onClick = { viewModel.snoozeAlarm(activeMed) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isHighContrast) Color.Yellow else Color.DarkGray),
                                        border = ButtonDefaults.outlinedButtonBorder.copy()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Snooze, contentDescription = null)
                                            Text("۱۰ دقیقه بعد یادآوری کن ⏰", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Reject or Call out caregiver simulation
                                    Button(
                                        onClick = { viewModel.rejectOrTimeOutAlarm(activeMed) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(60.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PillRed)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Icon(Icons.Default.Cancel, contentDescription = null, tint = Color.White)
                                            Text("نمی‌خورم / عدم پاسخ ❌", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))
                                
                                Text(
                                    "این هشدار صوتی به صورت خودکار تکرار می‌شود.\nدر صورت عدم تایید مصرف، پیام هشدار اضطراری برای خانواده ارسال خواهد شد.",
                                    fontSize = getFontSize(13f).sp,
                                    color = Color.DarkGray,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Add Medication Dialog Form
        if (showAddMedicationDialog) {
            AddMedicationDialog(
                viewModel = viewModel,
                colorsList = colorsList,
                onDismiss = { showAddMedicationDialog = false },
                getFontSize = ::getFontSize
            )
        }

        // Add Daily Health Note & Pulse Form
        if (showAddNoteDialog) {
            AddHealthNoteDialog(
                viewModel = viewModel,
                onDismiss = { showAddNoteDialog = false },
                getFontSize = ::getFontSize
            )
        }

        // Add Caregiver Contact Form
        if (showAddCaregiverDialog) {
            AddCaregiverDialog(
                viewModel = viewModel,
                onDismiss = { showAddCaregiverDialog = false },
                getFontSize = ::getFontSize
            )
        }

        // Side effect prompt logging dialog
        showSideEffectLogDialog?.let { med ->
            SideEffectLogDialog(
                medication = med,
                onLog = { sideEffect ->
                    viewModel.confirmPillIntake(med, sideEffect)
                    showSideEffectLogDialog = null
                },
                onDismiss = { showSideEffectLogDialog = null },
                getFontSize = ::getFontSize
            )
        }
    }
}

// ==================== TAB 0: REMINDERS (خانه / یادآورها) ====================
@Composable
fun RemindersTab(
    viewModel: MedicationViewModel,
    medications: List<Medication>,
    onAddMedicationClick: () -> Unit,
    onLogSideEffect: (Medication) -> Unit,
    getFontSize: (Float) -> Float,
    isHighContrast: Boolean,
    isElderlyMode: Boolean,
    colorsList: List<Color>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Adaptive Greeting Header Card for both Parents/Elders and Younger Adults
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isHighContrast) {
                    Color(0xFF121212)
                } else {
                    if (isElderlyMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.tertiaryContainer
                }
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                if (isElderlyMode) {
                    Text(
                        "سلام پدربزرگ و مادربزرگ عزیز! ❤️",
                        fontSize = getFontSize(22f).sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        "سلام همراه عزیز! 📱🌟",
                        fontSize = getFontSize(20f).sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighContrast) Color.Green else MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                
                if (isElderlyMode) {
                    Text(
                        "امروز دوشنبه ۱۸ خرداد - قرص پایش شده و آماده است.",
                        fontSize = getFontSize(15f).sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                } else {
                    Text(
                        "امروز دوشنبه ۱۸ خرداد - پایش هوشمند نسخه و تعامل دارویی فعال است.",
                        fontSize = getFontSize(14f).sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Prompt helpful Voice Read-out helper button
                    Button(
                        onClick = {
                            val statusSummary = if (medications.isEmpty()) {
                                "امروز هیچ دارویی برای مصرف ثبت نشده است."
                            } else {
                                "شما ${medications.size} داروی برنامه‌ریزی شده دارید. برای شنیدن هشدارهای صوتی برنامه آماده است."
                            }
                            viewModel.speakText("خوش آمدید. $statusSummary")
                        },
                        modifier = Modifier.wrapContentSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isElderlyMode) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(20.dp))
                            Text("🔊 خواندن صوتی وضعیت", fontSize = getFontSize(13f).sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // A dynamic visual indicator of standard-mode setup!
                    Button(
                        onClick = {
                            val nextMode = !isElderlyMode
                            viewModel.isElderlyMode.value = nextMode
                            if (nextMode) {
                                viewModel.fontScale.value = 1.3f
                                viewModel.speakText("حالت کاربری به سالمند تغییر یافت")
                            } else {
                                viewModel.fontScale.value = 1.0f
                                viewModel.speakText("حالت کاربری به جوان و مدرن تغییر یافت")
                            }
                        },
                        modifier = Modifier.wrapContentSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isHighContrast) Color.DarkGray else Color.Black.copy(alpha = 0.08f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (isElderlyMode) "حالت جوانان 📱" else "حالت سالمند 👵👴",
                            fontSize = getFontSize(12f).sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "⏰ برنامه‌های دارویی امروز شما:",
                fontSize = getFontSize(18f).sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = onAddMedicationClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_medication_button")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "افزودن")
                    Text("داروی جدید", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (medications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(
                        Icons.Default.MedicalServices,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "هنوز برنامه دارویی ثبت نشده است.",
                        fontSize = getFontSize(16f).sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(medications) { med ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (isHighContrast) 2.dp else 0.dp,
                                color = if (isHighContrast) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(16.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Visual color cue represents active medication type
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .background(
                                                colorsList.getOrElse(med.colorIndex) { PillRed },
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.MedicalServices,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            med.name,
                                            fontSize = getFontSize(18f).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isHighContrast) Color.Yellow else Color.Unspecified
                                        )
                                        Text(
                                            "میزان: ${med.dose} - ساعت ${med.time}",
                                            fontSize = getFontSize(15f).sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }

                                // Delete medication safety button (with large target)
                                IconButton(
                                    onClick = { viewModel.deleteMedication(med) },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "حذف دارو",
                                        tint = PillRed
                                    )
                                }
                            }

                            if (med.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "توضیحات پزشک: ${med.notes}",
                                    fontSize = getFontSize(13f).sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                            }

                            // --- Automatic Prescription Verification & Safety Warning ---
                            Spacer(modifier = Modifier.height(8.dp))
                            val context = LocalContext.current
                            if (!med.isVerified) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(if (isHighContrast) Color.Black else Color(0xFFE0F7FA), RoundedCornerShape(10.dp))
                                        .border(if (isHighContrast) 1.dp else 0.dp, Color.White, RoundedCornerShape(10.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = if (isHighContrast) Color.Yellow else Color(0xFF00796B)
                                    )
                                    Text(
                                        "🔍 در حال پایش علمی ایمنی دوز و دستور مصرف در پس‌زمینه...",
                                        fontSize = getFontSize(12f).sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isHighContrast) Color.White else Color(0xFF004D40)
                                    )
                                }
                            } else {
                                med.verificationMsg?.let { msg ->
                                    val containsWarning = msg.contains("هشدار") || msg.contains("تماس") || msg.contains("پزشک") || msg.contains("⚠️") || msg.lowercase().contains("warning")
                                    val bgColors = if (isHighContrast) Color.Black else (if (containsWarning) Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
                                    val textColors = if (isHighContrast) (if (containsWarning) Color.Yellow else Color.Green) else (if (containsWarning) Color(0xFFD84315) else Color(0xFF2E7D32))
                                    val borderColors = if (isHighContrast) Color.White else (if (containsWarning) Color(0xFFFFB74D) else Color(0xFF81C784))
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(bgColors, RoundedCornerShape(12.dp))
                                            .border(1.dp, borderColors, RoundedCornerShape(12.dp))
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (containsWarning) Icons.Default.Warning else Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = textColors,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                if (containsWarning) "⚠️ هشدار تایید دوز و دستور مصرف:" else "✅ پایش سلامت نسخه و دوز:",
                                                fontSize = getFontSize(13f).sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textColors
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            msg,
                                            fontSize = getFontSize(13f).sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isHighContrast) Color.White else textColors.copy(alpha = 0.9f)
                                        )
                                        
                                        if (containsWarning) {
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = {
                                                    try {
                                                        val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                                            data = android.net.Uri.parse("tel:09123456789")
                                                        }
                                                        context.startActivity(intent)
                                                    } catch (e: Exception) {
                                                        android.util.Log.e("Dialer", "Cant start dialer", e)
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = PillRed),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(38.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(Icons.Default.Phone, contentDescription = "تماس با پزشک", tint = Color.White, modifier = Modifier.size(16.dp))
                                                    Text("📞 تماس با پزشک معالج برای اطمینان", fontSize = getFontSize(12f).sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive action row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Simulate intake immediately
                                Button(
                                    onClick = { viewModel.confirmPillIntake(med) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PillGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("consume_check_button_${med.id}")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                        Text("خوردم ✅", fontSize = getFontSize(15f).sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }

                                // Log Side Effects & Complication
                                OutlinedButton(
                                    onClick = { onLogSideEffect(med) },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isHighContrast) Color.White else MaterialTheme.colorScheme.primary),
                                    border = ButtonDefaults.outlinedButtonBorder.copy()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.BugReport, contentDescription = null)
                                        Text("دارای عوارض؟ ⚠️", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // SIMULATOR BUTTON (Trigger manual full screen test alarm)
                            Button(
                                onClick = { viewModel.triggerSimulatedAlarm(med) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (isHighContrast) Color.Black else Color.White)
                                    Text("شبیه‌سازی هشدار و یادآوری صوتی این دارو 🔊", fontSize = getFontSize(12f).sp, fontWeight = FontWeight.Bold, color = if (isHighContrast) Color.Black else Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 1: CHART & HISTORIC LOGS (بخش نمودار و تاریخچه) ====================
@Composable
fun HistoryAndChartTab(
    viewModel: MedicationViewModel,
    logs: List<IntakeLog>,
    medications: List<Medication>,
    getFontSize: (Float) -> Float,
    isHighContrast: Boolean
) {
    val complianceText = if (logs.isEmpty()) "داده‌ای موجود نیست" else {
        val takenCount = logs.count { it.status == "TAKEN" }
        val percent = (takenCount.toFloat() / logs.size.toFloat() * 100).toInt()
        "میزان پایبندی کلی شما: $percent٪"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "📊 نمودار پایش روند درمان شما",
            fontSize = getFontSize(19f).sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Customizable Chart using local system constraints Canvas (as per rules: NO remote sheets/web assets)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    "پایبندی ۷ روز گذشته (درصد خوردن به موقع قرص‌ها)",
                    fontSize = getFontSize(12f).sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Simple simulated weekly values representing medication compliance %
                val mockWeeklyData = listOf(100, 75, 100, 50, 100, 80, 100)
                val mockDays = listOf("شنبه", "۱شنبه", "۲شنبه", "۳شنبه", "۴شنبه", "۵شنبه", "جمعه")
                
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 12.dp)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val barWidth = 24.dp.toPx()
                        val spacing = (size.width - (barWidth * mockWeeklyData.size)) / (mockWeeklyData.size + 1)
                        val maxHeight = size.height - 20.dp.toPx()

                        mockWeeklyData.forEachIndexed { idx, valPercent ->
                            val xPos = spacing + idx * (barWidth + spacing)
                            val barHeight = maxHeight * (valPercent / 100f)
                            val yPos = size.height - barHeight - 14.dp.toPx()

                            // Draw rounded bar chart
                            drawRoundRect(
                                color = if (isHighContrast) Color.Yellow else WarmTealSecondary,
                                topLeft = Offset(xPos, yPos),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                            )
                        }
                    }

                    // Grid text representing Persian days below coordinates
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        mockDays.forEach { day ->
                            Text(
                                day,
                                fontSize = getFontSize(10f).sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isHighContrast) Color.Yellow else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📋 تاریخچه دقیق و عوارض جانبی:",
                fontSize = getFontSize(17f).sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                complianceText,
                fontSize = getFontSize(14f).sp,
                fontWeight = FontWeight.Bold,
                color = PillGreen
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "هنوز سابقه‌ای ثبت نشده است.",
                    fontSize = getFontSize(16f).sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(logs) { log ->
                    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val exactTimeString = sdf.format(Date(log.actualTime))
                    val isTaken = log.status == "TAKEN"

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isTaken) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (isTaken) PillGreen else PillRed,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Text(
                                        log.medName,
                                        fontSize = getFontSize(16f).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    if (isTaken) "ساعت مصرف: $exactTimeString" else "عدم پاسخ",
                                    fontSize = getFontSize(13f).sp,
                                    color = Color.Gray
                                )
                            }
                            
                            if (log.sideEffects.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isHighContrast) Color.Black else Color(0xFFFFEBEE)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Default.Warning, contentDescription = null, tint = PillRed, modifier = Modifier.size(16.dp))
                                        Text(
                                            "عوارض گزارش شده: ${log.sideEffects}",
                                            fontSize = getFontSize(13f).sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PillRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 2: HEALTH JOURNAL & NOTES (دفترچه یادداشت‌های روزانه و عوارض) ====================
@Composable
fun HealthJournalTab(
    viewModel: MedicationViewModel,
    notes: List<DailyHealthNote>,
    onAddNoteClick: () -> Unit,
    getFontSize: (Float) -> Float,
    isHighContrast: Boolean
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "📝 یادداشت‌های روزانه سلامت",
                fontSize = getFontSize(18f).sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onAddNoteClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.EditNote, contentDescription = null)
                    Text("ثبت یادداشت جدید", fontSize = getFontSize(13f).sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Professional action button: Share report directly with specialist doctor!
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isHighContrast) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = null,
                    tint = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "اشتراک‌گذاری گزارش سلامت با پزشک",
                        fontSize = getFontSize(15f).sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "ارسال سابقه مصرف داروها و یادداشت‌های روزانه به پزشک معالج شما.",
                        fontSize = getFontSize(12f).sp,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = {
                        // Draft report content in elegant Persian format
                        val reportText = buildString {
                            appendLine("گزارش دوره‌ای درمان و علائم بالینی (تولید شده توسط برنامه قرصام)")
                            appendLine("--------------------------------------------------")
                            appendLine("وضعیت عمومی بیمار و علائم روزانه:")
                            notes.forEach { note ->
                                appendLine("- تاریخ: ${note.date} | وضعیت عمومی: ${note.generalStatus}")
                                appendLine("  فشار خون: ${note.systolicBP}/${note.diastolicBP} | ضربان قلب: ${note.heartRate}")
                                appendLine("  یادداشت بیمار: ${note.note}")
                                appendLine()
                            }
                        }
                        
                        // Open standard share sheet using deep android Intent mechanism!
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            putExtra(android.content.Intent.EXTRA_TEXT, reportText)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, "ارسال گزارش دارو به پزشک معالج")
                        context.startActivity(shareIntent)

                        viewModel.addLog("گزارش دوره‌ای پزشک آماده اشتراک‌گذاری شد.")
                        viewModel.speakText("گزارش سلامت شما جهت ارسال برای پزشک معالج آماده شد.")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ارسال 📲", fontSize = getFontSize(13f).sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "هنوز هیچ یادداشت روزانه‌ای برای سلامتی وارد نشده است.",
                    fontSize = getFontSize(14f).sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notes) { note ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "تاریخ ثبت: ${note.date}",
                                    fontSize = getFontSize(15f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isHighContrast) Color.Yellow else MaterialTheme.colorScheme.primary
                                )
                                
                                Badge(
                                    containerColor = when (note.generalStatus) {
                                        "عالی" -> PillGreen
                                        "خوب" -> WarmTealSecondary
                                        "معمولی" -> PillYellow
                                        else -> PillRed
                                    },
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        "حال عمومی: ${note.generalStatus}",
                                        fontSize = getFontSize(12f).sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(vertical = 2.dp, horizontal = 6.dp),
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Stats display (bp, hr)
                            if (note.systolicBP.isNotEmpty() || note.heartRate.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isHighContrast) Color.Black else Color(0xFFF9F9F9),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    if (note.systolicBP.isNotEmpty()) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("فشار خون 🩺", fontSize = getFontSize(12f).sp, color = Color.Gray)
                                            Text("${note.systolicBP}/${note.diastolicBP}", fontSize = getFontSize(16f).sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    if (note.heartRate.isNotEmpty()) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("ضربان قلب ❤️", fontSize = getFontSize(12f).sp, color = Color.Gray)
                                            Text("${note.heartRate} بار/دقیقه", fontSize = getFontSize(16f).sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Text(
                                note.note,
                                fontSize = getFontSize(16f).sp,
                                lineHeight = 22.sp
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Delete Note button
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                IconButton(
                                    onClick = { viewModel.deleteHealthNote(note) },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف یادداشت", tint = PillRed)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== TAB 3: SMARTWATCH SYNC & CAREGIVER CONTACTS (همگام‌سازی ساعت و خانواده) ====================
@Composable
fun CaregiversAndWatchTab(
    viewModel: MedicationViewModel,
    contacts: List<CaregiverContact>,
    systemLogs: List<String>,
    isWatchSynced: Boolean,
    onAddCaregiverClick: () -> Unit,
    getFontSize: (Float) -> Float,
    isHighContrast: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Smartwatch synchronization panel
        Text(
            "⌚ همگام‌سازی با ساعت هوشمند",
            fontSize = getFontSize(18f).sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color(0xFFE0F2F1)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isWatchSynced) Icons.Default.Watch else Icons.Default.WatchOff,
                            contentDescription = null,
                            tint = if (isWatchSynced) PillGreen else PillRed,
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                if (isWatchSynced) "وضعیت: متصل به ساعت هوشمند شما ✅" else "وضعیت: غیرفعال",
                                fontSize = getFontSize(15f).sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "انتقال خودکار اعلان‌ها و هشدارهای ضربه‌ای روی مچ دست شما",
                                fontSize = getFontSize(12f).sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Switch(
                        checked = isWatchSynced,
                        onCheckedChange = {
                            viewModel.isSmartwatchSynced.value = it
                            val watchStr = if (it) "فعال" else "قطع"
                            viewModel.speakText("همگام سازی ساعت هوشمند $watchStr شد")
                            viewModel.addLog(" وضعیت اتصال ساعت تغییر یافت: $watchStr")
                        }
                    )
                }
            }
        }

        // Family Support contacts section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "👨‍👩‍👧‍👦 شماره تماس مراقبان و خانواده:",
                fontSize = getFontSize(17f).sp,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = onAddCaregiverClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null)
                    Text("افزودن مراقب", fontSize = getFontSize(13f).sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (contacts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "هیچ شماره تماسی برای مراقبان ثبت نشده است.",
                    fontSize = getFontSize(14f).sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(contacts) { caregiver ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isHighContrast) Color(0xFF1E1E1E) else Color.White
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    caregiver.name,
                                    fontSize = getFontSize(16f).sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "شماره همراه: ${caregiver.phone}",
                                    fontSize = getFontSize(13f).sp,
                                    color = Color.Gray
                                )
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Badge(
                                    containerColor = PillGreen.copy(alpha = 0.8f),
                                ) {
                                    Text("پیام اضطراری خودکار", fontSize = 11.sp, modifier = Modifier.padding(4.dp), color = Color.White)
                                }

                                IconButton(
                                    onClick = { viewModel.deleteCaregiver(caregiver) },
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "حذف مراقب", tint = PillRed)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // System notifications and wearable syncing audit logs
        Text(
            "📋 تاریخچه همگام‌ساز این هوشمند:",
            fontSize = getFontSize(16f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = if (isHighContrast) Color.Black else Color(0xFF263238)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(systemLogs) { log ->
                    Text(
                        log,
                        fontSize = 13.sp,
                        color = if (isHighContrast) Color.Yellow else Color(0xFFECEFF1),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// ==================== SUB-COMPONENTS/SHEET DIALOGS ====================

// Add Medication Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicationDialog(
    viewModel: MedicationViewModel,
    colorsList: List<Color>,
    onDismiss: () -> Unit,
    getFontSize: (Float) -> Float
) {
    var name by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }
    var hour by remember { mutableStateOf("08") }
    var minute by remember { mutableStateOf("00") }
    var frequency by remember { mutableStateOf("روزانه") }
    var notes by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("➕ برنامه دارویی جدید", fontSize = getFontSize(20f).sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام دارو (به فارسی)", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth().testTag("med_name_input"),
                    placeholder = { Text("مثلاً: متفورمین") }
                )

                OutlinedTextField(
                    value = dose,
                    onValueChange = { dose = it },
                    label = { Text("میزان کپسول یا قرص در هر وعده", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth().testTag("med_dose_input"),
                    placeholder = { Text("مثلاً: ۱ عدد یا نصف قرص") }
                )

                // Simulating elderly friendly clock chooser
                Text("ساعت زنگ هشدار مصرف:", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hour,
                        onValueChange = { if (it.length <= 2) hour = it },
                        label = { Text("ساعت (دو رقم)", fontSize = getFontSize(10f).sp) },
                        modifier = Modifier.weight(1f).testTag("med_hour_input")
                    )
                    Text(":", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = minute,
                        onValueChange = { if (it.length <= 2) minute = it },
                        label = { Text("دقیقه (دو رقم)", fontSize = getFontSize(10f).sp) },
                        modifier = Modifier.weight(1f).testTag("med_minute_input")
                    )
                }

                // Color tagging for visual scanning by elderly
                Text("شخصی‌سازی رنگ قرص (برای تشخیص راحت‌تر):", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.SemiBold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    colorsList.forEachIndexed { index, color ->
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(color, shape = CircleShape)
                                .border(
                                    width = if (selectedColorIndex == index) 3.dp else 0.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
                                .clickable { selectedColorIndex = index }
                        )
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("توضیحات زنگ یادآوری (اختیاری)", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("مثلاً: همراه با صبحانه") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && dose.isNotEmpty()) {
                        val formattedTime = "${hour.padStart(2, '0')}:${minute.padStart(2, '0')}"
                        viewModel.addMedication(
                            name = name,
                            dose = dose,
                            time = formattedTime,
                            frequency = frequency,
                            notes = notes,
                            colorIndex = selectedColorIndex
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_medication_button")
            ) {
                Text("ذخیره نهایی برنامه ✅", fontSize = getFontSize(14f).sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", fontSize = getFontSize(14f).sp)
            }
        }
    )
}

// Side Effect prompting dialog
@Composable
fun SideEffectLogDialog(
    medication: Medication,
    onLog: (String) -> Unit,
    onDismiss: () -> Unit,
    getFontSize: (Float) -> Float
) {
    var enteredSideEffect by remember { mutableStateOf("") }
    
    val popularSymptoms = listOf("سرگیجه و عدم تعادل", "سردرد", "حالت تهوع یا دل‌درد", "خواب‌آلودگی مداوم")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("⚠️ ثبت عارضه جانبی داروی ${medication.name}", fontSize = getFontSize(18f).sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "آیا پس از مصرف این دارو عارضه ویژه‌ای احساس کردید؟ یکی را انتخاب کنید یا بنویسید:",
                    fontSize = getFontSize(14f).sp
                )
                
                // Clickable recommendation badges
                popularSymptoms.forEach { symptom ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { enteredSideEffect = symptom },
                        colors = CardDefaults.cardColors(
                            containerColor = if (enteredSideEffect == symptom) PillRed.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            symptom,
                            fontSize = getFontSize(13f).sp,
                            modifier = Modifier.padding(10.dp),
                            color = if (enteredSideEffect == symptom) PillRed else Color.Unspecified
                        )
                    }
                }

                OutlinedTextField(
                    value = enteredSideEffect,
                    onValueChange = { enteredSideEffect = it },
                    label = { Text("توضیح یا عوارض دیگر", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onLog(enteredSideEffect) },
                colors = ButtonDefaults.buttonColors(containerColor = PillRed)
            ) {
                Text("ثبت مصرف با عارضه جانبی ⚠️", fontSize = getFontSize(14f).sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منصرف شدم", fontSize = getFontSize(14f).sp)
            }
        }
    )
}

// Add Daily health journal note dialog
@Composable
fun AddHealthNoteDialog(
    viewModel: MedicationViewModel,
    onDismiss: () -> Unit,
    getFontSize: (Float) -> Float
) {
    var noteText by remember { mutableStateOf("") }
    var systolic by remember { mutableStateOf("۱۲۰") }
    var diastolic by remember { mutableStateOf("۸۰") }
    var heartRate by remember { mutableStateOf("۷۲") }
    var selectedStatus by remember { mutableStateOf("خوب") }

    val statuses = listOf("عالی", "خوب", "معمولی", "ضعیف")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("📝 ثبت وضعیت سلامت روزانه", fontSize = getFontSize(18f).sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("وضعیت عمومی امروز شما:", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    statuses.forEach { status ->
                        Button(
                            onClick = { selectedStatus = status },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedStatus == status) WarmTealPrimary else Color.LightGray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text(status, fontSize = getFontSize(12f).sp, color = Color.White)
                        }
                    }
                }

                Text("پایش علائم بیومتریک (اختیاری):", fontSize = getFontSize(14f).sp, fontWeight = FontWeight.SemiBold)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = systolic,
                        onValueChange = { systolic = it },
                        label = { Text("فشار سیستولیک", fontSize = getFontSize(9f).sp) },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = diastolic,
                        onValueChange = { diastolic = it },
                        label = { Text("فشار دیاستولیک", fontSize = getFontSize(9f).sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = heartRate,
                    onValueChange = { heartRate = it },
                    label = { Text("ضربان قلب بار در دقیقه", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("روزنوشت علائم و عوارض شما", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("امروز چطور بودید؟ آیا علامت خاصی داشتید؟") },
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (noteText.isNotEmpty()) {
                        viewModel.addHealthNote(noteText, systolic, diastolic, heartRate, selectedStatus)
                        onDismiss()
                    }
                }
            ) {
                Text("ثبت یادداشت سلامت روزانه ✅", fontSize = getFontSize(14f).sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", fontSize = getFontSize(14f).sp)
            }
        }
    )
}

// Add Caregiver Contact Details Dialog
@Composable
fun AddCaregiverDialog(
    viewModel: MedicationViewModel,
    onDismiss: () -> Unit,
    getFontSize: (Float) -> Float
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isEmergencyEnabled by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("👨‍👩‍👧‍👦 ثبت مکنات جدید مراقب (خانواده)", fontSize = getFontSize(18f).sp, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "در صورت فراموشی دوز دارو و پاسخ ندادن به هشدارها، پیامک خودکار اضطراری به این شخص ارسال خواهد شد تا پیگیری کنند.",
                    fontSize = getFontSize(13f).sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("نام فرد مراقب (مثلا: دخترم مریم)", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("شماره موبایل جهت پیام ارسال پیامک هشدار", fontSize = getFontSize(12f).sp) },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ارسال پیام خودکار فعال باشد", fontSize = getFontSize(14f).sp)
                    Switch(
                        checked = isEmergencyEnabled,
                        onCheckedChange = { isEmergencyEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && phone.isNotEmpty()) {
                        viewModel.addCaregiver(name, phone, isEmergencyEnabled)
                        onDismiss()
                    }
                }
            ) {
                Text("ثبت و ذخیره مخاطب 👨‍👩‍👧‍👦", fontSize = getFontSize(14f).sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف", fontSize = getFontSize(14f).sp)
            }
        }
    )
}

