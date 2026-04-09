package com.example.deteccionviolencia.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.deteccionviolencia.ui.theme.ErrorPastel
import com.example.deteccionviolencia.ui.theme.SuccessPastel

/**
 * Pantalla final que presenta el resultado del análisis de violencia
 * @param onNavigateToResources Callback para dirigir al usuario a la sección de ayuda
 * @param onNewAnalysis Callback para reiniciar el estado y volver al inicio
 * @param homeViewModel ViewModel que contiene el resultado de la clasificación y el estado de guardado
 */
@Composable
fun ResultsScreen(
    onNavigateToResources: () -> Unit,
    onNewAnalysis: () -> Unit,
    homeViewModel: HomeViewModel
) {
    val uiState by homeViewModel.state.collectAsState()
    val result = uiState.classificationResult

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Resultado del Análisis",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        /**
         * Tarjeta de resultados
         */
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (result?.isViolence == true)
                    ErrorPastel
                else
                    SuccessPastel
            ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (result?.isViolence == true)
                        "Se identificaron posibles indicadores de violencia"
                    else
                        "No se identificaron indicadores comúnmente asociados a la violencia",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        /** Muestra sugerencia de recursos solo si se detectó violencia */
        if (result?.isViolence == true) {
            Button(
                onClick = onNavigateToResources,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Ver recursos de ayuda")
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        /** Lógica de guardado */
        if (!uiState.isSaved) {
            Button(
                onClick = {
                    homeViewModel.onEvent(HomeEvent.SaveAnalysis)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Guardar en mi historial")
            }
            Spacer(modifier = Modifier.height(12.dp))
        } else {
            Text(
                text = "✓ Guardado en historial",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedButton(
            onClick = onNewAnalysis,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Realizar nuevo análisis")
        }
    }
}
