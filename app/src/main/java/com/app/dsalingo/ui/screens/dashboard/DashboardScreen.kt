package com.app.dsalingo.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.theme.*
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.R
import com.app.dsalingo.ui.components.LottieAnimationRawRes

@Composable
fun DashboardScreen(
    onNavigateToLearn: () -> Unit,
    onNavigateToChallenges: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            WelcomeBanner(username = "Alex")
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            StatsRow()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            DailyGoalCard(currentXp = 35, targetXp = 50)
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            DuoButton(text = "CONTINUE LEARNING", onClick = onNavigateToLearn, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
            DuoButton(text = "TAKE CHALLENGE", onClick = onNavigateToChallenges, color = DuoBlue, shadowColor = DuoBlueDark, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            AchievementsSection()
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            ActivityCalendar()
        }
    }
}



@Composable
fun WelcomeBanner(username: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(60.dp).background(DuoGrayLight, RoundedCornerShape(30.dp)), contentAlignment = Alignment.Center) {
            LottieAnimationRawRes(resId = R.raw.angry_bird)        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text("Welcome back, $username!", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text("Ready to crush some DSA?", color = DuoGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatsRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatBadge(icon = "🔥", value = "12", label = "Streak")
        StatBadge(icon = "👑", value = "45", label = "Crowns")
        StatBadge(icon = "⭐", value = "1.2k", label = "XP")
    }
}

@Composable
fun StatBadge(icon: String, value: String, label: String) {
    Surface(
        modifier = Modifier.width(100.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight)
    ) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(label, fontSize = 10.sp, color = DuoGray)
        }
    }
}

@Composable
fun DailyGoalCard(currentXp: Int, targetXp: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("DAILY GOAL", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = DuoGray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                LinearProgressIndicator(
                    progress = { currentXp.toFloat() / targetXp.toFloat() },
                    modifier = Modifier.weight(1f).height(12.dp).clip(RoundedCornerShape(6.dp)),
                    color = DuoOrange,
                    trackColor = DuoGrayLight
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("$currentXp/$targetXp XP", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun AchievementsSection() {
    Column {
        Text("ACHIEVEMENTS", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = DuoGray)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AchievementCircle("🥇")
            AchievementCircle("🎯")
            AchievementCircle("🚀")
            AchievementCircle("💎")
        }
    }
}

@Composable
fun AchievementCircle(icon: String) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .border(2.dp, DuoGrayLight, RoundedCornerShape(30.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 30.sp)
    }
}

@Composable
fun ActivityCalendar() {
    Column {
        Text("ACTIVITY", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = DuoGray)
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                // Mock Heatmap
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    repeat(10) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(7) {
                                Box(modifier = Modifier.size(12.dp).background(if ((0..1).random() == 1) DuoGreen else DuoGrayLight, RoundedCornerShape(2.dp)))
                            }
                        }
                    }
                }
            }
        }
    }
}
