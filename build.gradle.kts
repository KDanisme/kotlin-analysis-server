import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version kotlinBom.versions.kotlin
    alias(libs.plugins.versionCatalogUpdate)
    application
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/kotlin-ide-plugin-dependencies")
    maven("https://www.jetbrains.com/intellij-repository/releases")
    maven("https://www.jetbrains.com/intellij-repository/snapshots")
    maven("https://cache-redirector.jetbrains.com/intellij-third-party-dependencies")
}
//
dependencies {

//    // Kotlin standard library
    implementation(kotlinBom.kotlin.kotlinStdlib)
//    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
//    // LSP library
    implementation(libs.lsp4j)
//    // IntelliJ IDEA APIs distributed as a library (required by the analysis API and Kotlin compiler)
    implementation(libs.intellij.platform.core)
    implementation(libs.intellij.platform.core.impl)
    implementation(libs.intellij.platform.util)

//    // Kotlin compiler and analysis API
//    // See https://github.com/google/ksp/blob/319ddf/kotlin-analysis-api/build.gradle.kts
    implementation(kotlinBom.kotlin.kotlinCompiler)
    listOf(
        libs.kotlin.analysis.api.platform.`interface`,
        libs.kotlin.analysis.api.standalone,
        libs.kotlin.analysis.api.providers,
        libs.kotlin.analysis.project.structure,
        libs.kotlin.high.level.api.api,
        libs.kotlin.high.level.api.fir,
        libs.kotlin.high.level.api.impl,
        libs.kotlin.low.level.api.fir,
        libs.kotlin.symbol.light.classes,
    ).forEach { implementation(it) { isTransitive = false } }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_23)
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest()
        }
    }
}

application {
    mainClass.set("dev.fwcd.kas.MainKt")
}
