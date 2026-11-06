package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val desc: String,
    val category: String, // "song", "drama", "comedy", "story", "culture", etc.
    val youtubeId: String,
    val likesCount: Int = 0,
    val isLiked: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val isCustomUserVideo: Boolean = false
)

@Entity(tableName = "comments")
data class CommentItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val videoId: Long,
    val authorName: String,
    val commentText: String,
    val timestamp: Long = System.currentTimeMillis()
)
