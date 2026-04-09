package com.example.deteccionviolencia.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.deteccionviolencia.domain.model.UserProfile

/**
 * Formulario para la edición de los datos personales del usuario
 * @param profile Modelo de datos con la información del perfil
 * @param onFieldChange Callback disparado al modificar cualquier campo
 * @param onSave Callback para confirmar los cambios en Firestore
 * @param onCancel Callback para descartar la edición y cambios
 * @param isLoading Indica si se está realizando una operación de guardado
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileForm(
    profile: UserProfile,
    onFieldChange: (field: String, value: String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    isLoading: Boolean
) {
    /** Listas de opciones */
    val opcionesSexo = listOf("Hombre", "Mujer", "Prefiero no decirlo")
    val opcionesGenero = listOf("Femenino", "Masculino", "No binario", "Otro", "Prefiero no decirlo")
    val opcionesRelacion = listOf("Estudiante", "Profesor", "Trabajador")

    /** Estados para la expansión de los menús desplegables */
    var sexoExpanded by remember { mutableStateOf(false) }
    var generoExpanded by remember { mutableStateOf(false) }
    var relacionExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Editar Información",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        /** Entrada de texto para el nombre */
        OutlinedTextField(
            value = profile.nombre,
            onValueChange = { onFieldChange("name", it) },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        /** Entrada de texto para la edad */
        OutlinedTextField(
            value = profile.edad,
            onValueChange = { onFieldChange("age", it) },
            label = { Text("Edad") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        /** Selector desplegable para Sexo */
        ExposedDropdownMenuBox(
            expanded = sexoExpanded,
            onExpandedChange = { sexoExpanded = it }
        ) {
            OutlinedTextField(
                value = profile.sexo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Sexo") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sexoExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = sexoExpanded,
                onDismissRequest = { sexoExpanded = false }
            ) {
                opcionesSexo.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onFieldChange("sex", opcion)
                            sexoExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        /** Selector desplegable para Género */
        ExposedDropdownMenuBox(
            expanded = generoExpanded,
            onExpandedChange = { generoExpanded = it }
        ) {
            OutlinedTextField(
                value = profile.genero,
                onValueChange = {},
                readOnly = true,
                label = { Text("Género") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = generoExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = generoExpanded,
                onDismissRequest = { generoExpanded = false }
            ) {
                opcionesGenero.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onFieldChange("gender", opcion)
                            generoExpanded = false
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        /** Selector desplegable para la relación con la institución */
        ExposedDropdownMenuBox(
            expanded = relacionExpanded,
            onExpandedChange = { relacionExpanded = it }
        ) {
            OutlinedTextField(
                value = profile.relacionUAM,
                onValueChange = {},
                readOnly = true,
                label = { Text("Relación con UAM") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = relacionExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = relacionExpanded,
                onDismissRequest = { relacionExpanded = false }
            ) {
                opcionesRelacion.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = {
                            onFieldChange("uamRelationship", opcion)
                            relacionExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onSave,
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar")
                }
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !isLoading,
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
        }
    }
}
