package com.example.deteccionviolencia.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.deteccionviolencia.domain.model.Analysis
import com.example.deteccionviolencia.presentation.navigation.Screen
import com.example.deteccionviolencia.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Pantalla donde se visualiza la lista de análisis realizados
 * @param navController Controlador de navegación
 * @param viewModel Instancia del ViewModel que provee el estado del historial
 */
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Mi Historial",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 40.dp, bottom = 8.dp)
        )
        Text(
            text = "Análisis previos guardados",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.analyses.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No hay análisis en tu historial",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Realiza tu primer análisis para verlo aquí",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.analyses, key = { it.id }) { analysis ->
                        HistoryItemCard(
                            analysis = analysis,
                            onDelete = { viewModel.deleteAnalysis(analysis.id) },
                            onClick = {
                                navController.currentBackStackEntry?.savedStateHandle?.set("analysis", analysis)
                                navController.navigate(Screen.HistoryDetail.route)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta que presenta un elemento individual del historial
 * @param analysis Datos del análisis
 * @param onDelete Acción a ejecutar al presionar el botón de eliminar
 * @param onClick Acción a ejecutar al presionar la tarjeta para ver detalles
 */
@Composable
fun HistoryItemCard(
    analysis: Analysis,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy - HH:mm", Locale("es"))
    val dateStr = dateFormat.format(analysis.timestamp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = GrayDark
                )

                Text(
                    text = if (analysis.isViolence) "⚠️" else "✅",
                    modifier = Modifier
                        .background(
                            color = if (analysis.isViolence) AlertPastel else SuccessPastel,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = analysis.transcribedText,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (analysis.isViolence) "Posibles indicadores detectados" else "Sin indicadores detectados",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (analysis.isViolence) ErrorDark else SuccessDark,
                    modifier = Modifier
                        .background(
                            color = if (analysis.isViolence) ErrorPastel else SuccessPastel,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
                
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
