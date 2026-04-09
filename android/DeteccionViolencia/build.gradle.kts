// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // dependency for the Google services Gradle plugin (Firebase)
    id("com.google.gms.google-services") version "4.4.4" apply false
    // Kotlin Symbol Processing (KSP)
    id("com.google.devtools.ksp") version "2.0.20-1.0.25" apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
}
