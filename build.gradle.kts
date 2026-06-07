// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    // Agrega esta línea para las rutas de navegación:
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false // O la versión que te asigne Firebase
    // Agrega el plugin de KSP aquí a nivel proyecto:
    alias(libs.plugins.ksp) apply false
}