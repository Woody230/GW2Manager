
import org.gradle.api.JavaVersion

object Versions {
    const val KOTLIN = "1.8.10"
    const val WRAPPER = "3.2.0"
    const val EXTENSION = "5.3.0"

    const val COMPOSE = "1.3.1"
    const val COMPOSE_COMPILER = "1.4.2"
    const val RESOURCE = "0.22.0"
    const val LIBRARY = "10.6.2"
    const val KODEIN_DB = "0.9.0-beta"
    const val KTOR = "2.3.0"
    const val SETTINGS = "1.0.0"
    const val DECOMPOSE = "0.5.2"
    const val DESUGAR = "1.1.5"
    const val BUILD_CONFIG = "0.11.0"
    const val INJECT = "0.4.1"
}

object Metadata {
    const val DEBUG = false
    const val PACKAGE_NAME = "com.bselzer.gw2.manager"
    const val VERSION_NAME = "1.2.2"
    const val VERSION_CODE = 9
    const val ANDROID_JVM_TARGET = "11"
    const val DESKTOP_JVM_TARGET = "11"
    const val COMPILE_SDK = 33
    const val MIN_SDK = 21
    const val TARGET_SDK = 33
    val ANDROID_JAVA_VERSION = JavaVersion.VERSION_11
    const val ANDROID_MANIFEST_PATH = "src/main/AndroidManifest.xml"
    const val TEST_INSTRUMENTATION_RUNNER = "androidx.test.runner.AndroidJUnitRunner"
}

object LocalProperty {
    const val STORE_FILE = "storeFile"
    const val STORE_PASSWORD = "storePassword"
    const val KEY_PASSWORD = "keyPassword"
    const val KEY_ALIAS = "keyAlias"
}