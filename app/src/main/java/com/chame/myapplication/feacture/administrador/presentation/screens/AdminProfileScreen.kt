package com.chame.myapplication.feacture.administrador.presentation.screens

import android.graphics.Bitmap
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.feacture.administrador.presentation.viewModel.AdminDashboardViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    sessionManager: SessionManager,
    onBackClick: () -> Unit,
    dashboardEntry: androidx.navigation.NavBackStackEntry,
    viewModel: AdminDashboardViewModel = hiltViewModel(dashboardEntry)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)
    val successColor = Color(0xFF2E7D32)

    var profileBitmap by remember {
        mutableStateOf(base64ToBitmapAdmin(sessionManager.profilePhotoBase64))
    }
    var darkMode by remember { mutableStateOf(sessionManager.isDarkMode) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            profileBitmap = bitmap
            sessionManager.saveProfilePhoto(bitmapToBase64Admin(bitmap))
        }
    }

    val totalSales = state.sales.size
    val totalEarnings = state.sales.sumOf { it.price }
    val completedOrders = state.sales.count { it.status == "COMPLETED" || it.status == "DELIVERED" }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("MI PERFIL", fontWeight = FontWeight.Black, fontSize = 18.sp)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- FOTO DE PERFIL ---
            Surface(
                modifier = Modifier.size(130.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (profileBitmap != null) {
                        Image(
                            bitmap = profileBitmap!!.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = pizzaOrange,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { cameraLauncher.launch(null) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange)
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("Tomar foto", fontWeight = FontWeight.Bold)
            }

            // --- DATOS DEL ADMIN ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "INFORMACIÓN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = pizzaOrange,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(12.dp))
                    AdminInfoRow(
                        icon = Icons.Default.Person,
                        label = "Nombre",
                        value = sessionManager.userName.ifBlank { "Sin nombre" }
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))
                    AdminInfoRow(
                        icon = Icons.Default.Email,
                        label = "Correo",
                        value = sessionManager.userEmail.ifBlank { "Sin correo" }
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))
                    AdminInfoRow(
                        icon = Icons.Default.AdminPanelSettings,
                        label = "Rol",
                        value = "ADMINISTRADOR"
                    )
                }
            }

            // --- ESTADÍSTICAS ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.horizontalGradient(listOf(pizzaOrange, pizzaYellow)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "MIS ESTADÍSTICAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White.copy(alpha = 0.85f),
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(
                            label = "VENTAS",
                            value = "$totalSales",
                            icon = Icons.Default.ShoppingCart
                        )
                        StatItem(
                            label = "COMPLETADAS",
                            value = "$completedOrders",
                            icon = Icons.Default.ShoppingCart
                        )
                        StatItem(
                            label = "GANANCIAS",
                            value = "$${String.format(java.util.Locale.US, "%.0f", totalEarnings)}",
                            icon = Icons.Default.ShoppingCart
                        )
                    }
                }
            }

            // --- MODO OSCURO ---
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.DarkMode, contentDescription = null, tint = pizzaOrange)
                        Column {
                            Text("Modo oscuro", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Cambia la apariencia de la app",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    Switch(
                        checked = darkMode,
                        onCheckedChange = { enabled ->
                            darkMode = enabled
                            sessionManager.setDarkMode(enabled)
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun AdminInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(8.dp))
            Text(label, color = Color.Gray, fontSize = 14.sp)
        }
        Text(value, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.Black, color = Color.White)
    }
}

private fun bitmapToBase64Admin(bitmap: Bitmap): String {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
    return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT)
}

private fun base64ToBitmapAdmin(base64: String): Bitmap? {
    return runCatching {
        if (base64.isBlank()) return null
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}
