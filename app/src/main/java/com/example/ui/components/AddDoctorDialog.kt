package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DoctorEntity
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBluePrimary

@Composable
fun AddDoctorDialog(
    doctorToEdit: DoctorEntity? = null,
    onDismiss: () -> Unit,
    onSave: (DoctorEntity) -> Unit
) {
    var name by remember { mutableStateOf(doctorToEdit?.name ?: "Dr. ") }
    var specialty by remember { mutableStateOf(doctorToEdit?.specialty ?: "Cardiologist") }
    var hospital by remember { mutableStateOf(doctorToEdit?.hospital ?: "City Care Multi-Specialty Hospital") }
    var location by remember { mutableStateOf(doctorToEdit?.location ?: "Downtown Medical Enclave") }
    var experienceText by remember { mutableStateOf((doctorToEdit?.experienceYears ?: 10).toString()) }
    var feeText by remember { mutableStateOf((doctorToEdit?.consultationFee ?: 500.0).toInt().toString()) }
    var ratingText by remember { mutableStateOf((doctorToEdit?.rating ?: 4.8).toString()) }
    var availableDays by remember { mutableStateOf(doctorToEdit?.availableDays ?: "Mon, Wed, Fri") }
    var whatsappNumber by remember { mutableStateOf(doctorToEdit?.whatsappNumber ?: "+919831498878") }
    var bio by remember { mutableStateOf(doctorToEdit?.bio ?: "Specialist with extensive clinical expertise.") }
    var error by remember { mutableStateOf<String?>(null) }

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
                            text = if (doctorToEdit == null) "Add New Doctor" else "Edit Doctor Profile",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MedicalBlueDark
                        )
                        Text(
                            text = "Manage hospital specialist roster & WhatsApp hotline",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Doctor Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("doctor_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = specialty,
                    onValueChange = { specialty = it },
                    label = { Text("Specialty *") },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null) },
                    placeholder = { Text("e.g. Cardiologist, Dermatologist, Orthopedic") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = hospital,
                    onValueChange = { hospital = it },
                    label = { Text("Hospital Name *") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Hospital Location / Area *") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = experienceText,
                        onValueChange = { experienceText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Exp (Yrs)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = feeText,
                        onValueChange = { feeText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Fee (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = ratingText,
                        onValueChange = { ratingText = it },
                        label = { Text("Rating") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = availableDays,
                    onValueChange = { availableDays = it },
                    label = { Text("Available Days *") },
                    placeholder = { Text("e.g. Mon, Wed, Fri or All Weekdays") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = whatsappNumber,
                    onValueChange = { whatsappNumber = it },
                    label = { Text("Doctor WhatsApp Number *") },
                    leadingIcon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    placeholder = { Text("+919831498878") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Doctor Bio & Expertise") },
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(error ?: "", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || specialty.isBlank() || hospital.isBlank()) {
                            error = "Please fill in all required fields"
                            return@Button
                        }
                        val doc = DoctorEntity(
                            id = doctorToEdit?.id ?: 0,
                            name = name.trim(),
                            specialty = specialty.trim(),
                            hospital = hospital.trim(),
                            location = location.trim(),
                            experienceYears = experienceText.toIntOrNull() ?: 5,
                            consultationFee = feeText.toDoubleOrNull() ?: 500.0,
                            rating = ratingText.toDoubleOrNull() ?: 4.8,
                            availableDays = availableDays.trim(),
                            whatsappNumber = whatsappNumber.trim(),
                            department = specialty.trim(),
                            bio = bio.trim(),
                            isAvailable = doctorToEdit?.isAvailable ?: true
                        )
                        onSave(doc)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_doctor_button")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (doctorToEdit == null) "Add Doctor to Directory" else "Save Changes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
