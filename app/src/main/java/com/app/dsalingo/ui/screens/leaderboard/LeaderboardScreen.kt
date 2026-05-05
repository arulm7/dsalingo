package com.app.dsalingo.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.app.dsalingo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Leaderboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Compete with others and earn your spot",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    shape = RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp)
                ) { Text("This Week") }
                SegmentedButton(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    shape = RoundedCornerShape(0.dp)
                ) { Text("This Month") }
                SegmentedButton(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) { Text("All Time") }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            Podium()
            Spacer(modifier = Modifier.height(32.dp))
        }

        items(10) { index ->
            val rank = index + 4
            LeaderboardItem(rank = rank, username = "User $rank", xp = 5000 - (rank * 100), streak = 10, isCurrentUser = rank == 7)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun Podium() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(listOf(BluePrimary, Color(0xFF4F46E5))),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            PodiumPlace(rank = 2, username = "Sarah", xp = 4800, height = 100.dp, color = Color(0xFF9CA3AF))
            PodiumPlace(rank = 1, username = "Alex", xp = 5200, height = 140.dp, color = Color(0xFFEAB308))
            PodiumPlace(rank = 3, username = "John", xp = 4500, height = 80.dp, color = Color(0xFFF97316))
        }
    }
}

@Composable
fun PodiumPlace(rank: Int, username: String, xp: Int, height: androidx.compose.ui.unit.Dp, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(username.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("$xp XP", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .width(70.dp)
                .height(height)
                .background(color, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                rank.toString(),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun LeaderboardItem(rank: Int, username: String, xp: Int, streak: Int, isCurrentUser: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentUser) BluePrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isCurrentUser) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                rank.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.width(30.dp)
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BluePrimary.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(username.take(1), fontWeight = FontWeight.Bold, color = BluePrimary)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(username, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                if (isCurrentUser) {
                    Text("You", fontSize = 12.sp, color = BluePrimary)
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text("$xp XP", fontWeight = FontWeight.Bold, color = XPGold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(streak.toString(), color = StreakOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("🔥", fontSize = 12.sp)
                }
            }
        }
    }
}
