package com.chame.myapplication.feacture.register.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chame.myapplication.feacture.register.presentation.viewModel.RegisterViewModel
import com.chame.myapplication.feacture.register.presentation.viewModel.RegisterViewModelFactory

@Composable
fun RegisterScreen(
    viewModelFactory: RegisterViewModelFactory,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    // Obtenemos el ViewModel con la factoría
    val vm: RegisterViewModel = viewModel(factory = viewModelFactory)
    val pizzaOrange = Color(0xFFE65100)

    AlertDialog(
        onDismissRequest = {
            vm.clearError()
            onBack()
        },
        title = {
            Text("REGISTRO DE USUARIO", fontWeight = FontWeight.Black)
        },
        text = {
            Column {
                // CAMPO NOMBRE - Conectado al ViewModel
                OutlinedTextField(
                    value = vm.nameInput.value,
                    onValueChange = { vm.onNameChange(it) },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO EMAIL - Conectado al ViewModel
                OutlinedTextField(
                    value = vm.emailInput.value,
                    onValueChange = { vm.onEmailChange(it) },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // CAMPO CONTRASEÑA - Conectado al ViewModel
                OutlinedTextField(
                    value = vm.passwordInput.value,
                    onValueChange = { vm.onPasswordChange(it) },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                // Mensaje de Error dinámico desde el ViewModel
                if (!vm.errorMessage.value.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = vm.errorMessage.value!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Ya no pasamos parámetros aquí, el VM ya tiene los datos
                    vm.register { onSuccess() }
                },
                colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange),
                // Deshabilitar mientras carga o si los campos están vacíos
                enabled = !vm.isLoading.value &&
                        vm.nameInput.value.isNotBlank() &&
                        vm.emailInput.value.isNotBlank() &&
                        vm.passwordInput.value.isNotBlank()
            ) {
                if (vm.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("CREAR CUENTA", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                vm.clearError()
                onBack()
            }) {
                Text("CANCELAR", color = Color.Gray)
            }
        }
    )
}