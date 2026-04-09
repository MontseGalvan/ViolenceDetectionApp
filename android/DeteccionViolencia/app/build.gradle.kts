plugins {
    alias(libs.plugins.android.application)     // Android Application
    alias(libs.plugins.kotlinAndroid)           // Kotlin + Compose
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")        // Firebase
    id("com.google.devtools.ksp")               // KSP
    id("kotlin-parcelize")                      // Para objetos parcelables
    alias(libs.plugins.hilt)                    // Hilt

}

android {
    namespace = "com.example.deteccionviolencia"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.deteccionviolencia"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //buildConfigField("String", "OPENAI_API_KEY", "\"${project.findProperty("OPENAI_API_KEY") ?: ""}\"")

        // whisper.cpp
        externalNativeBuild {
            cmake {
                arguments("-DANDROID_STL=c++_shared")
                cppFlags("-O3", "-funroll-loops", "-fPIC")
                abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
            }
        }
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }

    externalNativeBuild {
        cmake {
            path = file("src/main/jni/CMakeLists.txt")
        }
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.text)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Firebase BoM, se encarga de que todas las versiones de Firebase sean compatibles entre sí
    implementation(platform("com.google.firebase:firebase-bom:34.10.0"))
    // Firebase
    implementation("com.google.firebase:firebase-auth")
    // Coroutines Play Services (para usar .await() en Firebase)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    // ViewModel para Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    // Iconos extendidos para Visibility/VisibilityOff
    implementation("androidx.compose.material:material-icons-extended")

    // Navegación en Compose
    implementation("androidx.navigation:navigation-compose:2.8.7")

    // Para bottom navigation
    implementation("androidx.compose.material3:material3")

    // Para permisos
    implementation("com.google.accompanist:accompanist-permissions:0.35.0-alpha")

    // Retrofit para API
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Firestore (para perfil y recursos)
    implementation("com.google.firebase:firebase-firestore")

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Media3 para reproducción de audio
    implementation("androidx.media3:media3-exoplayer:1.4.0")
    implementation("androidx.media3:media3-ui:1.4.0")

    implementation("org.jetbrains.kotlinx:kotlinx-io-core:0.5.4")

    // Para manejar corrutinas y callbacks desde C++
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

}
