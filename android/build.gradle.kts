plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.KOTLIN
    id("org.jetbrains.compose") version Versions.COMPOSE
}

android {
    compileSdk = Metadata.COMPILE_SDK
    sourceSets.getByName("main").manifest.srcFile(Metadata.ANDROID_MANIFEST_PATH)
    defaultConfig {
        applicationId = "${Metadata.PACKAGE_NAME}.android"
        minSdk = Metadata.MIN_SDK
        targetSdk = Metadata.TARGET_SDK
        testInstrumentationRunner = Metadata.TEST_INSTRUMENTATION_RUNNER
        versionName = Metadata.VERSION_NAME
        versionCode = Metadata.VERSION_CODE
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = Metadata.ANDROID_JAVA_VERSION
        targetCompatibility = Metadata.ANDROID_JAVA_VERSION
    }
    buildFeatures {
        compose = true
    }

    appBundle()
    signing()
    proguard()

    kotlinOptions {
        jvmTarget = Metadata.ANDROID_JVM_TARGET
    }
}

dependencies {
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}")
    implementation(project(":common"))
}

fun com.android.build.gradle.internal.dsl.BaseAppModuleExtension.appBundle() {
    bundle {
        // Need to disable language split otherwise if the user tries to swap languages then a change is not immediate for non-GW2 strings because they aren't downloaded.
        language {
            enableSplit = false
        }
    }
}

fun com.android.build.gradle.internal.dsl.BaseAppModuleExtension.proguard() {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk.debugSymbolLevel = "FULL"

            if (project.hasStoreFile()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
}

fun com.android.build.gradle.internal.dsl.BaseAppModuleExtension.signing() {
    if (project.hasStoreFile()) {
        signingConfigs {
            create("release") {
                storeFile = project.file(project.localProperty(LocalProperty.STORE_FILE))
                storePassword = project.localProperty(LocalProperty.STORE_PASSWORD)
                keyPassword = project.localProperty(LocalProperty.KEY_PASSWORD)
                keyAlias = project.localProperty(LocalProperty.KEY_ALIAS)
            }
        }
    }
}