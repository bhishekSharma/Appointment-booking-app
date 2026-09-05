package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DoctorEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MedicalTealLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BookAppointmentDialog(
    doctor: DoctorEntity,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onConfirmBooking: (
        patientName: String,
        patientAge: Int,
        patientGender: String,
        userMobile: String,
        userEmail: String,
        preferredDate: String,
        reason: String,
        consultationType: String
    ) -> Unit
) {
    var patientName by remember { mutableStateOf(currentUser?.name ?: "") }
    var patientAgeText by remember { mutableStateOf("32") }
    var patientGender by remember { mutableStateOf("Male") }
    var mobileNumber by remember { mutableStateOf(currentUser?.mobile ?: "") }
    var emailAddress by remember { mutableStateOf(currentUser?.email ?: "") }
    var reason by remember { mutableStateOf("") }
    var consultationType by remember { mutableStateOf("In-Person Consultation") }

    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val cal = Calendar.getInstance()
    val todayStr = dateFormat.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrowStr = dateFormat.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val dayAfterStr = dateFormat.format(cal.time)

    var preferredDate by remember { mutableStateOf(tomorrowStr) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                            text = "Request Appointment",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalBlueDark
                        )
                        Text(
                            text = "No fixed time needed now. Admin will assign time & token.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Doctor Info Summary Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MedicalBlueLight.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        DoctorAvatarBadge(
                            name = doctor.name,
                            specialty = doctor.specialty,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = doctor.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MedicalBlueDark
                            )
                            Text(
                                text = "${doctor.specialty} • ${doctor.hospital}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Fee: ₹${doctor.consultationFee.toInt()} | Available: ${doctor.availableDays}",
                                fontSize = 11.sp,
                                color = MedicalTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Consultation Type Selection
                Text(
                    text = "Consultation Mode",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val modes = listOf(
                        Pair("In-Person Consultation", Icons.Default.MedicalServices),
                        Pair("Video Consultation", Icons.Default.Videocam)
                    )
                    modes.forEach { (mode, icon) ->
                        val isSelected = consultationType == mode
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) MedicalBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { consultationType = mode }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = mode,
                                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (mode.startsWith("In-Person")) "Hospital Visit" else "Video Consult",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Preferred Date Selector
                Text(
                    text = "Preferred Date",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dates = listOf(
                        Pair("Today", todayStr),
                        Pair("Tomorrow", tomorrowStr),
                        Pair("+2 Days", dayAfterStr)
                    )
                    dates.forEach { (label, dateVal) ->
                        val isSelected = preferredDate == dateVal
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MedicalTealLight else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier
                                .weight(1f)
                                .border(
                                    1.dp,
                                    if (isSelected) MedicalTeal else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { preferredDate = dateVal }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) MedicalTeal else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dateVal.take(6),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Patient Details Fields
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text("Patient Full Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = patientAgeText,
                        onValueChange = { patientAgeText = it.filter { ch -> ch.isDigit() }.take(3) },
                        label = { Text("Age *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.4f)
                    )

                    Column(modifier = Modifier.weight(0.6f)) {
                        Text("Gender", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            listOf("Male", "Female").forEach { g ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { patientGender = g }
                                        .padding(end = 6.dp)
                                ) {
                                    RadioButton(
                                        selected = patientGender == g,
                                        onClick = { patientGender = g }
                                    )
                                    Text(g, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = mobileNumber,
                    onValueChange = { mobileNumber = it.filter { ch -> ch.isDigit() }.take(10) },
                    label = { Text("Mobile Number (for WhatsApp) *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("patient_mobile_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = emailAddress,
                    onValueChange = { emailAddress = it },
                    label = { Text("Email Address (Optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Symptoms / Reason for Visit") },
                    placeholder = { Text("e.g. Fever, routine checkup, knee pain") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pending Info Note
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Initial Status: PENDING. Hospital Admin will verify doctor availability, assign your token number and exact time slot, and send WhatsApp confirmation.",
                            fontSize = 11.sp,
                            color = Color(0xFFE65100),
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (patientName.isBlank()) {
                            errorMessage = "Please enter patient name"
                            return@Button
                        }
                        if (mobileNumber.length < 10) {
                            errorMessage = "Please enter valid 10-digit mobile number"
                            return@Button
                        }
                        val age = patientAgeText.toIntOrNull() ?: 30
                        onConfirmBooking(
                            patientName,
                            age,
                            patientGender,
                            mobileNumber,
                            emailAddress,
                            preferredDate,
                            reason,
                            consultationType
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedicalBluePrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_booking_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Submit Appointment Request",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
