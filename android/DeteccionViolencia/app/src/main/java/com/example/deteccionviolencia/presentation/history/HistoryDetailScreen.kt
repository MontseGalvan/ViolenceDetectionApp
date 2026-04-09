package com.example.deteccionviolencia.presentation.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.deteccionviolencia.domain.model.Analysis
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pantalla que presenta la información detallada de un análisis
 * @param navController Controlador de navegación
 * @param analysis Objeto que contiene los datos del análisis a visualizar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    navController: NavController,
    analysis: Analysis
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es"))
    val formattedDate = dateFormat.format(Date(analysis.timestamp))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Análisis") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            /** Tarjeta que muestra si se detectó violencia */
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (analysis.isViolence) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (analysis.isViolence) "⚠️ Se detectaron indicadores de violencia" else "✅ Sin indicadores de violencia detectados",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            /** Tarjeta que mestra la fecha y el texto completo del relato */
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fecha: $formattedDate",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Relato completo:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    Text(
                        text = analysis.transcribedText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
