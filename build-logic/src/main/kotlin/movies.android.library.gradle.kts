import com.android.build.api.dsl.LibraryExtension

plugins {
    id("com.android.library")
}

extensions.configure<LibraryExtension> {
    namespace = "com.movies.ifood_challenge." + project.path.trim(':').replace(':', '.').replace('-', '_')
    compileSdk = 36
    defaultConfig { minSdk = 31 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    testOptions.unitTests.isIncludeAndroidResources = true
}

tasks.withType<Test>().configureEach {
    failOnNoDiscoveredTests = false
}
