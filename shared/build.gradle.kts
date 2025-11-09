plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    androidTarget()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }
        val commonTest by getting

        val androidMain by getting
        val androidUnitTest by getting
    }
    jvmToolchain(21)

}

android {
    namespace = "com.example.restaurantapp.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }

    // 🔴 یہ نیا حصہ add کرو
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}
