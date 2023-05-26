import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.INT
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

plugins {
    id(libs.plugins.woody230.gradle.internal.aboutlibraries.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.library.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.buildkonfig.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.moko.resources.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.kotlininject.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.test.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.test.get().pluginId)
    alias(libs.plugins.ktx.serialization)
}

androidLibraryExtension {
    namespace.category.set("gw2.manager")
}

val PACKAGE_NAME = "com.bselzer.gw2.manager"
multiplatformResources {
    multiplatformResourcesPackage = "$PACKAGE_NAME.common"
    multiplatformResourcesClassName = "AppResources"
}

buildkonfig {
    packageName = PACKAGE_NAME
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        buildConfigField(BOOLEAN, "DEBUG", "false")
        buildConfigField(STRING, "VERSION_NAME", libs.versions.woody230.gw2.manager.name.get())
        buildConfigField(INT, "VERSION_CODE", libs.versions.woody230.gw2.manager.code.get())
        buildConfigField(STRING, "PACKAGE_NAME", PACKAGE_NAME)
    }
}

kotlin {
    android {
        apply(plugin = "kotlin-parcelize")
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.bundles.compose)
                api(libs.bundles.decompose)
                api(libs.bundles.woody230.ktx)
                api(libs.bundles.kodein.db)
                api(libs.bundles.woody230.gw2)
                api(libs.bundles.woody230.ktx)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.android.material)
                api(libs.androidx.activity.compose)
                api(libs.androidx.appcompat)
                api(libs.androidx.datastore)
                api(libs.androidx.core.ktx)
                api(libs.androidx.lifecycle.runtime)
                api(libs.ktor.client.okhttp)

                // TODO cannot update to latest due to missing method exception related to androidx/compose/animation/core/Animatable
                api(libs.mapcompose)

                api(libs.settings.datastore)
            }
        }
        val jvmMain by getting {
            dependencies {
                api(libs.ktor.client.okhttp)
                api(libs.kodein.db.level)
            }
        }
    }
}