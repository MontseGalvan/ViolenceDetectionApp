package com.example.deteccionviolencia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.deteccionviolencia.presentation.login.LoginScreen
import com.example.deteccionviolencia.presentation.login.LoginViewModel
import com.example.deteccionviolencia.presentation.main.MainScreen
import com.example.deteccionviolencia.presentation.navigation.Screen
import com.example.deteccionviolencia.presentation.register.RegisterScreen
import com.example.deteccionviolencia.presentation.register.RegisterViewModel
import com.example.deteccionviolencia.ui.theme.AppViolenciaTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal de la aplicación y entrada tras el lanzamiento
 * Configura el sistema de navegación y habilita la inyección de dependencias
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    /**
     * Inicializa la actividad configurando el tema visual y el grafo de navegación
     * 
     * @param savedInstanceState Estado previo de la actividad en caso de recreación
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppViolenciaTheme {
                val navController = rememberNavController()

                /**
                 * Define el NavHost que gestiona el intercambio entre las pantallas principales
                 * Inicia en la pantalla de Inicio de Sesión
                 */
                NavHost(
                    navController = navController,
                    startDestination = Screen.Login.route
                ) {
                    /** Configuración de la pantalla de inicio de sesión */
                    composable(Screen.Login.route) {
                        val loginViewModel: LoginViewModel = hiltViewModel()
                        
                        LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate(Screen.Register.route)
                            }
                        )
                    }
                    
                    /** Configuración de la pantalla de registro */
                    composable(Screen.Register.route) {
                        val registerViewModel: RegisterViewModel = hiltViewModel()
                        
                        RegisterScreen(
                            viewModel = registerViewModel,
                            onRegisterSuccess = {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(Screen.Login.route) { inclusive = true }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                    
                    /** Configuración de la pantalla principal tras la autenticación exitosa */
                    composable(Screen.Main.route) {
                        MainScreen(onLogout = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Main.route) { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}
