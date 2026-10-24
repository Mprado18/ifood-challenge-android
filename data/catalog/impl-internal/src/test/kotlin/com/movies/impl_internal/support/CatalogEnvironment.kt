package com.movies.impl_internal.support

import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/** Servidor TMDB falso + grafo de repositórios sobre Room em memória, na ordem correta de ciclo de vida. */
class CatalogEnvironment : TestRule {
    val tmdb = TmdbServerRule()
    val graph = CatalogGraphRule(tmdb)

    override fun apply(base: Statement, description: Description): Statement =
        RuleChain.outerRule(tmdb).around(graph).apply(base, description)
}
