import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("movies.hilt")
    id("org.jetbrains.kotlin.plugin.serialization")
}

fun tmdbToken(): String {
    val file = rootProject.file("local.properties")
    val fromFile = if (file.exists()) Properties().apply { file.inputStream().use(::load) }.getProperty("TMDB_TOKEN") else null
    return (fromFile ?: System.getenv("TMDB_TOKEN")).orEmpty().trim()
}

android {
    namespace = "com.movies.ifood_challenge"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.movies.ifood_challenge"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.movies.ifood_challenge.HiltTestRunner"
        buildConfigField("String", "TMDB_TOKEN", "\"${tmdbToken()}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":designsystem"))
    implementation(project(":feature:home:public"))
    implementation(project(":feature:favorites:public"))
    implementation(project(":feature:detail:public"))
    implementation(project(":domain:catalog:public"))
    runtimeOnly(project(":data:catalog:impl-internal"))
    implementation(project(":core:network:public"))
    runtimeOnly(project(":core:network:impl-internal"))
    runtimeOnly(project(":core:database"))
    runtimeOnly(project(":feature:home:impl-internal"))
    runtimeOnly(project(":feature:favorites:impl-internal"))
    runtimeOnly(project(":feature:detail:impl-internal"))
    implementation(libs.androidx.navigation)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.mockwebserver)
    kspAndroidTest(libs.hilt.compiler)
}
