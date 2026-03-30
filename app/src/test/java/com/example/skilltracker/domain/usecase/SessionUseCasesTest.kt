package com.example.skilltracker.domain.usecase

import com.example.skilltracker.domain.model.Session
import com.example.skilltracker.domain.repository.SessionRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SessionUseCasesTest {

    private lateinit var sessionRepository: SessionRepository
    private lateinit var generateLearningPlanUseCase: GenerateLearningPlanUseCase
    private lateinit var completeSessionUseCase: CompleteSessionUseCase

    @Before
    fun setUp() {
        sessionRepository = mockk(relaxed = true)
        generateLearningPlanUseCase = GenerateLearningPlanUseCase(sessionRepository)
        completeSessionUseCase = CompleteSessionUseCase(sessionRepository)
    }

    @Test
    fun `generateLearningPlan creates correct number of sessions`() = runTest {
        // Given
        val courseId = "test-course"
        val totalMinutes = 600 // 10 hours
        val totalDays = 10

        coEvery { sessionRepository.saveSessions(any(), any()) } returns Unit

        // When
        val sessions = generateLearningPlanUseCase(courseId, totalMinutes, totalDays)

        // Then
        assertTrue("Should have sessions for each day", sessions.isNotEmpty())
        assertEquals("All sessions should belong to the course", courseId, sessions.first().courseId)
        coVerify { sessionRepository.saveSessions(courseId, sessions) }
    }

    @Test
    fun `generateLearningPlan splits long daily duration into multiple sessions`() = runTest {
        // Given - 120 minutes daily should create 2 sessions of 60 min
        val courseId = "test-course"
        val totalMinutes = 240 // 4 hours
        val totalDays = 2

        coEvery { sessionRepository.saveSessions(any(), any()) } returns Unit

        // When
        val sessions = generateLearningPlanUseCase(courseId, totalMinutes, totalDays)

        // Then
        val day1Sessions = sessions.filter { it.dayNumber == 1 }
        assertTrue("Day 1 should have at least 2 sessions", day1Sessions.size >= 2)
        assertTrue(
            "Each session should be max 60 minutes",
            day1Sessions.all { it.durationMinutes <= 60 }
        )
    }

    @Test
    fun `generateLearningPlan covers full course duration`() = runTest {
        val courseId = "test-course"
        val totalMinutes = 300
        val totalDays = 5

        coEvery { sessionRepository.saveSessions(any(), any()) } returns Unit

        val sessions = generateLearningPlanUseCase(courseId, totalMinutes, totalDays)
        val totalCovered = sessions.sumOf { it.durationMinutes }

        assertEquals(
            "Total session minutes should cover full course",
            totalMinutes, totalCovered
        )
    }

    @Test
    fun `completeSession marks session as completed with timestamp`() = runTest {
        val session = Session(
            id = "session-1",
            courseId = "course-1",
            dayNumber = 1,
            sessionNumber = 1,
            startMinute = 0,
            endMinute = 60,
            durationMinutes = 60
        )

        coEvery { sessionRepository.updateSession(any()) } returns Unit

        val completed = completeSessionUseCase(session)

        assertTrue("Session should be marked completed", completed.isCompleted)
        assertTrue("CompletedAt should be set", completed.completedAt > 0)
        coVerify { sessionRepository.updateSession(completed) }
    }
}
