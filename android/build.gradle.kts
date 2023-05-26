plugins {
    id(libs.plugins.woody230.gradle.internal.android.application.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.android.desugar.get().pluginId)
    kotlin("android")
    id("kotlin-parcelize")
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
    alias(libs.plugins.ktx.serialization)
}

androidApplicationExtension {
    namespace.category.set("gw2.manager")
    versionName.set(libs.versions.woody230.gw2.manager.name)
    versionCode.set(libs.versions.woody230.gw2.manager.code.get().toInt())
}

dependencies {
    implementation(project(":common"))
}