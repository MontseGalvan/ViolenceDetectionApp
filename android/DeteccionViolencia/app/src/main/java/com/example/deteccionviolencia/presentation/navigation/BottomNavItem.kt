package com.example.deteccionviolencia.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Clase que define los destinos de la barra de navegación inferior
 * 
 * @property route Identificador de la ruta para la navegación
 * @property title Etiqueta que se muestra bajo el icono
 * @property selectedIcon Icono que se muestra cuando la pestaña está activa
 * @property unselectedIcon Icono que se muestra cuando la pestaña no está activa
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    /** Pestaña de inicio  */
    data object Home : BottomNavItem(
        route = "home",
        title = "Inicio",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )
    
    /** Pestaña de recursos  */
    data object Resources : BottomNavItem(
        route = "resources",
        title = "Recursos",
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info
    )
    
    /** Pestaña de historial */
    data object History : BottomNavItem(
        route = "history",
        title = "Historial",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
    
    /** Pestaña de perfil */
    data object Profile : BottomNavItem(
        route = "profile",
        title = "Perfil",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
}
