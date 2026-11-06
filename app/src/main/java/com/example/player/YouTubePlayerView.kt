package com.example.player

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.SaffronGold
import com.example.util.YouTubeUrlParser

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayerView(
    youtubeId: String,
    modifier: Modifier = Modifier,
    autoplay: Boolean = true,
    isExpandedOrFullscreen: Boolean = false,
    onFullscreenToggle: (() -> Unit)? = null
) {
    var isPlaying by remember(youtubeId) { mutableStateOf(autoplay) }

    Box(
        modifier = modifier
            .background(ComposeColor.Black),
        contentAlignment = Alignment.Center
    ) {
        if (!isPlaying) {
            // Thumbnail with Play overlay
            val thumbUrl = YouTubeUrlParser.getThumbnailUrl(youtubeId)
            AsyncImage(
                model = thumbUrl,
                contentDescription = "Video Thumbnail",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Dark tint
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ComposeColor.Black.copy(alpha = 0.35f))
            )

            // Play button circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(CrimsonAccent.copy(alpha = 0.9f))
                    .clickable { isPlaying = true },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Video",
                    tint = ComposeColor.White,
                    modifier = Modifier.size(38.dp)
                )
            }
        } else {
            // Live WebView player
            AndroidYouTubeWebView(
                youtubeId = youtubeId,
                autoplay = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AndroidYouTubeWebView(
    youtubeId: String,
    autoplay: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPageLoaded by remember { mutableStateOf(false) }

    val htmlData = remember(youtubeId, autoplay) {
        val autoPlayFlag = if (autoplay) 1 else 0
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                html, body {
                    width: 100%;
                    height: 100%;
                    background: #000000;
                    overflow: hidden;
                }
                #player {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                }
            </style>
        </head>
        <body>
            <div id="player"></div>
            <script>
                var tag = document.createElement('script');
                tag.src = "https://www.youtube.com/iframe_api";
                var firstScriptTag = document.getElementsByTagName('script')[0];
                firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

                var player;
                function onYouTubeIframeAPIReady() {
                    player = new YT.Player('player', {
                        videoId: '$youtubeId',
                        playerVars: {
                            'autoplay': $autoPlayFlag,
                            'controls': 1,
                            'rel': 0,
                            'playsinline': 1,
                            'fs': 1,
                            'modestbranding': 1,
                            'enablejsapi': 1,
                            'iv_load_policy': 3
                        },
                        events: {
                            'onReady': function(e) {
                                if ($autoPlayFlag === 1) {
                                    e.target.playVideo();
                                }
                            }
                        }
                    });
                }
            </script>
        </body>
        </html>
        """.trimIndent()
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(Color.BLACK)

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        allowFileAccess = true
                        databaseEnabled = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_DEFAULT
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isPageLoaded = true
                        }
                    }

                    webChromeClient = WebChromeClient()

                    loadDataWithBaseURL(
                        "https://www.youtube.com",
                        htmlData,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            update = { webView ->
                // Refresh if youtubeId changed
                if (webView.tag != youtubeId) {
                    webView.tag = youtubeId
                    webView.loadDataWithBaseURL(
                        "https://www.youtube.com",
                        htmlData,
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (!isPageLoaded) {
            CircularProgressIndicator(
                color = SaffronGold,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
