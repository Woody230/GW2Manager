buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://storage.googleapis.com/r8-releases/raw")
    }
    dependencies {
        // Need to use 3.0.77 for proper metadata handling https://stackoverflow.com/a/70331139, otherwise Gw2Client default recovery can't find the id parameter.
        classpath("com.android.tools:r8:3.0.77")

        classpath("com.android.tools.build:gradle:7.0.4")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.mikepenz.aboutlibraries.plugin:aboutlibraries-plugin:10.0.0-b03")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    group = "com.bselzer.gw2.manager"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}