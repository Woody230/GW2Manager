import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
}

dependencies {
    implementation(projects.common)
    implementation(compose.desktop.currentOs)
}

// TODO proguard

compose.desktop {
    application {
        mainClass = "${Metadata.PACKAGE_NAME}.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = Metadata.PACKAGE_NAME
            packageVersion = libs.versions.woody230.gw2.manager.name.get()
        }
    }
}