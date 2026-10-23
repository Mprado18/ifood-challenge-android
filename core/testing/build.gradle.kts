plugins { id("movies.android.library") }
dependencies {
    api(project(":core:common"))
    api(project(":domain:catalog:public"))
    api(libs.junit)
    api(libs.coroutines.test)
}
