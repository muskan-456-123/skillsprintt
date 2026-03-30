package com.example.skilltracker.presentation.fitness

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skilltracker.domain.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessScreen(
    onNavigateBack: () -> Unit
) {
    val exercises = remember {
        listOf(
            Exercise("Neck Rolls", "Slowly rotate your neck in circles, 5 times each direction", 60, "self_improvement"),
            Exercise("Wrist Stretches", "Extend your arms and flex wrists up and down, hold each for 10 seconds", 60, "pan_tool"),
            Exercise("Eye Exercise (20-20-20)", "Look at something 20 feet away for 20 seconds, then blink 20 times", 60, "visibility"),
            Exercise("Deep Breathing (4-7-8)", "Inhale 4 seconds, hold 7 seconds, exhale 8 seconds. Repeat 3 times", 60, "air"),
            Exercise("Desk Push-ups", "Place hands on desk edge, do 10 slow push-ups", 60, "fitness_center")
        )
    }

    var completedExercises by remember { mutableStateOf(setOf<Int>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Wellness Break", fontWeight = FontWeight.Bold)
                        Text(
                            "5-minute micro exercises",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Progress Header
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFF1B5E20).copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🧘", fontSize = 40.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "${completedExercises.size}/${exercises.size} done",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            LinearProgressIndicator(
                                progress = completedExercises.size.toFloat() / exercises.size,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .padding(top = 4.dp),
                                color = Color(0xFF4CAF50),
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }

            items(exercises.size) { index ->
                val exercise = exercises[index]
                val isCompleted = completedExercises.contains(index)

                ExerciseCard(
                    exercise = exercise,
                    isCompleted = isCompleted,
                    onToggle = {
                        completedExercises = if (isCompleted) {
                            completedExercises - index
                        } else {
                            completedExercises + index
                        }
                    }
                )
            }

            // Done button
            if (completedExercises.size == exercises.size) {
                item {
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("All done! Back to studying 📚", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseCard(
    exercise: Exercise,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    val icon = when (exercise.iconName) {
        "self_improvement" -> Icons.Default.SelfImprovement
        "pan_tool" -> Icons.Default.PanTool
        "visibility" -> Icons.Default.Visibility
        "air" -> Icons.Default.Air
        "fitness_center" -> Icons.Default.FitnessCenter
        else -> Icons.Default.FitnessCenter
    }

    ElevatedCard(
        onClick = onToggle,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (isCompleted)
                Color(0xFF4CAF50).copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(14.dp),
                color = if (isCompleted)
                    Color(0xFF4CAF50).copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                    } else {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    exercise.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    "${exercise.durationSeconds}s",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
