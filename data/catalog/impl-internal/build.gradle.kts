plugins {
    id("movies.android.library")
    id("movies.hilt")
    id("movies.tests")
    id("org.jetbrains.kotlin.plugin.serialization")
}
dependencies {
    implementation(project(":domain:catalog:public"))
    implementation(project(":core:common"))
    implementation(project(":core:network:public"))
    implementation(project(":core:database"))
    implementation(libs.retrofit)
    implementation(libs.serialization.json)
    implementation(libs.paging.runtime)
    testImplementation(libs.retrofit.serialization)
    testImplementation(libs.mockwebserver)
    testImplementation(libs.paging.testing)
    testImplementation(libs.room.runtime)
}
