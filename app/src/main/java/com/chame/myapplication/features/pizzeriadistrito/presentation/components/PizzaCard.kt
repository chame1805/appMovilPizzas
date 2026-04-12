package com.chame.myapplication.features.pizzeriadistrito.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PizzaCard(
    name: String,
    price: Double,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    // Definimos los colores aquí para evitar errores de referencia
    val pizzaOrange = Color(0xFFE65100)
    val pizzaYellow = Color(0xFFFFB74D)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Imagen de $name",
                modifier = Modifier
                    .size(110.dp)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(end = 16.dp, top = 12.dp, bottom = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Usamos el amarillo suave de tu paleta oficial
                Surface(
                    color = pizzaYellow.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "$${String.format("%.2f", price)}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = pizzaOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}