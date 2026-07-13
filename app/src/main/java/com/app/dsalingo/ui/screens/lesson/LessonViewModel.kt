package com.app.dsalingo.ui.screens.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.dsalingo.data.model.Question
import com.app.dsalingo.data.repository.QuestionRepository
import com.app.dsalingo.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor(
    private val repository: QuestionRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    fun loadQuestions(categoryId: String, lessonId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _isError.value = false
            try {
                val loadedQuestions = repository.getQuestionsForLesson(categoryId)
                if (loadedQuestions.isEmpty()) {
                    _isError.value = true
                } else {
                    val lessonIndex = lessonId.removePrefix("lesson_").toIntOrNull() ?: 0
                    val questionsPerLesson = 2
                    val slicedQuestions = loadedQuestions
                        .drop(lessonIndex * questionsPerLesson)
                        .take(questionsPerLesson)
                    
                    if (slicedQuestions.isEmpty()) {
                        _isError.value = true
                    } else {
                        _questions.value = slicedQuestions
                    }
                }
            } catch (e: Exception) {
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun completeQuestion(questionId: String) {
        viewModelScope.launch {
            userRepository.completeQuestion(questionId)
        }
    }

    fun addXp(xpGain: Int) {
        viewModelScope.launch {
            userRepository.updateStats(xpGain = xpGain)
        }
    }
}
