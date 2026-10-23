plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.android.library) apply false
}

dependencies {
    kover(project(":feature:home:impl-internal"))
    kover(project(":feature:favorites:impl-internal"))
    kover(project(":feature:detail:impl-internal"))
}

kover {
    reports {
        filters {
            includes { classes("*HomeViewModel", "*CatalogViewModel", "*FavoritesViewModel", "*DetailViewModel") }
        }
        verify {
            rule {
                groupBy = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.CLASS
                minBound(85)
            }
        }
    }
}

tasks.register("verifyViewModelCoverage") {
    group = "verification"
    dependsOn("koverVerify")
}
subprojects {
    apply(plugin = rootProject.libs.plugins.ktlint.get().pluginId)
    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        android.set(true)
        filter { exclude { it.file.path.contains("/build/") } }
    }

    extensions.configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
        toolVersion = rootProject.libs.versions.detekt.get()
        buildUponDefaultConfig = true
        config.setFrom(files("${rootProject.projectDir}/config/detekt/detekt.yml"))
        source.setFrom(files("src/main/kotlin", "src/main/java"))
    }

    dependencies {
        add("detektPlugins", rootProject.libs.detekt.compose)
    }
}
