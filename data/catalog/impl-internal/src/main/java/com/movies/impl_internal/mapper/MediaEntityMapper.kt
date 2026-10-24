package com.movies.impl_internal.mapper

import com.movies.database.entity.DetailEntity
import com.movies.database.entity.GenreEntity
import com.movies.database.entity.MediaEntity
import com.movies.domain.model.CastMember
import com.movies.domain.model.Genre
import com.movies.domain.model.MediaDetail
import com.movies.domain.model.MediaKey
import com.movies.domain.model.MediaSummary
import com.movies.domain.model.MediaType
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

private val storeJson = Json { ignoreUnknownKeys = true }

@Serializable
private data class StoredGenre(val id: Int, val name: String)

@Serializable
private data class StoredCast(
    val id: Int,
    val name: String,
    val character: String?,
    val profileUrl: String?,
)

internal fun MediaKey.toStoredKey() = "${type.apiValue}:$id"

internal fun String.toMediaKeyOrNull(): MediaKey? {
    val type = MediaType.fromApiValue(substringBefore(':')) ?: return null
    val id = substringAfter(':').toIntOrNull() ?: return null
    return MediaKey(type, id)
}

private fun encodeGenreIds(ids: List<Int>) = ids.joinToString(separator = ",", prefix = ",", postfix = ",")

private fun decodeGenreIds(raw: String) = raw.split(',').mapNotNull { it.toIntOrNull() }

internal fun MediaSummary.toEntity() = MediaEntity(
    key = key.toStoredKey(),
    type = key.type.apiValue,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate,
    genreIds = encodeGenreIds(genreIds),
)

internal fun MediaEntity.toSummary() = MediaSummary(
    key = MediaKey(requireNotNull(MediaType.fromApiValue(type)), key.substringAfter(':').toInt()),
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    voteAverage = voteAverage,
    voteCount = voteCount,
    releaseDate = releaseDate,
    genreIds = decodeGenreIds(genreIds),
)

internal fun Genre.toEntity() = GenreEntity(mediaType.apiValue, id, name)

internal fun GenreEntity.toGenre() = Genre(id, name, requireNotNull(MediaType.fromApiValue(mediaType)))

internal fun MediaDetail.toEntity(fetchedAt: Long) = DetailEntity(
    mediaKey = summary.key.toStoredKey(),
    tagline = tagline,
    status = status,
    runtimeMinutes = runtimeMinutes,
    seasons = seasons,
    episodes = episodes,
    genresJson = storeJson.encodeToString(
        ListSerializer(StoredGenre.serializer()),
        genres.map { StoredGenre(it.id, it.name) },
    ),
    castJson = storeJson.encodeToString(
        ListSerializer(StoredCast.serializer()),
        cast.map { StoredCast(it.id, it.name, it.character, it.profileUrl) },
    ),
    fetchedAt = fetchedAt,
)

internal fun DetailEntity.toDetail(summary: MediaSummary) = MediaDetail(
    summary = summary,
    tagline = tagline,
    genres = storeJson.decodeFromString(ListSerializer(StoredGenre.serializer()), genresJson)
        .map { Genre(it.id, it.name, summary.key.type) },
    status = status,
    runtimeMinutes = runtimeMinutes,
    seasons = seasons,
    episodes = episodes,
    cast = storeJson.decodeFromString(ListSerializer(StoredCast.serializer()), castJson)
        .map { CastMember(it.id, it.name, it.character, it.profileUrl) },
)
