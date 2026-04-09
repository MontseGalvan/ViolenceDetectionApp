package com.example.deteccionviolencia

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase base de la aplicación que extiende de Application y sirve como el contenedor de
 * dependencias para Hilt
 */
@HiltAndroidApp
class DeteccionViolenciaApp : Application()
