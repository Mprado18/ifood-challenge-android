package com.movies.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.movies.database.entity.ListItemEntity
import com.movies.database.entity.MediaEntity
import com.movies.database.entity.RemoteKeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListDao {
    @Query(
        """
        SELECT m.* FROM media m JOIN list_item l ON m.`key` = l.mediaKey
        WHERE l.scope = :scope AND $GENRE_FILTER
        ORDER BY l.position
        """,
    )
    fun paging(
        scope: String,
        filtered: Int,
        movieGenre: Int,
        tvGenre: Int,
    ): PagingSource<Int, MediaEntity>

    @Query(
        """
        SELECT m.* FROM media m JOIN list_item l ON m.`key` = l.mediaKey
        WHERE l.scope = :scope AND $GENRE_FILTER
        ORDER BY l.position LIMIT :limit
        """,
    )
    fun preview(
        scope: String,
        filtered: Int,
        movieGenre: Int,
        tvGenre: Int,
        limit: Int,
    ): Flow<List<MediaEntity>>

    @Upsert
    suspend fun upsertAll(items: List<ListItemEntity>)

    @Query("DELETE FROM list_item WHERE scope = :scope")
    suspend fun clear(scope: String)

    @Query("SELECT * FROM remote_key WHERE scope = :scope")
    suspend fun remoteKey(scope: String): RemoteKeyEntity?

    @Upsert
    suspend fun upsertRemoteKey(key: RemoteKeyEntity)
}
