package com.movies.ifood_challenge

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import kotlin.concurrent.thread

/** TMDB falso para os testes E2E: responde por path e nunca depende de token ou internet. */
object TmdbServerStub {
    private val server = MockWebServer()

    @Volatile
    var failEverything = false

    val baseUrl: String by lazy {
        var url = ""
        // Sobe fora da main thread, pois o Hilt pode pedir a URL durante a criação do app.
        thread { server.dispatcher = TmdbDispatcher; server.start(); url = server.url("/").toString() }.join()
        url
    }

    private object TmdbDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            if (failEverything) return MockResponse().setResponseCode(500).setBody("{}")
            val path = request.path.orEmpty().substringBefore("?")
            val body = when {
                path == "/3/movie/popular" || path == "/3/movie/now_playing" -> moviePage
                path == "/3/tv/popular" || path == "/3/tv/top_rated" -> tvPage
                path.startsWith("/3/genre/") -> """{"genres":[{"id":28,"name":"Ação"}]}"""
                path == "/3/movie/11" -> movieDetail
                else -> return MockResponse().setResponseCode(404).setBody("{}")
            }
            return MockResponse().setBody(body)
        }
    }

    private const val moviePage = """{"page":1,"total_pages":1,"results":[
        {"id":11,"title":"Star Wars","overview":"Uma princesa é capturada.","genre_ids":[28],"vote_average":8.2,"vote_count":100,"release_date":"1977-05-25"}]}"""

    private const val tvPage = """{"page":1,"total_pages":1,"results":[
        {"id":1399,"name":"Game of Thrones","overview":"Casas disputam o trono.","genre_ids":[18],"vote_average":8.4,"vote_count":50,"first_air_date":"2011-04-17"}]}"""

    private const val movieDetail = """{"id":11,"title":"Star Wars","overview":"Uma princesa é capturada.",
        "release_date":"1977-05-25","vote_average":8.2,"vote_count":100,"runtime":121,"tagline":"A galáxia te espera",
        "status":"Released","genres":[{"id":28,"name":"Ação"}],
        "credits":{"cast":[{"id":2,"name":"Mark Hamill","character":"Luke Skywalker","profile_path":null}],"crew":[]}}"""
}
