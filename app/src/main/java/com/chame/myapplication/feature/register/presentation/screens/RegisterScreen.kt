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
    val vm: RegisterViewModel = viewModel(factory = viewModelFactory)

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val pizzaOrange = Color(0xFFE65100)

    AlertDialog(
        onDismissRequest = {
            vm.clearError()
            onBack()
        },
        title = { Text("REGISTRO", fontWeight = FontWeight.Black) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                if (!vm.errorMessage.value.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(vm.errorMessage.value!!, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    vm.register(name, email, password) {
                        onSuccess()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = pizzaOrange),
                enabled = !vm.isLoading.value
            ) {
                if (vm.isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
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
