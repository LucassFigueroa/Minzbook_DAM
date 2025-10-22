plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.lucasmatiasminzbook"

    // Usa una API estable instalada en tu SDK Manager
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.lucasmatiasminzbook"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
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

    // ⚠️ AGP moderno pide Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures { compose = true }

    // Alineado con Compose 1.7.x / Material3 recientes
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Core AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ===== Compose BOM =====
    // (No pongas versiones a los artefactos cubiertos por el BOM)
    implementation(platform("androidx.compose:compose-bom:2024.10.01"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.10.01"))

    // UI Compose (sin versión, cubiertos por el BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity Compose (no está en el BOM → sí requiere versión)
    implementation("androidx.activity:activity-compose:1.9.3")

    // Navigation Compose (no está en el BOM → sí requiere versión)
    implementation("androidx.navigation:navigation-compose:2.8.4")

    // (Opcional pero útil) Lifecycle Compose helpers
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")

    // Testing (según tu catalog)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
