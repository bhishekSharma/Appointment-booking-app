package com.example.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AppointmentEntity
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.StatusConfirmedText
import com.example.ui.theme.WhatsAppGreen

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfirmAppointmentDialog(
    appointment: AppointmentEntity,
    onDismiss: () -> Unit,
    onConfirm: (
        confirmedDate: String,
        confirmedTime: String,
        tokenNumber: String,
        estimatedWaitingTime: String,
        adminNotes: String,
        sendWhatsAppImmediately: Boolean,
        context: Context
    ) -> Unit
) {
    val context = LocalContext.current
    var confirmedDate by remember { mutableStateOf(appointment.preferredDate) }
    var confirmedTime by remember { mutableStateOf("10:30 AM") }
    var tokenNumber by remember { mutableStateOf(if (appointment.tokenNumber.isNotBlank()) appointment.tokenNumber else "TK-${(101..199).random()}") }
    var estimatedWaitingTime by remember { mutableStateOf(if (appointment.estimatedWaitingTime.isNotBlank()) appointment.estimatedWaitingTime else "15-20 mins") }
    var adminNotes by remember { mutableStateOf("OPD Room #204, 2nd Floor, Main Block") }
    var sendWhatsAppNow by remember { mutableStateOf(true) }

    val presetTimes = listOf("09:30 AM", "10:30 AM", "11:15 AM", "02:00 PM", "04:30 PM", "05:45 PM")
    val presetWaiting = listOf("5-10 mins", "15-20 mins", "25-30 mins", "35-45 mins")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Confirm & Assign Slot",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalBlueDark
                        )
                        Text(
                            text = "Admin Scheduling & Token Allocation",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Patient and Doctor Details Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MedicalBlueLight.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${appointment.patientName} (${appointment.patientAge} yrs, ${appointment.patientGender})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MedicalBlueDark
                            )
                        }
                        Text(
                            text = "Mobile: ${appointment.userMobile} • Mode: ${appointment.consultationType}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dr: ${appointment.doctorName} (${appointment.doctorSpecialty})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MedicalBluePrimary
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
                                color = Color(0xFF475569),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confirmed Date
                OutlinedTextField(
                    value = confirmedDate,
                    onValueChange = { confirmedDate = it },
                    label = { Text("Confirmed Date *") },
                    leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Time Slot with Preset Chips
                Text("Select Confirmed Time Slot *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetTimes.forEach { slot ->
                        val isSelected = confirmedTime == slot
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) MedicalBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { confirmedTime = slot }
                        ) {
                            Text(
                                text = slot,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = confirmedTime,
                    onValueChange = { confirmedTime = it },
                    label = { Text("Custom Time Slot") },
                    leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Token Number & Waiting Time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = tokenNumber,
                        onValueChange = { tokenNumber = it },
                        label = { Text("Token Number *") },
                        leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_token_input")
                    )

                    OutlinedTextField(
                        value = estimatedWaitingTime,
                        onValueChange = { estimatedWaitingTime = it },
                        label = { Text("Est. Wait Time *") },
                        leadingIcon = { Icon(Icons.Default.HourglassBottom, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_wait_time_input")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                // Quick wait time chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presetWaiting.forEach { wait ->
                        val isSelected = estimatedWaitingTime == wait
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isSelected) Color(0xFFE0F2FE) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { estimatedWaitingTime = wait }
                        ) {
                            Text(
                                text = wait,
                                fontSize = 10.sp,
                                color = if (isSelected) MedicalBluePrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = adminNotes,
                    onValueChange = { adminNotes = it },
                    label = { Text("Admin / Location Instructions") },
                    placeholder = { Text("e.g. Room 204, Bring prior test reports") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Send WhatsApp confirmation checkbox
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { sendWhatsAppNow = !sendWhatsAppNow }
                            .padding(10.dp)
                    ) {
                        Checkbox(
                            checked = sendWhatsAppNow,
                            onCheckedChange = { sendWhatsAppNow = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Chat, contentDescription = null, tint = WhatsAppGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Send WhatsApp Confirmation with 1-Click",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                            Text(
                                text = "Sends formatted token details to ${appointment.userMobile} & logs timestamp",
                                fontSize = 11.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onConfirm(
                            confirmedDate,
                            confirmedTime,
                            tokenNumber,
                            estimatedWaitingTime,
                            adminNotes,
                            sendWhatsAppNow,
                            context
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StatusConfirmedText
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("admin_confirm_submit_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm Appointment & Assign Token",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
