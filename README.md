# Violence Detection App

Aplicación móvil para la detección temprana de violencia a partir de relatos de voz, utilizando Procesamiento de Lenguaje Natural y aprendizaje automático. Desarrollada como proyecto de integración en Ingeniería en Computación.

## Tabla de Contenidos

- [Funcionamiento](#funcionamiento)
- [Tecnologías empleadas](#tecnologías-empleadas)
- [Manual de instalación y despliegue](#manual-de-instalación-y-despliegue)
- [Base de datos y autenticación](#base-de-datos-y-autenticación)
- [Autor](#autor)

## Funcionamiento

El usuario comienza registrándose o iniciando sesión en la aplicación mediante Firebase Authentication. Una vez autenticado, accede a la pantalla principal donde puede grabar un relato de voz que la aplicación transcribe de forma local utilizando el modelo Whisper de OpenAI. El texto transcrito aparece en un campo editable, lo que permite al usuario corregir posibles errores de reconocimiento de voz. A continuación, el texto se envía a un servidor web que aplica un proceso de limpieza, lematización y vectorización TF‑IDF. Un clasificador Máquina de Vectores de Soporte determina si el relato contiene indicios de violencia o no. El resultado se muestra en la pantalla de resultados; si se detecta violencia, se ofrece un botón para acceder a una lista de recursos de ayuda almacenados en Firestore. El usuario puede guardar el análisis en su historial que se mantiene privado en el dispositivo mediante la base de datos Room, y consultarlo o eliminarlo posteriormente.

## Tecnologías empleadas

### Android
- Kotlin
- Jetpack Compose 
- Hilt
- Retrofit 
- Room
- Whisper.cpp con JNI

### Backend API REST
- Python 3.11
- FastAPI
- scikit‑learn 
- spaCy, NLTK 

### Servicios en la nube
- Firebase Auth y Firestore
- Render 

## Manual de instalación y despliegue

### Requisitos previos
- Android Studio 
- SDK Android 24+
- Dispositivo físico o emulador con Android 7.0+

### Pasos para ejecutar la aplicación
1. **Obtener el código fuente**:
   - Clonar el repositorio:
     ```bash
     git clone https://github.com/MontseGalvan/ViolenceDetectionApp.git
     ```
   - O descargar el ZIP y extraerlo.
2. Abrir Android Studio y seleccionar **Open an Existing Project**, luego elegir la carpeta `android/DeteccionViolencia` dentro del repositorio clonado o extraído.
3. Esperar a que Gradle sincronice las dependencias.
4. Conectar un dispositivo físico (con depuración USB habilitada) o iniciar un emulador.
5. Compilar y ejecutar la aplicación.

## Base de datos y autenticación

El proyecto utiliza tres servicios de almacenamiento:
- **Firebase Authentication:** gestiona el registro e inicio de sesión de los usuarios mediante correo electrónico y contraseña. 
- **Firestore (base de datos en la nube):** almacena dos colecciones:
  - `usuarios`: perfiles de usuario.
  - `Recursos_ayuda`: contactos de instituciones de apoyo.
- **Room (base de datos local):** guarda el historial de análisis de cada usuario en el dispositivo.

No se requieren configuraciones adicionales; los servicios de Firebase ya están integrados mediante las credenciales incluidas en el proyecto (archivo `google-services.json`). La colección de recursos de ayuda puede ser actualizada directamente desde la consola de Firestore sin modificar la aplicación. Y la aplicación móvil ya está configurada para conectarse al backend desplegado en la URL pública `https://violencedetectionapp.onrender.com`.

## Autor
Montserrath Galván Velázquez
