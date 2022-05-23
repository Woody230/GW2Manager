import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import org.gradle.api.JavaVersion
import org.gradle.api.Project

object Versions {
    const val KOTLIN = "1.6.10"
    const val COMPOSE = "1.1.0"
    const val RESOURCE = "0.19.0"
    const val LIBRARY = "10.0.0"
    const val WRAPPER = "2.0.0"
    const val KODEIN_DB = "0.9.0-beta"
    const val EXTENSION = "4.0.0"
    const val KTOR = "2.0.0"
    const val SETTINGS = "0.8.1"
    const val DECOMPOSE = "0.5.2"
    const val DESUGAR = "1.1.5"
    const val BUILD_CONFIG = "0.11.0"
}

object Metadata {
    const val DEBUG = false
    const val PACKAGE_NAME = "com.bselzer.gw2.manager"
    const val VERSION_NAME = "1.1.0"
    const val VERSION_CODE = 3
    const val ANDROID_JVM_TARGET = "8"
    const val DESKTOP_JVM_TARGET = "11"
    const val COMPILE_SDK = 31
    const val MIN_SDK = 21
    const val TARGET_SDK = 31
    val ANDROID_JAVA_VERSION = JavaVersion.VERSION_1_8
    const val ANDROID_MANIFEST_PATH = "src/main/AndroidManifest.xml"
    const val COMMON_MANIFEST_PATH = "src/androidMain/AndroidManifest.xml"
    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}

object LocalProperty {
    const val STORE_FILE = "storeFile"
    const val STORE_PASSWORD = "storePassword"
    const val KEY_PASSWORD = "keyPassword"
    const val KEY_ALIAS = "keyAlias"
}

fun Project.hasStoreFile() = gradleLocalProperties(rootDir).containsKey(LocalProperty.STORE_FILE)
fun Project.localProperty(name: String) = gradleLocalProperties(rootDir).getProperty(name) ?: throw NotImplementedError("Set the $name in local properties.")