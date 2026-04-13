package com.chame.myapplication.feactures.Admin.presentation.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chame.myapplication.feactures.Admin.presentation.viewModel.AdminViewModel
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onBackClick: () -> Unit = {},
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pizzaOrange = Color(0xFFE65100)

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            viewModel.setEditPhotoBase64(bitmapToBase64(bitmap))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ADMINISTRACIÓN", fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Text("Gestión del menú", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Salir", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = pizzaOrange,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
                containerColor = pizzaOrange,
                contentColor = Color.White
            ) { Icon(Icons.Default.Add, "Nuevo") }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.pizzas) { pizza ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        val photoBitmap = decodeBase64ToBitmap(state.localPhotosByName[pizza.nombre.trim().lowercase()].orEmpty())
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (photoBitmap != null) {
                                Image(
                                    bitmap = photoBitmap.asImageBitmap(),
                                    contentDescription = "Foto pizza",
                                    modifier = Modifier.size(54.dp)
                                )
                                Spacer(Modifier.size(10.dp))
                            }
                            Column(Modifier.weight(1f)) {
                                Text(pizza.nombre, fontWeight = FontWeight.Black, fontSize = 18.sp)
                                Text("$${pizza.precio}", color = pizzaOrange, fontWeight = FontWeight.Bold)
                            }
                            Row {
                                IconButton(onClick = { viewModel.openEditDialog(pizza) }) {
                                    Icon(Icons.Default.Edit, "Editar", tint = Color.Gray)
                                }
                                IconButton(onClick = { pizza.id?.let { viewModel.removePizza(it) } }) {
                                    Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = pizzaOrange
                )
            }

            state.error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (state.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.closeDialog() },
                title = {
                    Text(
                        if (state.selectedPizza == null) "NUEVA PIZZA" else "EDITAR PIZZA",
                        fontWeight = FontWeight.Black
                    )
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = state.editNombre,
                            onValueChange = { viewModel.setEditNombre(it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.editPrecio,
                            onValueChange = { viewModel.setEditPrecio(it) },
                            label = { Text("Precio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { cameraLauncher.launch(null) }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null)
                            Spacer(Modifier.size(8.dp))
                            Text("Tomar foto local")
                        }
                        val preview = decodeBase64ToBitmap(state.editPhotoBase64)
                        if (preview != null) {
                            Spacer(Modifier.height(8.dp))
                            Image(
                                bitmap = preview.asImageBitmap(),
                                contentDescription = "Preview",
                                modifier = Modifier.fillMaxWidth().height(120.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val p = state.editPrecio.toDoubleOrNull() ?: 0.0
                            if (state.selectedPizza == null) {
                                viewModel.addPizza(state.editNombre, p)
                            } else {
                                state.selectedPizza?.id?.let { viewModel.editPizza(it, state.editNombre, p) }
                            }
                            viewModel.closeDialog()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange),
                        enabled = state.editNombre.isNotBlank() && state.editPrecio.isNotBlank()
                    ) { Text("GUARDAR") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.closeDialog() }) {
                        Text("CANCELAR", color = Color.Gray)
                    }
                }
            )
        }
    }
}

private fun bitmapToBase64(bitmap: Bitmap): String {
    val out = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
    return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
}

private fun decodeBase64ToBitmap(base64: String): Bitmap? {
    if (base64.isBlank()) return null
    return runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }.getOrNull()
}
