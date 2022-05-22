import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version Versions.COMPOSE
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Metadata.DESKTOP_JVM_TARGET
        }
    }
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
        mainClass = "${Metadata.PACKAGE_NAME}.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = Metadata.PACKAGE_NAME
            packageVersion = Metadata.VERSION_NAME
        }
    }
}