package com.app.dsalingo.ui.screens.landing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.theme.*
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.ui.components.DuoSecondaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "DSALINGO",
                        fontWeight = FontWeight.ExtraBold,
                        color = DuoGreen
                    )
                },
                actions = {
                    TextButton(onClick = onNavigateToLogin) {
                        Text("LOG IN", fontWeight = FontWeight.Bold, color = DuoGray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(64.dp))
                
                // Mascot Placeholder
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(DuoGrayLight, RoundedCornerShape(90.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🦉", fontSize = 80.sp) // Representing Duo
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Text(
                    text = "The free, fun, and effective way to learn DSA!",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    lineHeight = 34.sp
                )
                
                Spacer(modifier = Modifier.height(48.dp))
                
                DuoButton(
                    text = "GET STARTED",
                    onClick = onNavigateToSignup,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                DuoSecondaryButton(
                    text = "I ALREADY HAVE AN ACCOUNT",
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
                )
                
                Spacer(modifier = Modifier.height(64.dp))
            }
            
            item {
                FeaturesSection()
            }
        }
    }
}

@Composable
fun FeaturesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DuoGrayLight.copy(alpha = 0.3f))
            .padding(24.dp)
    ) {
        FeatureItem("⚡", "Effective and efficient", "Our bite-sized lessons work. Learn DSA in just 5 minutes a day.")
        Spacer(modifier = Modifier.height(24.dp))
        FeatureItem("🎮", "Gamified learning", "Stay motivated with game-like features and fun challenges.")
        Spacer(modifier = Modifier.height(24.dp))
        FeatureItem("📊", "Personalized for you", "Lessons adapt to your learning style.")
    }
}

@Composable
fun FeatureItem(icon: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 40.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(desc, color = DuoGray, fontSize = 14.sp)
        }
    }
}
