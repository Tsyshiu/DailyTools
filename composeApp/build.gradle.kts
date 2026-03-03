import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // alias(libs.plugins.androidApplication)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    androidLibrary {
        namespace = "com.github.tsyshiu.dailytoolslibrary"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }

        androidResources {
            enable = true
        }
    }

    jvm()


    @OptIn(ExperimentalKotlinGradlePluginApi::class)

    dependencies {
        /*Dependency aliases supported by the Compose Multiplatform Gradle plugin (compose.ui and others)
        are deprecated with the 1.10.0-beta01 release. We encourage you to add direct library references to your version catalogs.
        Specific references are suggested in the corresponding deprecation notices.
        This change should make dependency management for Compose Multiplatform libraries a bit more transparent.
        In the future, we hope to provide a BOM for Compose Multiplatform to simplify setting up compatible versions.
        https://kotlinlang.org/docs/multiplatform/whats-new-compose-110.html#deprecated-dependency-aliases
        */
        implementation(libs.compose.runtime)
        implementation(libs.compose.foundation)
        implementation(libs.compose.material3)
        implementation(libs.compose.ui)
        implementation(libs.compose.components.resources)
        implementation(libs.compose.uiToolingPreview)
        implementation(libs.androidx.lifecycle.viewmodelCompose)
        implementation(libs.androidx.lifecycle.runtimeCompose)
        // implementation(libs.compose.material3.window.size)
        implementation(libs.compose.material3.adaptive)
        implementation(libs.compose.icons)



        // 3rd party
        implementation(libs.kermit)

        testImplementation(libs.kotlin.test)

    }
}



dependencies {
    androidRuntimeClasspath(libs.compose.uiTooling)
}


