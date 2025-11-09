plugins {
    id("com.android.application") version "8.9.1" apply false
    id("com.android.library") version "8.9.1" apply false

    kotlin("android") version "2.0.0" apply false
    kotlin("multiplatform") version "2.0.0" apply false

    // 🔴 یہ نئی لائن: Kotlin 2.0 والا Compose Gradle plugin
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false

    id("com.google.gms.google-services") version "4.4.2" apply false
}
