import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser
import groovy.xml.XmlUtil


plugins {

    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("com.android.library")

    id("kotlinx-serialization")
    id("de.jensklingenberg.ktorfit") version "2.2.0"
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.crashlytics) apply false

}

val ktorfitVersion = "2.2.0"



kotlin {
    androidTarget()
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    cocoapods {
        version = "1.0.2"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            isStatic = true
            baseName = "shared"

        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.ktorfit.lib)
                //Only needed when you want to use Kotlin Serialization
                implementation(libs.ktor.client.serialization)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.bignum)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(
                    compose.components.resources
                )
                runtimeOnly(libs.kotlinx.coroutines.swing)
                implementation(libs.m3winsize)
                implementation(libs.insetsx)
                implementation(libs.napier)
                implementation(libs.kotlinx.datetime)
                implementation(libs.ui.text.google.fonts)
                implementation(libs.kotlin.inject.runtime)
                implementation(libs.multiplatform.settings.no.arg)
                implementation(libs.multiplatform.locale)
                implementation(libs.material.kolor)
                implementation(libs.kmpalette.core)
                implementation(libs.kmpalette.extensions.network)
                implementation(compose.components.resources)
                implementation(libs.kotlinx.datetime.ext)
                implementation(libs.navigation.compose)

                implementation(libs.gitlive.firebase.auth) // This line
                implementation(libs.firebase.common)// This line
                implementation(libs.firebase.analytics)
                api(libs.gitlive.firebase.kotlin.crashlytics)
                implementation(libs.gitlive.firebase.messaging)

                implementation(libs.human.readable)
                implementation(kotlin("reflect"))


                implementation(libs.peekaboo.ui)
                implementation(libs.peekaboo.image.picker)
                implementation(libs.coil.compose)
                implementation(libs.calf.permissions)

                implementation(libs.sain)

                implementation("network.chaintech:qr-kit:3.0.3")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                api(libs.activity.compose)
                api(libs.appcompat)
                api(libs.core.ktx)
                implementation(libs.ktor.client.android)
                implementation(libs.coil.network.okhttp)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }


    }
}


android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "io.shiji"
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()

    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    add("kspCommonMainMetadata", "de.jensklingenberg.ktorfit:ktorfit-ksp:$ktorfitVersion")
    add("kspAndroid", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    add("kspIosX64", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    add("kspIosArm64", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")
    add("kspIosSimulatorArm64", "me.tatarka.inject:kotlin-inject-compiler-ksp:0.7.2")

}

tasks.register("replace double quote") {
    doFirst {
        print("start to replace the locale")
        val dir = layout.projectDirectory.dir("src/commonMain/composeResources")
        dir.asFileTree.files.filter { it.name == "strings.xml" }.forEach { file ->
            val info = XmlParser().parse(file)
            info.children().forEach {
                it as Node
                it.setValue(
                    ((it.value() as NodeList)[0] as String).removeSurrounding('"'.toString())
                        .replace("'", "").replace(
                            "\\", "'"
                        )
                )
            }
            file.writeText(XmlUtil.serialize(info))
        }
    }
}
task("testClasses")

data class LanguageMetaInfo(
    val language: String,
    val region: String,
    val script: String,
    val name: String,
    val localizedName: String,

    ) {
    fun toKtString(): String {
        return """
            LanguageMetaInfo(
                language=$language,
                region=$region,
                script=$script,
                name=$name,
                localizedName=$localizedName,
                )
        """.trimIndent()
    }
}

