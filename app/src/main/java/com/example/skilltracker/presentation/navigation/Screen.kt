package com.example.skilltracker.presentation.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object SkillSelection : Screen("skill_selection")
    data object CoursePicker : Screen("course_picker/{skillName}") {
        fun createRoute(skillName: String) = "course_picker/$skillName"
    }
    data object PlanSetup : Screen("plan_setup/{courseId}") {
        fun createRoute(courseId: String) = "plan_setup/$courseId"
    }
    data object Dashboard : Screen("dashboard")
    data object Quiz : Screen("quiz/{sessionId}/{courseId}") {
        fun createRoute(sessionId: String, courseId: String) = "quiz/$sessionId/$courseId"
    }
    data object Fitness : Screen("fitness")
    data object Chatbot : Screen("chatbot")
    data object Settings : Screen("settings")
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: String
) {
    data object Today : BottomNavItem("today", "Today", "today")
    data object Progress : BottomNavItem("progress", "Progress", "progress")
    data object Chat : BottomNavItem("chatbot", "AI Tutor", "chat")
    data object Settings : BottomNavItem("settings", "Settings", "settings")
}
