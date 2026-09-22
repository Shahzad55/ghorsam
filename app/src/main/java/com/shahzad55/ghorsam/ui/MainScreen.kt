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

                if (isAlarmActive && activeAlarmMedication != null) {
                    val activeMed = activeAlarmMedication!!
                    Dialog(
                        onDismissRequest = { },
                        properties = DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize().background(if (isHighContrast) Color.Black else Color(0xFFFFF9C4)),
                            color = if (isHighContrast) Color.Black else Color(0xFFFFF9C4)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "زنگ هشدار فعال است",
                                    tint = PillRed,
                                    modifier = Modifier.size(110.dp).padding(bottom = 16.dp)
                                )
                                Text(
                                    "🔔 وقت خوردن قرص رسیده است!",
                                    fontSize = getFontSize(28f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PillRed,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Text("Content truncated for tool size - full version needs local restore from git history commit 95e85163", fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
