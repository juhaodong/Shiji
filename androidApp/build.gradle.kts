import com.android.build.api.dsl.Packaging

plugins {
    kotlin("multiplatform")
    id("com.android.application")
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

kotlin {
    androidTarget()
    sourceSets {
        val androidMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation(libs.androidx.profileinstaller)

            }
        }
    }
}

android {
    signingConfigs {
        create("innerKen") {
            enableV1Signing = true
            enableV2Signing = true
            storeFile = file("innerken.jks")
            storePassword = "asd123456"
            keyAlias = "key0"
            keyPassword = "asd123456"
        }
    }
    fun Packaging.() {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
        }
    }
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "io.shiji.app"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")


    defaultConfig {
        applicationId = "io.shiji.app"
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
        versionCode =
            (findProperty("aaden.mobile.version.name") as String).split(".").last().toInt()
        versionName = (findProperty("aaden.mobile.version.name") as String)
        signingConfig = signingConfigs.getByName("innerKen")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("innerKen")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("innerKen")
        }
    }
    buildFeatures {
        buildConfig = true
    }
    kotlin {
        jvmToolchain(11)
    }

}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.common)
    implementation(libs.filekit.core)
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.messaging)
    // Enables FileKit with Composable utilities
    implementation(libs.filekit.compose)


}

configurations.all {
    resolutionStrategy {
    }
}


