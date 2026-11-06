package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [VideoItem::class, CommentItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao
    abstract fun commentDao(): CommentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vidplayer_db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Prepopulate default prototype videos
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).videoDao()
                            dao.insertVideos(defaultVideos)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        val defaultVideos = listOf(
            VideoItem(
                id = 1,
                title = "हरदौल की लीला - बुंदेली नाटक",
                desc = "बुंदेलखंड की प्रसिद्ध लोक कथा और ऐतिहासिक नाटक",
                category = "drama",
                youtubeId = "4NpfAs5_vLs",
                likesCount = 124,
                isLiked = false
            ),
            VideoItem(
                id = 2,
                title = "बुंदेली लोकगीत - रैया",
                desc = "माटी की खुशबू भरे पारंपरिक लोकगीत व गायन",
                category = "song",
                youtubeId = "o0bkp9zwNk4",
                likesCount = 98,
                isLiked = false
            ),
            VideoItem(
                id = 3,
                title = "बुंदेली हास्य कवि सम्मेलन",
                desc = "हँसी-मज़ाक, चुटकुले और ठहाकों का मेला",
                category = "comedy",
                youtubeId = "dQw4w9WgXcQ",
                likesCount = 215,
                isLiked = false
            ),
            VideoItem(
                id = 4,
                title = "छाँद कौरी की कहानी",
                desc = "बुंदेलखंड की अमर प्रेम कथा एवं गाथा",
                category = "story",
                youtubeId = "juKd26qkWAo",
                likesCount = 87,
                isLiked = false
            ),
            VideoItem(
                id = 5,
                title = "दीवारियाँ नाटक - बुंदेली",
                desc = "समाज के रीति-रिवाजों और आधुनिकता पर व्यंग्य नाटक",
                category = "drama",
                youtubeId = "kqtD5dpn9C8",
                likesCount = 64,
                isLiked = false
            ),
            VideoItem(
                id = 6,
                title = "बुंदेली संस्कृति और त्योहार",
                desc = "हमारी पहचान, हमारे पर्व, हमारी समृद्ध धरोहर",
                category = "culture",
                youtubeId = "wNDGgL73ihY",
                likesCount = 142,
                isLiked = false
            ),
            VideoItem(
                id = 7,
                title = "लाला हार्डोल को जहर पिलाने की कहानी",
                desc = "भारतीय साहित्य और बुंदेलखंड की वीर गाथा",
                category = "story",
                youtubeId = "bu8s60bzVZY",
                likesCount = 173,
                isLiked = false
            )
        )
    }
}
