plugins { id("movies.feature") }
dependencies {
    implementation(project(":feature:favorites:public"))
    implementation(project(":feature:detail:public"))
}

tasks.withType<org.gradle.api.tasks.testing.Test>().configureEach {
    jvmArgs("--add-exports=java.base/jdk.internal.access=ALL-UNNAMED")
}
