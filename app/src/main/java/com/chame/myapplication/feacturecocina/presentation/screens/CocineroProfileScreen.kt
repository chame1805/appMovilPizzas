package com.chame.myapplication.feacturecocina.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.chame.myapplication.core.session.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocineroProfileScreen(
    sessionManager: SessionManager,
    onBackClick: () -> Unit
) {
    val pizzaOrange = Color(0xFFE65100)
    var darkMode by remember { mutableStateOf(sessionManager.isDarkMode) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = pizzaOrange,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            }

            ProfileCard("Usuario", sessionManager.userName.ifBlank { "Sin nombre" })
            ProfileCard("Correo", sessionManager.userEmail.ifBlank { "Sin correo" })
            ProfileCard("Rol", "COCINERO")

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
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
                        Text("Modo oscuro", fontWeight = FontWeight.SemiBold)
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
        }
    }
}

@Composable
private fun ProfileCard(label: String, value: String) {
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
