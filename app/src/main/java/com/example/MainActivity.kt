package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FullscreenPlayerDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.theme.CrimsonAccent
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.ForestBlack
import com.example.ui.theme.ForestCard
import com.example.ui.theme.ForestCardBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SaffronGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.VideoViewModel

data class NavDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

class MainActivity : ComponentActivity() {

    private val viewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen(viewModel = viewModel, onExitApp = { finish() })
            }
        }
    }
}

@Composable
fun MainAppScreen(
    viewModel: VideoViewModel,
    onExitApp: () -> Unit
) {
    val context = LocalContext.current
    val currentScreen by viewModel.currentScreen.collectAsState()
    val fullscreenVideo by viewModel.fullscreenVideo.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // Double-press back handler for home screen, or return to home from other screens
    BackHandler {
        if (fullscreenVideo != null) {
            viewModel.closeFullscreen()
        } else if (currentScreen != "home") {
            viewModel.setScreen("home")
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackPressTime < 2000L) {
                onExitApp()
            } else {
                lastBackPressTime = now
                Toast.makeText(context, "🔙 ऐप बंद करने के लिए फिर से बैक बटन दबाएं", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Collect notifications/toasts
    LaunchedEffect(Unit) {
        viewModel.notifications.collect { notif ->
            Toast.makeText(context, notif.message, Toast.LENGTH_SHORT).show()
        }
    }

    val destinations = listOf(
        NavDestination(
            route = "home",
            label = "होम",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        NavDestination(
            route = "search",
            label = "खोजें",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search
        ),
        NavDestination(
            route = "upload",
            label = "जोड़ें",
            selectedIcon = Icons.Filled.AddCircle,
            unselectedIcon = Icons.Outlined.AddCircleOutline
        ),
        NavDestination(
            route = "profile",
            label = "प्रोफ़ाइल",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person
        )
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = ForestBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = ForestCard,
                tonalElevation = 8.dp
            ) {
                destinations.forEach { dest ->
                    val isSelected = currentScreen == dest.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setScreen(dest.route) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) dest.selectedIcon else dest.unselectedIcon,
                                contentDescription = dest.label
                            )
                        },
                        label = {
                            Text(
                                text = dest.label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SaffronGold,
                            selectedTextColor = SaffronGold,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = EmeraldPrimary.copy(alpha = 0.25f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                "home" -> HomeScreen(viewModel = viewModel)
                "search" -> SearchScreen(viewModel = viewModel)
                "upload" -> UploadScreen(viewModel = viewModel)
                "profile" -> ProfileScreen(viewModel = viewModel)
            }
        }
    }

    // Fullscreen video viewer
    fullscreenVideo?.let { video ->
        FullscreenPlayerDialog(
            video = video,
            onClose = { viewModel.closeFullscreen() }
        )
    }
}
