package com.app.dsalingo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.dsalingo.ui.navigation.Screen
import com.app.dsalingo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isPrivateScreen = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Learn.route,
        Screen.CategoryDetail.route,
        Screen.Challenges.route,
        Screen.Leaderboard.route,
        Screen.Profile.route
    )

    val showBottomBar = currentRoute in listOf(
        Screen.Dashboard.route,
        Screen.Learn.route,
        Screen.Challenges.route,
        Screen.Leaderboard.route,
        Screen.Profile.route
    )

    Scaffold(
        topBar = {
            if (isPrivateScreen) {
                Surface(
                    color = Color.White
                ) {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    "DSALINGO",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = DuoGreen,
                                    fontSize = 20.sp,
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.Dashboard.route) {
                                            popUpTo(Screen.Dashboard.route) { inclusive = true }
                                        }
                                    }
                                )
                            },
                            actions = {
                                // Mock Stats
                                StatIcon(text = "8", icon = "❤️", color = DuoRed)
                                StatIcon(text = "12", icon = "🔥", color = DuoOrange)
                                StatIcon(text = "45", icon = "👑", color = DuoYellow)
                                
                                IconButton(onClick = onToggleTheme) {
                                    Icon(
                                        imageVector = if (isDarkTheme) Icons.Default.Face else Icons.Default.Settings,
                                        contentDescription = "Toggle Theme",
                                        tint = DuoGray
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White
                            )
                        )
                        Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(DuoGrayLight))
                    }
                }
            }
        },
        bottomBar = {
            if (showBottomBar) {
                DuoFloatingBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            content(paddingValues)
        }
    }
}

@Composable
fun StatIcon(text: String, icon: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(2.dp))
        Text(text, fontWeight = FontWeight.ExtraBold, color = color, fontSize = 16.sp)
    }
}
