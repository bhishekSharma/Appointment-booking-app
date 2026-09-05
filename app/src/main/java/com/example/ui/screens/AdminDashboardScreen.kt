package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.AppointmentEntity
import com.example.data.model.DoctorEntity
import com.example.ui.components.AddDoctorDialog
import com.example.ui.components.ConfirmAppointmentDialog
import com.example.ui.components.DoctorAvatarBadge
import com.example.ui.components.MetricStatCard
import com.example.ui.components.StatusBadge
import com.example.ui.components.WhatsAppButton
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MedicalBlueSecondary
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.StatusCancelledText
import com.example.ui.theme.StatusConfirmedBg
import com.example.ui.theme.StatusConfirmedText
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingText
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.HospitalViewModel
import com.example.util.WhatsAppHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: HospitalViewModel,
    onSwitchToUserMode: () -> Unit
) {
    val context = LocalContext.current
    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Appointments, 1: Doctors, 2: Analytics

    val appointments by viewModel.adminAppointments.collectAsStateWithLifecycle()
    val doctors by viewModel.doctors.collectAsStateWithLifecycle()
    val analytics by viewModel.analyticsData.collectAsStateWithLifecycle()
    val searchQuery by viewModel.adminSearchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.adminStatusFilter.collectAsStateWithLifecycle()

    var appointmentToConfirm by remember { mutableStateOf<AppointmentEntity?>(null) }
    var doctorToEdit by remember { mutableStateOf<DoctorEntity?>(null) }
    var showAddDoctorDialog by remember { mutableStateOf(false) }
    var doctorToDelete by remember { mutableStateOf<DoctorEntity?>(null) }

    if (appointmentToConfirm != null) {
        ConfirmAppointmentDialog(
            appointment = appointmentToConfirm!!,
            onDismiss = { appointmentToConfirm = null },
            onConfirm = { confirmedDate, confirmedTime, tokenNumber, estimatedWaitingTime, adminNotes, sendWhatsAppImmediately, ctx ->
                viewModel.adminConfirmAppointment(
                    appointment = appointmentToConfirm!!,
                    confirmedDate = confirmedDate,
                    confirmedTime = confirmedTime,
                    tokenNumber = tokenNumber,
                    estimatedWaitingTime = estimatedWaitingTime,
                    adminNotes = adminNotes,
                    sendWhatsAppImmediately = sendWhatsAppImmediately,
                    context = ctx,
                    onSuccess = { appointmentToConfirm = null }
                )
            }
        )
    }

    if (showAddDoctorDialog || doctorToEdit != null) {
        AddDoctorDialog(
            doctorToEdit = doctorToEdit,
            onDismiss = {
                showAddDoctorDialog = false
                doctorToEdit = null
            },
            onSave = { doc ->
                if (doctorToEdit == null) {
                    viewModel.addDoctor(doc) {
                        showAddDoctorDialog = false
                    }
                } else {
                    viewModel.updateDoctor(doc) {
                        doctorToEdit = null
                    }
                }
            }
        )
    }

    if (doctorToDelete != null) {
        AlertDialog(
            onDismissRequest = { doctorToDelete = null },
            title = { Text("Remove Doctor?") },
            text = { Text("Are you sure you want to remove ${doctorToDelete?.name} from the hospital directory?") },
            confirmButton = {
                Button(
                    onClick = {
                        doctorToDelete?.let { viewModel.deleteDoctor(it) }
                        doctorToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { doctorToDelete = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (selectedAdminTab == 1) {
                FloatingActionButton(
                    onClick = { showAddDoctorDialog = true },
                    containerColor = MedicalBluePrimary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 72.dp).testTag("add_doctor_fab")
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Add Doctor")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Doctor", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .testTag("admin_dashboard_screen")
        ) {
            // Admin App Bar
            Surface(
                color = MedicalBlueDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Admin Dashboard",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "Admin: 9831488878 | Super Admin",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Switch to User Home Mode Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.18f),
                                modifier = Modifier.clickable {
                                    viewModel.switchToUserDirect()
                                    onSwitchToUserMode()
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("User App", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Logout Button
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFEF4444).copy(alpha = 0.35f),
                                modifier = Modifier.clickable {
                                    viewModel.logout()
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Log Out", tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Logout", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            // Admin Navigation Tabs
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MedicalBluePrimary
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Appointments", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.MedicalServices, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Doctors", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedAdminTab == 2,
                    onClick = {
                        selectedAdminTab = 2
                        viewModel.refreshAnalytics()
                    },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                )
            }

            when (selectedAdminTab) {
                0 -> AdminAppointmentsTab(
                    appointments = appointments,
                    searchQuery = searchQuery,
                    statusFilter = statusFilter,
                    onSearchChange = { viewModel.setAdminSearchQuery(it) },
                    onStatusFilterChange = { viewModel.setAdminStatusFilter(it) },
                    onConfirmClick = { appointmentToConfirm = it },
                    onSendWhatsAppClick = { viewModel.sendWhatsAppConfirmation(context, it) },
                    onMarkCompleted = { viewModel.updateAppointmentStatus(it.id, "COMPLETED") },
                    onCancelClick = { viewModel.updateAppointmentStatus(it.id, "CANCELLED") }
                )
                1 -> AdminDoctorsTab(
                    doctors = doctors,
                    onEditClick = { doctorToEdit = it },
                    onDeleteClick = { doctorToDelete = it },
                    onToggleAvailability = { viewModel.toggleDoctorAvailability(it) }
                )
                2 -> AdminAnalyticsTab(
                    analytics = analytics,
                    appointments = appointments,
                    onRefresh = { viewModel.refreshAnalytics() }
                )
            }
        }
    }
}

@Composable
fun AdminAppointmentsTab(
    appointments: List<AppointmentEntity>,
    searchQuery: String,
    statusFilter: String,
    onSearchChange: (String) -> Unit,
    onStatusFilterChange: (String) -> Unit,
    onConfirmClick: (AppointmentEntity) -> Unit,
    onSendWhatsAppClick: (AppointmentEntity) -> Unit,
    onMarkCompleted: (AppointmentEntity) -> Unit,
    onCancelClick: (AppointmentEntity) -> Unit
) {
    val filters = listOf("All", "Pending", "Confirmed", "Completed", "Cancelled")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
    ) {
        // Search & Filter controls
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search by patient, doctor, hospital, or token #...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MedicalBluePrimary) },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().testTag("admin_search_appointments_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { f ->
                        val isSelected = statusFilter.equals(f, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onStatusFilterChange(f) },
                            label = { Text(f, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedicalBluePrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Appointments Found (${appointments.size})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        if (appointments.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No matching appointments", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        } else {
            items(appointments, key = { it.id }) { appt ->
                AdminAppointmentItemCard(
                    appointment = appt,
                    onConfirmClick = { onConfirmClick(appt) },
                    onSendWhatsAppClick = { onSendWhatsAppClick(appt) },
                    onMarkCompleted = { onMarkCompleted(appt) },
                    onCancelClick = { onCancelClick(appt) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun AdminAppointmentItemCard(
    appointment: AppointmentEntity,
    onConfirmClick: () -> Unit,
    onSendWhatsAppClick: () -> Unit,
    onMarkCompleted: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPending = appointment.status.equals("PENDING", ignoreCase = true)
    val isConfirmed = appointment.status.equals("CONFIRMED", ignoreCase = true)
    val hasWhatsAppSent = appointment.whatsappSentAt != null

    val timeFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .testTag("admin_appointment_item_${appointment.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = appointment.patientName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MedicalBlueDark
                    )
                    Text(
                        text = " (${appointment.patientAge}y, ${appointment.patientGender})",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(status = appointment.status)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Mobile: ${appointment.userMobile} • Mode: ${appointment.consultationType}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0D47A1)
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Doctor: ${appointment.doctorName} (${appointment.doctorSpecialty})",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Hospital: ${appointment.hospital}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (appointment.reason.isNotBlank()) {
                Text(
                    text = "Reason: \"${appointment.reason}\"",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Scheduling Details Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isConfirmed) StatusConfirmedBg else Color(0xFFFFF8E1),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    if (isConfirmed) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🎫 Token: ${appointment.tokenNumber}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = StatusConfirmedText
                            )
                            Text(
                                text = "⏳ Wait: ${appointment.estimatedWaitingTime}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = Color(0xFFE65100)
                            )
                        }
                        Text(
                            text = "Slot: ${appointment.confirmedDate} at ${appointment.confirmedTime}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MedicalBlueDark
                        )
                        if (appointment.adminNotes.isNotBlank()) {
                            Text(
                                text = "Notes: ${appointment.adminNotes}",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    } else {
                        Text(
                            text = "Preferred Date: ${appointment.preferredDate}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "Awaiting admin confirmation to assign token number & time slot.",
                            fontSize = 11.sp,
                            color = Color(0xFF8D6E63)
                        )
                    }

                    // WhatsApp Sent Log Timestamp
                    if (hasWhatsAppSent) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "WhatsApp Sent: ${timeFormat.format(Date(appointment.whatsappSentAt!!))}",
                                fontSize = 10.sp,
                                color = WhatsAppDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (isPending) {
                    Button(
                        onClick = onConfirmClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                        modifier = Modifier.weight(1f).height(40.dp).testTag("admin_confirm_btn_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Confirm & Assign Token", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isConfirmed) {
                    Button(
                        onClick = onSendWhatsAppClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppGreen),
                        modifier = Modifier.weight(1.1f).height(40.dp).testTag("admin_send_whatsapp_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (hasWhatsAppSent) "Resend WhatsApp" else "1-Click WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onMarkCompleted,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(0.7f).height(40.dp)
                    ) {
                        Text("Complete", fontSize = 11.sp)
                    }
                }

                if (isPending || isConfirmed) {
                    IconButton(
                        onClick = onCancelClick,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminDoctorsTab(
    doctors: List<DoctorEntity>,
    onEditClick: (DoctorEntity) -> Unit,
    onDeleteClick: (DoctorEntity) -> Unit,
    onToggleAvailability: (DoctorEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hospital Specialists Directory (${doctors.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalBlueDark
                )
            }
        }

        items(doctors, key = { it.id }) { doc ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            DoctorAvatarBadge(
                                name = doc.name,
                                specialty = doc.specialty,
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(doc.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MedicalBlueDark)
                                Text(doc.specialty, fontSize = 12.sp, color = MedicalTeal, fontWeight = FontWeight.SemiBold)
                                Text("${doc.hospital} • ${doc.location}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onEditClick(doc) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MedicalBluePrimary)
                            }
                            IconButton(onClick = { onDeleteClick(doc) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fee: ₹${doc.consultationFee.toInt()} | ${doc.experienceYears}y exp", fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (doc.isAvailable) "Available" else "On Leave", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (doc.isAvailable) StatusConfirmedText else Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Switch(
                                checked = doc.isAvailable,
                                onCheckedChange = { onToggleAvailability(doc) },
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Days: ${doc.availableDays} • WhatsApp: ${doc.whatsappNumber}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun AdminAnalyticsTab(
    analytics: com.example.data.repository.AdminAnalyticsData,
    appointments: List<AppointmentEntity>,
    onRefresh: () -> Unit
) {
    var auditSearch by remember { mutableStateOf("") }
    val filteredLogs = remember(appointments, auditSearch) {
        if (auditSearch.isBlank()) appointments
        else appointments.filter {
            it.patientName.contains(auditSearch, ignoreCase = true) ||
            it.doctorName.contains(auditSearch, ignoreCase = true) ||
            it.tokenNumber.contains(auditSearch, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, top = 12.dp)
    ) {
        // Analytics Summary Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hospital Operations Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MedicalBlueDark)
                    Text("Real-time appointments, revenue & capacity metrics", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = MedicalBluePrimary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Metrics Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Total Bookings",
                        value = "${analytics.totalAppointments}",
                        subtitle = "+12% this week",
                        icon = Icons.Default.CalendarMonth,
                        accentColor = MedicalBluePrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Pending Action",
                        value = "${analytics.pendingRequests}",
                        subtitle = "Needs token",
                        icon = Icons.Default.HourglassTop,
                        accentColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Confirmed Today",
                        value = "${analytics.confirmedToday}",
                        subtitle = "Slots scheduled",
                        icon = Icons.Default.CheckCircle,
                        accentColor = StatusConfirmedText,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Est. Revenue",
                        value = "₹${analytics.totalRevenue.toInt()}",
                        subtitle = "Consultation fees",
                        icon = Icons.Default.AttachMoney,
                        accentColor = MedicalTeal,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricStatCard(
                        title = "Active Doctors",
                        value = "${analytics.totalDoctors}",
                        subtitle = "Across 6 depts",
                        icon = Icons.Default.MedicalServices,
                        accentColor = Color(0xFF6A1B9A),
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Cloud Records",
                        value = "${analytics.totalRecords}",
                        subtitle = "256-bit Encrypted",
                        icon = Icons.Default.FolderShared,
                        accentColor = MedicalBlueSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Status Breakdown Visualizer
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Appointment Status Distribution", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicalBlueDark)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Proportional Visual Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        val pending = analytics.pendingRequests.coerceAtLeast(1)
                        val confirmed = analytics.confirmedToday.coerceAtLeast(1)
                        val completed = analytics.completedAppointments.coerceAtLeast(1)

                        Box(modifier = Modifier.weight(pending.toFloat()).fillMaxSize().background(Color(0xFFFFA000)))
                        Box(modifier = Modifier.weight(confirmed.toFloat()).fillMaxSize().background(StatusConfirmedText))
                        Box(modifier = Modifier.weight(completed.toFloat()).fillMaxSize().background(MedicalBluePrimary))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFFFA000)))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pending (${analytics.pendingRequests})", fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(StatusConfirmedText))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Confirmed (${analytics.confirmedToday})", fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MedicalBluePrimary))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Completed (${analytics.completedAppointments})", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Searchable Audit Logs
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text("Searchable Patient Bookings Audit Log", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MedicalBlueDark)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = auditSearch,
                    onValueChange = { auditSearch = it },
                    placeholder = { Text("Filter audit records by patient name or token...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("analytics_audit_search_input")
                )
            }
        }

        items(filteredLogs) { log ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${log.patientName} (${log.userMobile})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${log.doctorName} • ${log.hospital}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Token: ${if (log.tokenNumber.isNotBlank()) log.tokenNumber else "Pending"} • Date: ${log.preferredDate}",
                            fontSize = 11.sp,
                            color = MedicalBluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    StatusBadge(status = log.status)
                }
            }
        }
    }
}
