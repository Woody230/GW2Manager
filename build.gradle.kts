buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://storage.googleapis.com/r8-releases/raw")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:10.0.0")
        classpath("dev.icerock.moko:resources-generator:0.19.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    group = "com.bselzer.gw2.manager"
    version = "1.1.0"

    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}