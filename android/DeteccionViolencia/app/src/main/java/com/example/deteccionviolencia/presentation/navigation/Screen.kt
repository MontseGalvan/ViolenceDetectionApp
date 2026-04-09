package com.example.deteccionviolencia.presentation.navigation

/**
 * Clase que define todas las rutas de navegación disponibles en la aplicación
 * 
 * @property route Identificador único de la ruta para el NavHost
 */
sealed class Screen(val route: String) {
    /** Pantalla para el inicio de sesión */
    data object Login : Screen("login")
    
    /** Pantalla para el registro  */
    data object Register : Screen("register")
    
    /** Contenedor principal que contiene la navegación por pestañas */
    data object Main : Screen("main")
    
    /** Pantalla de captura de audio  */
    data object Recording : Screen("recording")
    
    /** Pantalla para confirmar o descartar el audio grabado */
    data object Confirmation : Screen("confirmation")
    
    /** Pantalla que muestra el progreso de la transcripción y el análisis de riesgo */
    data object Analysis : Screen("analysis")
    
    /** Pantalla que presenta los resultados de la clasificación */
    data object Results : Screen("results_view")
    
    /** Pantalla de detalle para visualizar un análisis específico */
    data object HistoryDetail : Screen("history_detail")
}
