package com.example.deteccionviolencia.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Configuración del esquema de colores para el tema claro de la aplicación
 * Mapea los colores personalizados a los roles definidos por Material Design 3
 */
private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = White,
    secondary = TealPrimary,
    onSecondary = Black,
    background = BackgroundLight,
    onBackground = Black,
    surface = White,
    onSurface = Black,
    error = Color(0xFFB00020),
    onError = White,
)

/**
 * Función composable que aplica el tema visual global de la aplicación
 * Configura colores, tipografía y formas para todos los componentes hijos
 * 
 * @param content Contenido de la interfaz al que se aplicará el tema
 */
@Composable
fun AppViolenciaTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(8.dp),
            large = RoundedCornerShape(16.dp)
        ),
        content = content
    )
}
