import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.COMPOSE
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.KOTLIN
    id("com.mikepenz.aboutlibraries.plugin")
    id("dev.icerock.mobile.multiplatform-resources")
}

kotlin {
    android {
        apply(plugin = "kotlin-parcelize")
    }
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Metadata.DESKTOP_JVM_TARGET
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
                api("com.arkivanov.decompose:decompose:${Versions.DECOMPOSE}")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:${Versions.DECOMPOSE}")

                // Extensions
                api("com.bselzer.ktx:compose-resource:${Versions.EXTENSION}")
                api("com.bselzer.ktx:compose-accompanist:${Versions.EXTENSION}")
                api("com.bselzer.ktx:compose-image:${Versions.EXTENSION}")
                api("com.bselzer.ktx:coroutine:${Versions.EXTENSION}")
                api("com.bselzer.ktx:function:${Versions.EXTENSION}")
                api("com.bselzer.ktx:intent:${Versions.EXTENSION}")
                api("com.bselzer.ktx:library:${Versions.EXTENSION}")
                api("com.bselzer.ktx:logging:${Versions.EXTENSION}")
                api("com.bselzer.ktx:serialization:${Versions.EXTENSION}")
                api("com.bselzer.ktx:settings-compose:${Versions.EXTENSION}")

                // GW2 API Wrapper
                api("com.bselzer.gw2:asset-cdn:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-client:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-model:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-model-extension:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-model-enumeration:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-scope:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-tile:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-emblem:${Versions.WRAPPER}")

                // GW2 Database
                api("com.bselzer.gw2:v2-cache:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-tile-cache:${Versions.WRAPPER}")
                api("org.kodein.db:kodein-db:${Versions.KODEIN_DB}")
                api("org.kodein.db:kodein-db-serializer-kotlinx:${Versions.KODEIN_DB}")
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
                api("com.russhwolf:multiplatform-settings-datastore:${Versions.SETTINGS}")
                api("androidx.datastore:datastore-preferences:1.0.0")

                // Compose
                api("androidx.activity:activity-compose:1.4.0")

                // HTTP Client
                api("io.ktor:ktor-client-okhttp:${Versions.KTOR}")
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
                api("io.ktor:ktor-client-okhttp:${Versions.KTOR}")

                // GW2 Database
                implementation("org.kodein.db:kodein-leveldb-jni-jvm-windows:${Versions.KODEIN_DB}")
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
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "${Metadata.PACKAGE_NAME}.common"
    multiplatformResourcesClassName = "Gw2Resources"
}

aboutLibraries {
    registerAndroidTasks = false
}

val aboutLibrariesResource = task("aboutLibrariesResource") {
    dependsOn("exportLibraryDefinitions")

    // Move aboutlibraries.json so it can be used by moko-resources.
    copy {
        from("$buildDir\\generated\\aboutLibraries\\debug\\res\\raw") {
            include("aboutlibraries.json")
        }
        into("$projectDir\\src\\commonMain\\resources\\MR\\assets")
    }
}

tasks.whenTaskAdded {
    if (name == "generateMRcommonMain") {
        dependsOn(aboutLibrariesResource)
    }
}