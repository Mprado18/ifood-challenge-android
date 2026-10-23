plugins { id("movies.feature") }
dependencies {
    implementation(project(":feature:home:public"))
    implementation(project(":feature:detail:public"))
}
