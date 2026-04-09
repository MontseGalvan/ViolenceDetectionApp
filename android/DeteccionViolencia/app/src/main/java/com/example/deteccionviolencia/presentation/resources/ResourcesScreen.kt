package com.example.deteccionviolencia.presentation.resources

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deteccionviolencia.domain.model.HelpResource
import com.example.deteccionviolencia.ui.theme.GrayMedium

/**
 * Pantalla donde se visualiza una lista de recursos de ayuda
 * @param viewModel Instancia del ViewModel que provee el estado de los recursos
 */
@Composable
fun ResourcesScreen(
    viewModel: ResourcesViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        /** Muestra un indicador de carga */
        if (state.isLoading && state.helpResources.isEmpty()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.error != null) {
            /** Muestra un mensaje de error y permite reintentar la carga */
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.loadResources() }) {
                    Text("Reintentar")
                }
            }
        } else {
            /** Lista scrollable que genera cada recurso de ayuda */
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Recursos de Ayuda",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                }
                items(state.helpResources) { resource ->
                    ResourceCard(resource)
                }
            }
        }
    }
}

/**
 * Tarjeta que muestra la información de contacto de una institución de ayuda
 * @param helpResource Modelo de datos con el título, descripción y medios de contacto
 */
@Composable
fun ResourceCard(helpResource: HelpResource) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = helpResource.titulo,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = helpResource.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium
            )
            /** Muestra el teléfono si existe  */
            if (!helpResource.telefono.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "📞 ${helpResource.telefono}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            /** Muestra el correo electrónico si existe */
            if (!helpResource.email.isNullOrBlank()) {
                Text(
                    text = "✉️ ${helpResource.email}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
