package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommentItem
import com.example.data.model.VideoItem
import com.example.player.YouTubePlayerView
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
import com.example.ui.theme.TextTertiary

@Composable
fun VideoCard(
    video: VideoItem,
    isCommentsExpanded: Boolean,
    comments: List<CommentItem>,
    onToggleLike: () -> Unit,
    onToggleComments: () -> Unit,
    onAddComment: (String) -> Unit,
    onDeleteComment: (Long) -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    onFullscreen: () -> Unit,
    onAppShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    var newCommentText by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ForestCard),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(ForestCardBorder)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Video Player Box (16:9)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp))
                    .background(Color.Black)
            ) {
                YouTubePlayerView(
                    youtubeId = video.youtubeId,
                    modifier = Modifier.fillMaxWidth()
                )

                // Fullscreen floating button inside player top-right corner
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(36.dp)
                ) {
                    IconButton(onClick = onFullscreen) {
                        Icon(
                            Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Video Info Compact
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = video.title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CategoryBadge(category = video.category)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.desc,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Divider
            Divider(color = ForestCardBorder, thickness = 1.dp)

            // Action Buttons Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestCard)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like Button
                ActionButton(
                    icon = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    label = "${video.likesCount}",
                    tint = if (video.isLiked) CrimsonAccent else TextSecondary,
                    onClick = onToggleLike
                )

                // Comment Button
                ActionButton(
                    icon = Icons.Default.ChatBubbleOutline,
                    label = if (comments.isNotEmpty()) "${comments.size}" else "टिप्पणी",
                    tint = if (isCommentsExpanded) SaffronGold else TextSecondary,
                    onClick = onToggleComments
                )

                // Share Button
                ActionButton(
                    icon = Icons.Default.Share,
                    label = "शेयर",
                    tint = TextSecondary,
                    onClick = onShare
                )

                // Download / Link Helper Button
                ActionButton(
                    icon = Icons.Default.FileDownload,
                    label = "डाउनलोड",
                    tint = TextSecondary,
                    onClick = onDownload
                )

                // Fullscreen Button
                ActionButton(
                    icon = Icons.Default.Fullscreen,
                    label = "फुल स्क्रीन",
                    tint = TextSecondary,
                    onClick = onFullscreen
                )
            }

            // Promo Banner (as in prototype)
            PromoBanner(onShareApp = onAppShare)

            // Expandable Comments Section
            AnimatedVisibility(
                visible = isCommentsExpanded,
                enter = expandVertically(animationSpec = tween(250)) + fadeIn(),
                exit = shrinkVertically(animationSpec = tween(250)) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(ForestBlack)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "💭 टिप्पणियाँ (${comments.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronGold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("अपनी राय लिखें...", fontSize = 12.sp, color = TextTertiary) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ForestCard,
                                unfocusedContainerColor = ForestCard,
                                focusedBorderColor = EmeraldPrimary,
                                unfocusedBorderColor = ForestCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    onAddComment(newCommentText)
                                    newCommentText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Comments list
                    if (comments.isEmpty()) {
                        Text(
                            text = "अभी तक कोई टिप्पणी नहीं है। पहली टिप्पणी लिखें!",
                            fontSize = 11.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        comments.forEach { comment ->
                            CommentRow(
                                comment = comment,
                                onDelete = { onDeleteComment(comment.id) }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = tint,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PromoBanner(onShareApp: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            EmeraldDark,
                            EmeraldPrimary,
                            SaffronGold.copy(alpha = 0.85f)
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "📱 इस ऐप को अपने दोस्तों को शेयर करें",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "और इसे प्रसिद्ध बनाने में योगदान दें! 🚀",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Button(
                    onClick = onShareApp,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = EmeraldDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 12.dp,
                        vertical = 6.dp
                    )
                ) {
                    Text(
                        text = "📤 शेयर",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentRow(
    comment: CommentItem,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ForestCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${comment.authorName}:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SaffronGold
                )
                Text(
                    text = comment.commentText,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Delete comment",
                    tint = CrimsonAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CategoryBadge(category: String) {
    val (label, bg) = when (category) {
        "song" -> "लोकगीत" to EmeraldPrimary
        "drama" -> "नाटक" to CrimsonAccent
        "comedy" -> "हास्य" to SaffronGold
        "story" -> "कहानी" to EmeraldDark
        "culture" -> "संस्कृति" to Color(0xFF6A1B9A)
        else -> category to EmeraldPrimary
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg.copy(alpha = 0.25f),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(bg.copy(alpha = 0.6f))
        )
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (bg == SaffronGold) SaffronGold else EmeraldLight,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )
    }
}
