package com.app.dsalingo.ui.screens.lesson

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.dsalingo.data.model.Question
import com.app.dsalingo.data.model.QuestionType
import com.app.dsalingo.ui.theme.*
import com.app.dsalingo.ui.components.DuoButton
import com.app.dsalingo.ui.components.LottieAnimationView
import com.app.dsalingo.ui.components.LottieAnimationRawRes
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

    LaunchedEffect(categoryId) {
        viewModel.loadQuestions("python", categoryId)
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
                                    streakCount++
                                    if (currentQuestionIndex < questions.size - 1) {
                                        currentQuestionIndex++
                                    } else {
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
    var showResult by remember(question.id) { mutableStateOf(false) }
    var isCorrect by remember(question.id) { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Character + Speech Bubble
            Row(verticalAlignment = Alignment.CenterVertically) {
                val birdYOffset by animateDpAsState(
                    targetValue = if (showResult && isCorrect) (-150).dp else 0.dp,
                    animationSpec = if (showResult && isCorrect) tween(600, easing = FastOutSlowInEasing) else snap(),
                    label = "birdFlight"
                )
                val birdAlpha by animateFloatAsState(
                    targetValue = if (showResult && isCorrect) 0f else 1f,
                    animationSpec = if (showResult && isCorrect) tween(600) else snap(),
                    label = "birdAlpha"
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .offset(y = birdYOffset)
                        .graphicsLayer { alpha = birdAlpha },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        showResult && !isCorrect && hearts <= 1 -> {
                            LottieAnimationView(url = "https://lottie.host/80447384-5a67-466d-966a-12798e3b3303/4O2oN5kF0T.json")
                        }
                        showResult && !isCorrect -> {
                            LottieAnimationRawRes(resId = R.raw.angry_bird)
                        }
                        streakCount >= 5 && !showResult -> {
                            LottieAnimationView(url = "https://lottie.host/3e6f9661-0d32-4777-a843-8f6a9e224e2c/6L8B8YI8aQ.json")
                        }
                        streakCount >= 3 && !showResult -> {
                            Text("🔥", fontSize = 60.sp)
                        }
                        else -> {
                            Text(if (showResult && isCorrect) "🥳" else "🦉", fontSize = 60.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, DuoGrayLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (streakCount >= 3) {
                            Text("🔥 $streakCount STREAK!", color = DuoOrange, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                        Text(
                            text = question.question,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            Box(modifier = Modifier.weight(1f)) {
                when (question.type) {
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
                                Surface(
                                    color = DuoGrayLight.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                ) {
                                    Text(
                                        text = question.code,
                                        modifier = Modifier.padding(12.dp),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 14.sp
                                    )
                                }
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
                                }
                            },
                            color = if (isCorrect) DuoGreen else DuoRed,
                            shadowColor = if (isCorrect) DuoGreenDark else DuoRedDark
                        )
                    }
                }

                if (!showResult) {
                    DuoButton(
                        text = "CHECK",
                        onClick = {
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
                                else -> false
                            }
                            if (isCorrect) onCorrectAnswer() else onWrongAnswer()
                            showResult = true
                        },
                        enabled = selectedOptionIndex != null || textInput.isNotBlank(),
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
