import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.KOTLIN
    id("org.jetbrains.compose") version Versions.COMPOSE
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
        applicationId = "${Metadata.PACKAGE_NAME}.android"
        minSdk = 21
        targetSdk = 31
        versionCode = Metadata.VERSION_CODE
        versionName = Metadata.VERSION_NAME

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
        jvmTarget = Metadata.ANDROID_JVM_TARGET
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}")
    implementation(project(":common"))
}