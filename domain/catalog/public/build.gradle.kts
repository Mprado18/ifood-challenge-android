plugins { id("movies.kotlin.library") }
dependencies {
    api(libs.coroutines.core)
    api(libs.paging.common)
    implementation(libs.serialization.json)
}
