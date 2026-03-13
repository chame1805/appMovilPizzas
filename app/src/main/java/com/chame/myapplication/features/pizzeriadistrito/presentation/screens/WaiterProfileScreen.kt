package com.chame.myapplication.features.pizzeriadistrito.presentation.screens

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.chame.myapplication.core.session.SessionManager
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaiterProfileScreen(
    sessionManager: SessionManager,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val pizzaOrange = Color(0xFFE65100)

    var profileBitmap by remember { mutableStateOf(base64ToBitmap(sessionManager.profilePhotoBase64)) }
    var biometricEnabled by remember { mutableStateOf(sessionManager.biometricEnabled) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            profileBitmap = bitmap
            sessionManager.saveProfilePhoto(bitmapToBase64(bitmap))
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("MI PERFIL", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(130.dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap!!.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                    }
                }
            }

            Button(onClick = { cameraLauncher.launch(null) }, shape = RoundedCornerShape(14.dp)) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Tomar foto")
            }

            CardItem("Usuario", sessionManager.userName.ifBlank { "Sin nombre" })
            CardItem("Correo", sessionManager.userEmail.ifBlank { "Sin correo" })
            CardItem("Rol", sessionManager.userRole.ifBlank { "MESERO" })

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Seguridad", fontWeight = FontWeight.Bold)
                    Text("Activar huella digital", style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { checked ->
                            if (!checked) {
                                biometricEnabled = false
                                sessionManager.setBiometricEnabled(false)
                            } else {
                                showBiometricPrompt(
                                    context = context,
                                    onSuccess = {
                                        biometricEnabled = true
                                        sessionManager.setBiometricEnabled(true)
                                    },
                                    onFailure = {
                                        biometricEnabled = false
                                        sessionManager.setBiometricEnabled(false)
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardItem(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(2.dp))
            Text(value, fontWeight = FontWeight.SemiBold)
        }
    }
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
}

private fun base64ToBitmap(base64: String): Bitmap? {
    return runCatching {
        if (base64.isBlank()) return null
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}

private fun showBiometricPrompt(
    context: Context,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val activity = context.findFragmentActivity() ?: run {
        onFailure(); return
    }

    val canAuth = BiometricManager.from(activity).canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )
    if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) {
        onFailure()
        return
    }

    val executor = ContextCompat.getMainExecutor(activity)
    val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess()
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            onFailure()
        }

        override fun onAuthenticationFailed() {
            onFailure()
        }
    })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Activar acceso biométrico")
        .setSubtitle("Confirma tu huella para activar")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        .build()

    prompt.authenticate(promptInfo)
}

private tailrec fun Context.findFragmentActivity(): FragmentActivity? = when (this) {
    is FragmentActivity -> this
    is ContextWrapper -> baseContext.findFragmentActivity()
    else -> null
}
