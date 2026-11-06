package com.example.util

import java.util.regex.Pattern

object YouTubeUrlParser {

    private val YOUTUBE_PATTERNS = listOf(
        // youtu.be/<id>
        Pattern.compile("(?:https?://)?(?:www\\.)?youtu\\.be/([a-zA-Z0-9_-]{11})", Pattern.CASE_INSENSITIVE),
        // youtube.com/watch?v=<id>
        Pattern.compile("(?:https?://)?(?:[a-zA-Z0-9_]+\\.)?youtube\\.com/watch\\?.*v=([a-zA-Z0-9_-]{11})", Pattern.CASE_INSENSITIVE),
        // youtube.com/shorts/<id>
        Pattern.compile("(?:https?://)?(?:[a-zA-Z0-9_]+\\.)?youtube\\.com/shorts/([a-zA-Z0-9_-]{11})", Pattern.CASE_INSENSITIVE),
        // youtube.com/embed/<id>
        Pattern.compile("(?:https?://)?(?:[a-zA-Z0-9_]+\\.)?youtube\\.com/embed/([a-zA-Z0-9_-]{11})", Pattern.CASE_INSENSITIVE),
        // youtube.com/v/<id>
        Pattern.compile("(?:https?://)?(?:[a-zA-Z0-9_]+\\.)?youtube\\.com/v/([a-zA-Z0-9_-]{11})", Pattern.CASE_INSENSITIVE),
        // youtube.com/live/<id>
        Pattern.compile("(?:https?://)?(?:[a-zA-Z0-9_]+\\.)?youtube\\.com/live/([a-zA-Z0-9_-]{11})", Pattern.CASE_INSENSITIVE),
        // bare 11-char ID
        Pattern.compile("^([a-zA-Z0-9_-]{11})$")
    )

    /**
     * Extracts YouTube video ID from various URL formats or raw ID input.
     * Returns the 11-char ID if found, or the cleaned input if already looks like an ID.
     */
    fun extractVideoId(input: String?): String? {
        if (input.isNullOrBlank()) return null
        val trimmed = input.trim()

        for (pattern in YOUTUBE_PATTERNS) {
            val matcher = pattern.matcher(trimmed)
            if (matcher.find()) {
                val id = matcher.group(1)
                if (!id.isNullOrBlank()) {
                    return id
                }
            }
        }

        // Fallback: check query parameter v= or split by /
        if (trimmed.contains("v=")) {
            val afterV = trimmed.substringAfter("v=")
            val id = afterV.takeWhile { it != '&' && it != '?' && it != '#' }
            if (id.length == 11) return id
        }

        if (trimmed.contains("youtu.be/")) {
            val afterSlash = trimmed.substringAfter("youtu.be/")
            val id = afterSlash.takeWhile { it != '?' && it != '&' && it != '/' && it != '#' }
            if (id.length == 11) return id
        }

        // If the user entered just letters/numbers between 8 and 15 chars, return it as fallback
        if (trimmed.matches(Regex("^[a-zA-Z0-9_-]{8,15}$"))) {
            return trimmed
        }

        return null
    }

    fun getThumbnailUrl(videoId: String): String {
        return "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
    }

    fun getWatchUrl(videoId: String): String {
        return "https://www.youtube.com/watch?v=$videoId"
    }

    fun getShortUrl(videoId: String): String {
        return "https://youtu.be/$videoId"
    }
}
