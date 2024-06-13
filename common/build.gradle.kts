import com.bselzer.gradle.buildkonfig.boolean
import com.bselzer.gradle.buildkonfig.int
import com.bselzer.gradle.buildkonfig.string
import com.bselzer.gradle.multiplatform.configure.sourceset.multiplatformDependencies

plugins {
    // Order is important since there are checks on whether plugins exist.
    id(libs.plugins.woody230.gradle.internal.android.library.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)

    id(libs.plugins.woody230.gradle.internal.multiplatform.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.test.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.test.get().pluginId)

    alias(libs.plugins.moko.resources)
    id(libs.plugins.woody230.gradle.internal.buildkonfig.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.kotlininject.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.aboutlibraries.get().pluginId)

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

multiplatformResources {
    resourcesPackage = "${Metadata.PACKAGE_NAME}.common"
    resourcesClassName = "AppResources"
}

buildkonfig {
    packageName = Metadata.PACKAGE_NAME
    exposeObjectWithName = "BuildKonfig"

    defaultConfigs {
        boolean("DEBUG", false)
        string("VERSION_NAME", libs.versions.woody230.gw2.manager.name.get())
        int("VERSION_CODE", libs.versions.woody230.gw2.manager.code.get().toInt())
        string("PACKAGE_NAME", Metadata.PACKAGE_NAME)
    }
}

kotlin {
    androidTarget {
        apply(plugin = libs.plugins.kotlin.parcelize.get().pluginId)
    }
}

multiplatformDependencies {
    commonMain {
        api(libs.bundles.compose)
        api(libs.bundles.decompose)
        api(libs.bundles.kodein.db)
        api(libs.bundles.woody230.gw2)
        api(libs.bundles.woody230.ktx)
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
    }
    jvmMain {
        api(libs.ktor.client.okhttp)
        api(libs.kodein.db.level.windows)
    }
}