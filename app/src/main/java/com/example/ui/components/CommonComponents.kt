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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MedicalBlueBadge
import com.example.ui.theme.MedicalBlueDark
import com.example.ui.theme.MedicalBlueLight
import com.example.ui.theme.MedicalBluePrimary
import com.example.ui.theme.MedicalBlueSecondary
import com.example.ui.theme.MedicalBorder
import com.example.ui.theme.MedicalTeal
import com.example.ui.theme.MedicalTealLight
import com.example.ui.theme.StatusCancelledBg
import com.example.ui.theme.StatusCancelledText
import com.example.ui.theme.StatusCompletedBg
import com.example.ui.theme.StatusCompletedText
import com.example.ui.theme.StatusConfirmedBg
import com.example.ui.theme.StatusConfirmedText
import com.example.ui.theme.StatusPendingBg
import com.example.ui.theme.StatusPendingText
import com.example.ui.theme.WhatsAppDark
import com.example.ui.theme.WhatsAppGreen

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (bgColor, textColor, icon) = when (status.uppercase()) {
        "CONFIRMED" -> Triple(StatusConfirmedBg, StatusConfirmedText, Icons.Default.CheckCircle)
        "PENDING" -> Triple(StatusPendingBg, StatusPendingText, Icons.Default.HourglassTop)
        "COMPLETED" -> Triple(StatusCompletedBg, StatusCompletedText, Icons.Default.MedicalServices)
        "CANCELLED" -> Triple(StatusCancelledBg, StatusCancelledText, Icons.Default.Warning)
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Icons.Default.HourglassTop)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.uppercase(),
                color = textColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.4.sp
            )
        }
    }
}

@Composable
fun DoctorAvatarBadge(
    name: String,
    specialty: String,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .filter { it.isNotBlank() && !it.startsWith("Dr", ignoreCase = true) }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifBlank { "DR" }

    val (bgGradient, textColor) = when (specialty.lowercase()) {
        "cardiologist", "cardiology" -> Pair(Brush.linearGradient(listOf(Color(0xFFEFF6FF), Color(0xFFDBEAFE))), Color(0xFF1D4ED8))
        "dermatologist", "dermatology" -> Pair(Brush.linearGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))), Color(0xFFB45309))
        "orthopedic surgeon", "orthopedics" -> Pair(Brush.linearGradient(listOf(Color(0xFFFEF2F2), Color(0xFFFEE2E2))), Color(0xFFB91C1C))
        "pediatrician", "pediatrics" -> Pair(Brush.linearGradient(listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))), Color(0xFF047857))
        "neurologist", "neurology" -> Pair(Brush.linearGradient(listOf(Color(0xFFF0FDFA), Color(0xFFCCFBF1))), Color(0xFF0F766E))
        "dental" -> Pair(Brush.linearGradient(listOf(Color(0xFFFFF7ED), Color(0xFFFFEDD5))), Color(0xFFC2410C))
        "eye care", "ophthalmology" -> Pair(Brush.linearGradient(listOf(Color(0xFFFAF5FF), Color(0xFFF3E8FF))), Color(0xFF7E22CE))
        else -> Pair(Brush.linearGradient(listOf(MedicalBlueLight, MedicalBlueBadge)), MedicalBlueSecondary)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgGradient)
            .border(1.dp, MedicalBorder, RoundedCornerShape(16.dp))
    ) {
        Text(
            text = initials,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun WhatsAppButton(
    onClick: () -> Unit,
    text: String = "WhatsApp",
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    if (isOutlined) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = WhatsAppDark
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.linearGradient(listOf(WhatsAppGreen, WhatsAppDark))
            ),
            modifier = modifier.testTag("whatsapp_button")
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "WhatsApp",
                tint = WhatsAppGreen,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    } else {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = WhatsAppGreen,
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
            modifier = modifier.testTag("whatsapp_button")
        ) {
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "WhatsApp",
                tint = Color.White,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.border(1.dp, MedicalBorder, RoundedCornerShape(20.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CloudSecurityBanner(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MedicalTealLight,
        modifier = modifier.border(1.dp, MedicalTeal.copy(alpha = 0.2f), RoundedCornerShape(18.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(MedicalTeal)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Encrypted Vault",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "End-to-End Encrypted Cloud Storage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MedicalBlueDark
                )
                Text(
                    text = "Patient records & prescriptions stored with 256-bit AES HIPAA compliant security",
                    fontSize = 10.sp,
                    color = Color(0xFF0F766E),
                    lineHeight = 14.sp
                )
            }
        }
    }
}
