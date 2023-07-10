pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://jitpack.io")
        maven(url = "https://dl.bintray.com/icerockdev/moko")
    }

    plugins {
//        val kotlinVersion = extra["kotlin.version"] as String
//        val agpVersion = extra["agp.version"] as String
//        val composeVersion = extra["compose.version"] as String
//        val mokoResourcesVersion = extra["moko.resources.version"] as String
//
//        kotlin("jvm").version(kotlinVersion)
//        kotlin("multiplatform").version(kotlinVersion)
//        kotlin("android").version(kotlinVersion)
//
//        id("com.android.application").version(agpVersion)
//        id("com.android.library").version(agpVersion)
//
//        id("org.jetbrains.compose").version(composeVersion)
//
//        id("dev.icerock.mobile.multiplatform-resources").version(mokoResourcesVersion)
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io") // for moko-media android picker

    }
}

rootProject.name = "MyApplication"
include(":androidApp")
include(":shared")
