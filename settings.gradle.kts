rootProject.name = "ShijiApp"

include(":androidApp")
include(":shared")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://repo.repsy.io/mvn/chrynan/public")
        }
        google()
        mavenLocal()
        maven { url = uri("https://jitpack.io") }
    }
    plugins {
        val kotlinVersion = extra["kotlin.version"] as String
        val agpVersion = extra["agp.version"] as String
        val composeVersion = extra["compose.version"] as String
        id("com.codingfeline.buildkonfig")
        kotlin("jvm").version(kotlinVersion)
        kotlin("multiplatform").version(kotlinVersion)
        kotlin("android").version(kotlinVersion)
        id("com.android.application").version(agpVersion)
        id("com.android.library").version(agpVersion)

        id("org.jetbrains.compose").version(composeVersion)
    }
}

dependencyResolutionManagement {
    repositories {

        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        google()
        maven {
            url = uri("https://repo.repsy.io/mvn/chrynan/public")
        }
        mavenLocal()
        maven { url = uri("https://jitpack.io") }

    }
}
