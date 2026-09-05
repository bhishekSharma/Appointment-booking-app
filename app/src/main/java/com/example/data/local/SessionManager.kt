package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.UserEntity
import java.util.UUID

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "medicare_secure_session_prefs"
        private const val KEY_IS_LOGGED_IN = "key_is_logged_in"
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_MOBILE = "key_user_mobile"
        private const val KEY_USER_ROLE = "key_user_role"
        private const val KEY_SESSION_TOKEN = "key_session_token"
        private const val KEY_LOGIN_TIMESTAMP = "key_login_timestamp"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_EMAIL = "key_user_email"
    }

    /**
     * Saves user login session securely to persistent SharedPreferences.
     */
    fun saveSession(user: UserEntity) {
        val existingToken = prefs.getString(KEY_SESSION_TOKEN, null)
        val sessionToken = existingToken ?: ("MDC-" + UUID.randomUUID().toString().take(8).uppercase())
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putLong(KEY_USER_ID, user.id)
            .putString(KEY_USER_MOBILE, user.mobile)
            .putString(KEY_USER_ROLE, user.role)
            .putString(KEY_USER_NAME, user.name)
            .putString(KEY_USER_EMAIL, user.email)
            .putString(KEY_SESSION_TOKEN, sessionToken)
            .putLong(KEY_LOGIN_TIMESTAMP, System.currentTimeMillis())
            .apply()
    }

    /**
     * Checks if a valid session exists.
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Retrieves the stored user ID.
     */
    fun getLoggedInUserId(): Long {
        return prefs.getLong(KEY_USER_ID, -1L)
    }

    /**
     * Retrieves the stored mobile number.
     */
    fun getLoggedInUserMobile(): String? {
        return prefs.getString(KEY_USER_MOBILE, null)
    }

    /**
     * Retrieves the stored user role (USER or ADMIN).
     */
    fun getLoggedInUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, "USER")
    }

    /**
     * Retrieves active session token.
     */
    fun getSessionToken(): String {
        return prefs.getString(KEY_SESSION_TOKEN, "MDC-ACTIVE") ?: "MDC-ACTIVE"
    }

    /**
     * Retrieves login timestamp.
     */
    fun getLoginTimestamp(): Long {
        return prefs.getLong(KEY_LOGIN_TIMESTAMP, 0L)
    }

    /**
     * Clears only the session state. Does NOT delete user data from Room database.
     */
    fun clearSession() {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_ID)
            .remove(KEY_USER_MOBILE)
            .remove(KEY_USER_ROLE)
            .remove(KEY_SESSION_TOKEN)
            .remove(KEY_LOGIN_TIMESTAMP)
            .apply()
    }
}
