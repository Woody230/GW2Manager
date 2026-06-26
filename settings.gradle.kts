pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("io.github.woody230.gradle.internal.bundled") version "2.0.0"
}

rootProject.name = "GW2Manager"
include(":android")
// TODO re-enable
// include(":desktop")
include(":common")