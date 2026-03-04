plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.teixeira.vcspace.common"
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

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.security.crypto)

    implementation(libs.google.material)
    implementation(libs.google.gson)

    implementation(libs.common.utilcode)
    implementation(libs.termux.app.termux.shared)
    implementation(project(":core:resources"))
}
