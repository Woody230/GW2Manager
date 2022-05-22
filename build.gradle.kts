buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://storage.googleapis.com/r8-releases/raw")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:${Versions.LIBRARY}")
        classpath("dev.icerock.moko:resources-generator:${Versions.RESOURCE}")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    group = Metadata.PACKAGE_NAME
    version = Metadata.VERSION_NAME

    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}