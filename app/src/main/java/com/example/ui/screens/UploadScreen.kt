package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.viewmodel.VideoViewModel
import com.example.util.YouTubeUrlParser

@Composable
fun UploadScreen(
    viewModel: VideoViewModel,
    modifier: Modifier = Modifier
) {
    var title by remember { mutableStateOf("") }
    var urlOrId by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("song") }
    var desc by remember { mutableStateOf("") }
    var isTestPlaying by remember { mutableStateOf(false) }

    val extractedId = remember(urlOrId) {
        YouTubeUrlParser.extractVideoId(urlOrId)
    }

    val categories = listOf(
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "➕ वीडियो जोड़ें",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SaffronGold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Text(
            text = "अपना वीडियो YouTube लिंक या ID के साथ यहाँ साझा करें।",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = ForestCard),
            border = CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(ForestCardBorder)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Title Input
                Text(
                    text = "वीडियो का शीर्षक",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("जैसे: बुंदेली लोकगीत - रैया", fontSize = 13.sp, color = TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = SaffronGold) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ForestBlack,
                        unfocusedContainerColor = ForestBlack,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = ForestCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // YouTube URL or ID Input
                Text(
                    text = "YouTube लिंक या ID",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = urlOrId,
                    onValueChange = {
                        urlOrId = it
                        isTestPlaying = false
                    },
                    placeholder = { Text("जैसे: https://youtu.be/4NpfAs5_vLs या 4NpfAs5_vLs", fontSize = 12.sp, color = TextTertiary) },
                    leadingIcon = { Icon(Icons.Default.Link, contentDescription = null, tint = SaffronGold) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ForestBlack,
                        unfocusedContainerColor = ForestBlack,
                        focusedBorderColor = if (extractedId != null) EmeraldLight else EmeraldPrimary,
                        unfocusedBorderColor = ForestCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                // ID recognition badge & test play
                if (extractedId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "पहचाना गया ID: $extractedId",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldLight
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldDark,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { isTestPlaying = !isTestPlaying }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SaffronGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = if (isTestPlaying) "बंद करें" else "टेस्ट चलाएं",
                                    fontSize = 11.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Test Player Box
                    if (isTestPlaying) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            YouTubePlayerView(
                                youtubeId = extractedId,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selection
                Text(
                    text = "श्रेणी (Category)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (catKey, catLabel) ->
                        val isSelected = category == catKey
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) CrimsonAccent else ForestBlack,
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(
                                    if (isSelected) CrimsonAccent else ForestCardBorder
                                )
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { category = catKey }
                        ) {
                            Text(
                                text = catLabel,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = TextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Description
                Text(
                    text = "विवरण (वैकल्पिक)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    placeholder = { Text("वीडियो के बारे में संक्षेप में लिखें...", fontSize = 12.sp, color = TextTertiary) },
                    singleLine = false,
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ForestBlack,
                        unfocusedContainerColor = ForestBlack,
                        focusedBorderColor = EmeraldPrimary,
                        unfocusedBorderColor = ForestCardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Submit Button
                Button(
                    onClick = {
                        val success = viewModel.submitNewVideo(
                            title = title,
                            urlOrId = urlOrId,
                            category = category,
                            desc = desc
                        )
                        if (success) {
                            title = ""
                            urlOrId = ""
                            desc = ""
                            isTestPlaying = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "सबमिट करें",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
