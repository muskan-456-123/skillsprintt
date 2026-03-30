package com.example.skilltracker.domain.model

data class Skill(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconName: String = "",
    val isCustom: Boolean = false
)
