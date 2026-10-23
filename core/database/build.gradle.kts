plugins {
    id("movies.android.library")
    id("movies.hilt")
}

dependencies {
    api(libs.room.runtime)
    implementation(libs.room.paging)
    implementation(libs.coroutines.core)
    ksp(libs.room.compiler)
}

ksp { arg("room.schemaLocation", "$projectDir/schemas") }
