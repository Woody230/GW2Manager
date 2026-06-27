import com.bselzer.gradle.buildkonfig.boolean
import com.bselzer.gradle.buildkonfig.int
import com.bselzer.gradle.buildkonfig.string
import com.bselzer.gradle.multiplatform.configure.sourceset.multiplatformDependencies

plugins {
    // Order is important since there are checks on whether plugins exist.
    id(libs.plugins.woody230.gradle.internal.multiplatform.android.library.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)

    id(libs.plugins.woody230.gradle.internal.multiplatform.asProvider().get().pluginId)

    id(libs.plugins.woody230.gradle.internal.multiplatform.jvm.target.get().pluginId)

    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.test.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.test.get().pluginId)

    alias(libs.plugins.moko.resources)
    id(libs.plugins.woody230.gradle.internal.buildkonfig.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.aboutlibraries.get().pluginId)

    alias(libs.plugins.ktx.serialization)
}

multiplatformAndroidLibraryExtension {
    namespace.category.set(Metadata.CATEGORY)
    buildConfig.set(true)
}

multiplatformResources {
    resourcesPackage.set("${Metadata.PACKAGE_NAME}.common")
    resourcesClassName.set("AppResources")
}

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

multiplatformDependencies {
    commonMain {
        api(libs.bundles.coil)
        api(libs.bundles.compose)
        api(libs.bundles.decompose)
        api(libs.bundles.woody230.gw2)
        api(libs.bundles.woody230.ktx)
        api(libs.korlibs.io)
        api(libs.ktx.coroutines.core)
        api(libs.moko.resources)
    }
    androidMain {
        api(libs.android.material)
        api(libs.androidx.activity.compose)
        api(libs.androidx.appcompat)
        api(libs.androidx.datastore)
        api(libs.androidx.core.ktx)
        api(libs.androidx.lifecycle.runtime)
        api(libs.ktor.client.okhttp)
        api(libs.ktx.coroutines.android)
        api(libs.mapcompose)
        api(libs.settings.datastore)
    }
    jvmMain {
        api(libs.ktor.client.okhttp)
        api(libs.ktx.coroutines.swing)
    }
}