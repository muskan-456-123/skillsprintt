package com.example.skilltracker.domain.model

data class QuizQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),
    val answer: String = ""
)

data class QuizResult(
    val sessionId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 5,
    val questions: List<QuizQuestion> = emptyList()
)
