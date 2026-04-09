package com.example.deteccionviolencia.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Pantalla que permite al usuario capturar su relato de voz
 * @param onRecordingComplete Callback que se ejecuta cuando el usuario detiene la grabación
 * @param onCancel Callback para regresar a la pantalla anterior descartando la grabación actual
 * @param homeViewModel ViewModel compartido que gestiona el estado del grabador de audio
 */
@Composable
fun RecordingScreen(
    onRecordingComplete: () -> Unit,
    onCancel: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.state.collectAsState(initial = HomeState())
    var timer by remember { mutableIntStateOf(0) }

    /** Efecto que incrementa el temporizador cada segundo */
    LaunchedEffect(uiState.isRecording) {
        if (uiState.isRecording) {
            while (uiState.isRecording) {
                delay(1000)
                timer++
            }
        } else {
            timer = 0
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Grabar Relato",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 40.dp)
        )
        Text(
            text = "Presiona el micrófono para comenzar a grabar",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        /** Botón que alterna entre los estados de grabación*/
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .clickable {
                    if (!uiState.isRecording) {
                        homeViewModel.onEvent(HomeEvent.StartRecording)
                        timer = 0
                    } else {
                        homeViewModel.onEvent(HomeEvent.StopRecording)
                        onRecordingComplete()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (uiState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color.White
            )
        }

        /** Visualización del tiempo transcurrido */
        Text(
            text = formatTime(timer),
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Button(
            onClick = onCancel,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }

        Text(
            text = " Tu información es confidencial y segura",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 40.dp)
        )
    }
}

/**
 * Convierte una cantidad de segundos en una cadena de texto como minutos y segundos
 * @param seconds Cantidad total de segundos
 * @return Cadena de texto con el formato MM:SS
 */
private fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}
