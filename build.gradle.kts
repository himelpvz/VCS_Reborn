import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension

plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.compose) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.aboutlibraries) apply false
}

subprojects {
  configurations.configureEach {
    // Work around D8/R8 Kotlin metadata rewriting crashes from optional compose stability tracker runtime
    exclude(group = "com.skydoves", module = "compose-stability-runtime")
    exclude(group = "com.github.skydoves", module = "compose-stability-runtime")
    exclude(group = "com.skydoves", module = "stability-runtime")
    exclude(group = "com.github.skydoves", module = "stability-runtime")
  }

  pluginManager.withPlugin("com.android.application") {
    extensions.configure<ApplicationExtension> {
      compileSdk = 36

      defaultConfig {
        minSdk = 26
        targetSdk = 35
        versionCode = 201
        versionName = "2.0.1"
      }

      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
      }
    }
  }

  pluginManager.withPlugin("com.android.library") {
    extensions.configure<LibraryExtension> {
      compileSdk = 36

      defaultConfig {
        minSdk = 26
      }

      compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
      }
    }
  }
}

tasks.register<Delete>("clean") { delete(rootProject.layout.buildDirectory) }
