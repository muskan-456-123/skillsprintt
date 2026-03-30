package com.example.skilltracker.domain.repository

import com.example.skilltracker.domain.model.Skill
import kotlinx.coroutines.flow.Flow

interface SkillRepository {
    fun getAllSkills(): Flow<List<Skill>>
    suspend fun getLatestSkill(): Skill?
    suspend fun saveSkill(skill: Skill)
}
