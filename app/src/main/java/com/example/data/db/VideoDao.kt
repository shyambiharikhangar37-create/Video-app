package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE category = :category ORDER BY id DESC")
    fun getVideosByCategory(category: String): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR desc LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchVideos(query: String): Flow<List<VideoItem>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoById(id: Long): VideoItem?

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoItem>)

    @Update
    suspend fun updateVideo(video: VideoItem)

    @Delete
    suspend fun deleteVideo(video: VideoItem)

    @Query("DELETE FROM videos")
    suspend fun clearAllVideos()
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: Long): Flow<List<CommentItem>>

    @Query("SELECT COUNT(*) FROM comments WHERE videoId = :videoId")
    fun getCommentCountForVideo(videoId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM comments")
    suspend fun getTotalCommentsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentItem): Long

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteComment(commentId: Long)

    @Query("DELETE FROM comments WHERE videoId = :videoId")
    suspend fun deleteCommentsForVideo(videoId: Long)

    @Query("DELETE FROM comments")
    suspend fun clearAllComments()
}
