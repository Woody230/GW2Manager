import com.bselzer.gradle.buildkonfig.boolean
import com.bselzer.gradle.buildkonfig.int
import com.bselzer.gradle.buildkonfig.string
import com.bselzer.gradle.multiplatform.configure.sourceset.multiplatformDependencies

plugins {
    // Order is important since there are checks on whether plugins exist.
    id(libs.plugins.woody230.gradle.internal.android.library.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)

    id(libs.plugins.woody230.gradle.internal.multiplatform.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.android.target.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.jvm.target.get().pluginId)

    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.test.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.test.get().pluginId)

    // TODO re-enable
    //alias(libs.plugins.moko.resources)
    id(libs.plugins.woody230.gradle.internal.buildkonfig.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.aboutlibraries.get().pluginId)
    alias(libs.plugins.sqldelight)

    alias(libs.plugins.ktx.serialization)
}

androidLibraryExtension {
    namespace.category.set(Metadata.CATEGORY)
    buildConfig.set(true)
}

android {
    composeOptions {
        // TODO need to add @NoLiveLiterals for GridComposition https://stackoverflow.com/a/71189923
        useLiveLiterals = false
    }
}

/* TODO re-enable
multiplatformResources {
    resourcesPackage.set("${Metadata.PACKAGE_NAME}.common")
    resourcesClassName.set("AppResources")
}
 */

buildkonfig {
    packageName = Metadata.PACKAGE_NAME
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        boolean("DEBUG", false)
        string("VERSION_NAME", libs.versions.woody230.gw2.manager.name.get())
        int("VERSION_CODE", libs.versions.woody230.gw2.manager.code.get().toInt())
        string("PACKAGE_NAME", Metadata.PACKAGE_NAME)
        string("DATABASE_NAME", Metadata.DATABASE_NAME)
    }
}

sqldelight {
    databases {
        create(Metadata.DATABASE_NAME) {
            generateAsync.set(true)
            packageName.set(Metadata.PACKAGE_NAME)
        }
    }
}

multiplatformDependencies {
    commonMain {
        api(libs.bundles.coil)
        api(libs.bundles.compose)
        api(libs.bundles.decompose)
        api(libs.bundles.woody230.gw2)
        api(libs.bundles.woody230.ktx)
        api(libs.moko.resources)
        api(libs.sqldelight.adapters)
    }
    androidMain {
        api(libs.android.material)
        api(libs.androidx.activity.compose)
        api(libs.androidx.appcompat)
        api(libs.androidx.datastore)
        api(libs.androidx.core.ktx)
        api(libs.androidx.lifecycle.runtime)
        api(libs.ktor.client.okhttp)
        api(libs.mapcompose)
        api(libs.settings.datastore)
        api(libs.sqldelight.android)
    }
    jvmMain {
        api(libs.ktor.client.okhttp)
        api(libs.sqldelight.jvm)
    }
}