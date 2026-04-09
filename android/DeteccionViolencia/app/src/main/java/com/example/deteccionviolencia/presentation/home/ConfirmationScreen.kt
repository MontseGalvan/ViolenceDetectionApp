package com.example.deteccionviolencia.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.deteccionviolencia.data.audio.AudioPlaybackManager

/**
 * Pantalla de confirmación que permite al usuario revisar su audio grabado
 * @param onReRecord Callback para descartar el audio actual y volver a la pantalla de grabación
 * @param onAnalyze Callback para iniciar el proceso de clasificación
 * @param homeViewModel ViewModel que contiene el estado del audio y la transcripción actual
 */
@Composable
fun ConfirmationScreen(
    onReRecord: () -> Unit,
    onAnalyze: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.state.collectAsState(initial = HomeState())
    val context = LocalContext.current
    
    /** Gestor de reproducción para permitir al usuario escuchar su grabación */
    val playbackManager = remember { AudioPlaybackManager(context) }
    val isPlaying by playbackManager.isPlaying.collectAsState()

    /** Prepara el reproductor cada vez que el archivo de audio en el estado cambia */
    LaunchedEffect(uiState.audioFile) {
        uiState.audioFile?.let { playbackManager.prepare(it) }
    }

    /** Asegura la liberación de los recursos del reproductor al salir del composable */
    DisposableEffect(Unit) {
        onDispose { playbackManager.release() }
    }

    /** Formateo de la duración del audio para su visualización */
    val durationFormatted = remember(uiState.durationMs) {
        val totalSeconds = (uiState.durationMs / 1000).toInt()
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .padding(top = 40.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Grabación Finalizada",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(
            text = "Duración: $durationFormatted",
            style = MaterialTheme.typography.bodyLarge
        )
        
        /** Indicador de progreso de la transcripción */
        if (uiState.isProcessing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Text("Transcribiendo audio...")
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "¿Qué te gustaría hacer ahora?",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { playbackManager.playPause() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(if (isPlaying) "Pausar" else "Reproducir")
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onReRecord,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Regrabar")
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onAnalyze,
            modifier = Modifier.fillMaxWidth(),
            enabled = uiState.transcribedText.isNotBlank() && !uiState.isProcessing
        ) {
            Text("Analizar Ahora")
        }

        Spacer(modifier = Modifier.height(24.dp))

        /** Campo de texto que muestra la transcripción y permite al usuario editarlo manualmente */
        OutlinedTextField(
            value = uiState.transcribedText,
            onValueChange = { homeViewModel.onEvent(HomeEvent.TranscribedTextChanged(it)) },
            label = { Text("Transcripción (puedes editar)") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            maxLines = 5,
            enabled = !uiState.isProcessing
        )
    }
}
