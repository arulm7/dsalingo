package com.app.dsalingo.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.dsalingo.ui.navigation.Screen
import com.app.dsalingo.ui.theme.BluePrimary
import com.app.dsalingo.ui.theme.CrownYellow
import com.app.dsalingo.ui.theme.HeartRed
import com.app.dsalingo.ui.theme.StreakOrange

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
                TopAppBar(
                    title = {
                        Text(
                            "DSALINGO",
                            fontWeight = FontWeight.ExtraBold,
                            color = BluePrimary,
                            modifier = Modifier.clickable {
                                navController.navigate(Screen.Dashboard.route) {
                                    popUpTo(Screen.Dashboard.route) { inclusive = true }
                                }
                            }
                        )
                    },
                    actions = {
                        // Mock Stats
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            Text("8", fontWeight = FontWeight.Bold, color = HeartRed)
                            Text("❤️", modifier = Modifier.padding(start = 2.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            Text("12", fontWeight = FontWeight.Bold, color = StreakOrange)
                            Text("🔥", modifier = Modifier.padding(start = 2.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            Text("45", fontWeight = FontWeight.Bold, color = CrownYellow)
                            Text("👑", modifier = Modifier.padding(start = 2.dp))
                        }
                        IconButton(onClick = onToggleTheme) {
                            Icon(
                                imageVector = if (isDarkTheme) Icons.Default.Face else Icons.Default.Star, // Placeholder for Moon/Sun
                                contentDescription = "Toggle Theme"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Screen.Learn.route || currentRoute == Screen.CategoryDetail.route,
                        onClick = { 
                            navController.navigate(Screen.Learn.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            } 
                        },
                        icon = { Icon(Icons.Default.Menu, contentDescription = "Learn") },
                        label = { Text("Learn") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Challenges.route,
                        onClick = { 
                            navController.navigate(Screen.Challenges.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            } 
                        },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Challenges") },
                        label = { Text("Challenges") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Leaderboard.route,
                        onClick = { 
                            navController.navigate(Screen.Leaderboard.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            } 
                        },
                        icon = { Icon(Icons.Default.Star, contentDescription = "Leaderboard") },
                        label = { Text("Leaderboard") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == Screen.Profile.route,
                        onClick = { 
                            navController.navigate(Screen.Profile.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            } 
                        },
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text("Profile") }
                    )
                }
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}
