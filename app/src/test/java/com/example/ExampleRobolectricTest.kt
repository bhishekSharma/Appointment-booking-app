package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.SessionManager
import com.example.data.model.UserEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MediCare", appName)
    }

    @Test
    fun `test persistent session save and logout`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sessionManager = SessionManager(context)

        val user = UserEntity(
            id = 42,
            name = "Sarah Connor",
            mobile = "9876543210",
            email = "sarah@example.com",
            password = "password123",
            role = "USER"
        )

        // Save session
        sessionManager.saveSession(user)
        assertTrue(sessionManager.isLoggedIn())
        assertEquals(42L, sessionManager.getLoggedInUserId())
        assertEquals("9876543210", sessionManager.getLoggedInUserMobile())
        assertEquals("USER", sessionManager.getLoggedInUserRole())

        // Clear session (Logout)
        sessionManager.clearSession()
        assertFalse(sessionManager.isLoggedIn())
        assertEquals(-1L, sessionManager.getLoggedInUserId())
    }

    @Test
    fun `test admin session recognition`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sessionManager = SessionManager(context)

        val adminUser = UserEntity(
            id = 10,
            name = "Hospital Administrator",
            mobile = "9831488878",
            email = "admin@medicare.com",
            password = "admin@1234",
            role = "ADMIN"
        )

        sessionManager.saveSession(adminUser)
        assertTrue(sessionManager.isLoggedIn())
        assertEquals("ADMIN", sessionManager.getLoggedInUserRole())
        assertEquals("9831488878", sessionManager.getLoggedInUserMobile())
    }
}
