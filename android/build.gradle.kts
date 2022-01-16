import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    id("org.jetbrains.compose") version "1.0.1"
    id("com.mikepenz.aboutlibraries.plugin")
}

val localProperties = gradleLocalProperties(rootDir)
android {
    val hasStoreFile = localProperties.containsKey("storeFile")
    if (hasStoreFile) {
        signingConfigs {
            create("release") {
                storeFile = file(localProperties["storeFile"] ?: throw NotImplementedError("Set the store file in local properties."))
                storePassword = localProperties["storePassword"]?.toString() ?: throw NotImplementedError("Set the store password in local properties.")
                keyPassword = localProperties["keyPassword"]?.toString() ?: throw NotImplementedError("Set the key password in local properties.")
                keyAlias = localProperties["keyAlias"]?.toString() ?: throw NotImplementedError("Set the key alias in local properties.")
            }
        }
    }

    compileSdk = 31
    defaultConfig {
        applicationId = "com.bselzer.gw2.manager.android"
        minSdk = 21
        targetSdk = 31
        versionCode = 2
        versionName = "1.0.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk.debugSymbolLevel = "FULL"

            if (hasStoreFile) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation(project(":common"))
}