plugins {
    id("com.android.application")
    kotlin("android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.31"
    id("org.jetbrains.compose") version "1.0.0-rc3"
}

android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.bselzer.gw2.manager.android"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    // TODO global
    val composeVersion = "1.0.5"
    val kotlinVersion = "1.5.31"
    val wrapperVersion = "1.0.0"
    val kodeinDbVersion = "0.9.0-beta"
    val kodeinDiVersion = "7.9.0"
    val extensionVersion = "2.1.0"
    val ktorVersion = "1.6.4"
    val serializationVersion = "1.3.0"
    val coilVersion = "1.4.0"

    // Android
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")

    // Compose
    implementation("androidx.activity:activity-compose:1.4.0")
    implementation("androidx.compose.ui:ui-tooling:$composeVersion")

    // Preferences
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.preference:preference-ktx:1.1.1")

    // Image loading
    implementation("io.coil-kt:coil:$coilVersion")
    implementation("io.coil-kt:coil-compose:$coilVersion")

    // Logging
    implementation("com.jakewharton.timber:timber:5.0.1")

    // GW2 API Wrapper
    // TODO jitpack
    implementation("com.bselzer.library.gw2:v2-client:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-model:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-model-extension:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-model-enumeration:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-scope:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-tile:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-emblem:$wrapperVersion")

    // GW2 Database
    // TODO jitpack
    implementation("com.bselzer.library.gw2:v2-cache:$wrapperVersion")
    implementation("com.bselzer.library.gw2:v2-tile-cache:$wrapperVersion")
    implementation("org.kodein.db:kodein-db:$kodeinDbVersion")
    implementation("org.kodein.db:kodein-db-serializer-kotlinx:$kodeinDbVersion")

    // Dependency Injection
    implementation("org.kodein.di:kodein-di:$kodeinDiVersion")
    implementation("org.kodein.di:kodein-di-framework-android-core:$kodeinDiVersion")

    // HTTP Client
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion") // Using OkHttp because Coil relies on it.

    // Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
    implementation("io.github.pdvrieze.xmlutil:serialization-android:0.83.0")

    // Extensions
    // TODO jitpack
    implementation("com.bselzer.library.kotlin.extension:compose:$extensionVersion")
    implementation("com.bselzer.library.kotlin.extension:function:$extensionVersion")
    implementation("com.bselzer.library.kotlin.extension:preference:$extensionVersion")

    // Test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}