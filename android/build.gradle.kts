plugins {
    id(libs.plugins.woody230.gradle.internal.android.application.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    alias(libs.plugins.ktx.serialization)
}

androidApplicationExtension {
    namespace.category.set(Metadata.CATEGORY)
    versionName.set(libs.versions.woody230.gw2.manager.name)
    versionCode.set(libs.versions.woody230.gw2.manager.code.get().toInt())
    minSdk.set(libs.versions.woody230.gw2.android.minSdk.get().toInt())
}

android {
    bundle {
        language {
            // Have all languages available, otherwise in-app language changes won't have the strings available because they aren't downloaded.
            // https://developer.android.com/guide/app-bundle/configure-base#handling_language_changes
            // TODO on demand language downloading https://developer.android.com/guide/playcore/feature-delivery/on-demand#lang_resources
            enableSplit = false
        }
    }
}

dependencies {
    implementation(projects.common)
}