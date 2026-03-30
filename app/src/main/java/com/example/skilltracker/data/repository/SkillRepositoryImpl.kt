package com.example.skilltracker.data.repository

import com.example.skilltracker.data.local.dao.SkillDao
import com.example.skilltracker.data.local.toDomain
import com.example.skilltracker.data.local.toEntity
import com.example.skilltracker.data.remote.FirestoreService
import com.example.skilltracker.domain.model.Skill
import com.example.skilltracker.domain.repository.SkillRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillRepositoryImpl @Inject constructor(
    private val skillDao: SkillDao,
    private val firestoreService: FirestoreService
) : SkillRepository {

    override fun getAllSkills(): Flow<List<Skill>> {
        return skillDao.getAllSkills().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getLatestSkill(): Skill? {
        return skillDao.getLatestSkill()?.toDomain()
    }

    override suspend fun saveSkill(skill: Skill) {
        skillDao.insertSkill(skill.toEntity())
        try {
            firestoreService.saveSelectedSkill(skill)
        } catch (_: Exception) {
            // Offline-first: Firestore will sync when back online
        }
    }
}
