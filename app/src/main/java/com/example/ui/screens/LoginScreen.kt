package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MedicalBorder
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.MedicalTextMuted
import com.example.ui.theme.MedicalTextPrimary
import com.example.ui.theme.MedicalTextSecondary
import com.example.ui.viewmodel.HospitalViewModel

@Composable
fun LoginScreen(
    viewModel: HospitalViewModel,
    onLoginSuccess: (isAdmin: Boolean) -> Unit
) {
    val focusManager = LocalFocusManager.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register

    // Form inputs
    var mobileInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val cleanMobile = mobileInput.trim()
    val cleanPassword = passwordInput.trim()

    val isAdminMatch = (cleanMobile == "9831488878" && cleanPassword == "admin@1234") ||
                       (cleanMobile == "9831498878" && cleanPassword == "art@1234")

    fun performSubmit() {
        errorMessage = null
        if (cleanMobile.isBlank()) {
            errorMessage = "Please enter mobile number"
            return
        }
        if (cleanPassword.length < 8) {
            errorMessage = "Password must be at least 8 characters"
            return
        }

        isLoading = true
        focusManager.clearFocus()

        if (selectedTab == 0) {
            viewModel.login(cleanMobile, cleanPassword) { success, msg ->
                isLoading = false
                if (success) {
                    val user = viewModel.currentUser.value
                    val isAdmin = (user?.role == "ADMIN") || isAdminMatch
                    onLoginSuccess(isAdmin)
                } else {
                    errorMessage = msg ?: "Login failed. Please check credentials."
                }
            }
        } else {
            if (nameInput.trim().isBlank()) {
                isLoading = false
                errorMessage = "Please enter full name"
                return
            }
            viewModel.register(nameInput.trim(), cleanMobile, emailInput.trim(), cleanPassword) { success, msg ->
                isLoading = false
                if (success) {
                    val user = viewModel.currentUser.value
                    val isAdmin = (user?.role == "ADMIN") || isAdminMatch
                    onLoginSuccess(isAdmin)
                } else {
                    errorMessage = msg ?: "Registration failed."
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Branding Header
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MedicalBluePrimary)
                    .border(2.dp, Color.White, RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.LocalHospital,
                    contentDescription = "MediCare Logo",
                    tint = Color.White,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MediCare Hospital",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MedicalBlueDark,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Single portal for patients & hospital administrators",
                fontSize = 12.sp,
                color = MedicalTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Card Container
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MedicalBorder, RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Sign In / Register Tabs
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color(0xFFF1F5F9),
                        contentColor = MedicalBluePrimary,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MedicalBorder, RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0; errorMessage = null },
                            text = {
                                Text(
                                    text = "Sign In",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1; errorMessage = null },
                            text = {
                                Text(
                                    text = "New Patient",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Admin Detection Pill
                    AnimatedVisibility(visible = isAdminMatch) {
                        Surface(
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AdminPanelSettings,
                                    contentDescription = null,
                                    tint = Color(0xFF16A34A),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Admin Credentials Recognized",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF166534)
                                    )
                                    Text(
                                        text = "Signing in will open the Admin Management Dashboard.",
                                        fontSize = 10.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }

                    // Form Fields
                    if (selectedTab == 1) {
                        // Registration Name
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text("Full Name *") },
                            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MedicalBluePrimary) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("register_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = MedicalBluePrimary,
                                unfocusedLabelColor = Color(0xFF334155),
                                focusedPlaceholderColor = Color(0xFF64748B),
                                unfocusedPlaceholderColor = Color(0xFF64748B),
                                focusedLeadingIconColor = MedicalBluePrimary,
                                unfocusedLeadingIconColor = Color(0xFF475569),
                                focusedBorderColor = MedicalBluePrimary,
                                unfocusedBorderColor = Color(0xFF94A3B8),
                                cursorColor = MedicalBluePrimary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Mobile Number
                    OutlinedTextField(
                        value = mobileInput,
                        onValueChange = { mobileInput = it },
                        label = { Text("Mobile Number *") },
                        placeholder = { Text("e.g. 9831488878 or 9876543210") },
                        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = MedicalBluePrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_mobile_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = MedicalBluePrimary,
                            unfocusedLabelColor = Color(0xFF334155),
                            focusedPlaceholderColor = Color(0xFF64748B),
                            unfocusedPlaceholderColor = Color(0xFF64748B),
                            focusedLeadingIconColor = MedicalBluePrimary,
                            unfocusedLeadingIconColor = Color(0xFF475569),
                            focusedBorderColor = MedicalBluePrimary,
                            unfocusedBorderColor = Color(0xFF94A3B8),
                            cursorColor = MedicalBluePrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (selectedTab == 1) {
                        // Optional Email
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            label = { Text("Email Address (Optional)") },
                            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MedicalBluePrimary) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedLabelColor = MedicalBluePrimary,
                                unfocusedLabelColor = Color(0xFF334155),
                                focusedPlaceholderColor = Color(0xFF64748B),
                                unfocusedPlaceholderColor = Color(0xFF64748B),
                                focusedLeadingIconColor = MedicalBluePrimary,
                                unfocusedLeadingIconColor = Color(0xFF475569),
                                focusedBorderColor = MedicalBluePrimary,
                                unfocusedBorderColor = Color(0xFF94A3B8),
                                cursorColor = MedicalBluePrimary,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Password Input
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        label = { Text("Password (min 8 chars) *") },
                        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MedicalBluePrimary) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFF475569)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { performSubmit() }),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = MedicalBluePrimary,
                            unfocusedLabelColor = Color(0xFF334155),
                            focusedPlaceholderColor = Color(0xFF64748B),
                            unfocusedPlaceholderColor = Color(0xFF64748B),
                            focusedLeadingIconColor = MedicalBluePrimary,
                            unfocusedLeadingIconColor = Color(0xFF475569),
                            focusedTrailingIconColor = MedicalBluePrimary,
                            unfocusedTrailingIconColor = Color(0xFF475569),
                            focusedBorderColor = MedicalBluePrimary,
                            unfocusedBorderColor = Color(0xFF94A3B8),
                            cursorColor = MedicalBluePrimary,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = EmergencyRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button
                    Button(
                        onClick = { performSubmit() },
                        enabled = !isLoading,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MedicalBluePrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("login_submit_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (selectedTab == 0) "Log In & Continue" else "Create Patient Account",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Session Security Notice
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MedicalTealLight,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MedicalTeal.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MedicalTeal,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Secure persistent session. You remain logged in across launches until you explicitly log out.",
                        fontSize = 10.sp,
                        color = Color(0xFF0F766E),
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
