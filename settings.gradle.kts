pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("io.github.woody230.gradle.internal.bundled") version "1.2.1"
}

rootProject.name = "GW2Manager"
include(":android")
include(":desktop")
include(":common")