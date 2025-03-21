import dev.aga.gradle.versioncatalogs.Generator.generate
rootProject.name = "kotlin-analysis-server"
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
        maven("https://www.jetbrains.com/intellij-repository/snapshots")
        maven("https://www.jetbrains.com/intellij-repository/releases")
    }
}


plugins {
    id("dev.aga.gradle.version-catalog-generator") version ("3.2.0")
}

dependencyResolutionManagement {
    repositories.mavenCentral()
    versionCatalogs {
        generate("kotlinBom") {
            fromToml("kotlinBom")
        }
    }
}