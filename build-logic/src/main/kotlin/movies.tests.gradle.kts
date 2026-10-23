val catalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    listOf("junit", "mockk", "coroutines-test", "robolectric", "androidx-test-core")
        .forEach { add("testImplementation", catalog.findLibrary(it).get()) }
    add("testImplementation", project(":core:testing"))
}
