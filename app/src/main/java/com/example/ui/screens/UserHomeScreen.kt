package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.DoctorEntity
import com.example.data.model.UserEntity
import com.example.ui.components.AuthDialog
import com.example.ui.components.BookAppointmentDialog
import com.example.ui.components.CloudSecurityBanner
import com.example.ui.components.DoctorAvatarBadge
import com.example.ui.components.UserProfileDialog
import com.example.ui.components.WhatsAppButton
import com.example.ui.theme.AmberRating
import com.example.ui.theme.AmberRatingBg
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MedicalBlueBadge
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MedicalBlueSecondary
import com.example.ui.theme.MedicalBorder
import com.example.ui.theme.MedicalBorderMedium
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTextMuted
import com.example.ui.theme.MedicalTextPrimary
import com.example.ui.theme.MedicalTextSecondary
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen
import com.example.ui.viewmodel.HospitalViewModel
import com.example.util.WhatsAppHelper

data class SpecialtyItem(
    val name: String,
    val emoji: String,
    val bgColor: Color,
    val activeColor: Color
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserHomeScreen(
    viewModel: HospitalViewModel,
    onNavigateToAppointments: () -> Unit,
    onNavigateToRecords: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val context = LocalContext.current
    val doctors by viewModel.doctors.collectAsStateWithLifecycle()
    val searchQuery by viewModel.doctorSearchQuery.collectAsStateWithLifecycle()
    val selectedSpecialty by viewModel.selectedSpecialty.collectAsStateWithLifecycle()
    val selectedHospital by viewModel.selectedHospital.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    var selectedDoctorForBooking by remember { mutableStateOf<DoctorEntity?>(null) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val userAppointments by viewModel.userAppointments.collectAsStateWithLifecycle()
    val medicalRecords by viewModel.medicalRecords.collectAsStateWithLifecycle()

    if (showProfileDialog && currentUser != null) {
        UserProfileDialog(
            user = currentUser,
            sessionToken = viewModel.getSessionToken(),
            totalBookings = userAppointments.size,
            totalRecords = medicalRecords.size,
            onDismiss = { showProfileDialog = false },
            onLogout = {
                showProfileDialog = false
                viewModel.logout()
            },
            onUpdateProfile = { updated ->
                viewModel.updateUserProfile(updated)
            }
        )
    }

    val specialtyItems = listOf(
        SpecialtyItem("All", "🏥", Color(0xFFEFF6FF), Color(0xFF2563EB)),
        SpecialtyItem("Cardiologist", "🫀", Color(0xFFEFF6FF), Color(0xFF1D4ED8)),
        SpecialtyItem("Neurologist", "🧠", Color(0xFFF0FDFA), Color(0xFF0F766E)),
        SpecialtyItem("Dental", "🦷", Color(0xFFFFF7ED), Color(0xFFC2410C)),
        SpecialtyItem("Eye Care", "👁️", Color(0xFFFAF5FF), Color(0xFF7E22CE)),
        SpecialtyItem("Orthopedic Surgeon", "🦴", Color(0xFFFEF2F2), Color(0xFFB91C1C)),
        SpecialtyItem("Pediatrician", "👶", Color(0xFFECFDF5), Color(0xFF047857)),
        SpecialtyItem("Dermatologist", "✨", Color(0xFFFFFBEB), Color(0xFFB45309)),
        SpecialtyItem("General Physician", "🩺", Color(0xFFF0FDF4), Color(0xFF15803D)),
        SpecialtyItem("ENT", "👂", Color(0xFFF5F3FF), Color(0xFF6D28D9)),
        SpecialtyItem("Gynecologist", "🌸", Color(0xFFFDF2F8), Color(0xFFBE185D))
    )

    val hospitals = listOf("All", "City Care Multi-Specialty Hospital", "Apollo Health Center", "Metro General Hospital", "Lifeline Children's Hospital", "St. Jude Neuro & Spine Institute")

    if (selectedDoctorForBooking != null) {
        BookAppointmentDialog(
            doctor = selectedDoctorForBooking!!,
            currentUser = currentUser,
            onDismiss = { selectedDoctorForBooking = null },
            onConfirmBooking = { patientName, patientAge, patientGender, userMobile, userEmail, preferredDate, reason, consultationType ->
                viewModel.bookAppointment(
                    doctor = selectedDoctorForBooking!!,
                    patientName = patientName,
                    patientAge = patientAge,
                    patientGender = patientGender,
                    userMobile = userMobile,
                    userEmail = userEmail,
                    preferredDate = preferredDate,
                    reason = reason,
                    consultationType = consultationType,
                    onSuccess = {
                        selectedDoctorForBooking = null
                        onNavigateToAppointments()
                    }
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("user_home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // High-Density Header
        item {
            Surface(
                color = Color.White,
                tonalElevation = 1.dp,
                shadowElevation = 1.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "GOOD MORNING",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MedicalBluePrimary,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentUser?.name ?: "Alex Thompson",
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MedicalTextPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isAdmin) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFFDCFCE7),
                                    modifier = Modifier.clickable { onNavigateToAdmin() }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    }
                                }
                            }

                            // User Avatar Pill with initials
                            val userInitials = (currentUser?.name ?: "Alex Thompson")
                                .split(" ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .joinToString("")
                                .ifBlank { "AT" }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MedicalBlueBadge)
                                    .clickable {
                                        showProfileDialog = true
                                    }
                            ) {
                                Text(
                                    text = userInitials,
                                    color = MedicalBlueSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // High-Density Clean Search Bar (Slate-100 container, rounded-2xl)
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MedicalTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextField(
                                value = searchQuery,
                                onValueChange = { viewModel.setDoctorSearchQuery(it) },
                                placeholder = {
                                    Text(
                                        "Search doctors, specialty, hospital...",
                                        fontSize = 13.sp,
                                        color = MedicalTextMuted
                                    )
                                },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    disabledContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("doctor_search_input")
                            )
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setDoctorSearchQuery("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MedicalTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Specialties Horizontal Section (High-Density style with pastel rounded squares & icons)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Specialties",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTextPrimary
                    )
                    Text(
                        text = if (selectedSpecialty != "All") "Reset" else "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalBluePrimary,
                        modifier = Modifier.clickable {
                            if (selectedSpecialty != "All") {
                                viewModel.setSelectedSpecialty("All")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(specialtyItems) { item ->
                        val isSelected = selectedSpecialty == item.name || (item.name == "All" && selectedSpecialty == "All")
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable {
                                    viewModel.setSelectedSpecialty(item.name)
                                }
                                .padding(vertical = 2.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) item.activeColor else item.bgColor)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) item.activeColor else MedicalBorder,
                                        shape = RoundedCornerShape(18.dp)
                                    )
                            ) {
                                Text(
                                    text = item.emoji,
                                    fontSize = 22.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (item.name == "General Physician") "General" else if (item.name == "Orthopedic Surgeon") "Orthopedic" else item.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) item.activeColor else MedicalTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation Tiles: My Bookings & Cloud Vault
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToAppointments() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MedicalBlueLight)
                        ) {
                            Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MedicalBluePrimary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("My Bookings", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MedicalTextPrimary)
                            Text("Status & tokens", fontSize = 10.sp, color = MedicalTextSecondary)
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
                        .clickable { onNavigateToRecords() }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MedicalTealLight)
                        ) {
                            Icon(Icons.Default.FolderShared, contentDescription = null, tint = MedicalTeal, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Cloud Vault", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MedicalTextPrimary)
                            Text("Secure records", fontSize = 10.sp, color = MedicalTextSecondary)
                        }
                    }
                }
            }
        }

        // Top Rated Doctors Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Top Rated Doctors",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MedicalTextPrimary
                )
                Text(
                    text = "${doctors.size} available today",
                    fontSize = 11.sp,
                    color = MedicalTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Doctor Cards in High-Density Layout
        if (doctors.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .border(1.dp, MedicalBorder, RoundedCornerShape(20.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = MedicalTextMuted,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No doctors match search criteria",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MedicalTextPrimary
                        )
                        Text(
                            text = "Try clearing search keywords or selecting another specialty.",
                            fontSize = 11.sp,
                            color = MedicalTextSecondary
                        )
                    }
                }
            }
        } else {
            items(doctors, key = { it.id }) { doctor ->
                DoctorCard(
                    doctor = doctor,
                    onBookClick = { selectedDoctorForBooking = doctor },
                    onWhatsAppClick = {
                        WhatsAppHelper.openDoctorChat(
                            context = context,
                            rawPhone = doctor.whatsappNumber,
                            doctorName = doctor.name,
                            specialty = doctor.specialty,
                            hospital = doctor.hospital
                        )
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                )
            }
        }

        // Bottom Cloud Storage Banner
        item {
            CloudSecurityBanner(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun DoctorCard(
    doctor: DoctorEntity,
    onBookClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MedicalBorder, RoundedCornerShape(22.dp))
            .testTag("doctor_card_${doctor.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Section: Avatar, Name, Rating pill, Specialty, Hospital & Exp
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                DoctorAvatarBadge(
                    name = doctor.name,
                    specialty = doctor.specialty,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = doctor.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MedicalTextPrimary,
                            maxLines = 1
                        )
                        // Amber Star Rating Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AmberRatingBg
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "★",
                                    color = AmberRating,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "${doctor.rating}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = doctor.specialty,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = MedicalBluePrimary
                    )

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${doctor.hospital} • ${doctor.experienceYears} yrs exp",
                        fontSize = 10.sp,
                        color = MedicalTextSecondary,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Row: Consultation Fee on Left | WhatsApp & Book Now on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .border(
                        width = 0.5.dp,
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(0.dp)
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Consultation Fee",
                        fontSize = 9.sp,
                        color = MedicalTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "₹${doctor.consultationFee.toInt()}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MedicalTextPrimary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    WhatsAppButton(
                        onClick = onWhatsAppClick,
                        text = "WhatsApp",
                        isOutlined = false,
                        modifier = Modifier.height(36.dp)
                    )

                    Button(
                        onClick = onBookClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                        modifier = Modifier
                            .height(36.dp)
                            .testTag("book_doctor_btn_${doctor.id}")
                    ) {
                        Text(
                            text = "Book Now",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

