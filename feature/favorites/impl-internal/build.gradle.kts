plugins { id("movies.feature") }
dependencies {
    implementation(project(":feature:favorites:public"))
    implementation(project(":feature:detail:public"))
}
