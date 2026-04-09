package com.example.deteccionviolencia.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.deteccionviolencia.domain.model.UserProfile
import com.example.deteccionviolencia.ui.theme.GrayMedium

/**
 * Pantalla de perfil que permite al usuario visualizar y eidtar su información personal
 * @param onLogout Callback que se ejecuta cuando el usuario cierra sesión
 * @param viewModel ViewModel que gestiona el estado del perfil y la comunicación con Firestore
 */
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel
) {
    val state by viewModel.state.collectAsState()

    /** Carga inicial del perfil al entrar en la pantalla */
    LaunchedEffect(Unit) {
        viewModel.onEvent(ProfileEvent.LoadProfile)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        /** Visualización del nombre o título por defecto */
        Text(
            text = if (state.profile.nombre.isNotBlank()) state.profile.nombre else "Tu Perfil",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )

        /** Correo electrónico vinculado a la cuenta */
        Text(
            text = state.email,
            style = MaterialTheme.typography.bodyMedium,
            color = GrayMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        /** Alterna entre la vista de información y el formulario */
        if (!state.isEditing) {
            ProfileInfo(
                profile = state.profile,
                onEditClick = { viewModel.onEvent(ProfileEvent.ToggleEditMode) }
            )
        } else {
            ProfileForm(
                profile = state.profile,
                onFieldChange = { field, value ->
                    viewModel.onEvent(ProfileEvent.UpdateField(field, value))
                },
                onSave = { viewModel.onEvent(ProfileEvent.SaveProfile) },
                onCancel = { viewModel.onEvent(ProfileEvent.CancelEdit) },
                isLoading = state.isLoading
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        /** Botón para finalizar la sesión */
        Button(
            onClick = {
                viewModel.onEvent(ProfileEvent.Logout)
                onLogout()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text("Cerrar Sesión")
        }

        /** Notificación de errores */
        state.errorMessage?.let {
            Snackbar(
                modifier = Modifier.padding(8.dp),
                action = {
                    TextButton(onClick = { viewModel.onEvent(ProfileEvent.DismissError) }) {
                        Text("OK")
                    }
                }
            ) {
                Text(it)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

/**
 * Componente que muestra la información personal del usuario en modo lectura
 * @param profile Modelo de datos con la información del perfil
 * @param onEditClick Callback para activar el modo de edición
 */
@Composable
fun ProfileInfo(
    profile: UserProfile,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Información Personal",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(vertical = 16.dp)
        )

        InfoRow(label = "Nombre", value = profile.nombre)
        InfoRow(label = "Edad", value = profile.edad)
        InfoRow(label = "Sexo", value = profile.sexo)
        InfoRow(label = "Género", value = profile.genero)
        InfoRow(label = "Relación con UAM", value = profile.relacionUAM)

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Edit, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Editar Perfil")
        }
    }
}

/**
 * @param label Descripción del dato
 * @param value Valor del dato a mostrar
 */
@Composable
fun InfoRow(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayMedium
            )
            Text(
                text = value.ifEmpty { "No especificado" },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
