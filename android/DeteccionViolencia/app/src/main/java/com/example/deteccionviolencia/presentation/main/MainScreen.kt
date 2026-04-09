package com.example.deteccionviolencia.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.deteccionviolencia.domain.model.Analysis
import com.example.deteccionviolencia.presentation.history.HistoryDetailScreen
import com.example.deteccionviolencia.presentation.history.HistoryScreen
import com.example.deteccionviolencia.presentation.history.HistoryViewModel
import com.example.deteccionviolencia.presentation.home.*
import com.example.deteccionviolencia.presentation.navigation.BottomNavItem
import com.example.deteccionviolencia.presentation.navigation.Screen
import com.example.deteccionviolencia.presentation.profile.ProfileScreen
import com.example.deteccionviolencia.presentation.profile.ProfileViewModel
import com.example.deteccionviolencia.presentation.resources.ResourcesScreen
import com.example.deteccionviolencia.presentation.resources.ResourcesViewModel

/**
 * Componente principal contenedor de la aplicación tras el inicio de sesión
 * 
 * @param onLogout Callback ejecutado cuando el usuario decide cerrar su sesión
 */
@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    val homeViewModel: HomeViewModel = hiltViewModel()
    val resourcesViewModel: ResourcesViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        /**
         * NavHost interno que maneja la navegación entre las secciones
         * y los flujos secundarios
         */
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            /** Sección de Inicio */
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    onStartRecording = { navController.navigate(Screen.Recording.route) },
                    homeViewModel = homeViewModel
                )
            }

            /** Flujo de Grabación */
            composable(Screen.Recording.route) {
                RecordingScreen(
                    onRecordingComplete = { navController.navigate(Screen.Confirmation.route) },
                    onCancel = { navController.popBackStack() },
                    homeViewModel = homeViewModel
                )
            }

            /** Flujo de Confirmación */
            composable(Screen.Confirmation.route) {
                ConfirmationScreen(
                    onReRecord = { navController.popBackStack(Screen.Recording.route, inclusive = false) },
                    onAnalyze = { 
                        homeViewModel.onEvent(HomeEvent.AnalyzeText)
                        navController.navigate(Screen.Analysis.route) 
                    },
                    homeViewModel = homeViewModel
                )
            }

            /** Flujo de Análisis*/
            composable(Screen.Analysis.route) {
                AnalysisScreen(
                    onAnalysisComplete = {
                        navController.navigate(Screen.Results.route) {
                            popUpTo(Screen.Analysis.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    homeViewModel = homeViewModel
                )
            }

            /** Flujo de Resultados */
            composable(Screen.Results.route) {
                ResultsScreen(
                    onNavigateToResources = { navController.navigate(BottomNavItem.Resources.route) },
                    onNewAnalysis = {
                        homeViewModel.clearState()
                        navController.popBackStack(BottomNavItem.Home.route, inclusive = false)
                    },
                    homeViewModel = homeViewModel
                )
            }

            /** Sección de Recursos */
            composable(BottomNavItem.Resources.route) {
                ResourcesScreen(viewModel = resourcesViewModel)
            }

            /** Sección de Historial */
            composable(BottomNavItem.History.route) {
                HistoryScreen(
                    navController = navController,
                    viewModel = historyViewModel
                )
            }
            
            /** Detalle del Historial */
            composable(Screen.HistoryDetail.route) {
                val analysis = remember {
                    navController.previousBackStackEntry?.savedStateHandle?.get<Analysis>("analysis")
                }
                if (analysis != null) {
                    HistoryDetailScreen(navController = navController, analysis = analysis)
                } else {
                    Box(Modifier.fillMaxSize())
                }
            }

            /** Sección de Perfil */
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onLogout = onLogout,
                    viewModel = profileViewModel
                )
            }
        }
    }
}

/**
 * Componente que renderiza la barra de navegación inferior
 * 
 * @param navController Controlador para gestionar las transiciones entre pestañas
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Resources,
        BottomNavItem.History,
        BottomNavItem.Profile
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) }
            )
        }
    }
}
