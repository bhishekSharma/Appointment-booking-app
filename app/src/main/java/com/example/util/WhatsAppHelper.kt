package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import java.net.URLEncoder

object WhatsAppHelper {

    fun openDoctorChat(
        context: Context,
        rawPhone: String,
        doctorName: String,
        specialty: String,
        hospital: String
    ) {
        val cleanPhone = formatPhoneNumber(rawPhone)
        val defaultMessage = "Hello Dr. $doctorName ($specialty, $hospital), I am inquiring about appointment availability and consultation details via MediCare App."
        launchWhatsApp(context, cleanPhone, defaultMessage)
    }

    fun sendAppointmentConfirmation(
        context: Context,
        rawPhone: String,
        patientName: String,
        doctorName: String,
        hospital: String,
        date: String,
        time: String,
        token: String,
        waitingTime: String
    ) {
        val cleanPhone = formatPhoneNumber(rawPhone)
        val message = """
            🏥 *MediCare Hospital Appointment Confirmed*
            
            Dear *$patientName*,
            Your appointment has been successfully scheduled!
            
            👨‍⚕️ *Doctor:* Dr. $doctorName
            🏢 *Hospital:* $hospital
            📅 *Date:* $date
            ⏰ *Time Slot:* $time
            🎫 *Token Number:* $token
            ⏳ *Est. Waiting Time:* $waitingTime
            
            Please arrive 10 minutes prior to your time slot. Show this token at the reception.
            For queries, reply to this message.
        """.trimIndent()

        launchWhatsApp(context, cleanPhone, message)
    }

    fun openHelpdeskChat(context: Context) {
        val phone = "919831498878"
        val message = "Hello MediCare Hospital Helpdesk, I need assistance with booking an appointment."
        launchWhatsApp(context, phone, message)
    }

    private fun launchWhatsApp(context: Context, phone: String, message: String) {
        try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            try {
                // Fallback to web link
                val webUri = Uri.parse("https://wa.me/$phone?text=${URLEncoder.encode(message, "UTF-8")}")
                val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(webIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "Could not open WhatsApp: ${ex.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        val digitsOnly = phone.filter { it.isDigit() }
        return if (digitsOnly.length == 10) {
            "91$digitsOnly"
        } else if (digitsOnly.startsWith("91") && digitsOnly.length == 12) {
            digitsOnly
        } else if (digitsOnly.isEmpty()) {
            "919831498878"
        } else {
            digitsOnly
        }
    }
}
