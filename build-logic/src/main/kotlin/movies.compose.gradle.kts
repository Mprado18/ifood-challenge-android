import com.android.build.api.dsl.LibraryExtension

plugins {
    id("movies.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<LibraryExtension> { buildFeatures.compose = true }

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    add("implementation", platform(catalog.findLibrary("androidx-compose-bom").get()))
    listOf("androidx-compose-material3", "androidx-compose-ui", "androidx-compose-ui-tooling-preview")
        .forEach { add("implementation", catalog.findLibrary(it).get()) }
    add("debugImplementation", catalog.findLibrary("androidx-compose-ui-tooling").get())
}
