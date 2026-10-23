plugins {
    id("movies.compose")
    id("movies.tests")
}
dependencies {
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
