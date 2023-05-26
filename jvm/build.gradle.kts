import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    id(libs.plugins.woody230.gradle.internal.multiplatform.asProvider().get().pluginId)
    id(libs.plugins.woody230.gradle.internal.multiplatform.compose.asProvider().get().pluginId)
}

kotlin {
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

// TODO proguard

compose.desktop {
    application {
        val PACKAGE_NAME = "com.bselzer.gw2.manager"
        mainClass = "$PACKAGE_NAME.jvm.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = PACKAGE_NAME
            packageVersion = libs.versions.woody230.gw2.manager.name
        }
    }
}