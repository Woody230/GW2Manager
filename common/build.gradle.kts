import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.1.0"
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val wrapperVersion = "2.0.0"
        val kodeinDbVersion = "0.9.0-beta"
        val extensionVersion = "4.0.0"
        val ktorVersion = "2.0.0"
        val serializationVersion = "1.3.2"
        val settingsVersion = "0.8.1"
        val decomposeVersion = "0.5.2"

        val commonMain by getting {
            dependencies {
                // Compose
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.preview)
                api("com.arkivanov.decompose:decompose:$decomposeVersion")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:$decomposeVersion")

                // Extensions
                api("com.bselzer.ktx:compose-resource:$extensionVersion")
                api("com.bselzer.ktx:compose-accompanist:$extensionVersion")
                api("com.bselzer.ktx:compose-image:$extensionVersion")
                api("com.bselzer.ktx:coroutine:$extensionVersion")
                api("com.bselzer.ktx:function:$extensionVersion")
                api("com.bselzer.ktx:library:$extensionVersion")
                api("com.bselzer.ktx:logging:$extensionVersion")
                api("com.bselzer.ktx:settings-compose:$extensionVersion")

                // GW2 API Wrapper
                api("com.bselzer.gw2:asset-cdn:$wrapperVersion")
                api("com.bselzer.gw2:v2-client:$wrapperVersion")
                api("com.bselzer.gw2:v2-model:$wrapperVersion")
                api("com.bselzer.gw2:v2-model-extension:$wrapperVersion")
                api("com.bselzer.gw2:v2-model-enumeration:$wrapperVersion")
                api("com.bselzer.gw2:v2-scope:$wrapperVersion")
                api("com.bselzer.gw2:v2-tile:$wrapperVersion")
                api("com.bselzer.gw2:v2-emblem:$wrapperVersion")

                // Serialization
                api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                api("io.github.pdvrieze.xmlutil:serialization:0.83.0")

                // GW2 Database
                api("com.bselzer.gw2:v2-cache:$wrapperVersion")
                api("com.bselzer.gw2:v2-tile-cache:$wrapperVersion")
                api("org.kodein.db:kodein-db:$kodeinDbVersion")
                api("org.kodein.db:kodein-db-serializer-kotlinx:$kodeinDbVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                // Android
                api("androidx.core:core-ktx:1.7.0")
                api("androidx.appcompat:appcompat:1.4.1")
                api("com.google.android.material:material:1.6.0")
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

                // Settings
                api("com.russhwolf:multiplatform-settings-datastore:$settingsVersion")
                api("androidx.datastore:datastore-preferences:1.0.0")

                // Compose
                api("androidx.activity:activity-compose:1.4.0")

                // HTTP Client
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            dependencies {
                // HTTP Client
                api("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.bselzer.gw2.manager.common"
    multiplatformResourcesClassName = "Gw2Resources"
}