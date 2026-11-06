package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.VideoItem
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestCard
import com.example.ui.theme.ForestCardBorder
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.YouTubeUrlParser
import java.net.URLEncoder

@Composable
fun ShareDialog(
    video: VideoItem,
    onDismiss: () -> Unit,
    onToast: (String) -> Unit
) {
    val context = LocalContext.current
    val videoUrl = YouTubeUrlParser.getShortUrl(video.youtubeId)
    val shareText = "${video.title} - यह वीडियो VidPlayer पर देखें: $videoUrl"

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ForestCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ForestCardBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📤 शेयर करें",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronGold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = video.title,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Grid of share targets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareTargetButton(label = "WhatsApp", iconText = "💬", color = Color(0xFF25D366)) {
                        launchExternalShare(context, "com.whatsapp", shareText, videoUrl)
                        onDismiss()
                    }
                    ShareTargetButton(label = "Telegram", iconText = "✈️", color = Color(0xFF0088CC)) {
                        launchExternalShare(context, "org.telegram.messenger", shareText, videoUrl)
                        onDismiss()
                    }
                    ShareTargetButton(label = "Facebook", iconText = "📘", color = Color(0xFF1877F2)) {
                        val fbUrl = "https://www.facebook.com/sharer/sharer.php?u=${URLEncoder.encode(videoUrl, "UTF-8")}"
                        openWebUrl(context, fbUrl)
                        onDismiss()
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ShareTargetButton(label = "X / Twitter", iconText = "🐦", color = Color(0xFF1DA1F2)) {
                        val xUrl = "https://twitter.com/intent/tweet?text=${URLEncoder.encode(shareText, "UTF-8")}"
                        openWebUrl(context, xUrl)
                        onDismiss()
                    }
                    ShareTargetButton(label = "Copy Link", iconText = "🔗", color = SaffronGold) {
                        copyToClipboard(context, videoUrl)
                        onToast("🔗 लिंक कॉपी हो गया!")
                        onDismiss()
                    }
                    ShareTargetButton(label = "अन्य ऐप", iconText = "⋯", color = EmeraldPrimary) {
                        launchNativeShare(context, shareText, videoUrl)
                        onDismiss()
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("बंद करें", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DownloadDialog(
    video: VideoItem,
    onDismiss: () -> Unit,
    onToast: (String) -> Unit
) {
    val context = LocalContext.current
    val watchUrl = YouTubeUrlParser.getWatchUrl(video.youtubeId)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ForestCard),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ForestCardBorder))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⬇️ डाउनलोड सहायता",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronGold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "वीडियो लिंक कॉपी करें या ब्राउज़र में डाउनलोडर खोलें:",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = watchUrl,
                        fontSize = 12.sp,
                        color = SaffronGold,
                        modifier = Modifier.padding(10.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        copyToClipboard(context, watchUrl)
                        onToast("📋 लिंक कॉपी हो गया!")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("📋 लिंक कॉपी करें", color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val downloaderUrl = "https://www.y2mate.com/youtube/${video.youtubeId}"
                        openWebUrl(context, downloaderUrl)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("🔽 डाउनलोडर खोलें", color = TextPrimary)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("रद्द करें", color = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun ShareTargetButton(
    label: String,
    iconText: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = color.copy(alpha = 0.2f),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(text = iconText, fontSize = 22.sp)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextPrimary)
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("VidPlayer", text)
    clipboard.setPrimaryClip(clip)
}

private fun openWebUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {}
}

private fun launchExternalShare(context: Context, packageName: String, text: String, url: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$text $url")
            setPackage(packageName)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        launchNativeShare(context, text, url)
    }
}

private fun launchNativeShare(context: Context, text: String, url: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "$text $url")
        }
        context.startActivity(Intent.createChooser(intent, "शेयर करें"))
    } catch (_: Exception) {}
}
