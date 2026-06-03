package com.app.dsalingo.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.ui.theme.*

@Composable
fun ProfileScreen(
    onSignOut: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            ProfileHeaderCard(onSignOut = onSignOut)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            ProfileStatsGrid()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            com.app.dsalingo.ui.screens.dashboard.DailyGoalCard(currentXp = 35, targetXp = 50)
            Spacer(modifier = Modifier.height(24.dp))
        }



        item {
            com.app.dsalingo.ui.screens.dashboard.AchievementsSection()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            com.app.dsalingo.ui.screens.dashboard.ActivityCalendar()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            ProUpgradeCard()
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileHeaderCard(onSignOut: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = { /* Edit Profile */ }) {
                    Icon(Icons.Default.Settings, contentDescription = "Edit Profile")
                }
                IconButton(onClick = onSignOut) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sign Out", tint = Color(0xFFEF4444))
                }
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Brush.linearGradient(listOf(BluePrimary, PurplePrimary)), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("A", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("AlexCoder", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("alex@example.com", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = BluePrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "Level 12",
                    color = BluePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text("Joined April 2026", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun ProfileStatsGrid() {
    Column {
        Text("Stats", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCardCompact(modifier = Modifier.weight(1f), icon = "⭐", title = "Total XP", value = "5,200")
            StatCardCompact(modifier = Modifier.weight(1f), icon = "🔥", title = "Current Streak", value = "12")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCardCompact(modifier = Modifier.weight(1f), icon = "👑", title = "Crowns Earned", value = "45")
            StatCardCompact(modifier = Modifier.weight(1f), icon = "📚", title = "Lessons Done", value = "128")
        }
    }
}

@Composable
fun StatCardCompact(modifier: Modifier = Modifier, icon: String, title: String, value: String) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight),
        color = Color.White
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}



@Composable
fun ProUpgradeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(PurplePrimary, Color(0xFF6B21A8))))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("⭐", fontSize = 32.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upgrade to Pro", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Unlock unlimited hearts, advanced features, and personalized learning paths.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                DuoButton(
                    text = "UPGRADE NOW",
                    onClick = { /* Upgrade */ },
                    color = Color.White,
                    shadowColor = Color.White.copy(alpha = 0.5f),
                    textColor = PurplePrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
