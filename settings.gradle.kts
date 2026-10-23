pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ifood-challenge"
include(":app")
include(":designsystem")
include(":core:common", ":core:database", ":core:testing")
include(":core:network:public", ":core:network:impl-internal")
include(":domain:catalog:public", ":data:catalog:impl-internal")
include(":feature:home:public", ":feature:home:impl-internal")
include(":feature:favorites:public", ":feature:favorites:impl-internal")
include(":feature:detail:public", ":feature:detail:impl-internal")
include(":build-logic")
