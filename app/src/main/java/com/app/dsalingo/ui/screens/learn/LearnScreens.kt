package com.app.dsalingo.ui.screens.learn

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.data.model.DataStructureCategory
import com.app.dsalingo.ui.theme.*

// Added lessonCount to mock data
val mockCategories = listOf(
    DataStructureCategory("basics", "Fundamentals", "📚", 0xFF58CC02, 20, 10),
    DataStructureCategory("array", "Arrays", "📊", 0xFF1CB0F6, 45, 12),
    DataStructureCategory("string", "Strings", "🔤", 0xFFCE82FF, 30, 5),
    DataStructureCategory("linkedlist", "Linked Lists", "🔗", 0xFFFF9600, 25, 0),
    DataStructureCategory("stack", "Stacks", "📚", 0xFFEA2B2B, 15, 0)
)

// Helper to get dynamic lesson count for UI path
fun getLessonCountForCategory(categoryId: String): Int {
    return when(categoryId) {
        "basics" -> 10
        "array" -> 12
        "string" -> 8
        "linkedlist" -> 10
        "stack" -> 6
        else -> 5
    }
}

@Composable
fun LearnScreen(
    onNavigateToCategory: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("LEARNING PATH", fontWeight = FontWeight.ExtraBold, color = DuoGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(24.dp))
        }

        items(mockCategories) { category ->
            DuoCategoryNode(category, onNavigateToCategory)
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DuoCategoryNode(category: DataStructureCategory, onClick: (String) -> Unit) {
    val color = Color(category.color)
    val darkColor = when(category.color.toLong()) {
        0xFF58CC02L -> DuoGreenDark
        0xFF1CB0F6L -> DuoBlueDark
        0xFFFF9600L -> DuoOrangeDark
        0xFFEA2B2BL -> DuoRedDark
        else -> color.copy(alpha = 0.8f)
    }

    val progressTarget = category.completedQuestions.toFloat() / category.totalQuestions.toFloat()
    val animatedProgress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(96.dp),
                color = DuoYellow,
                strokeWidth = 8.dp,
                trackColor = DuoGrayLight.copy(alpha = 0.3f)
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clickable { onClick(category.id) },
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.fillMaxSize().padding(top = 6.dp).background(darkColor, CircleShape))
                Box(
                    modifier = Modifier.fillMaxSize().padding(bottom = 6.dp).background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(category.icon, fontSize = 36.sp)
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(category.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    categoryId: String,
    onNavigateBack: () -> Unit,
    onNavigateToLesson: (String, String) -> Unit
) {
    val category = mockCategories.find { it.id == categoryId } ?: mockCategories[0]
    val color = Color(category.color)
    val totalLessons = getLessonCountForCategory(categoryId)
    
    // Dynamic completed logic for mock UI
    val completedCount = (category.completedQuestions.toFloat() / category.totalQuestions.toFloat() * totalLessons).toInt()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.title, fontWeight = FontWeight.ExtraBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = null) }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 32.dp)
        ) {
            items(totalLessons) { i ->
                val isLocked = i > completedCount
                val isCompleted = i < completedCount
                
                DuoLessonStep(
                    index = i,
                    isLocked = isLocked,
                    isCompleted = isCompleted,
                    color = color,
                    onClick = { onNavigateToLesson(categoryId, "lesson_$i") }
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DuoLessonStep(index: Int, isLocked: Boolean, isCompleted: Boolean, color: Color, onClick: () -> Unit) {
    val xOffset = when(index % 4) {
        0 -> 0.dp
        1 -> 45.dp
        2 -> 0.dp
        3 -> (-45.dp)
        else -> 0.dp
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val currentScale = if (!isLocked && !isCompleted) scale else 1f

    Box(
        modifier = Modifier
            .offset(x = xOffset)
            .graphicsLayer {
                scaleX = currentScale
                scaleY = currentScale
            }
            .size(75.dp)
            .clickable(enabled = !isLocked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        val baseColor = if (isLocked) DuoGrayLight else color
        val shadowColor = if (isLocked) DuoGray else color.copy(alpha = 0.7f)

        Box(modifier = Modifier.fillMaxSize().padding(top = 4.dp).background(shadowColor, CircleShape))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp)
                .background(baseColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isLocked) {
                Text("🔒", fontSize = 20.sp)
            } else if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            } else {
                Text("⭐", fontSize = 32.sp, color = Color.White)
            }
        }
    }
}
