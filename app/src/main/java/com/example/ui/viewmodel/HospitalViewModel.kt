package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.AppointmentEntity
import com.example.data.model.DoctorEntity
import com.example.data.model.MedicalRecordEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AdminAnalyticsData
import com.example.data.repository.HospitalRepository
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HospitalViewModel(application: Application) : AndroidViewModel(application) {

    val sessionManager = com.example.data.local.SessionManager(application)

    private val repository: HospitalRepository =
        HospitalRepository(AppDatabase.getDatabase(application), sessionManager)

    val currentUser: StateFlow<UserEntity?> = repository.currentUser
    val isAdminLoggedIn: StateFlow<Boolean> = repository.isAdminLoggedIn

    // Doctor filtering state
    private val _doctorSearchQuery = MutableStateFlow("")
    val doctorSearchQuery: StateFlow<String> = _doctorSearchQuery.asStateFlow()

    private val _selectedSpecialty = MutableStateFlow("All")
    val selectedSpecialty: StateFlow<String> = _selectedSpecialty.asStateFlow()

    private val _selectedHospital = MutableStateFlow("All")
    val selectedHospital: StateFlow<String> = _selectedHospital.asStateFlow()

    // Filtered Doctors list
    val doctors: StateFlow<List<DoctorEntity>> = combine(
        _doctorSearchQuery,
        _selectedSpecialty,
        _selectedHospital
    ) { query, specialty, hospital ->
        Triple(query, if (specialty == "All") "" else specialty, if (hospital == "All") "" else hospital)
    }.flatMapLatest { (q, spec, hosp) ->
        repository.searchDoctors(q, spec, hosp)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User Appointments
    val userAppointments: StateFlow<List<AppointmentEntity>> = repository.getAppointmentsForCurrentUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Admin Appointments Management state
    private val _adminSearchQuery = MutableStateFlow("")
    val adminSearchQuery: StateFlow<String> = _adminSearchQuery.asStateFlow()

    private val _adminStatusFilter = MutableStateFlow("All")
    val adminStatusFilter: StateFlow<String> = _adminStatusFilter.asStateFlow()

    val adminAppointments: StateFlow<List<AppointmentEntity>> = combine(
        _adminSearchQuery,
        _adminStatusFilter
    ) { query, status ->
        Pair(query, if (status == "All") "" else status.uppercase())
    }.flatMapLatest { (q, st) ->
        repository.searchAppointments(q, st)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Medical Records state
    private val _recordCategory = MutableStateFlow("All")
    val recordCategory: StateFlow<String> = _recordCategory.asStateFlow()

    private val _recordSearchQuery = MutableStateFlow("")
    val recordSearchQuery: StateFlow<String> = _recordSearchQuery.asStateFlow()

    val medicalRecords: StateFlow<List<MedicalRecordEntity>> = _recordCategory.flatMapLatest { cat ->
        repository.getRecordsByCategory(cat)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Admin Analytics
    private val _analyticsData = MutableStateFlow(AdminAnalyticsData())
    val analyticsData: StateFlow<AdminAnalyticsData> = _analyticsData.asStateFlow()

    // Notification toast events
    private val _toastEvents = MutableSharedFlow<String>()
    val toastEvents: SharedFlow<String> = _toastEvents.asSharedFlow()

    init {
        refreshAnalytics()
    }

    fun setDoctorSearchQuery(query: String) {
        _doctorSearchQuery.value = query
    }

    fun setSelectedSpecialty(specialty: String) {
        _selectedSpecialty.value = specialty
    }

    fun setSelectedHospital(hospital: String) {
        _selectedHospital.value = hospital
    }

    fun setAdminSearchQuery(query: String) {
        _adminSearchQuery.value = query
    }

    fun setAdminStatusFilter(status: String) {
        _adminStatusFilter.value = status
    }

    fun setRecordCategory(cat: String) {
        _recordCategory.value = cat
    }

    fun setRecordSearchQuery(query: String) {
        _recordSearchQuery.value = query
    }

    fun refreshAnalytics() {
        viewModelScope.launch {
            _analyticsData.value = repository.getAdminAnalytics()
        }
    }

    // --- Actions ---
    fun bookAppointment(
        doctor: DoctorEntity,
        patientName: String,
        patientAge: Int,
        patientGender: String,
        userMobile: String,
        userEmail: String,
        preferredDate: String,
        reason: String,
        consultationType: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.createAppointmentRequest(
                doctor = doctor,
                patientName = patientName,
                patientAge = patientAge,
                patientGender = patientGender,
                userMobile = userMobile,
                userEmail = userEmail,
                preferredDate = preferredDate,
                reason = reason,
                consultationType = consultationType
            )
            refreshAnalytics()
            _toastEvents.emit("Appointment requested! Status: Pending Admin Confirmation")
            onSuccess()
        }
    }

    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(appointmentId)
            refreshAnalytics()
            _toastEvents.emit("Appointment cancelled.")
        }
    }

    fun adminConfirmAppointment(
        appointment: AppointmentEntity,
        confirmedDate: String,
        confirmedTime: String,
        tokenNumber: String,
        estimatedWaitingTime: String,
        adminNotes: String,
        sendWhatsAppImmediately: Boolean,
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.confirmAppointment(
                appointmentId = appointment.id,
                confirmedDate = confirmedDate,
                confirmedTime = confirmedTime,
                tokenNumber = tokenNumber,
                estimatedWaitingTime = estimatedWaitingTime,
                adminNotes = adminNotes
            )
            if (sendWhatsAppImmediately) {
                WhatsAppHelper.sendAppointmentConfirmation(
                    context = context,
                    rawPhone = appointment.userMobile,
                    patientName = appointment.patientName,
                    doctorName = appointment.doctorName,
                    hospital = appointment.hospital,
                    date = confirmedDate,
                    time = confirmedTime,
                    token = tokenNumber,
                    waitingTime = estimatedWaitingTime
                )
                repository.markWhatsAppSent(appointment.id)
            }
            refreshAnalytics()
            _toastEvents.emit("Appointment #$tokenNumber confirmed successfully!")
            onSuccess()
        }
    }

    fun sendWhatsAppConfirmation(context: Context, appointment: AppointmentEntity) {
        viewModelScope.launch {
            WhatsAppHelper.sendAppointmentConfirmation(
                context = context,
                rawPhone = appointment.userMobile,
                patientName = appointment.patientName,
                doctorName = appointment.doctorName,
                hospital = appointment.hospital,
                date = appointment.confirmedDate.ifBlank { appointment.preferredDate },
                time = appointment.confirmedTime.ifBlank { "Assigned Slot" },
                token = appointment.tokenNumber.ifBlank { "TK-${appointment.id}" },
                waitingTime = appointment.estimatedWaitingTime.ifBlank { "15 mins" }
            )
            repository.markWhatsAppSent(appointment.id)
            refreshAnalytics()
            _toastEvents.emit("WhatsApp confirmation message launched and logged.")
        }
    }

    fun updateAppointmentStatus(appointmentId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateAppointmentStatus(appointmentId, newStatus)
            refreshAnalytics()
            _toastEvents.emit("Appointment marked as $newStatus")
        }
    }

    fun addDoctor(doctor: DoctorEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.insertDoctor(doctor)
            refreshAnalytics()
            _toastEvents.emit("Doctor ${doctor.name} added successfully!")
            onSuccess()
        }
    }

    fun updateDoctor(doctor: DoctorEntity, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.updateDoctor(doctor)
            refreshAnalytics()
            _toastEvents.emit("Doctor details updated.")
            onSuccess()
        }
    }

    fun deleteDoctor(doctor: DoctorEntity) {
        viewModelScope.launch {
            repository.deleteDoctor(doctor)
            refreshAnalytics()
            _toastEvents.emit("Doctor removed from directory.")
        }
    }

    fun toggleDoctorAvailability(doctor: DoctorEntity) {
        viewModelScope.launch {
            val updated = doctor.copy(isAvailable = !doctor.isAvailable)
            repository.updateDoctor(updated)
            _toastEvents.emit("${doctor.name} marked as ${if (updated.isAvailable) "Available" else "On Leave"}")
        }
    }

    fun addMedicalRecord(
        patientName: String,
        recordType: String,
        title: String,
        doctorName: String,
        hospitalName: String,
        date: String,
        notes: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.addMedicalRecord(
                patientName = patientName,
                recordType = recordType,
                title = title,
                doctorName = doctorName,
                hospitalName = hospitalName,
                date = date,
                notes = notes
            )
            refreshAnalytics()
            _toastEvents.emit("Medical record uploaded & encrypted into Cloud Vault!")
            onSuccess()
        }
    }

    fun deleteMedicalRecord(record: MedicalRecordEntity) {
        viewModelScope.launch {
            repository.deleteMedicalRecord(record)
            refreshAnalytics()
            _toastEvents.emit("Record removed from cloud vault.")
        }
    }

    // --- Authentication ---
    fun login(mobile: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.login(mobile, password)
            if (res.isSuccess) {
                val user = res.getOrNull()
                refreshAnalytics()
                if (user?.role == "ADMIN") {
                    onResult(true, "Admin verified. Welcome to Admin Dashboard!")
                } else {
                    onResult(true, "Welcome back, ${user?.name}!")
                }
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, mobile: String, email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val res = repository.registerUser(name, mobile, email, password)
            if (res.isSuccess) {
                val user = res.getOrNull()
                refreshAnalytics()
                if (user?.role == "ADMIN") {
                    onResult(true, "Admin credentials registered! Opening Admin Dashboard.")
                } else {
                    onResult(true, "Account created successfully!")
                }
            } else {
                onResult(false, res.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun logout() {
        repository.logout()
        refreshAnalytics()
    }

    fun updateUserProfile(updatedUser: UserEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.updateUserProfile(updatedUser)
            _toastEvents.emit("Profile details updated successfully.")
            onComplete()
        }
    }

    fun getSessionToken(): String = sessionManager.getSessionToken()

    fun switchToAdminDirect() {
        repository.setAdminDirect(true)
        refreshAnalytics()
    }

    fun switchToUserDirect() {
        repository.setAdminDirect(false)
        refreshAnalytics()
    }
}

class HospitalViewModelFactory(private val application: Application) : androidx.lifecycle.ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HospitalViewModel::class.java)) {
            return HospitalViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


