import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    // alias(libs.plugins.androidApplication)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.symbolCraft)
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

symbolCraft {
    // Generated Kotlin package name (required)
    packageName.set("com.github.tsyshiu.dailytools.symbols")

    // Output directory (supports multiplatform projects)
    outputDirectory.set("src/commonMain/kotlin")

    // Cache configuration
    cacheEnabled.set(true)  // Default: true
    cacheDirectory.set("symbolcraft-cache")  // Default: "symbolcraft-cache" (relative to build/)

    // Preview configuration
    generatePreview.set(false)  // Default: false - Whether to generate Compose @Preview functions

    // Download retry configuration
    maxRetries.set(3)  // Default: 3 - Maximum number of retry attempts for failed downloads
    retryDelayMs.set(1000)  // Default: 1000ms - Initial delay between retries

    // Icon naming configuration (optional)
    naming {
        pascalCase()  // Default naming convention
        // Available options: pascalCase(), camelCase(), snakeCase(), kebabCase(), etc.
    }

    // // Individual icon configuration (using Int weight values)
    // materialSymbol("search") {
    //     style(weight = 400, variant = SymbolVariant.OUTLINED, fill = SymbolFill.UNFILLED)
    //     style(weight = 500, variant = SymbolVariant.OUTLINED, fill = SymbolFill.FILLED)
    // }
    //
    // // Or using SymbolWeight enum for type safety
    // materialSymbol("home") {
    //     style(weight = SymbolWeight.W400, variant = SymbolVariant.OUTLINED)
    //     style(weight = SymbolWeight.W500, variant = SymbolVariant.ROUNDED)
    // }
    //
    // // Convenient batch configuration methods
    // materialSymbol("person") {
    //     standardWeights() // Auto-add 400, 500, 700 weights
    // }
    //
    // materialSymbol("settings") {
    //     allVariants(weight = 400) // Add all variants (outlined, rounded, sharp)
    // }
    //
    // materialSymbol("favorite") {
    //     bothFills(weight = 500, variant = SymbolVariant.ROUNDED) // Add both filled and unfilled
    // }
    //
    // // Batch configure multiple icons
    // materialSymbols("star", "bookmark") {
    //     weights(400, 500, variant = SymbolVariant.OUTLINED)
    // }
    //
    // // Local SVG files stored in the repository
    // localIcons {
    //     directory = "src/commonMain/resources/icons"
    //     // include("**/*.svg") // optional, defaults to **/*.svg
    // }
    //
    // localIcons(libraryName = "brand") {
    //     directory = "design/exported"
    //     include("brand/**/*.svg")
    //     exclude("legacy/**")
    // }
}


