package com.example.skilltracker.presentation.skill

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilltracker.domain.model.Skill
import com.example.skilltracker.domain.usecase.SaveSkillUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SkillSelectionState(
    val skills: List<Skill> = predefinedSkills,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val selectedSkill: Skill? = null,
    val navigateToCoursePicker: String? = null
)

val predefinedSkills = listOf(
    Skill("1", "Python", "Learn Python programming from scratch", "code", false),
    Skill("2", "UI/UX Design", "Master user interface & experience design", "design", false),
    Skill("3", "Data Science", "Analyze data and build ML models", "analytics", false),
    Skill("4", "Web Development", "Build modern websites and web apps", "web", false),
    Skill("5", "Machine Learning", "Deep dive into AI and ML algorithms", "psychology", false),
    Skill("6", "Android Development", "Build native Android applications", "phone_android", false),
    Skill("7", "DSA", "Data Structures & Algorithms mastery", "account_tree", false),
    Skill("8", "Cloud Computing", "AWS, Azure, and GCP fundamentals", "cloud", false),
    Skill("9", "Cybersecurity", "Learn ethical hacking & security", "security", false),
    Skill("10", "DevOps", "CI/CD, Docker, Kubernetes", "settings", false),
    Skill("11", "Flutter", "Cross-platform mobile development", "flutter_dash", false),
    Skill("12", "Blockchain", "Web3, Smart Contracts & DApps", "link", false),
)

@HiltViewModel
class SkillSelectionViewModel @Inject constructor(
    private val saveSkillUseCase: SaveSkillUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SkillSelectionState())
    val state: StateFlow<SkillSelectionState> = _state.asStateFlow()

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            skills = if (query.isBlank()) predefinedSkills
            else predefinedSkills.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        )
    }

    fun onSkillSelected(skill: Skill) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            saveSkillUseCase(skill)
            _state.value = _state.value.copy(
                isLoading = false,
                selectedSkill = skill,
                navigateToCoursePicker = skill.name
            )
        }
    }

    fun onCustomSkillSubmit(name: String) {
        if (name.isBlank()) return
        val customSkill = Skill(
            id = UUID.randomUUID().toString(),
            name = name.trim(),
            description = "Custom skill: ${name.trim()}",
            iconName = "school",
            isCustom = true
        )
        onSkillSelected(customSkill)
    }

    fun onNavigationHandled() {
        _state.value = _state.value.copy(navigateToCoursePicker = null)
    }
}
