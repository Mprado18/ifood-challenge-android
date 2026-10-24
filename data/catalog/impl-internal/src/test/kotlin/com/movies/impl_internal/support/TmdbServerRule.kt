package com.movies.impl_internal.support

import com.movies.common.DispatcherProvider
import com.movies.impl_internal.api.ApiExecutor
import com.movies.impl_internal.api.TmdbApiService
import com.movies.network.NetworkConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.QueueDispatcher
import okhttp3.mockwebserver.RecordedRequest
import org.junit.rules.ExternalResource
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

// Stub de apoio aos testes: network config.
class NetworkConfigStub(
    override val baseUrl: String,
    override val bearerToken: String = "token-fake",
) : NetworkConfig

// Stub de apoio aos testes: dispatchers.
object DispatchersStub : DispatcherProvider {
    override val main: CoroutineDispatcher = Dispatchers.Unconfined
    override val io: CoroutineDispatcher = Dispatchers.Unconfined
    override val default: CoroutineDispatcher = Dispatchers.Unconfined
}

// Regra JUnit de apoio aos testes: tmdb server.
class TmdbServerRule : ExternalResource() {
    val server = MockWebServer()
    private val routes = mutableMapOf<String, () -> MockResponse>()

    internal lateinit var api: TmdbApiService
        private set
    internal lateinit var executor: ApiExecutor
        private set

    override fun before() {
        server.dispatcher = object : QueueDispatcher() {
            init {
                setFailFast(MockResponse().setResponseCode(404))
            }

            override fun dispatch(request: RecordedRequest): MockResponse {
                val url = request.requestUrl!!
                val keyWithPage = "${url.encodedPath}?page=${url.queryParameter("page")}"
                val route = routes[keyWithPage] ?: routes[url.encodedPath]
                return route?.invoke() ?: super.dispatch(request)
            }
        }
        server.start()
        val config = NetworkConfigStub(server.url("/").toString())
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        api = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(TmdbApiService::class.java)
        executor = ApiExecutor(config, DispatchersStub)
    }

    override fun after() {
        runCatching { server.shutdown() }
    }

    fun routeMock(
        path: String,
        response: () -> MockResponse,
    ) {
        routes[path] = response
    }

    fun routeJsonMock(
        path: String,
        body: String,
    ) = routeMock(path) { MockResponse().setBody(body) }

    fun routeErrorMock(
        path: String,
        code: Int,
    ) = routeMock(path) { MockResponse().setResponseCode(code) }

    fun enqueueMock(response: MockResponse) = server.enqueue(response)
}

fun fixture(name: String): String = TmdbServerRule::class.java.getResource("/fixtures/$name")!!.readText()
