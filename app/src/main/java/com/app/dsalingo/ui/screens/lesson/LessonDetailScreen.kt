package com.app.dsalingo.ui.screens.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.dsalingo.data.model.Question
import com.app.dsalingo.data.model.QuestionType
import com.app.dsalingo.ui.theme.*
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.ui.components.LottieAnimationView
import com.app.dsalingo.ui.components.LottieAnimationRawRes
import com.app.dsalingo.ui.components.LottieAnimationAsset
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.dsalingo.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    categoryId: String,
    lessonId: String,
    onNavigateBack: () -> Unit,
    onLessonComplete: () -> Unit,
    viewModel: LessonViewModel = hiltViewModel()
) {
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var hearts by remember { mutableIntStateOf(5) }
    var streakCount by remember { mutableIntStateOf(0) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var isLessonFinished by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }

    val questions by viewModel.questions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(categoryId, lessonId) {
        viewModel.loadQuestions(categoryId, lessonId)
    }

    val currentQuestion = questions.getOrNull(currentQuestionIndex)
    val progress = if (questions.isNotEmpty()) (currentQuestionIndex).toFloat() / questions.size.toFloat() else 0f

    BackHandler { showLeaveDialog = true }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text("Wait, don't go!", fontWeight = FontWeight.Bold) },
            text = { Text("You'll lose your streak if you quit now.") },
            confirmButton = {
                Button(onClick = { showLeaveDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)) {
                    Text("KEEP LEARNING")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false; onNavigateBack() }) {
                    Text("END SESSION", color = DuoRed)
                }
            }
        )
    }

    if (isGameOver) {
        GameOverScreen(onQuit = onNavigateBack)
    } else if (isLessonFinished) {
        LessonCompleteScreen(xpReward = 50, onContinue = onLessonComplete)
    } else {
        Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DuoGreen)
                }
            } else if (currentQuestion != null) {
                Scaffold(
                    topBar = {
                        LessonTopBar(progress = progress, hearts = hearts, onCloseClick = { showLeaveDialog = true })
                    }
                ) { padding ->
                    Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                        AnimatedContent(
                            targetState = currentQuestionIndex,
                            transitionSpec = {
                                if (targetState > initialState) {
                                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> -width } + fadeOut())
                                } else {
                                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                        slideOutHorizontally { width -> width } + fadeOut())
                                }.using(SizeTransform(clip = false))
                            },
                            label = "questionTransition"
                        ) { index ->
                            val question = questions[index]
                            QuestionBody(
                                question = question,
                                hearts = hearts,
                                streakCount = streakCount,
                                onCorrectAnswer = {
                                    viewModel.completeQuestion(question.id)
                                    if (question.type != QuestionType.THEORY) {
                                        streakCount++
                                    }
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                    } else {
                                        viewModel.addXp(50)
                                        isLessonFinished = true
                                    }
                                },
                                onWrongAnswer = {
                                    streakCount = 0
                                    if (hearts > 1) {
                                        hearts--
                                    } else {
                                        hearts = 0
                                        isGameOver = true
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No questions found for this lesson.", color = DuoGray)
                }
            }
        }
    }
}

@Composable
fun LessonTopBar(progress: Float, hearts: Int, onCloseClick: () -> Unit) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "progressBar"
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onCloseClick) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = DuoGray)
        }
        
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.weight(1f).height(16.dp).clip(RoundedCornerShape(8.dp)),
            color = DuoGreen,
            trackColor = DuoGrayLight
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("❤️", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(4.dp))
            AnimatedContent(
                targetState = hearts,
                transitionSpec = {
                    (scaleIn() + fadeIn()).togetherWith(scaleOut() + fadeOut())
                },
                label = "heartsAnim"
            ) { targetHearts ->
                Text(targetHearts.toString(), fontWeight = FontWeight.ExtraBold, color = DuoRed, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun QuestionBody(
    question: Question,
    hearts: Int,
    streakCount: Int,
    onCorrectAnswer: () -> Unit,
    onWrongAnswer: () -> Unit
) {
    var selectedOptionIndex by remember(question.id) { mutableStateOf<Int?>(null) }
    var textInput by remember(question.id) { mutableStateOf("") }
    
    // Array Interaction State - using immutable lists for better Compose state tracking
    var currentArrayItems by remember(question.id) { 
        mutableStateOf(question.arrayData ?: emptyList()) 
    }
    var availableItems by remember(question.id) { 
        mutableStateOf(question.items ?: emptyList()) 
    }

    var showResult by remember(question.id) { mutableStateOf(false) }
    var isCorrect by remember(question.id) { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Character + Speech Bubble
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier.size(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        streakCount >= 3 && !showResult -> {
                            LottieAnimationRawRes(resId = R.raw.attack)
                        }
                        showResult && !isCorrect && hearts <= 1 -> {
                            // High stakes/game over soon
                            LottieAnimationRawRes(resId = R.raw.crying)
                        }
                        showResult && !isCorrect -> {
                            LottieAnimationRawRes(resId = R.raw.angry_bird)
                        }
                        showResult && isCorrect -> {
                            LottieAnimationAsset(assetPath = "questions/python/flying.json")
                        }
                        else -> {
                            LottieAnimationRawRes(resId = R.raw.angry_bird)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Speech Bubble Triangle
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .graphicsLayer { rotationZ = 45f }
                        .background(Color.White)
                        .border(2.dp, DuoGrayLight, RoundedCornerShape(2.dp))
                        .offset(x = 6.dp)
                )

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (question.type == QuestionType.THEORY) {
                            Surface(
                                color = DuoBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(bottom = 4.dp)
                            ) {
                                Text(
                                    "NEW CONCEPT",
                                    color = DuoBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else if (streakCount >= 3 && !showResult) {
                            Text("🔥 $streakCount STREAK!", color = DuoOrange, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                        Text(
                            text = question.question,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4B4B4B)
                        )
                    }
                }
            }
            
            if (!question.imageUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight),
                    color = DuoGrayLight.copy(alpha = 0.05f)
                ) {
                    AsyncImage(
                        model = question.imageUrl,
                        contentDescription = "Question Image",
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (question.type) {
                    QuestionType.THEORY -> {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, DuoBlue.copy(alpha = 0.3f)),
                                color = DuoBlue.copy(alpha = 0.05f)
                            ) {
                                Text(
                                    text = question.explanation,
                                    modifier = Modifier.padding(20.dp),
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    color = Color(0xFF4B4B4B)
                                )
                            }
                            if (question.code != null) {
                                CodeSnippet(question.code)
                            }
                        }
                    }
                    QuestionType.MULTIPLE_CHOICE -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            question.options?.forEachIndexed { index, optionText ->
                                val isSelected = selectedOptionIndex == index
                                val borderColor = if (isSelected) DuoBlue else DuoGrayLight
                                val bgColor = if (isSelected) DuoBlue.copy(alpha = 0.1f) else Color.White
                                
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = !showResult) { selectedOptionIndex = index },
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
                                    color = bgColor
                                ) {
                                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .border(2.dp, DuoGrayLight, RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text((index + 1).toString(), fontWeight = FontWeight.Bold, color = DuoGray)
                                        }
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(text = optionText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    QuestionType.FILL_BLANK, QuestionType.CODE_COMPLETION -> {
                        Column {
                            if (question.code != null) {
                                CodeSnippet(question.code)
                            }
                            OutlinedTextField(
                                value = textInput,
                                onValueChange = { if (!showResult) textInput = it },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                placeholder = { Text("Type your answer...") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DuoBlue,
                                    unfocusedBorderColor = DuoGrayLight
                                )
                            )
                        }
                    }
                    QuestionType.ARRAY_INTERACTION -> {
                        ArrayInteractionBody(
                            currentItems = currentArrayItems,
                            availableBank = availableItems,
                            showResult = showResult,
                            onItemClick = { item, fromBank ->
                                if (!showResult) {
                                    if (fromBank) {
                                        val emptyIndex = currentArrayItems.indexOf("(empty slot)")
                                        if (emptyIndex != -1) {
                                            val newList = currentArrayItems.toMutableList()
                                            newList[emptyIndex] = item
                                            currentArrayItems = newList
                                        } else {
                                            currentArrayItems = currentArrayItems + item
                                        }
                                        availableItems = availableItems - item
                                    } else {
                                        currentArrayItems = currentArrayItems - item
                                        availableItems = availableItems + item
                                    }
                                }
                            }
                        )
                    }
                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Result Card (Duolingo Style Bottom Sheet)
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                AnimatedVisibility(
                    visible = showResult,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    val cardColor = if (isCorrect) Color(0xFFD7FFB8) else Color(0xFFFFDFE0)
                    val textColor = if (isCorrect) DuoGreenDark else DuoRedDark
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(cardColor, RoundedCornerShape(24.dp))
                            .padding(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (isCorrect) "✅ Excellent!" else "❌ Not quite right", 
                                color = textColor,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (isCorrect && streakCount >= 3) {
                            Text("Streak: $streakCount! Keep it up!", color = DuoOrange, fontWeight = FontWeight.Bold)
                        }
                        Text(text = question.explanation, color = textColor)
                        Spacer(modifier = Modifier.height(16.dp))
                        DuoButton(
                            text = "CONTINUE",
                            onClick = {
                                if (isCorrect) {
                                    showResult = false
                                    onCorrectAnswer()
                                } else {
                                    showResult = false
                                    selectedOptionIndex = null
                                    textInput = ""
                                    // Reset array interaction if wrong
                                    currentArrayItems = question.arrayData ?: emptyList()
                                    availableItems = question.items ?: emptyList()
                                }
                            },
                            color = if (isCorrect) DuoGreen else DuoRed,
                            shadowColor = if (isCorrect) DuoGreenDark else DuoRedDark
                        )
                    }
                }

                if (!showResult) {
                    val isTheory = question.type == QuestionType.THEORY
                    DuoButton(
                        text = if (isTheory) "CONTINUE" else "CHECK",
                        onClick = {
                            if (isTheory) {
                                isCorrect = true
                                onCorrectAnswer()
                                // For theory, we don't show the result card, just move next
                                // Actually, Duolingo usually shows a "Continue" button and maybe a small tip
                                // Let's just move to next for simplicity or show a success state
                            } else {
                                isCorrect = when (question.type) {
                                    QuestionType.MULTIPLE_CHOICE -> {
                                        val correctVal = when (val res = question.correctAnswer) {
                                            is Number -> res.toInt()
                                            is String -> res.toDoubleOrNull()?.toInt() ?: -1
                                            else -> -1
                                        }
                                        selectedOptionIndex == correctVal
                                    }
                                    QuestionType.FILL_BLANK, QuestionType.CODE_COMPLETION -> {
                                        val correctStr = when (val res = question.correctAnswer) {
                                            is Double -> if (res % 1.0 == 0.0) res.toInt().toString() else res.toString()
                                            else -> res.toString()
                                        }
                                        textInput.trim().equals(correctStr, ignoreCase = true)
                                    }
                                    QuestionType.ARRAY_INTERACTION -> {
                                        val correctList = question.correctAnswer as? List<*>
                                        if (correctList != null) {
                                            if (correctList.all { it is Number }) {
                                                val expectedOrder = correctList.map { (it as Number).toInt() }
                                                val originalItems = question.items ?: emptyList()
                                                val expectedItems = expectedOrder.map { originalItems[it] }
                                                currentArrayItems == expectedItems
                                            } else {
                                                currentArrayItems == correctList.map { it.toString() }
                                            }
                                        } else false
                                    }
                                    else -> false
                                }
                                if (isCorrect) onCorrectAnswer() else onWrongAnswer()
                                showResult = true
                            }
                        },
                        enabled = when(question.type) {
                            QuestionType.THEORY -> true
                            QuestionType.MULTIPLE_CHOICE -> selectedOptionIndex != null
                            QuestionType.FILL_BLANK, QuestionType.CODE_COMPLETION -> textInput.isNotBlank()
                            QuestionType.ARRAY_INTERACTION -> currentArrayItems.isNotEmpty() && !currentArrayItems.contains("(empty slot)")
                            else -> false
                        },
                        color = DuoGreen,
                        shadowColor = DuoGreenDark
                    )
                }
            }
        }

        // Celebration Confetti
        if (showResult && isCorrect) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                LottieAnimationView(url = "https://assets10.lottiefiles.com/packages/lf20_rovf9gzu.json") // Confetti
            }
        }
    }
}

@Composable
fun CodeSnippet(code: String) {
    Surface(
        color = DuoGrayLight.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
    ) {
        Text(
            text = code,
            modifier = Modifier.padding(16.dp),
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun getImageSource(name: String): Any? {
    if (name.contains("|")) {
        val url = name.substringAfter("|").trim()
        if (url.isNotEmpty()) return url
    }
    val cleanName = name.substringBefore("|").trim()
    return when (cleanName) {
        "Zendaya" -> "https://m.media-amazon.com/images/M/MV5BMjAxZTk4YmYtYjUzMi00OTI0LThjN2EtMDljYTdmM2U2NGY2XkEyXkFqcGdeQXVyMjQwMDg0Ng@@._V1_.jpg"
        "Tom Cruise", "Tom" -> R.drawable.tom
        "The Rock" -> "https://m.media-amazon.com/images/M/MV5BMTkyNDQ3NzAxM15BMl5BanBnXkFtZTgwODIwMTQ0OEE@._V1_.jpg"
        "Spider-Man" -> "https://m.media-amazon.com/images/M/MV5BMjMwNDkxMTgzOF5BMl5BanBnXkFtZTgwNTkwNTQ3NjM@._V1_.jpg"
        "Iron Man" -> "https://m.media-amazon.com/images/M/MV5BMTczNTI2ODUwOF5BMl5BanBnXkFtZTcwMTU0NTIzMw@@._V1_.jpg"
        "Thor" -> "https://m.media-amazon.com/images/M/MV5BMTY3MTI5NjQ4Nl5BMl5BanBnXkFtZTcwOTU1OTU0OQ@@._V1_.jpg"
        "Avengers" -> "https://m.media-amazon.com/images/M/MV5BNDYxNjQyMjAtNjQxNy00ZGQ5LWFkOTAtZGQ5YzY2ZC00M2RlXkEyXkFqcGdeQXVyNjk1Njg0MzI@._V1_.jpg"
        "Batman" -> "https://m.media-amazon.com/images/M/MV5BMTYwNjAyODIyMF5BMl5BanBnXkFtZTYwNDMwMDk2._V1_.jpg"
        "Cars" -> "https://m.media-amazon.com/images/M/MV5BMTYxNjY5ZmYtNjcyOS00N2RmLWE3MzktYWU2OTliZTM4ZDExXkEyXkFqcGdeQXVyNjk1Njg0MzI@._V1_.jpg"
        "Robert" -> "https://m.media-amazon.com/images/M/MV5BNTk2OGU4NzktODA5Ni00MDYyLWIyYWUtOWI2NDI1Y2ZkY2M3XkEyXkFqcGdeQXVyMjQwMDg0Ng@@._V1_.jpg"
        "Cillian" -> "https://m.media-amazon.com/images/M/MV5BMjA5Njk3MjM4OV5BMl5BanBnXkFtZTcwMTc5MTE1Nw@@._V1_.jpg"
        else -> if (cleanName.startsWith("http://") || cleanName.startsWith("https://")) cleanName else null
    }
}

@Composable
fun ArrayInteractionBody(
    currentItems: List<String>,
    availableBank: List<String>,
    showResult: Boolean,
    onItemClick: (String, Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("🎬", fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text("FILM STRIP (ARRAY)", fontWeight = FontWeight.ExtraBold, color = DuoGray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(3.dp, DuoGrayLight),
            color = Color(0xFF1A1A1A) // Dark cinema background
        ) {
            LazyRow(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                itemsIndexed(currentItems) { index, item ->
                    val isEmpty = item == "(empty slot)"
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            modifier = Modifier
                                .size(90.dp, 120.dp) // Poster aspect ratio
                                .clickable(enabled = !isEmpty && !showResult) { onItemClick(item, false) },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isEmpty) Color.White.copy(alpha = 0.1f) else Color.White,
                            border = if (isEmpty) {
                                androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(alpha = 0.2f))
                            } else {
                                androidx.compose.foundation.BorderStroke(2.dp, DuoBlue)
                            }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (isEmpty) {
                                    Text("?", color = Color.White.copy(alpha = 0.3f), fontSize = 32.sp)
                                } else {
                                    val imageSource = getImageSource(item)
                                    if (imageSource != null) {
                                        AsyncImage(
                                            model = imageSource,
                                            contentDescription = item.substringBefore('|').trim(),
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                        // Overlay name
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .align(Alignment.BottomCenter)
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = item.substringBefore('|').trim(),
                                                color = Color.White,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    } else {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier.padding(8.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .background(DuoGrayLight, CircleShape),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val cleanName = item.substringBefore('|').trim()
                                                Text(if (cleanName.length > 1) cleanName.take(1) else "🎬")
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                text = item.substringBefore('|').trim(),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 12.sp,
                                                maxLines = 2
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Pos: $index",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        Text("CASTING COUCH (ELEMENT BANK)", fontWeight = FontWeight.ExtraBold, color = DuoGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        @OptIn(ExperimentalLayoutApi::class)
        androidx.compose.foundation.layout.FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            availableBank.forEach { item ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clickable(enabled = !showResult) { onItemClick(item, true) },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val imageSource = getImageSource(item)
                        if (imageSource != null) {
                            AsyncImage(
                                model = imageSource,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp).clip(CircleShape),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Text("👤", fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item.substringBefore('|').trim(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
