rootProject.name = "attributes-kt"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    val toolsVersion: String by extra

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
        mavenLocal()
    }

    plugins {
        id("space.kscience.gradle.project") version toolsVersion
        id("space.kscience.gradle.mpp") version toolsVersion
        id("space.kscience.gradle.jvm") version toolsVersion
    }
}

dependencyResolutionManagement {
    val toolsVersion: String by extra

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://repo.kotlin.link")
        mavenLocal()
    }

    versionCatalogs {
        create("spclibs") {
            from("space.kscience:version-catalog:$toolsVersion")
        }
    }
}

include(":attributes-kt-serialization")
