package com.app.dsalingo.data.repository

import android.content.Context
import com.app.dsalingo.data.model.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestionRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun getQuestionsForLesson(language: String, categoryId: String): List<Question> {
        return withContext(Dispatchers.IO) {
            try {
                // E.g., "questions/python/array_questions.json"
                val fileName = "questions/$language/${categoryId}_questions.json"
                val inputStream = context.assets.open(fileName)
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<Question>>() {}.type
                Gson().fromJson(reader, type)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }
}
