package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VideoItem
import com.example.ui.components.DownloadDialog
import com.example.ui.components.ShareDialog
import com.example.ui.components.VideoCard
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestBlack
import com.example.ui.theme.ForestCard
import com.example.ui.theme.ForestCardBorder
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VideoViewModel

@Composable
fun HomeScreen(
    viewModel: VideoViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videos by viewModel.videos.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val expandedCommentsVideoId by viewModel.expandedCommentsVideoId.collectAsState()
    val activeComments by viewModel.activeComments.collectAsState()

    var shareDialogVideo by remember { mutableStateOf<VideoItem?>(null) }
    var downloadDialogVideo by remember { mutableStateOf<VideoItem?>(null) }

    val categories = listOf(
        "all" to "सभी",
        "song" to "लोकगीत",
        "drama" to "नाटक",
        "comedy" to "हास्य",
        "story" to "कहानी",
        "culture" to "संस्कृति"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ForestBlack)
    ) {
        // App Header with vibrant gradient
        HeaderBar(
            onAdClick = {
                viewModel.showToast("📢 विज्ञापन स्पेस - आपके प्रचार के लिए!")
            }
        )

        // Categories Scrollable Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ForestCard)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { (catKey, catLabel) ->
                val isSelected = selectedCategory == catKey
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) CrimsonAccent else ForestCardBorder,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.setCategory(catKey) }
                ) {
                    Text(
                        text = catLabel,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Video Feed
        if (videos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PlayCircleOutline,
                        contentDescription = null,
                        tint = SaffronGold,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "इस श्रेणी में कोई वीडियो नहीं है",
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp)
            ) {
                items(videos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        isCommentsExpanded = expandedCommentsVideoId == video.id,
                        comments = if (expandedCommentsVideoId == video.id) activeComments else emptyList(),
                        onToggleLike = { viewModel.toggleLike(video) },
                        onToggleComments = { viewModel.toggleComments(video.id) },
                        onAddComment = { text -> viewModel.addComment(video.id, text) },
                        onDeleteComment = { commentId -> viewModel.deleteComment(commentId) },
                        onShare = { shareDialogVideo = video },
                        onDownload = { downloadDialogVideo = video },
                        onFullscreen = { viewModel.openFullscreen(video) },
                        onAppShare = {
                            shareEntireApp(context)
                        }
                    )
                }
            }
        }
    }

    // Share Dialog
    shareDialogVideo?.let { video ->
        ShareDialog(
            video = video,
            onDismiss = { shareDialogVideo = null },
            onToast = { viewModel.showToast(it) }
        )
    }

    // Download Dialog
    downloadDialogVideo?.let { video ->
        DownloadDialog(
            video = video,
            onDismiss = { downloadDialogVideo = null },
            onToast = { viewModel.showToast(it) }
        )
    }
}

@Composable
private fun HeaderBar(onAdClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(EmeraldDark, CrimsonAccent, SaffronGold)
                    )
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "वीडियो ऐप",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Ad frame placeholder
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.45f),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(SaffronGold.copy(alpha = 0.7f))
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable(onClick = onAdClick)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Campaign,
                            contentDescription = null,
                            tint = SaffronGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = "📢 विज्ञापन स्थान (Ad Space)",
                            fontSize = 11.sp,
                            color = SaffronGold,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

private fun shareEntireApp(context: Context) {
    try {
        val shareText = "यह वीडियो ऐप जरूर देखें! बुंदेली गीत, नाटक, हास्य और कहानियाँ। सीधे ऐप में देखें: https://youtu.be/4NpfAs5_vLs"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "ऐप शेयर करें"))
    } catch (_: Exception) {}
}
