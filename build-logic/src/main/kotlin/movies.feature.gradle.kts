plugins {
    id("movies.compose")
    id("movies.hilt")
    id("movies.tests")
    id("org.jetbrains.kotlinx.kover")
}

val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    add("testImplementation", platform(catalog.findLibrary("androidx-compose-bom").get()))
    add("testImplementation", catalog.findLibrary("androidx-compose-ui-test-junit4").get())
    add("debugImplementation", platform(catalog.findLibrary("androidx-compose-bom").get()))
    add("debugImplementation", catalog.findLibrary("androidx-compose-ui-test-manifest").get())
    add("implementation", project(":domain:catalog:public"))
    add("implementation", project(":core:common"))
    add("implementation", project(":designsystem"))
    listOf("androidx-lifecycle-compose", "androidx-viewmodel-compose", "androidx-hilt-compose", "paging-compose")
        .forEach { add("implementation", catalog.findLibrary(it).get()) }
}
