plugins {
    id("movies.android.library")
    id("movies.hilt")
    id("movies.tests")
}
dependencies {
    api(libs.coroutines.core)
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.navigation)
}
