package com.example.deteccionviolencia.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Pantalla de transición que informa al usuario que su relato está siendo procesado
 * @param onAnalysisComplete Callback que se dispara cuando el servidor devuelve un resultado
 * @param onNavigateBack Callback para regresar en caso de error en la comunicación
 * @param homeViewModel ViewModel que contiene el estado reactivo del proceso de análisis
 */
@Composable
fun AnalysisScreen(
    onAnalysisComplete: () -> Unit,
    onNavigateBack: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.state.collectAsState()

    /**
     * Efecto que reacciona a la llegada del resultado de clasificación
     * Dirige a la pantalla de resultados cuando el proceso termina exitosamente
     */
    LaunchedEffect(uiState.classificationResult) {
        if (uiState.classificationResult != null) {
            onAnalysisComplete()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            /** Caso de error: Muestra el mensaje de fallo y permite reintentar */
            if (uiState.errorMessage != null) {
                Text(
                    text = "No se pudo completar el análisis",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage ?: "Error desconocido",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateBack) {
                    Text("Volver a intentar")
                }
            } else {
                /** Estado de carga */
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp),
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Analizando tu relato",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Por favor ten paciencia...",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    textAlign = TextAlign.Center
                )
                
                /** Nota informativa */
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Nota: Si es el primer análisis en un tiempo, puede tardar hasta 1 minuto mientras el servidor de clasificación se inicia, gracias por su paciencia.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
