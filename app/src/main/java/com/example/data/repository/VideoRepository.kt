package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.AppDatabase
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VideoRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val videoDao = db.videoDao()
    private val commentDao = db.commentDao()
    private val prefs: SharedPreferences =
        context.getSharedPreferences("vidplayer_prefs", Context.MODE_PRIVATE)

    suspend fun ensureDefaultData() = withContext(Dispatchers.IO) {
        val count = videoDao.getVideoCount()
        if (count == 0) {
            videoDao.insertVideos(AppDatabase.defaultVideos)
        }
    }

    fun getAllVideos(): Flow<List<VideoItem>> = videoDao.getAllVideos()

    fun getVideosByCategory(category: String): Flow<List<VideoItem>> {
        return if (category == "all" || category.isBlank()) {
            videoDao.getAllVideos()
        } else {
            videoDao.getVideosByCategory(category)
        }
    }

    fun searchVideos(query: String): Flow<List<VideoItem>> = videoDao.searchVideos(query)

    fun getCommentsForVideo(videoId: Long): Flow<List<CommentItem>> =
        commentDao.getCommentsForVideo(videoId)

    suspend fun toggleLike(video: VideoItem) = withContext(Dispatchers.IO) {
        val newIsLiked = !video.isLiked
        val newCount = if (newIsLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        val updated = video.copy(isLiked = newIsLiked, likesCount = newCount)
        videoDao.updateVideo(updated)
    }

    suspend fun addComment(videoId: Long, authorName: String, text: String) = withContext(Dispatchers.IO) {
        val cleanAuthor = authorName.ifBlank { getUserName() }
        val comment = CommentItem(
            videoId = videoId,
            authorName = cleanAuthor,
            commentText = text.trim()
        )
        commentDao.insertComment(comment)
    }

    suspend fun deleteComment(commentId: Long) = withContext(Dispatchers.IO) {
        commentDao.deleteComment(commentId)
    }

    suspend fun addVideo(title: String, desc: String, category: String, youtubeId: String): Long =
        withContext(Dispatchers.IO) {
            val video = VideoItem(
                title = title.trim(),
                desc = desc.ifBlank { "उपयोगकर्ता द्वारा जोड़ा गया" },
                category = category,
                youtubeId = youtubeId.trim(),
                likesCount = 0,
                isLiked = false,
                isCustomUserVideo = true
            )
            videoDao.insertVideo(video)
        }

    suspend fun deleteVideo(video: VideoItem) = withContext(Dispatchers.IO) {
        commentDao.deleteCommentsForVideo(video.id)
        videoDao.deleteVideo(video)
    }

    fun getUserName(): String {
        return prefs.getString("user_name", "अतिथि") ?: "अतिथि"
    }

    fun setUserName(name: String) {
        prefs.edit().putString("user_name", name.trim().ifBlank { "अतिथि" }).apply()
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        videoDao.clearAllVideos()
        commentDao.clearAllComments()
        prefs.edit().clear().apply()
        // restore defaults
        videoDao.insertVideos(AppDatabase.defaultVideos)
    }
}
