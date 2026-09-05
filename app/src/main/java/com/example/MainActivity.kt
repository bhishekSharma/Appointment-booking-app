package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.HospitalRepository
import com.example.ui.components.AuthDialog
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.PatientRecordsScreen
import com.example.ui.screens.UserAppointmentsScreen
import com.example.ui.screens.UserHomeScreen
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HospitalViewModel
import com.example.ui.viewmodel.HospitalViewModelFactory

enum class Screen(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    HOME("Find Doctors", Icons.Filled.MedicalServices, Icons.Outlined.MedicalServices),
    APPOINTMENTS("My Bookings", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    RECORDS("Cloud Vault", Icons.Filled.FolderShared, Icons.Outlined.FolderShared),
    ADMIN("Admin Portal", Icons.Filled.AdminPanelSettings, Icons.Outlined.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val viewModel: HospitalViewModel = viewModel(
                    factory = HospitalViewModelFactory(application)
                )
                HospitalApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HospitalApp(viewModel: HospitalViewModel) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val appointments by viewModel.userAppointments.collectAsStateWithLifecycle()
    val pendingCount = remember(appointments) {
        appointments.count { it.status.equals("PENDING", ignoreCase = true) }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            currentScreen = if (currentUser?.role == "ADMIN" || isAdminLoggedIn) Screen.ADMIN else Screen.HOME
        }
    }

    // If user is not logged in, display the Single Login Page for all users
    if (currentUser == null) {
        LoginScreen(
            viewModel = viewModel,
            onLoginSuccess = { isAdmin ->
                if (isAdmin) {
                    currentScreen = Screen.ADMIN
                } else {
                    currentScreen = Screen.HOME
                }
            }
        )
        return
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val availableScreens = if (isAdminLoggedIn) {
                        listOf(Screen.ADMIN, Screen.HOME, Screen.APPOINTMENTS, Screen.RECORDS)
                    } else {
                        listOf(Screen.HOME, Screen.APPOINTMENTS, Screen.RECORDS)
                    }

                    availableScreens.forEach { screen ->
                        val isSelected = currentScreen == screen
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                currentScreen = screen
                            },
                            icon = {
                                if (screen == Screen.APPOINTMENTS && pendingCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(
                                                containerColor = Color(0xFFE65100),
                                                contentColor = Color.White
                                            ) {
                                                Text("$pendingCount")
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                        contentDescription = screen.title,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MedicalBluePrimary,
                                selectedTextColor = MedicalBluePrimary,
                                indicatorColor = MedicalBluePrimary.copy(alpha = 0.12f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${screen.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                Screen.HOME -> UserHomeScreen(
                    viewModel = viewModel,
                    onNavigateToAppointments = { currentScreen = Screen.APPOINTMENTS },
                    onNavigateToRecords = { currentScreen = Screen.RECORDS },
                    onNavigateToAdmin = { currentScreen = Screen.ADMIN }
                )
                Screen.APPOINTMENTS -> UserAppointmentsScreen(
                    viewModel = viewModel,
                    onNavigateToHome = { currentScreen = Screen.HOME }
                )
                Screen.RECORDS -> PatientRecordsScreen(
                    viewModel = viewModel
                )
                Screen.ADMIN -> AdminDashboardScreen(
                    viewModel = viewModel,
                    onSwitchToUserMode = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}
