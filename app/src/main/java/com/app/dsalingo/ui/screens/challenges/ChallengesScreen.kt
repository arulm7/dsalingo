package com.app.dsalingo.ui.screens.challenges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.ui.theme.BluePrimary
import com.app.dsalingo.ui.theme.GreenPrimary
import com.app.dsalingo.ui.theme.XPGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen() {
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Code Challenges",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Curated problems from Striver's A2Z DSA Sheet",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatBox(modifier = Modifier.weight(1f), label = "Total", value = "105", color = MaterialTheme.colorScheme.onSurface)
            StatBox(modifier = Modifier.weight(1f), label = "Easy", value = "42", color = GreenPrimary)
            StatBox(modifier = Modifier.weight(1f), label = "Medium", value = "48", color = Color(0xFFEAB308))
            StatBox(modifier = Modifier.weight(1f), label = "Hard", value = "15", color = Color(0xFFEF4444))
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search challenges...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = true, onClick = {}, label = { Text("All") })
            FilterChip(selected = false, onClick = {}, label = { Text("Array") })
            FilterChip(selected = false, onClick = {}, label = { Text("String") })
            FilterChip(selected = false, onClick = {}, label = { Text("Tree") })
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Showing 105 challenges", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(10) { index ->
                val difficultyColor = if (index % 3 == 0) GreenPrimary else if (index % 3 == 1) Color(0xFFEAB308) else Color(0xFFEF4444)
                val xp = if (index % 3 == 0) 50 else if (index % 3 == 1) 75 else 100
                ChallengeCard(
                    title = "Challenge ${index + 1}",
                    category = if (index % 2 == 0) "Array" else "String",
                    difficultyColor = difficultyColor,
                    xp = xp
                )
            }
        }
    }
}

@Composable
fun StatBox(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
    }
}

@Composable
fun ChallengeCard(title: String, category: String, difficultyColor: Color, xp: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).clip(RoundedCornerShape(6.dp)).background(difficultyColor))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Surface(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(category, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("+$xp XP", fontWeight = FontWeight.Bold, color = XPGold, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { /* Open Leetcode Link */ }, contentPadding = PaddingValues(horizontal = 16.dp)) {
                    Text("Start")
                }
            }
        }
    }
}
