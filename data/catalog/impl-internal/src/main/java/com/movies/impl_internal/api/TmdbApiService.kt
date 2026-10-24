package com.movies.impl_internal.api

import com.movies.impl_internal.dto.GenreListResponseDto
import com.movies.impl_internal.dto.MovieDetailDto
import com.movies.impl_internal.dto.MovieDto
import com.movies.impl_internal.dto.PageResponseDto
import com.movies.impl_internal.dto.SearchItemDto
import com.movies.impl_internal.dto.TvDetailDto
import com.movies.impl_internal.dto.TvDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TmdbApiService {
    @GET("3/movie/popular")
    suspend fun popularMovies(
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("region") region: String = REGION,
    ): PageResponseDto<MovieDto>

    @GET("3/movie/now_playing")
    suspend fun nowPlayingMovies(
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("region") region: String = REGION,
    ): PageResponseDto<MovieDto>

    @GET("3/tv/popular")
    suspend fun popularTv(
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
    ): PageResponseDto<TvDto>

    @GET("3/tv/top_rated")
    suspend fun topRatedTv(
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
    ): PageResponseDto<TvDto>

    @GET("3/search/multi")
    suspend fun searchMulti(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("include_adult") includeAdult: Boolean = false,
    ): PageResponseDto<SearchItemDto>

    @GET("3/genre/movie/list")
    suspend fun movieGenres(
        @Query("language") language: String = LANGUAGE,
    ): GenreListResponseDto

    @GET("3/genre/tv/list")
    suspend fun tvGenres(
        @Query("language") language: String = LANGUAGE,
    ): GenreListResponseDto

    @GET("3/movie/{id}")
    suspend fun movieDetail(
        @Path("id") id: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("append_to_response") append: String = "credits",
    ): MovieDetailDto

    @GET("3/tv/{id}")
    suspend fun tvDetail(
        @Path("id") id: Int,
        @Query("language") language: String = LANGUAGE,
        @Query("append_to_response") append: String = "credits",
    ): TvDetailDto

    companion object {
        const val LANGUAGE = "pt-BR"
        const val REGION = "BR"
    }
}
