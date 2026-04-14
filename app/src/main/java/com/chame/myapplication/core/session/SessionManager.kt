package com.chame.myapplication.core.session

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManager @Inject constructor(@ApplicationContext private val context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    var token: String = prefs.getString("token", "") ?: ""
        private set

    var userId: Int = prefs.getInt("userId", 0)
        private set

    var userName: String = prefs.getString("userName", "") ?: ""
        private set

    var userRole: String = prefs.getString("userRole", "") ?: ""
        private set

    var userEmail: String = prefs.getString("userEmail", "") ?: ""
        private set

    var profilePhotoBase64: String = prefs.getString("profilePhoto", "") ?: ""
        private set

    var biometricEnabled: Boolean = prefs.getBoolean("biometricEnabled", false)
        private set

    var fcmToken: String = ""
        private set

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkModeFlow: StateFlow<Boolean> = _isDarkMode.asStateFlow()
    val isDarkMode: Boolean get() = _isDarkMode.value

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        prefs.edit().putBoolean("dark_mode", enabled).apply()
    }

    fun saveFcmToken(token: String) {
        fcmToken = token
    }

    fun saveSession(token: String, userId: Int, name: String, role: String, email: String) {
        this.token = token
        this.userId = userId
        this.userName = name
        this.userRole = role
        this.userEmail = email
        prefs.edit()
            .putString("token", token)
            .putInt("userId", userId)
            .putString("userName", name)
            .putString("userRole", role)
            .putString("userEmail", email)
            .apply()
    }

    fun saveProfilePhoto(base64: String) {
        profilePhotoBase64 = base64
        prefs.edit().putString("profilePhoto", base64).apply()
    }

    fun setBiometricEnabled(enabled: Boolean) {
        biometricEnabled = enabled
        prefs.edit().putBoolean("biometricEnabled", enabled).apply()
    }

    fun clearSession() {
        profilePhotoBase64 = ""
        biometricEnabled = false
        token = ""
        userId = 0
        userName = ""
        userRole = ""
        userEmail = ""
        prefs.edit()
            .remove("token")
            .remove("userId")
            .remove("userName")
            .remove("userRole")
            .remove("userEmail")
            .remove("profilePhoto")
            .putBoolean("biometricEnabled", false)
            .apply()
    }

    val isLoggedIn: Boolean get() = token.isNotEmpty()
}
