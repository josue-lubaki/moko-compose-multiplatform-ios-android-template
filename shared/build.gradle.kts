import java.util.Properties
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    alias(libs.plugins.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.konfig)
    alias(libs.plugins.mokoResources)
}

kotlin {
    android()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "shared"
            isStatic = true
        }
        extraSpecAttributes["resources"] = "['src/commonMain/resources/**', 'src/iosMain/resources/**']"
        extraSpecAttributes["exclude_files"] = "['src/commonMain/resources/MR/**']"
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                // Moko libs
                implementation(libs.moko.resources.compose)
                implementation(libs.moko.mvvm.compose)
                implementation(libs.moko.permissions.compose)
                implementation(libs.moko.media.compose)
                implementation(libs.moko.biometry.compose)
                implementation(libs.moko.geo.compose)

                // Icons
                api(compose.materialIconsExtended)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)

                // Koin
                implementation(libs.koin.core)

                // Serialization
                implementation(libs.kotlinx.serialization.json)

                // Image Loader
                api(libs.image.loader)

                // precompose - for viewmodel and navigation
                api(libs.precompose)
                api(libs.precompose.viewmodel)

                // SQLDelight
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines.extensions)

                // fix of Could not find "shared/build/kotlinTransformedMetadataLibraries/commonMain/org.jetbrains.kotlinx-atomicfu-0.17.3-nativeInterop-8G5yng.klib"
                implementation("org.jetbrains.kotlinx:atomicfu:0.17.3")
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.activity.compose)
                api(libs.appcompat)
                api(libs.core.ktx)

                api(libs.koin.android)
                implementation(libs.ktor.okhttp)

                implementation(libs.ktor.client.android)
                implementation(libs.android.driver)
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
                implementation(libs.native.driver)
            }
        }
    }
}

// generateMRcommonMain
tasks.register("generateMRcommonMainTask") {
    doLast {
        exec {
            workingDir = project.rootDir
            commandLine = listOf(
                "./gradlew",
                "generateMRcommonMain",
                "&&",
                "./gradlew",
                "generateBuildKonfig"
            )
        }
    }
}

gradle.projectsEvaluated {
    tasks.named("preBuild") {
        dependsOn("generateMRcommonMainTask")
    }
}

multiplatformResources {
    multiplatformResourcesPackage = "com.myapplication.common"
}

buildkonfig {
    packageName = "com.myapplication"

    val properties = Properties()

    val localPropertiesFile = project.rootProject.file("local.properties")
    if(localPropertiesFile.exists()){
        localPropertiesFile.inputStream().use { properties.load(it) }
    }

    defaultConfigs {
        buildConfigField(STRING, "BASE_URL", properties.getProperty("BASE_URL"))
    }
}

android {
    compileSdk = (findProperty("android.compileSdk") as String).toInt()
    namespace = "com.myapplication.common"

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")
    sourceSets["main"].resources.exclude("src/commonMain/resources/MR")

    defaultConfig {
        minSdk = (findProperty("android.minSdk") as String).toInt()
        targetSdk = (findProperty("android.targetSdk") as String).toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        jvmToolchain(11)
    }
}
