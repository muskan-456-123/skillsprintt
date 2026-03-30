package com.example.skilltracker.presentation.session

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skilltracker.domain.model.Session
import com.example.skilltracker.presentation.theme.StreakOrange
import com.example.skilltracker.presentation.theme.SuccessGreen
import com.example.skilltracker.util.DateUtils
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySessionScreen(
    onNavigateToQuiz: (String, String) -> Unit,
    onNavigateToFitness: () -> Unit,
    viewModel: DailySessionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (state.course == null) {
            // No course selected state
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.School,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No course selected yet",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Go pick a skill and course to get started!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Streak Card
                item {
                    StreakCard(
                        currentStreak = state.streak.currentStreak,
                        longestStreak = state.streak.longestStreak
                    )
                }

                // Day Header
                item {
                    Text(
                        "Day ${state.currentDay}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Wellness Break Card (shows every 2 sessions)
                if (state.showWellnessBreak) {
                    item {
                        WellnessBreakCard(onTap = onNavigateToFitness)
                    }
                }

                // Session Cards
                items(state.sessions, key = { it.id }) { session ->
                    SessionCard(
                        session = session,
                        courseTitle = state.course?.title ?: "",
                        videoId = state.course?.videoId ?: "",
                        onComplete = { viewModel.onCompleteSession(session) },
                        onOpenYouTube = {
                            val startSeconds = session.startMinute * 60
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/watch?v=${state.course?.videoId}&t=${startSeconds}s")
                            )
                            context.startActivity(intent)
                        },
                        onTakeQuiz = {
                            onNavigateToQuiz(session.id, state.course?.id ?: "")
                        }
                    )
                }
            }
        }

        // Confetti Animation
        if (state.showConfetti) {
            ConfettiAnimation(onComplete = viewModel::onConfettiDone)
        }

        // Feedback Bottom Sheet
        if (state.showFeedbackSheet) {
            ModalBottomSheet(onDismissRequest = viewModel::onDismissFeedback) {
                FeedbackSheet(onFeedback = viewModel::onFeedbackSubmitted)
            }
        }
    }
}

@Composable
private fun StreakCard(currentStreak: Int, longestStreak: Int) {
    val animatedStreak by animateIntAsState(
        targetValue = currentStreak,
        animationSpec = tween(1000),
        label = "streak_anim"
    )

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔥", fontSize = 40.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$animatedStreak day streak!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = StreakOrange
                )
                Text(
                    "Longest: $longestStreak days",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: Session,
    courseTitle: String,
    videoId: String,
    onComplete: () -> Unit,
    onOpenYouTube: () -> Unit,
    onTakeQuiz: () -> Unit
) {
    val statusColor = when {
        session.isCompleted -> SuccessGreen
        session.isSkipped -> Color.Gray
        else -> MaterialTheme.colorScheme.primary
    }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Session ${session.sessionNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        session.isCompleted -> SuccessGreen.copy(alpha = 0.1f)
                        else -> MaterialTheme.colorScheme.primaryContainer
                    }
                ) {
                    Text(
                        when {
                            session.isCompleted -> "✓ Done"
                            session.isSkipped -> "Skipped"
                            else -> "Pending"
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = when {
                            session.isCompleted -> SuccessGreen
                            else -> MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Watch ${session.durationMinutes} min — ${DateUtils.formatTimestamp(session.startMinute)} to ${DateUtils.formatTimestamp(session.endMinute)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Open in YouTube button
                OutlinedButton(
                    onClick = onOpenYouTube,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Open YouTube",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Watch", style = MaterialTheme.typography.labelMedium)
                }

                if (!session.isCompleted) {
                    Button(
                        onClick = onComplete,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Mark Done",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Done", style = MaterialTheme.typography.labelMedium)
                    }
                } else {
                    // Quiz button for completed sessions
                    FilledTonalButton(
                        onClick = onTakeQuiz,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Quiz,
                            contentDescription = "Take Quiz",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Quiz", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WellnessBreakCard(onTap: () -> Unit) {
    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = Color(0xFF1B5E20).copy(alpha = 0.1f)
        ),
        onClick = onTap
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🧘", fontSize = 32.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Time for a Wellness Break!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "Take 5 minutes to stretch and refresh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}

@Composable
private fun FeedbackSheet(onFeedback: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "How was today's session?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeedbackButton("😴", "Too Easy", "too_easy", onFeedback)
            FeedbackButton("👌", "Just Right", "just_right", onFeedback)
            FeedbackButton("🤯", "Too Hard", "too_hard", onFeedback)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FeedbackButton(
    emoji: String,
    label: String,
    feedback: String,
    onClick: (String) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledTonalButton(
            onClick = { onClick(feedback) },
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(emoji, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ConfettiAnimation(onComplete: () -> Unit) {
    val particles = remember {
        List(50) {
            ConfettiParticle(
                x = Random.nextFloat(),
                y = Random.nextFloat() * -1f,
                speed = 2f + Random.nextFloat() * 4f,
                angle = Random.nextFloat() * 360f,
                color = listOf(
                    Color(0xFF6200EE), Color(0xFF03DAC6), Color(0xFFFF6B35),
                    Color(0xFF4CAF50), Color(0xFFFFCA28), Color(0xFFEF5350)
                ).random(),
                size = 4f + Random.nextFloat() * 8f
            )
        }
    }

    val progress by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(2000),
        finishedListener = { onComplete() },
        label = "confetti"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            val x = particle.x * size.width +
                    cos(particle.angle * Math.PI.toFloat() / 180f) * progress * 200f
            val y = particle.y * size.height +
                    progress * size.height * particle.speed / 4f
            val alpha = (1f - progress).coerceIn(0f, 1f)
            drawCircle(
                color = particle.color.copy(alpha = alpha),
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

private data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val speed: Float,
    val angle: Float,
    val color: Color,
    val size: Float
)
