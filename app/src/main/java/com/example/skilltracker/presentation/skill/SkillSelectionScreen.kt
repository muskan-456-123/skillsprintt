package com.example.skilltracker.presentation.skill

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.skilltracker.domain.model.Skill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillSelectionScreen(
    onNavigateToCoursePicker: (String) -> Unit,
    viewModel: SkillSelectionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateToCoursePicker) {
        state.navigateToCoursePicker?.let {
            onNavigateToCoursePicker(it)
            viewModel.onNavigationHandled()
        }
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "What do you want",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            "to learn today?",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Search Field
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                placeholder = { Text("Search or type a custom skill...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        Row {
                            IconButton(onClick = {
                                viewModel.onCustomSkillSubmit(state.searchQuery)
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Add custom skill")
                            }
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (state.searchQuery.isNotBlank() && state.skills.isEmpty()) {
                            viewModel.onCustomSkillSubmit(state.searchQuery)
                        }
                    }
                ),
                singleLine = true
            )

            // Skill Grid
            if (state.skills.isEmpty() && state.searchQuery.isNotBlank()) {
                // Empty state - offer to add custom skill
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "No matching skill found",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        "Tap + to add \"${state.searchQuery}\" as a custom skill",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.skills, key = { it.id }) { skill ->
                        SkillCard(
                            skill = skill,
                            onClick = { viewModel.onSkillSelected(skill) }
                        )
                    }
                }
            }
        }

        // Loading overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun SkillCard(
    skill: Skill,
    onClick: () -> Unit
) {
    val gradients = listOf(
        listOf(Color(0xFF6200EE), Color(0xFF9C27B0)),
        listOf(Color(0xFF0288D1), Color(0xFF26C6DA)),
        listOf(Color(0xFFE65100), Color(0xFFFFA726)),
        listOf(Color(0xFF2E7D32), Color(0xFF66BB6A)),
        listOf(Color(0xFFC62828), Color(0xFFEF5350)),
        listOf(Color(0xFF4527A0), Color(0xFF7E57C2)),
        listOf(Color(0xFF00695C), Color(0xFF4DB6AC)),
        listOf(Color(0xFF283593), Color(0xFF5C6BC0)),
        listOf(Color(0xFF6A1B9A), Color(0xFFAB47BC)),
        listOf(Color(0xFF1565C0), Color(0xFF42A5F5)),
        listOf(Color(0xFFAD1457), Color(0xFFEC407A)),
        listOf(Color(0xFF37474F), Color(0xFF78909C)),
    )

    val gradientIndex = skill.id.hashCode().let {
        (if (it < 0) -it else it) % gradients.size
    }
    val gradient = gradients[gradientIndex]

    val icon = when (skill.iconName) {
        "code" -> Icons.Default.Code
        "design" -> Icons.Default.Palette
        "analytics" -> Icons.Default.Analytics
        "web" -> Icons.Default.Language
        "psychology" -> Icons.Default.Psychology
        "phone_android" -> Icons.Default.PhoneAndroid
        "account_tree" -> Icons.Default.AccountTree
        "cloud" -> Icons.Default.Cloud
        "security" -> Icons.Default.Security
        "settings" -> Icons.Default.Build
        "flutter_dash" -> Icons.Default.PhoneAndroid
        "link" -> Icons.Default.Link
        else -> Icons.Default.School
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(gradient)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Column {
                    Text(
                        skill.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        skill.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
