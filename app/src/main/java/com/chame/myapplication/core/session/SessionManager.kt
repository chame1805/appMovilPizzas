package com.chame.myapplication.core.session

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    var token: String = ""
        private set

    var userId: Int = 0
        private set

    var userName: String = ""
        private set

    var userRole: String = ""
        private set

    var userEmail: String = ""
        private set

    var profilePhotoBase64: String = ""
        private set

    var biometricEnabled: Boolean = false
        private set

    fun saveSession(token: String, userId: Int, name: String, role: String, email: String) {
        this.token = token
        this.userId = userId
        this.userName = name
        this.userRole = role
        this.userEmail = email
    }


    fun saveProfilePhoto(base64: String) {
        profilePhotoBase64 = base64
    }

    fun setBiometricEnabled(enabled: Boolean) {
        biometricEnabled = enabled
    }

    fun clearSession() {
        profilePhotoBase64 = ""
        biometricEnabled = false
        token = ""
        userId = 0
        userName = ""
        userRole = ""
        userEmail = ""
    }

    val isLoggedIn: Boolean get() = token.isNotEmpty()
}
