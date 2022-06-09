import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.*
import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.COMPOSE
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.KOTLIN
    id("com.mikepenz.aboutlibraries.plugin")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig")

    // NOTE ksp issue with 1.6.20 for kotlin-inject https://github.com/evant/kotlin-inject/issues/193
    id("com.google.devtools.ksp") version "${Versions.KOTLIN}-1.0.4"
}

multiplatformResources {
    multiplatformResourcesPackage = "${Metadata.PACKAGE_NAME}.common"
    multiplatformResourcesClassName = "AppResources"
}

aboutLibraries {
    registerAndroidTasks = false
}

buildkonfig {
    packageName = Metadata.PACKAGE_NAME
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DEBUG", Metadata.DEBUG.toString())
        buildConfigField(STRING, "VERSION_NAME", Metadata.VERSION_NAME)
        buildConfigField(INT, "VERSION_CODE", Metadata.VERSION_CODE.toString())
        buildConfigField(STRING, "PACKAGE_NAME", Metadata.PACKAGE_NAME)
    }
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
        val commonMain by getting {
            dependencies {
                // Dependency Injection
                dependencies.add("kspMetadata", "me.tatarka.inject:kotlin-inject-compiler-ksp:${Versions.INJECT}")
                api("me.tatarka.inject:kotlin-inject-runtime:${Versions.INJECT}")

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
                api("com.bselzer.ktx:intl:${Versions.EXTENSION}")
                api("com.bselzer.ktx:library:${Versions.EXTENSION}")
                api("com.bselzer.ktx:logging:${Versions.EXTENSION}")
                api("com.bselzer.ktx:serialization:${Versions.EXTENSION}")
                api("com.bselzer.ktx:settings-compose:${Versions.EXTENSION}")

                // GW2 API Wrapper
                api("com.bselzer.gw2:asset-cdn:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-client:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-intl:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-model:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-model-extension:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-model-enumeration:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-resource:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-scope:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-tile:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-emblem:${Versions.WRAPPER}")

                // GW2 Database
                api("com.bselzer.gw2:v2-cache:${Versions.WRAPPER}")
                api("com.bselzer.gw2:v2-intl-cache:${Versions.WRAPPER}")
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
                api("com.google.android.material:material:1.6.1")
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
                api("org.kodein.db:kodein-leveldb-jni-jvm-windows:${Versions.KODEIN_DB}")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdk = Metadata.COMPILE_SDK
    sourceSets.getByName("main").manifest.srcFile(Metadata.COMMON_MANIFEST_PATH)
    defaultConfig {
        minSdk = Metadata.MIN_SDK
        targetSdk = Metadata.TARGET_SDK
        testInstrumentationRunner = Metadata.TEST_INSTRUMENTATION_RUNNER
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled
        sourceCompatibility = Metadata.ANDROID_JAVA_VERSION
        targetCompatibility = Metadata.ANDROID_JAVA_VERSION
    }
    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}")
    }
}

val aboutLibrariesResource = task("aboutLibrariesResource") {
    dependsOn("exportLibraryDefinitions")

    // Move aboutlibraries.json so it can be used by moko-resources.
    copy {
        from("$buildDir\\generated\\aboutLibraries") {
            include("aboutlibraries.json")
        }
        into("$projectDir\\src\\commonMain\\resources\\MR\\assets")
    }
}

tasks.whenTaskAdded {
    if (name == "generateMRcommonMain") {
        dependsOn(aboutLibrariesResource)
    }

    if (name == "build") {
        dependsOn("generateBuildKonfig")
    }
}

// Generate common code with ksp instead of per-platform, hopefully this won't be needed in the future.
// https://github.com/google/ksp/issues/567
kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}
tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinCompile<*>>().all {
    if (name != "kspKotlinMetadata") {
        dependsOn("kspKotlinMetadata")
    }
}