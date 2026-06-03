package com.app.dsalingo.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.dsalingo.data.model.Question
import com.app.dsalingo.data.model.QuestionType
import com.app.dsalingo.data.network.AdminQuestionRequest
import com.app.dsalingo.ui.screens.challenges.StatBox
import com.app.dsalingo.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onSignOut: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    var selectedCategoryId by remember { mutableStateOf("array") }
    val context = LocalContext.current

    val totalUsers by viewModel.totalUsers.collectAsState()
    val totalQuestions by viewModel.totalQuestions.collectAsState()
    val totalChallenges by viewModel.totalChallenges.collectAsState()
    val questions by viewModel.questions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val actionSuccess by viewModel.actionSuccess.collectAsState()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var questionToEdit by remember { mutableStateOf<Question?>(null) }

    LaunchedEffect(selectedCategoryId) {
        viewModel.loadDashboardData(selectedCategoryId)
    }

    LaunchedEffect(error, actionSuccess) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearNotifications()
        }
        actionSuccess?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearNotifications()
            showAddEditDialog = false
            questionToEdit = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Console", fontWeight = FontWeight.ExtraBold, color = DuoGray) },
                navigationIcon = {
                    IconButton(onClick = onSignOut) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Log Out", tint = DuoRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    questionToEdit = null
                    showAddEditDialog = true
                },
                containerColor = DuoGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Question")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatBox(modifier = Modifier.weight(1f), label = "Total Users", value = totalUsers.toString(), color = BluePrimary)
                StatBox(modifier = Modifier.weight(1f), label = "DB Questions", value = totalQuestions.toString(), color = Color(0xFFEAB308))
                StatBox(modifier = Modifier.weight(1f), label = "Challenges", value = totalChallenges.toString(), color = GreenPrimary)
            }

            Spacer(modifier = Modifier.height(8.dp))



            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Category Select
                listOf("basics" to "Basics", "array" to "Arrays", "string" to "Strings", "stack" to "Stacks").forEach { (id, title) ->
                    FilterChip(
                        selected = selectedCategoryId == id,
                        onClick = { selectedCategoryId = id },
                        label = { Text(title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Active Questions in database (${questions.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DuoGray)
            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DuoGreen)
                }
            } else if (questions.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("No questions registered in this section yet.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(questions) { question ->
                        AdminQuestionItem(
                            question = question,
                            onEditClick = {
                                questionToEdit = question
                                showAddEditDialog = true
                            },
                            onDeleteClick = {
                                viewModel.deleteQuestion(question.id, selectedCategoryId)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddEditDialog) {
        AddEditQuestionDialog(
            question = questionToEdit,
            defaultCategoryId = selectedCategoryId,
            onDismiss = {
                showAddEditDialog = false
                questionToEdit = null
            },
            onSubmit = { request ->
                if (questionToEdit == null) {
                    viewModel.addQuestion(request, selectedCategoryId)
                } else {
                    viewModel.editQuestion(request, selectedCategoryId)
                }
            }
        )
    }
}

@Composable
fun AdminQuestionItem(question: Question, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = DuoBlue.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = question.id.uppercase(),
                            color = DuoBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = Color(0xFFFF9600).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = question.type.name,
                            color = Color(0xFFFF9600),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(question.question, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(question.explanation, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DuoBlue)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DuoRed)
                }
            }
        }
    }
}

@Composable
fun AddEditQuestionDialog(
    question: Question?,
    defaultCategoryId: String,
    onDismiss: () -> Unit,
    onSubmit: (AdminQuestionRequest) -> Unit
) {
    var id by remember { mutableStateOf(question?.id ?: "") }
    var categoryId by remember { mutableStateOf(defaultCategoryId) }
    var type by remember { mutableStateOf(question?.type?.name ?: "MULTIPLE_CHOICE") }
    var questionText by remember { mutableStateOf(question?.question ?: "") }
    var explanation by remember { mutableStateOf(question?.explanation ?: "") }
    var codeSnippet by remember { mutableStateOf(question?.code ?: "") }
    
    // Correct Answer holds indices or text answers
    var correctAnswerInput by remember { mutableStateOf(question?.correctAnswer?.toString() ?: "") }
    
    // Complex fields formats (split options by comma for simplicity)
    var optionsInput by remember {
        mutableStateOf(
            question?.options?.joinToString(", ") ?: ""
        )
    }

    // New fields for array interaction & drag-drop
    var itemsInput by remember {
        mutableStateOf(
            question?.items?.joinToString(", ") ?: ""
        )
    }
    var correctOrderInput by remember {
        mutableStateOf(
            question?.correctOrder?.joinToString(", ") ?: ""
        )
    }
    var arrayDataInput by remember {
        mutableStateOf(
            question?.arrayData?.joinToString(", ") ?: ""
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (question == null) "Add Interactive Exercise" else "Edit Exercise",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text("Exercise ID (Unique)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = question == null
                    )
                }
                item {
                    OutlinedTextField(
                        value = categoryId,
                        onValueChange = { categoryId = it },
                        label = { Text("Category (basics, array, string, stack)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = type,
                        onValueChange = { type = it.uppercase() },
                        label = { Text("Question Type (THEORY, MULTIPLE_CHOICE, FILL_BLANK, ARRAY_INTERACTION)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        label = { Text("Question Instruction") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
                item {
                    OutlinedTextField(
                        value = explanation,
                        onValueChange = { explanation = it },
                        label = { Text("Correct Answer Explanation") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
                item {
                    OutlinedTextField(
                        value = codeSnippet,
                        onValueChange = { codeSnippet = it },
                        label = { Text("Code Snippet (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
                item {
                    OutlinedTextField(
                        value = correctAnswerInput,
                        onValueChange = { correctAnswerInput = it },
                        label = { Text("Correct Answer (index, e.g. 2, or list, e.g. [A, B])") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = optionsInput,
                        onValueChange = { optionsInput = it },
                        label = { Text("Options (For MULTIPLE_CHOICE, e.g. a, b, c, d)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = itemsInput,
                        onValueChange = { itemsInput = it },
                        label = { Text("Items / Element Bank (e.g. Zendaya, Robert)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = arrayDataInput,
                        onValueChange = { arrayDataInput = it },
                        label = { Text("Initial Array Layout (e.g. (empty slot), Tom)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    OutlinedTextField(
                        value = correctOrderInput,
                        onValueChange = { correctOrderInput = it },
                        label = { Text("Correct Order Indices (e.g. 0, 1, 2)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (id.isBlank() || categoryId.isBlank() || questionText.isBlank() || explanation.isBlank()) {
                        return@Button
                    }
                    
                    // Parse options
                    val parsedOptions = if (optionsInput.isNotBlank()) {
                        optionsInput.split(",").map { it.trim() }
                    } else null

                    // Parse correct answer as integer if applicable, else string or list
                    val parsedCorrect: Any = when {
                        correctAnswerInput.toIntOrNull() != null -> correctAnswerInput.toInt()
                        correctAnswerInput.startsWith("[") && correctAnswerInput.endsWith("]") -> {
                            // Parse simple bracket arrays of strings
                            correctAnswerInput.replace("[", "").replace("]", "").split(",").map { 
                                it.replace("\"", "").replace("'", "").trim() 
                            }
                        }
                        else -> correctAnswerInput
                    }

                    // Parse items
                    val parsedItems = if (itemsInput.isNotBlank()) {
                        itemsInput.split(",").map { it.trim() }
                    } else null

                    // Parse arrayData
                    val parsedArrayData = if (arrayDataInput.isNotBlank()) {
                        arrayDataInput.split(",").map { it.trim() }
                    } else null

                    // Parse correctOrder
                    val parsedCorrectOrder = if (correctOrderInput.isNotBlank()) {
                        correctOrderInput.split(",").mapNotNull { it.trim().toIntOrNull() }
                    } else null

                    val request = AdminQuestionRequest(
                        id = id,
                        categoryId = categoryId,
                        type = type,
                        question = questionText,
                        options = parsedOptions,
                        correctAnswer = parsedCorrect,
                        explanation = explanation,
                        code = if (codeSnippet.isNotBlank()) codeSnippet else null,
                        items = parsedItems,
                        arrayData = parsedArrayData,
                        correctOrder = parsedCorrectOrder
                    )
                    
                    onSubmit(request)
                },
                colors = ButtonDefaults.buttonColors(containerColor = DuoGreen)
            ) {
                Text("SAVE")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = DuoRed)
            }
        }
    )
}
