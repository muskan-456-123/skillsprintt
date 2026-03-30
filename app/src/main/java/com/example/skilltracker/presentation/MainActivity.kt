package com.example.skilltracker.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skilltracker.presentation.chatbot.ChatbotScreen
import com.example.skilltracker.presentation.course.CoursePickerScreen
import com.example.skilltracker.presentation.fitness.FitnessScreen
import com.example.skilltracker.presentation.login.LoginScreen
import com.example.skilltracker.presentation.navigation.Screen
import com.example.skilltracker.presentation.plan.PlanSetupScreen
import com.example.skilltracker.presentation.progress.ProgressScreen
import com.example.skilltracker.presentation.quiz.QuizScreen
import com.example.skilltracker.presentation.session.DailySessionScreen
import com.example.skilltracker.presentation.settings.SettingsScreen
import com.example.skilltracker.presentation.skill.SkillSelectionScreen
import com.example.skilltracker.presentation.theme.SkillTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillTrackerTheme {
                SkillTrackerApp()
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun SkillTrackerApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem("today", "Today", Icons.Filled.Today, Icons.Outlined.Today),
        BottomNavItem("progress", "Progress", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
        BottomNavItem("chatbot", "AI Tutor", Icons.Filled.SmartToy, Icons.Outlined.SmartToy),
        BottomNavItem("settings", "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300)
                )
            }
        ) {
            // Login
            composable(Screen.Login.route) {
                LoginScreen(
                    onSkipLogin = {
                        navController.navigate(Screen.SkillSelection.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // Skill Selection
            composable(Screen.SkillSelection.route) {
                SkillSelectionScreen(
                    onNavigateToCoursePicker = { skillName ->
                        navController.navigate(Screen.CoursePicker.createRoute(skillName))
                    }
                )
            }

            // Course Picker
            composable(
                Screen.CoursePicker.route,
                arguments = listOf(navArgument("skillName") { type = NavType.StringType })
            ) {
                CoursePickerScreen(
                    onNavigateToPlanSetup = { courseId ->
                        navController.navigate(Screen.PlanSetup.createRoute(courseId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Plan Setup
            composable(
                Screen.PlanSetup.route,
                arguments = listOf(navArgument("courseId") { type = NavType.StringType })
            ) {
                PlanSetupScreen(
                    onNavigateToDashboard = {
                        navController.navigate("today") {
                            popUpTo(Screen.SkillSelection.route) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Dashboard - Today Tab
            composable("today") {
                DailySessionScreen(
                    onNavigateToQuiz = { sessionId, courseId ->
                        navController.navigate(Screen.Quiz.createRoute(sessionId, courseId))
                    },
                    onNavigateToFitness = {
                        navController.navigate(Screen.Fitness.route)
                    }
                )
            }

            // Progress Tab
            composable("progress") {
                ProgressScreen()
            }

            // Chatbot Tab
            composable("chatbot") {
                ChatbotScreen()
            }

            // Settings Tab
            composable("settings") {
                SettingsScreen(
                    onNavigateToSkillSelection = {
                        navController.navigate(Screen.SkillSelection.route)
                    }
                )
            }

            // Quiz (floating)
            composable(
                Screen.Quiz.route,
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.StringType },
                    navArgument("courseId") { type = NavType.StringType }
                )
            ) {
                QuizScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Fitness (floating)
            composable(Screen.Fitness.route) {
                FitnessScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
