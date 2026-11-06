package com.example

import com.example.util.YouTubeUrlParser
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testYouTubeUrlParser_variousFormats() {
        assertEquals("4NpfAs5_vLs", YouTubeUrlParser.extractVideoId("4NpfAs5_vLs"))
        assertEquals("4NpfAs5_vLs", YouTubeUrlParser.extractVideoId("https://www.youtube.com/watch?v=4NpfAs5_vLs"))
        assertEquals("4NpfAs5_vLs", YouTubeUrlParser.extractVideoId("https://youtu.be/4NpfAs5_vLs"))
        assertEquals("dQw4w9WgXcQ", YouTubeUrlParser.extractVideoId("https://www.youtube.com/shorts/dQw4w9WgXcQ"))
        assertEquals("juKd26qkWAo", YouTubeUrlParser.extractVideoId("https://www.youtube.com/embed/juKd26qkWAo"))
        assertEquals("o0bkp9zwNk4", YouTubeUrlParser.extractVideoId("https://youtu.be/o0bkp9zwNk4?si=abc123xyz&t=30"))
        assertEquals("4NpfAs5_vLs", YouTubeUrlParser.extractVideoId("  4NpfAs5_vLs  "))
    }
}
