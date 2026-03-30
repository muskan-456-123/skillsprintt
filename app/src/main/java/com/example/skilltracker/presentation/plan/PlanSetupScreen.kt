package com.example.skilltracker.presentation.plan

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skilltracker.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanSetupScreen(
    onNavigateToDashboard: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: PlanSetupViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToDashboard) {
        if (state.navigateToDashboard) {
            onNavigateToDashboard()
            viewModel.onNavigationHandled()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Your Plan", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Course Info Card
            state.course?.let { course ->
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            course.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text(course.channelName) },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, null, Modifier.size(16.dp))
                                }
                            )
                            AssistChip(
                                onClick = {},
                                label = { Text(course.durationFormatted) },
                                leadingIcon = {
                                    Icon(Icons.Default.Schedule, null, Modifier.size(16.dp))
                                }
                            )
                        }
                    }
                }
            }

            // Days Picker
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.animateContentSize()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "How many days to complete?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${state.totalDays} days",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Slider(
                        value = state.totalDays.toFloat(),
                        onValueChange = { viewModel.onDaysChanged(it.toInt()) },
                        valueRange = 1f..60f,
                        steps = 58,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1 day", style = MaterialTheme.typography.bodySmall)
                        Text("60 days", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Daily Stats Preview
            state.course?.let { course ->
                val dailyMinutes = if (state.totalDays > 0) course.durationMinutes / state.totalDays else 0
                val sessionsPerDay = (dailyMinutes + 59) / 60 // Ceiling division

                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            "Your Daily Plan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                value = DateUtils.formatMinutes(dailyMinutes),
                                label = "Daily Study"
                            )
                            StatItem(
                                value = "$sessionsPerDay",
                                label = "Sessions/Day"
                            )
                            StatItem(
                                value = "${state.totalDays}",
                                label = "Total Days"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Generate Plan Button
            Button(
                onClick = viewModel::onGeneratePlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !state.isGenerating
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating Plan...")
                } else {
                    Icon(Icons.Default.AutoAwesome, "Generate")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate Learning Plan", style = MaterialTheme.typography.titleMedium)
                }
            }

            state.error?.let { error ->
                Text(
                    error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}
