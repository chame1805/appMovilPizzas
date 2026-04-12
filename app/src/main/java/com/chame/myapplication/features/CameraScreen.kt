package com.chame.myapplication.features.camera.presentation

import android.os.Environment
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.chame.myapplication.core.CameraManager
import java.io.File

@Composable
fun CameraScreen(
    cameraManager: CameraManager,
    onPhotoTaken: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }
    
    Column(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            AndroidView(
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        controller = cameraController
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    val outputDir = File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "PizzaPhotos"
                    ).apply { mkdirs() }
                    
                    cameraManager.takePhoto(outputDir) { photoPath ->
                        onPhotoTaken(photoPath)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("Tomar Foto")
            }
            
            Text(
                "Captura fotos de tus órdenes",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
