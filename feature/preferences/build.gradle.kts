plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin)
}

android {
    namespace = "com.teixeira.vcspace.preferences"
    compileSdk = 36

    defaultConfig { minSdk = 26 }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures { viewBinding = true }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.google.material)
    implementation(project(":core:common"))
    implementation(project(":core:resources"))
}
