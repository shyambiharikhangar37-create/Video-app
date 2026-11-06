package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import com.example.data.repository.VideoRepository
import com.example.util.YouTubeUrlParser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UiNotification(val message: String)

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoRepository(application)

    // Current Navigation Screen ("home", "search", "upload", "profile")
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    // Selected Category ("all", "song", "drama", "comedy", "story", "culture")
    private val _selectedCategory = MutableStateFlow("all")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    // Fullscreen video state
    private val _fullscreenVideo = MutableStateFlow<VideoItem?>(null)
    val fullscreenVideo: StateFlow<VideoItem?> = _fullscreenVideo.asStateFlow()

    // In-feed currently active/playing video ID
    private val _activePlayingId = MutableStateFlow<Long?>(null)
    val activePlayingId: StateFlow<Long?> = _activePlayingId.asStateFlow()

    // Videos for home tab based on selected category
    val videos: StateFlow<List<VideoItem>> = _selectedCategory
        .flatMapLatest { cat ->
            repository.getVideosByCategory(cat)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Search query & results
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<VideoItem>> = _searchQuery
        .flatMapLatest { q ->
            if (q.isBlank()) {
                repository.getAllVideos()
            } else {
                repository.searchVideos(q.trim())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Direct extracted video ID from search query if user pasted a link/id directly
    val directParsedSearchId: StateFlow<String?> = combine(_searchQuery) { (q) ->
        YouTubeUrlParser.extractVideoId(q)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // User name
    private val _userName = MutableStateFlow(repository.getUserName())
    val userName: StateFlow<String> = _userName.asStateFlow()

    // Toast notifications
    private val _notifications = MutableSharedFlow<UiNotification>()
    val notifications: SharedFlow<UiNotification> = _notifications.asSharedFlow()

    // Expanded comments video ID
    private val _expandedCommentsVideoId = MutableStateFlow<Long?>(null)
    val expandedCommentsVideoId: StateFlow<Long?> = _expandedCommentsVideoId.asStateFlow()

    // Active comments list for expanded video
    val activeComments: StateFlow<List<CommentItem>> = _expandedCommentsVideoId
        .flatMapLatest { vId ->
            if (vId != null) {
                repository.getCommentsForVideo(vId)
            } else {
                MutableStateFlow(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            repository.ensureDefaultData()
        }
    }

    fun setScreen(screen: String) {
        _currentScreen.value = screen
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setActivePlaying(videoId: Long?) {
        _activePlayingId.value = videoId
    }

    fun openFullscreen(video: VideoItem) {
        _fullscreenVideo.value = video
    }

    fun closeFullscreen() {
        _fullscreenVideo.value = null
    }

    fun toggleComments(videoId: Long) {
        _expandedCommentsVideoId.value = if (_expandedCommentsVideoId.value == videoId) null else videoId
    }

    fun toggleLike(video: VideoItem) {
        viewModelScope.launch {
            repository.toggleLike(video)
            val msg = if (!video.isLiked) "❤️ पसंद किया!" else "पसंद हटाया"
            _notifications.emit(UiNotification(msg))
        }
    }

    fun addComment(videoId: Long, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.addComment(videoId, _userName.value, text)
            _notifications.emit(UiNotification("✅ टिप्पणी जोड़ी गई!"))
        }
    }

    fun deleteComment(commentId: Long) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
            _notifications.emit(UiNotification("🗑️ टिप्पणी हटाई गई"))
        }
    }

    fun saveUserName(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            viewModelScope.launch {
                _notifications.emit(UiNotification("⚠️ नाम खाली नहीं हो सकता!"))
            }
            return
        }
        repository.setUserName(trimmed)
        _userName.value = trimmed
        viewModelScope.launch {
            _notifications.emit(UiNotification("✅ प्रोफ़ाइल अपडेट हो गई!"))
        }
    }

    fun submitNewVideo(title: String, urlOrId: String, category: String, desc: String = ""): Boolean {
        val cleanId = YouTubeUrlParser.extractVideoId(urlOrId)
        if (title.isBlank() || cleanId.isNullOrBlank()) {
            viewModelScope.launch {
                _notifications.emit(UiNotification("⚠️ कृपया शीर्षक और वैध YouTube URL/ID भरें!"))
            }
            return false
        }

        viewModelScope.launch {
            repository.addVideo(
                title = title,
                desc = desc.ifBlank { "उपयोगकर्ता द्वारा जोड़ा गया" },
                category = category,
                youtubeId = cleanId
            )
            _notifications.emit(UiNotification("✅ वीडियो सफलतापूर्वक जोड़ा गया!"))
            _currentScreen.value = "home"
        }
        return true
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
            _userName.value = repository.getUserName()
            _notifications.emit(UiNotification("🗑️ सभी डेटा साफ़ कर दिया गया!"))
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _notifications.emit(UiNotification(message))
        }
    }
}
