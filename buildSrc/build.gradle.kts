plugins {
    `kotlin-dsl`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
    mavenLocal()
}

dependencies {
    implementation(libs.woody230.gradle.internal.aboutlibraries.plugin)
    implementation(libs.woody230.gradle.internal.android.desugar.plugin)
    implementation(libs.woody230.gradle.internal.android.plugin)
    implementation(libs.woody230.gradle.internal.buildkonfig.plugin)
    implementation(libs.woody230.gradle.internal.multiplatform.compose.plugin)
    implementation(libs.woody230.gradle.internal.multiplatform.compose.test.plugin)
    implementation(libs.woody230.gradle.internal.multiplatform.plugin)
    implementation(libs.woody230.gradle.internal.multiplatform.publish.plugin)
    implementation(libs.woody230.gradle.internal.multiplatform.test.plugin)
}