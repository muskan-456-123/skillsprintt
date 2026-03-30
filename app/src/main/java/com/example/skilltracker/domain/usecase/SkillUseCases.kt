package com.example.skilltracker.domain.usecase

import com.example.skilltracker.domain.model.Skill
import com.example.skilltracker.domain.repository.SkillRepository
import javax.inject.Inject

class SaveSkillUseCase @Inject constructor(
    private val repository: SkillRepository
) {
    suspend operator fun invoke(skill: Skill) = repository.saveSkill(skill)
}

class GetLatestSkillUseCase @Inject constructor(
    private val repository: SkillRepository
) {
    suspend operator fun invoke() = repository.getLatestSkill()
}
