package com.app.dsalingo.ui.screens.learn

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.hilt.navigation.compose.hiltViewModel

// Helper to get dynamic lesson count for UI path
fun getLessonCountForCategory(categoryId: String): Int {
    return when(categoryId) {
        "basics" -> 5
        "array" -> 14
        "string" -> 8
        "linkedlist" -> 10
        "stack" -> 6
        else -> 5
    }
}

@Composable
fun LearnScreen(
    onNavigateToCategory: (String) -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DuoGreen)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                UnitHeader(
                    title = "Section 1: Data Structures",
                    description = "Master the building blocks of algorithms",
                    color = DuoGreen
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(categories) { category ->
                DuoCategoryNode(category, onNavigateToCategory)
                Spacer(modifier = Modifier.height(32.dp))
            }
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

    val progressTarget = if (category.totalQuestions > 0) {
        category.completedQuestions.toFloat() / category.totalQuestions.toFloat()
    } else 0f
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
    onNavigateToLesson: (String, String) -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
    }

    val category = categories.find { it.id == categoryId }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = DuoGreen)
        }
    } else if (category == null) {
        Box(modifier = Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            Text("Category not found.", color = DuoGray)
        }
    } else {
        val color = Color(category.color)
        val questionsPerLesson = 2
        val totalLessons = if (category.totalQuestions > 0) {
            (category.totalQuestions + questionsPerLesson - 1) / questionsPerLesson
        } else 1
        
        val completedCount = category.completedQuestions / questionsPerLesson

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(category.icon, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(category.title, fontWeight = FontWeight.ExtraBold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, contentDescription = null, tint = DuoGray) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).background(DuoGrayLight.copy(alpha = 0.2f)),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                item {
                    UnitHeader(
                        title = "Unit 1",
                        description = "Learn the fundamentals of ${category.title}",
                        color = color
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

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
}

@Composable
fun UnitHeader(title: String, description: String, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = title.uppercase(),
                color = Color.White.copy(alpha = 0.8f),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
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
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val currentScale = if (!isLocked && !isCompleted) scale else 1f

    Column(
        modifier = Modifier.offset(x = xOffset),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = currentScale
                    scaleY = currentScale
                }
                .size(85.dp)
                .clickable(enabled = !isLocked) { onClick() },
            contentAlignment = Alignment.Center
        ) {
            val baseColor = if (isLocked) DuoGrayLight else color
            val shadowColor = if (isLocked) DuoGray else color.copy(alpha = 0.7f)

            // 3D Shadow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
                    .background(shadowColor, CircleShape)
            )
            
            // Main Circle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp)
                    .background(baseColor, CircleShape)
                    .border(if (!isLocked && !isCompleted) 4.dp else 0.dp, DuoYellow.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isLocked) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Transparent) // placeholder
                    Text("🔒", fontSize = 24.sp)
                } else if (isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                } else {
                    Text("⭐", fontSize = 38.sp, color = Color.White)
                }
            }
        }
    }
}
