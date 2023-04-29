import Versions.COMPOSE_COMPILER
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.COMPOSE
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.serialization") version Versions.KOTLIN
    id("com.mikepenz.aboutlibraries.plugin")
    id("dev.icerock.mobile.multiplatform-resources")
    id("com.codingfeline.buildkonfig")

    // NOTE ksp issue for kotlin-inject https://github.com/evant/kotlin-inject/issues/193
    id("com.google.devtools.ksp") version "${Versions.KOTLIN}-1.0.9"
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
    jvmToolchain(Metadata.DESKTOP_JVM_TARGET.toInt())
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
                dependencies.add("kspCommonMainMetadata", "me.tatarka.inject:kotlin-inject-compiler-ksp:${Versions.INJECT}")
                api("me.tatarka.inject:kotlin-inject-runtime:${Versions.INJECT}")

                // Compose
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.preview)
                api("com.arkivanov.decompose:decompose:${Versions.DECOMPOSE}")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:${Versions.DECOMPOSE}")

                // Extensions
                api("io.github.woody230.ktx:compose-aboutlibraries:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-accompanist:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-resource:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-serialization:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-settings:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-geometry:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-graphics:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-intl:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-text:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-unit:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-layout-common:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:compose-ui-layout-custom:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:coroutine:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:function:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:intent:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:intl:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:intl-serialization:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:image-kodein-db:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:image-ktor-client:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:logging:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:serialization:${Versions.EXTENSION}")
                api("io.github.woody230.ktx:serialization-xml:${Versions.EXTENSION}")

                // GW2 API Wrapper
                api("io.github.woody230.gw2:asset-cdn:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-client:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-intl:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-model:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-model-extension:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-model-enumeration:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-resource:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-scope:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-tile-client:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-emblem:${Versions.WRAPPER}")

                // GW2 Database
                api("io.github.woody230.gw2:v2-model-kodein-db:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-intl-kodein-db:${Versions.WRAPPER}")
                api("io.github.woody230.gw2:v2-tile-kodein-db:${Versions.WRAPPER}")
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
                api("androidx.core:core-ktx:1.10.0")
                api("androidx.appcompat:appcompat:1.6.1")
                api("com.google.android.material:material:1.8.0")
                api("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")

                // Settings
                api("com.russhwolf:multiplatform-settings-datastore:${Versions.SETTINGS}")
                api("androidx.datastore:datastore-preferences:1.0.0")

                // Compose
                api("androidx.activity:activity-compose:1.7.1")

                // TODO cannot update to latest due to missing method exception related to androidx/compose/animation/core/Animatable
                api("ovh.plrapps:mapcompose:2.4.0")

                // HTTP Client
                api("io.ktor:ktor-client-okhttp:${Versions.KTOR}")
            }
        }
        val androidUnitTest by getting {
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
    namespace = "${Metadata.PACKAGE_NAME}.common"
    compileSdk = Metadata.COMPILE_SDK
    defaultConfig {
        minSdk = Metadata.MIN_SDK
        testInstrumentationRunner = Metadata.TEST_INSTRUMENTATION_RUNNER
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = Metadata.ANDROID_JAVA_VERSION
        targetCompatibility = Metadata.ANDROID_JAVA_VERSION
    }
    dependencies {
        coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:${Versions.DESUGAR}")
    }
    composeOptions {
        // https://mvnrepository.com/artifact/org.jetbrains.compose.compiler/compiler
        // https://github.com/JetBrains/compose-multiplatform/blob/master/gradle-plugins/compose/src/main/kotlin/org/jetbrains/compose/ComposeCompilerCompatibility.kt
        kotlinCompilerExtensionVersion = COMPOSE_COMPILER
    }
    testOptions {
        unitTests {
            androidResources {
                isIncludeAndroidResources = true
            }
        }
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
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}