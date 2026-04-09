package com.example.deteccionviolencia.presentation.home

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deteccionviolencia.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

/**
 * Pantalla principal de la aplicación tras el inicio de sesión
 * @param onStartRecording Callback para navegar a la pantalla de captura de audio
 * @param homeViewModel ViewModel que provee los datos del perfil y administra permisos
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onStartRecording: () -> Unit,
    homeViewModel: HomeViewModel
) {
    /** Estado para la solicitud del permiso de grabación de audio */
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    var shouldNavigate by remember { mutableStateOf(false) }
    
    /** Estado para la visibilidad del diálogo de consejos */
    var showTipsDialog by remember { mutableStateOf(false) }
    
    /** Observa el perfil del usuario para mostrar un saludo personalizado */
    val userProfile by homeViewModel.userProfile.collectAsStateWithLifecycle()
    val saludo = if (userProfile.nombre.isNotBlank()) {
        "¡Hola ${userProfile.nombre}!"
    } else {
        "¡Hola!"
    }

    /** Diálogo con recomendaciones para realizar la grabación */
    if (showTipsDialog) {
        AlertDialog(
            onDismissRequest = { showTipsDialog = false },
            title = { 
                Text(
                    text = "Consejos para tu grabación",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                ) 
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("• Busca un lugar tranquilo y privado donde te sientas cómod@ para hablar")
                    Text("• Habla de forma clara, esto ayuda a que el análisis sea más exacto")
                    Text("• Comenta cómo te sentiste en la situación descrita")
                    Text("• En caso de que alguna palabra no se haya detectado correctamente, puede editar tu relato por escrito")
                }
            },
            confirmButton = {
                TextButton(onClick = { showTipsDialog = false }) {
                    Text("Entendido")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    /** Efecto que reacciona a los cambios en el estado del permiso de micrófono */
    LaunchedEffect(permissionState.status) {
        if (permissionState.status == PermissionStatus.Granted && shouldNavigate) {
            onStartRecording()
            shouldNavigate = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = saludo,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "¿Hay alguna experiencia de la que quisieras hablar?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        /**
         * Tarjeta para iniciar la grabación
         * Gestiona la solicitud de permisos de audio si no han sido concedidos previamente
         */
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(28.dp))
                .clickable {
                    if (permissionState.status is PermissionStatus.Granted) {
                        onStartRecording()
                    } else {
                        shouldNavigate = true
                        permissionState.launchPermissionRequest()
                    }
                },
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(android.R.drawable.ic_btn_speak_now),
                    contentDescription = "Grabar Relato",
                    modifier = Modifier.size(80.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Grabar Relato",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        /** Botón informativo para usuarios que necesitan orientación */
        Text(
            text = "¿Es tu primera vez? → Consejos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f))
                .clickable { showTipsDialog = true }
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}
