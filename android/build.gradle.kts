plugins {
    id(libs.plugins.woody230.gradle.internal.android.application.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.parcelize.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    alias(libs.plugins.ktx.serialization)
}

androidApplicationExtension {
    namespace.category.set(Category.ANDROID)
    versionName.set(libs.versions.woody230.gw2.manager.name)
    versionCode.set(libs.versions.woody230.gw2.manager.code.get().toInt())
}

dependencies {
    implementation(projects.common)
}