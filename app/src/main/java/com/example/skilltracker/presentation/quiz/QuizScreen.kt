package com.example.skilltracker.presentation.quiz

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skilltracker.presentation.theme.ErrorRed
import com.example.skilltracker.presentation.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Time! 🧠", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, "Close")
                    }
                },
                actions = {
                    if (!state.isFinished && state.questions.isNotEmpty()) {
                        Text(
                            "${state.currentIndex + 1}/${state.questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Generating quiz with AI...")
                    }
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(state.error ?: "Error", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = viewModel::generateQuiz) {
                            Text("Retry")
                        }
                    }
                }
                state.isFinished -> {
                    // Score Screen
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            if (state.score >= 4) "🎉" else if (state.score >= 3) "👍" else "📚",
                            fontSize = 64.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "You got ${state.score}/${state.questions.size} correct!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            when {
                                state.score == state.questions.size -> "Perfect score! You're crushing it!"
                                state.score >= 4 -> "Great job! Almost perfect!"
                                state.score >= 3 -> "Good work! Keep reviewing."
                                else -> "Keep studying! You'll get there."
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(onClick = viewModel::generateQuiz) {
                                Icon(Icons.Default.Refresh, "Retry")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Try Again")
                            }
                            Button(onClick = onNavigateBack) {
                                Text("Done")
                            }
                        }
                    }
                }
                state.questions.isNotEmpty() -> {
                    val question = state.questions[state.currentIndex]

                    Column {
                        // Progress bar
                        LinearProgressIndicator(
                            progress = (state.currentIndex + 1).toFloat() / state.questions.size,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Question
                        Text(
                            question.question,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Options
                        question.options.forEachIndexed { index, option ->
                            val letter = ('A' + index).toString()
                            val isSelected = state.selectedAnswer == letter
                            val isCorrectAnswer = letter == question.answer

                            val containerColor = when {
                                !state.showResult -> {
                                    if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                }
                                isCorrectAnswer -> SuccessGreen.copy(alpha = 0.2f)
                                isSelected && !state.isCorrect -> ErrorRed.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }

                            val borderColor = when {
                                !state.showResult && isSelected -> MaterialTheme.colorScheme.primary
                                state.showResult && isCorrectAnswer -> SuccessGreen
                                state.showResult && isSelected && !state.isCorrect -> ErrorRed
                                else -> Color.Transparent
                            }

                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = {
                                    if (!state.showResult) viewModel.onAnswerSelected(letter)
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = containerColor
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(32.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        color = borderColor.copy(alpha = 0.3f)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                letter,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        option,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (state.showResult && isCorrectAnswer) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SuccessGreen
                                        )
                                    }
                                    if (state.showResult && isSelected && !state.isCorrect) {
                                        Icon(
                                            Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = ErrorRed
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Next Button
                        AnimatedVisibility(visible = state.showResult) {
                            Button(
                                onClick = viewModel::onNextQuestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(
                                    if (state.currentIndex < state.questions.size - 1) "Next Question"
                                    else "See Results",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
