plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}

android {
    namespace = "com.example.lucasmatiasminzbook"
    compileSdk = 36      // 👈 estable

    defaultConfig {
        applicationId = "com.example.lucasmatiasminzbook"
        minSdk = 24
        targetSdk = 36   // 👈 estable
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

    // AGP moderno → Java 17
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }

    // Compose 1.7.x
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get() // 1.5.14
    }

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
}

dependencies {
    // Core AndroidX (compatibles con SDK 34 y AGP 8.5.2)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // ===== Compose BOM compatible =====
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation(libs.androidx.compose.foundation.android)
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))

    // Compose UI (sin versión; usa el BOM de arriba)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity & Navigation compatibles
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Lifecycle Compose compatibles
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")

    // Room + KSP
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Tests (tu catálogo)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("androidx.biometric:biometric:1.1.0")
    implementation("androidx.compose.ui:ui-text")
    implementation("androidx.biometric:biometric:1.2.0-alpha05")
    implementation("com.google.android.material:material:1.12.0")
    implementation("io.coil-kt:coil-compose:2.6.0")        // ✅ Coil para cargar imágenes/URIs
    implementation("androidx.compose.material:material-icons-extended") // ✅ StarHalf, etc.
}
